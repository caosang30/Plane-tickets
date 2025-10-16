package projects.services;

import projects.DBConnection;
import projects.dao.*;
import projects.models.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class BookingService {

    private final CustomerDAO customerDAO;
    private final TicketDAO ticketDAO;
    private final TransactionDAO transactionDAO;
    private final NotificationDAO notificationDAO;
    private final InvoiceDAO invoiceDAO;
    private final InvoiceService invoiceService;
    private final FlightDAO flightDAO; // KHAI BÁO FlightDAO

    // Đặt buffer time: 4 giờ (4 * 60 * 60 * 1000 milliseconds)
    private static final long CHECK_IN_BUFFER_MS = TimeUnit.HOURS.toMillis(4); // 4 tiếng

    public BookingService() {
        this.customerDAO = new CustomerDAO();
        this.ticketDAO = new TicketDAO();
        this.transactionDAO = new TransactionDAO();
        this.notificationDAO = new NotificationDAO();
        this.invoiceDAO = new InvoiceDAO();
        this.invoiceService = new InvoiceService();
        this.flightDAO = new FlightDAO(); // KHỞI TẠO FlightDAO
    }

    public TicketDAO getTicketDAO() {
        return ticketDAO;
    }

    //----------------------------------------------------------------------------------------------------
    // BƯỚC 2: PHƯƠNG THỨC KIỂM TRA THỜI GIAN
    //----------------------------------------------------------------------------------------------------

    /**
     * Kiểm tra xem chuyến bay có còn hợp lệ để giao dịch (đặt/hủy/đổi) hay không.
     * @param maChuyenBay Mã chuyến bay cần kiểm tra.
     * @throws Exception nếu chuyến bay đã khởi hành hoặc quá cận kề (dưới 4 giờ).
     */
    private void checkFlightTimeValidity(int maChuyenBay) throws Exception {
        // Cần đảm bảo FlightDAO.getFlightById() trả về object Flight có NgayKhoiHanh
        Flight flight = flightDAO.getFlightById(maChuyenBay); 
        
        if (flight == null) {
            throw new Exception("Không tìm thấy thông tin chuyến bay có mã: " + maChuyenBay + " để kiểm tra thời gian.");
        }

        Date ngayKhoiHanh = flight.getNgayKhoiHanh();
        Date thoiGianHienTai = new Date();
        long thoiGianConLaiMs = ngayKhoiHanh.getTime() - thoiGianHienTai.getTime();

        // 1. Kiểm tra Ngày/Giờ trong quá khứ
        if (thoiGianConLaiMs < 0) {
            throw new Exception("Lỗi nghiệp vụ: Chuyến bay #" + maChuyenBay + " đã khởi hành. Không thể thực hiện giao dịch.");
        }

        // 2. Kiểm tra Thời gian đệm (Buffer Time: 4 giờ)
        if (thoiGianConLaiMs < CHECK_IN_BUFFER_MS) {
            long gioConLai = TimeUnit.MILLISECONDS.toHours(thoiGianConLaiMs);
            throw new Exception(String.format(
                "Lỗi nghiệp vụ: Chuyến bay #%d quá cận kề. Cần giao dịch ít nhất %d giờ trước giờ bay. (Hiện tại còn khoảng %d giờ)",
                maChuyenBay, 
                CHECK_IN_BUFFER_MS / TimeUnit.HOURS.toMillis(1), 
                gioConLai
            ));
        }
    }

    //----------------------------------------------------------------------------------------------------
    // BƯỚC 3: ÁP DỤNG KIỂM TRA VÀO CÁC PHƯƠNG THỨC
    //----------------------------------------------------------------------------------------------------

    /**
     * Thực hiện toàn bộ quy trình đặt vé trong một Transaction (Đã tích hợp Hóa đơn).
     */
    public int bookTicket(Ticket newTicket, Customer customerInfo, int maNhanVien, String hinhThucThanhToan) throws SQLException {
        Connection conn = null;
        int maKhachHang = 0;
        int maVeBan = 0;
        Ticket currentTicket;
        String filePath;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // *** ÁP DỤNG KIỂM TRA NGÀY KHỞI HÀNH HỢP LỆ ***
            checkFlightTimeValidity(newTicket.getMaChuyenBay());

            // Bước 1: Tìm khách hàng hiện tại (nếu có) hoặc Thêm mới
            Customer existingCustomer = customerDAO.findCustomerByIdentity(customerInfo.getCccd(), customerInfo.getSoDienThoai());
            if (existingCustomer == null) {
                maKhachHang = customerDAO.addCustomer(customerInfo, conn);
            } else {
                maKhachHang = existingCustomer.getMaKhachHang();
            }

            // Khởi tạo đối tượng vé với MaKhachHang đã xác định
            currentTicket = new Ticket(
                    newTicket.getMaVe(),
                    maNhanVien,
                    maKhachHang,
                    newTicket.getMaChuyenBay(),
                    newTicket.getSoLuongBan(),
                    newTicket.getLoaiVe(),
                    newTicket.getDonGia()
            );

            // Bước 2: Kiểm tra trùng vé
            int ticketsCount = ticketDAO.countExistingTickets(
                    maKhachHang,
                    currentTicket.getMaChuyenBay(),
                    currentTicket.getMaVe()
            );

            if (ticketsCount > 0) {
                throw new Exception(String.format(
                        "Khách hàng đã có %d vé loại '%s' trên chuyến bay này (Mã KH: %d). Không thể đặt trùng.",
                        ticketsCount, currentTicket.getLoaiVe(), maKhachHang
                ));
            }

            // Bước 3: Thêm vé bán và cập nhật ghế trống
            maVeBan = ticketDAO.addTicketSale(currentTicket, conn);
            currentTicket.setMaVeBan(maVeBan);

            // Bước 4: Ghi nhận lịch sử giao dịch (BOOK)
            Transaction logBook = new Transaction(maVeBan, maNhanVien, "BOOK", currentTicket.getThanhTien(), hinhThucThanhToan);
            transactionDAO.logTransaction(logBook, conn);

            // Bước 5: Tạo và lưu hóa đơn
            filePath = invoiceService.createInvoiceFile(currentTicket);
            Invoice newInvoice = new Invoice(maVeBan, currentTicket.getThanhTien(), filePath, "DA_XUAT");
            int maHoaDon = invoiceDAO.saveInvoice(newInvoice, conn);

            // Bước 6: Ghi log thông báo
            String logContent = "ĐẶT VÉ #" + maVeBan + " - Đã xuất Hóa đơn #" + maHoaDon + " - HTTT: " + hinhThucThanhToan;
            notificationDAO.logConfirmation(maVeBan, logContent, maNhanVien, conn);

            conn.commit();
            return maVeBan;

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw new SQLException("Lỗi giao dịch đặt vé (SQL): " + e.getMessage(), e);
        } catch (Exception e) {
            // Chuyển Exception nghiệp vụ (từ checkFlightTimeValidity và kiểm tra trùng vé) thành SQLException để xử lý Rollback
            if (conn != null) conn.rollback();
            throw new SQLException(e.getMessage(), e);
        } finally {
            if (conn != null) conn.close();
        }
    }

    /**
     * Hủy vé (ghi nhận số tiền âm trong Transaction)
     */
    public double cancelTicket(int maVeBan, int maNhanVien, String lyDoHuy, String hinhThucThanhToan) throws SQLException {
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Lấy chi tiết vé cũ
            Ticket oldTicket = ticketDAO.getTicketSaleDetails(maVeBan);
            if (oldTicket == null || oldTicket.getSoLuongBan() == 0) {
                throw new SQLException("Không tìm thấy vé bán hợp lệ cần hủy: #" + maVeBan);
            }
            
            // *** ÁP DỤNG KIỂM TRA NGÀY KHỞI HÀNH HỢP LỆ ***
            checkFlightTimeValidity(oldTicket.getMaChuyenBay());

            // 2. Tính toán tiền hoàn lại (Phí hủy 10%)
            double totalAmount = oldTicket.getThanhTien();
            double feeRate = 0.10;
            double refundAmount = totalAmount * (1 - feeRate);

            // 3. Thực hiện hủy và hoàn trả ghế
            ticketDAO.cancelTicketSale(
                    maVeBan,
                    oldTicket.getMaVe(),
                    oldTicket.getSoLuongBan(),
                    lyDoHuy,
                    oldTicket.getMaChuyenBay(),
                    conn
            );

            // 4. Ghi nhận lịch sử giao dịch (CANCEL)
            double soTienHoanGhiLog = -refundAmount;
            Transaction logCancel = new Transaction(maVeBan, maNhanVien, "CANCEL", soTienHoanGhiLog, hinhThucThanhToan);
            transactionDAO.logTransaction(logCancel, conn);

            // Ghi log thông báo HỦY VÉ
            String logContent = "HỦY VÉ #" + maVeBan + " - Hoàn tiền: " + String.format("%,.0f VNĐ", refundAmount);
            notificationDAO.logConfirmation(maVeBan, logContent, maNhanVien, conn);

            conn.commit();
            return refundAmount;

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw new SQLException("Lỗi giao dịch hủy vé: " + e.getMessage(), e);
        } catch (Exception e) {
             // Chuyển Exception nghiệp vụ (từ checkFlightTimeValidity) thành SQLException
            if (conn != null) conn.rollback();
            throw new SQLException("Lỗi nghiệp vụ hủy vé: " + e.getMessage(), e);
        } finally {
            if (conn != null) conn.close();
        }
    }

    /**
     * Đổi vé (ghi nhận chênh lệch và tạo hóa đơn mới)
     */
    public double exchangeTicket(int maVeBanCu, Ticket newTicket, Customer customerInfo, int maNhanVien, String hinhThucThanhToan) throws SQLException {
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Lấy chi tiết vé cũ
            Ticket oldTicket = ticketDAO.getTicketSaleDetails(maVeBanCu);
            if (oldTicket == null || oldTicket.getSoLuongBan() == 0) {
                throw new SQLException("Không tìm thấy vé bán cũ hợp lệ cần đổi: #" + maVeBanCu);
            }

            // *** ÁP DỤNG KIỂM TRA NGÀY KHỞI HÀNH HỢP LỆ ***
            // Phải kiểm tra cả vé cũ (để xem có quá hạn hủy/đổi không)
            checkFlightTimeValidity(oldTicket.getMaChuyenBay());
            // và vé mới (để xem chuyến mới có hợp lệ để đặt không)
            checkFlightTimeValidity(newTicket.getMaChuyenBay()); 

            // 2. Hủy vé cũ
            ticketDAO.cancelTicketSale(
                    maVeBanCu,
                    oldTicket.getMaVe(),
                    oldTicket.getSoLuongBan(),
                    "Đổi vé",
                    oldTicket.getMaChuyenBay(),
                    conn
            );

            // 3. Xử lý khách hàng
            int maKhachHang;
            Customer existingCustomer = customerDAO.findCustomerByIdentity(customerInfo.getCccd(), customerInfo.getSoDienThoai());
            if (existingCustomer == null) {
                maKhachHang = customerDAO.addCustomer(customerInfo, conn);
            } else {
                maKhachHang = existingCustomer.getMaKhachHang();
            }

            // 4. Đặt vé mới
            newTicket = new Ticket(
                    newTicket.getMaVe(),
                    maNhanVien,
                    maKhachHang,
                    newTicket.getMaChuyenBay(),
                    newTicket.getSoLuongBan(),
                    newTicket.getLoaiVe(),
                    newTicket.getDonGia()
            );

            int maVeBanMoi = ticketDAO.addTicketSale(newTicket, conn);
            newTicket.setMaVeBan(maVeBanMoi);

            // 5. Tạo hóa đơn mới
            String newFilePath = invoiceService.createInvoiceFile(newTicket);
            Invoice newInvoice = new Invoice(maVeBanMoi, newTicket.getThanhTien(), newFilePath, "DA_XUAT");
            int maHoaDonMoi = invoiceDAO.saveInvoice(newInvoice, conn);

            // 6. Tính toán chênh lệch
            double oldAmount = oldTicket.getThanhTien();
            double newAmount = newTicket.getThanhTien();
            double exchangeFeeRate = 0.05;
            double exchangeFee = newAmount * exchangeFeeRate;
            double difference = (newAmount + exchangeFee) - oldAmount;

            // 7. Ghi nhận giao dịch
            String logType = (difference >= 0) ? "THUPHI" : "HOANTI";
            double logAmount = difference;
            Transaction logExchange = new Transaction(maVeBanMoi, maNhanVien, logType, logAmount, hinhThucThanhToan);
            transactionDAO.logTransaction(logExchange, conn);

            // 8. Ghi log thông báo
            String logContent = "ĐỔI VÉ: Vé cũ #" + maVeBanCu + " sang vé mới #" + maVeBanMoi + " - Đã xuất Hóa đơn #" + maHoaDonMoi;
            notificationDAO.logConfirmation(maVeBanMoi, logContent, maNhanVien, conn);

            conn.commit();
            return difference;

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw new SQLException("Lỗi giao dịch đổi vé: " + e.getMessage(), e);
        } catch (Exception e) {
            // Chuyển Exception nghiệp vụ (từ checkFlightTimeValidity) thành SQLException
            if (conn != null) conn.rollback();
            throw new SQLException("Lỗi nghiệp vụ đổi vé: " + e.getMessage(), e);
        } finally {
            if (conn != null) conn.close();
        }
    }
}