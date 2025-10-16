package projects.utils;

import projects.dao.CustomerDAO;
import projects.models.Customer;
import projects.models.Ticket;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.text.NumberFormat;
import java.sql.SQLException;
import java.util.Locale;

public class TicketPrinter {
    
    private final CustomerDAO customerDAO;
    
    public TicketPrinter() {
        this.customerDAO = new CustomerDAO();
    }

    /**
     * Định dạng và hiển thị thông tin chi tiết của Vé Bán (In vé điện tử).
     * @param component JComponent cha để hiển thị hộp thoại.
     * @param maVeBan Mã vé bán đã đặt thành công.
     * @return Đối tượng Customer (để truyền cho NotificationService).
     */
    public Customer printE_Ticket(Component component, int maVeBan) {
        // Khắc phục cảnh báo Deprecated: dùng Locale.of() cho Java 19+
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));
        Customer customer = null;
        
        try {
            // Lấy chi tiết vé bán
            Ticket ticketDetails = customerDAO.getTicketDAO().getTicketSaleDetails(maVeBan);
            
            if (ticketDetails == null) {
                JOptionPane.showMessageDialog(component, "Không tìm thấy chi tiết vé để in.", "Lỗi In Vé", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            
            // Lấy thông tin khách hàng
            customer = customerDAO.getCustomerById(ticketDetails.getMaKhachHang());

            // Xây dựng nội dung vé
            StringBuilder ticketContent = new StringBuilder();
            ticketContent.append("=============== VÉ MÁY BAY ĐIỆN TỬ ==============\n");
            ticketContent.append(String.format("Mã Vé Bán: %d\n", maVeBan));
            ticketContent.append("---------------------------------------------------\n");
            ticketContent.append(String.format("KHÁCH HÀNG: %s\n", customer != null ? customer.getTenKhachHang() : "Không xác định"));
            ticketContent.append(String.format("CMND/CCCD: %s\n", customer != null ? customer.getCccd() : "N/A"));
            ticketContent.append("---------------------------------------------------\n");
            ticketContent.append(String.format("Loại Vé: %s\n", ticketDetails.getLoaiVe()));
            ticketContent.append(String.format("Chuyến Bay ID: %d\n", ticketDetails.getMaChuyenBay()));
            ticketContent.append(String.format("Số Lượng: %d\n", ticketDetails.getSoLuongBan()));
            ticketContent.append("---------------------------------------------------\n");
            ticketContent.append(String.format("Đơn Giá: %s\n", currencyFormat.format(ticketDetails.getDonGia())));
            ticketContent.append(String.format("THÀNH TIỀN: %s\n", currencyFormat.format(ticketDetails.getThanhTien())));
            ticketContent.append("================= XIN CẢM ƠN! ===================");

            // Hiển thị vé
            JOptionPane.showMessageDialog(component, ticketContent.toString(), "In Vé Điện Tử #" + maVeBan, JOptionPane.PLAIN_MESSAGE);
            
            return customer; // Trả về khách hàng đã tìm thấy

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, "Lỗi CSDL khi lấy thông tin in vé: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}