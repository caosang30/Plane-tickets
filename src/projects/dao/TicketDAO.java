package projects.dao;

import projects.DBConnection;
import projects.models.Ticket;

import java.sql.*;


public class TicketDAO {

    /** * Thêm vé bán mới và cập nhật số lượng vé còn lại (Dùng trong Transaction).
     */
    public int addTicketSale(Ticket ticket, Connection conn) throws SQLException {
        // Bước 1: Thêm vé bán vào bảng VEBAN
        String sqlVeban = "INSERT INTO VEBAN (MaVe, MaNhanVien, MaKhachHang, MaChuyenBay, SoLuongBan) VALUES (?, ?, ?, ?, ?)";
        int maVeBan = -1;
        
        try (PreparedStatement ps = conn.prepareStatement(sqlVeban, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, ticket.getMaVe());
            ps.setInt(2, ticket.getMaNhanVien());
            ps.setInt(3, ticket.getMaKhachHang());
            ps.setInt(4, ticket.getMaChuyenBay());
            ps.setInt(5, ticket.getSoLuongBan());
            
            ps.executeUpdate();
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    maVeBan = generatedKeys.getInt(1);
                    ticket.setMaVeBan(maVeBan); 
                } else {
                    throw new SQLException("Thêm vé bán thất bại, không lấy được ID.");
                }
            }
        }
        
        // Bước 2: Cập nhật SoLuongCon trong THONGTINCHITIETVE
        String sqlUpdateChiTiet = "UPDATE THONGTINCHITIETVE SET SoLuongCon = SoLuongCon - ? WHERE MaVe = ?";
        try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateChiTiet)) {
            psUpdate.setInt(1, ticket.getSoLuongBan());
            psUpdate.setInt(2, ticket.getMaVe());
            
            if (psUpdate.executeUpdate() == 0) {
                 throw new SQLException("Cập nhật số lượng chi tiết vé thất bại hoặc số lượng còn không đủ.");
            }
        }
        
        return maVeBan;
    }
    
    /**
     * KIỂM TRA TRÙNG VÉ.
     */
    public int countExistingTickets(int maKhachHang, int maChuyenBay, int maVe) throws SQLException {
        // Vé hợp lệ là vé có trong VEBAN VÀ không có trong VEHUY
        String sql = "SELECT ISNULL(SUM(vb.SoLuongBan), 0) FROM VEBAN vb " +
                     "LEFT JOIN dbo.VEHUY vh ON vb.MaVeBan = vh.MaVeBan " +
                     "WHERE vb.MaKhachHang = ? AND vb.MaChuyenBay = ? AND vb.MaVe = ? AND vh.MaVeBan IS NULL";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maKhachHang);
            ps.setInt(2, maChuyenBay);
            ps.setInt(3, maVe);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1); 
                }
            }
        }
        return 0;
    }

    /** * Lấy chi tiết vé bán (dùng cho Hủy/Đổi vé).
     */
    public Ticket getTicketSaleDetails(int maVeBan) throws SQLException {
        String sql = "SELECT vb.MaVeBan, vb.MaVe, vb.MaNhanVien, vb.MaKhachHang, vb.MaChuyenBay, vb.SoLuongBan, " +
                     " 	 tt.LoaiVe, tt.Gia " +
                     "FROM VEBAN vb " +
                     "JOIN THONGTINCHITIETVE tt ON vb.MaVe = tt.MaVe " +
                     "WHERE vb.MaVeBan = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maVeBan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Ticket(
                        rs.getInt("MaVeBan"),
                        rs.getInt("MaVe"),
                        rs.getInt("MaNhanVien"),
                        rs.getInt("MaKhachHang"),
                        rs.getInt("MaChuyenBay"),
                        rs.getInt("SoLuongBan"),
                        rs.getString("LoaiVe"),
                        rs.getDouble("Gia")
                    );
                }
            }
        }
        return null;
    }
    
    /** * Đánh dấu vé đã hủy và hoàn trả ghế (Dùng trong Transaction).
     * @param maVe Mã loại vé (để hoàn trả ghế)
     * @param soLuongHuy Số lượng vé hủy (thường bằng SoLuongBan)
     * @param maChuyenBay Mã chuyến bay (để ghi log vào VEHUY)
     */
    public void cancelTicketSale(int maVeBan, int maVe, int soLuongHuy, String lyDoHuy, int maChuyenBay, Connection conn) throws SQLException {
        
        // Bước 1: Ghi nhận hủy vé vào bảng VEHUY (Khắc phục lỗi NULL MaVe/MaChuyenBay/SoLuongHoanTra)
        String sqlCancel = "INSERT INTO VEHUY (MaVeBan, LyDoHuy, NgayHuy, MaVe, MaChuyenBay, SoLuongHoanTra) VALUES (?, ?, GETDATE(), ?, ?, ?)";
        
        try (PreparedStatement psCancel = conn.prepareStatement(sqlCancel)) {
            psCancel.setInt(1, maVeBan);
            psCancel.setString(2, lyDoHuy);
            psCancel.setInt(3, maVe); 
            psCancel.setInt(4, maChuyenBay); 
            psCancel.setInt(5, soLuongHuy); 
            psCancel.executeUpdate();
        }
        
        // Bước 2: Hoàn trả ghế (Cập nhật SoLuongCon trong THONGTINCHITIETVE)
        String sqlUpdateChiTiet = "UPDATE THONGTINCHITIETVE SET SoLuongCon = SoLuongCon + ? WHERE MaVe = ?";
        try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateChiTiet)) {
            psUpdate.setInt(1, soLuongHuy);
            psUpdate.setInt(2, maVe);
            psUpdate.executeUpdate();
        }

        // Bước 3: CẬP NHẬT SoLuongBan VỀ 0 ĐỂ ĐÁNH DẤU HỦY (Khắc phục lỗi Khóa Ngoại)
        String sqlUpdateVeban = "UPDATE VEBAN SET SoLuongBan = 0 WHERE MaVeBan = ?";
        try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateVeban)) {
            psUpdate.setInt(1, maVeBan);
            psUpdate.executeUpdate();
        }
    }
}