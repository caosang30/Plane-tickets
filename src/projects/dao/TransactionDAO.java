package projects.dao;

import projects.DBConnection;
import projects.models.Transaction;
import projects.models.TransactionDetail;
import projects.models.RevenueReport; 

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    private static final String TABLE_NAME = "LICHSUGIAODICH"; 

    /**
     * Ghi lại một bản ghi giao dịch (LOG) vào CSDL.
     * PHẢI được gọi bằng một Connection đang trong Transaction.
     * @param transaction Đối tượng Transaction cần ghi log 
     * @param conn Kết nối CSDL đang mở (dùng cho Transaction)
     * @return MaGiaoDich (ID tự tăng)
     */
    public int logTransaction(Transaction transaction, Connection conn) throws SQLException {
        
        String sql = "INSERT INTO " + TABLE_NAME + " (MaVeBan, MaNhanVien, LoaiGiaoDich, SoTienLienQuan, ThoiGian, HinhThucThanhToan) VALUES (?, ?, ?, ?, GETDATE(), ?)";
        int maGiaoDich = -1;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, transaction.getMaVeBan()); 
            ps.setInt(2, transaction.getMaNhanVien());
            ps.setString(3, transaction.getLoaiGiaoDich());
            ps.setDouble(4, transaction.getSoTienLienQuan());
            ps.setString(5, transaction.getHinhThucThanhToan()); 
            
            ps.executeUpdate();
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    maGiaoDich = generatedKeys.getInt(1);
                    transaction.setMaGiaoDich(maGiaoDich);
                } else {
                    throw new SQLException("Ghi log giao dịch thất bại, không lấy được ID.");
                }
            }
        }
        return maGiaoDich;
    }

    // --- BÁO CÁO TỔNG THU THEO NGÀY VÀ NHÂN VIÊN ---
    public List<RevenueReport> getEmployeeDailyRevenueReport() throws SQLException {
        List<RevenueReport> reports = new ArrayList<>();
        String sql = "SELECT " +
                     "CAST(ThoiGian AS DATE) AS NgayGiaoDich, " +
                     "MaNhanVien, " +
                     "ISNULL(SUM(SoTienLienQuan), 0) AS TongThu " +
                     "FROM " + TABLE_NAME +
                     " WHERE LoaiGiaoDich IN ('BOOK', 'THU_PHI', 'HOAN_DOI') " + 
                     " GROUP BY CAST(ThoiGian AS DATE), MaNhanVien " +
                     " ORDER BY NgayGiaoDich DESC, MaNhanVien ASC";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                RevenueReport report = new RevenueReport(
                    rs.getString("NgayGiaoDich"), 
                    rs.getInt("MaNhanVien"), 
                    rs.getDouble("TongThu")
                );
                reports.add(report);
            }
        }
        return reports;
    }
    
    // --- BÁO CÁO TỔNG THU VÀ HOÀN TIỀN THEO NHÂN VIÊN ---
    public List<RevenueReport> getRevenueByEmployee() throws SQLException {
        List<RevenueReport> reports = new ArrayList<>();
        String sql = "SELECT MaNhanVien, " +
                     "SUM(CASE WHEN LoaiGiaoDich IN ('BOOK', 'THU_PHI', 'HOAN_DOI') THEN SoTienLienQuan ELSE 0 END) AS TotalSales, " + 
                     "SUM(CASE WHEN LoaiGiaoDich IN ('HUY_VE', 'HOAN_DOI') THEN SoTienLienQuan ELSE 0 END) AS TotalRefunds " + 
                     "FROM " + TABLE_NAME + 
                     " GROUP BY MaNhanVien ORDER BY MaNhanVien";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                reports.add(new RevenueReport(
                    rs.getInt("MaNhanVien"),
                    rs.getDouble("TotalSales"),
                    rs.getDouble("TotalRefunds")
                ));
            }
        }
        return reports;
    }
    
    // --- BÁO CÁO PHÂN LOẠI THANH TOÁN ---
    public List<RevenueReport> getPaymentDistributionReport() throws SQLException {
        List<RevenueReport> distributions = new ArrayList<>();
        String sql = "SELECT HinhThucThanhToan, CAST(ThoiGian AS DATE) AS NgayGiaoDich, SUM(SoTienLienQuan) AS TotalAmount, COUNT(*) AS TotalCount " +
                     "FROM " + TABLE_NAME + 
                     " WHERE LoaiGiaoDich IN ('BOOK', 'THU_PHI', 'HOAN_DOI') " + 
                     " GROUP BY HinhThucThanhToan, CAST(ThoiGian AS DATE) " +
                     " ORDER BY NgayGiaoDich DESC, HinhThucThanhToan";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                distributions.add(new RevenueReport(
                    rs.getString("NgayGiaoDich"), 
                    rs.getString("HinhThucThanhToan"), 
                    rs.getDouble("TotalAmount"),
                    rs.getInt("TotalCount")
                ));
            }
        }
        return distributions;
    }
    
    /**
     * Tìm kiếm chi tiết giao dịch dựa trên MaGiaoDich.
     * @param id Mã giao dịch cần tra cứu
     * @return TransactionDetail nếu tìm thấy, ngược lại là null
     */
    public TransactionDetail getTransactionDetails(int id) throws SQLException {
        // CHỈ TÌM THEO MaGiaoDich (Đã sửa)
        String sql = "SELECT TOP 1 MaGiaoDich, MaVeBan, MaNhanVien, LoaiGiaoDich, SoTienLienQuan, ThoiGian, HinhThucThanhToan " +
                     "FROM " + TABLE_NAME + 
                     " WHERE MaGiaoDich = ?"; 
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id); // Chỉ gán tham số cho MaGiaoDich
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TransactionDetail(
                        rs.getInt("MaGiaoDich"),
                        rs.getInt("MaVeBan"),
                        rs.getInt("MaNhanVien"),
                        rs.getString("LoaiGiaoDich"),
                        rs.getDouble("SoTienLienQuan"),
                        rs.getTimestamp("ThoiGian"),
                        rs.getString("HinhThucThanhToan")
                    );
                }
            }
        }
        return null;
    }
    
    /**
     * Ghi lại giao dịch hoàn tiền/thu phí. (Tạo kết nối riêng)
     */
    public int logRefundTransaction(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (MaVeBan, MaNhanVien, LoaiGiaoDich, SoTienLienQuan, ThoiGian, HinhThucThanhToan) VALUES (?, ?, ?, ?, GETDATE(), ?)";
        int maGiaoDich = -1;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, transaction.getMaVeBan()); 
            ps.setInt(2, transaction.getMaNhanVien());
            ps.setString(3, transaction.getLoaiGiaoDich());
            ps.setDouble(4, transaction.getSoTienLienQuan());
            ps.setString(5, transaction.getHinhThucThanhToan()); 

            ps.executeUpdate();
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    maGiaoDich = generatedKeys.getInt(1);
                    transaction.setMaGiaoDich(maGiaoDich);
                } else {
                    throw new SQLException("Ghi log giao dịch hoàn tiền/phí thất bại, không lấy được ID.");
                }
            }
        }
        return maGiaoDich;
    }
}
