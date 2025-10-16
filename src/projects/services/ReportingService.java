package projects.services;

import projects.dao.TransactionDAO;
import projects.models.RevenueReport; 
import projects.models.TransactionDetail; // Cần import TransactionDetail để trả về chi tiết giao dịch
import projects.models.Transaction; // Cần import Transaction để ghi log giao dịch hoàn tiền/phí
import java.sql.SQLException;
import java.util.List;

public class ReportingService {

    private final TransactionDAO transactionDAO;

    public ReportingService() {
        this.transactionDAO = new TransactionDAO();
    }

    // ==========================================================
    // CHỨC NĂNG BÁO CÁO
    // ==========================================================
    
    /**
     * Lấy báo cáo tổng thu theo nhân viên và ngày.
     */
    public List<RevenueReport> getEmployeeDailyRevenueReport() throws SQLException {
        return transactionDAO.getEmployeeDailyRevenueReport(); 
    }
    
    /**
     * Lấy báo cáo phân loại thanh toán (theo hình thức: Tiền mặt, Thẻ, CK).
     */
    public List<RevenueReport> getPaymentDistributionReport() throws SQLException {
        return transactionDAO.getPaymentDistributionReport(); 
    }

    // ==========================================================
    // CHỨC NĂNG QUẢN LÝ GIAO DỊCH CHI TIẾT (MỚI)
    // ==========================================================
    
    /**
     * Tra cứu chi tiết giao dịch theo Mã giao dịch hoặc Mã vé bán.
     * @param id Mã giao dịch/vé bán cần tra cứu.
     */
    public TransactionDetail getTransactionDetails(int id) throws SQLException {
        return transactionDAO.getTransactionDetails(id);
    }
    
    /**
     * Xử lý và ghi nhận nghiệp vụ hoàn tiền/thu phí.
     * @param maVeBan Mã vé liên quan đến giao dịch hoàn tiền
     * @param maNhanVien Mã nhân viên thực hiện
     * @param loaiGiaoDich Loại giao dịch (CANCEL, EXCHANGE_REFUND, THU_PHI, ...)
     * @param soTien Số tiền liên quan (Số dương nếu là Thu, Số âm nếu là Hoàn)
     * @return true nếu ghi log thành công
     */
    public boolean processRefundOrFee(int maVeBan, int maNhanVien, String loaiGiaoDich, double soTien) throws SQLException {
        // TẠO OBJECT TRANSACTION
        Transaction transaction = new Transaction(); 
        transaction.setMaVeBan(maVeBan);
        transaction.setMaNhanVien(maNhanVien);
        transaction.setLoaiGiaoDich(loaiGiaoDich);
        transaction.setSoTienLienQuan(soTien);
        transaction.setHinhThucThanhToan("NA"); // Not Applicable cho giao dịch nội bộ/phụ

        // Ghi log giao dịch hoàn tiền/phí
        int maGiaoDichMoi = transactionDAO.logRefundTransaction(transaction);
        return maGiaoDichMoi != -1;
    }
    
    // PHƯƠNG THỨC IN RA CONSOLE (TESTING)

    /**
     * Ví dụ phương thức để in báo cáo Tổng thu ra console.
     */
    public void printEmployeeDailyRevenueReport() throws SQLException {
        List<RevenueReport> report = getEmployeeDailyRevenueReport(); 
        
        System.out.println("\n--- BÁO CÁO TỔNG THU THEO NHÂN VIÊN VÀ NGÀY ---");
        System.out.printf("%-15s %-15s %s\n", "NGÀY", "MÃ NV", "TỔNG THU");
        System.out.println("-------------------------------------------");
        
        for (RevenueReport entry : report) {
            System.out.printf("%-15s %-15d %,.0f VNĐ\n", 
                entry.getNgayGiaoDich(), 
                entry.getMaNhanVien(), 
                entry.getTongThu()); 
        }
        System.out.println("-------------------------------------------\n");
    }
}