package projects.dao;

import projects.models.Invoice;
import java.sql.*;

public class InvoiceDAO {

    private static final String TABLE_NAME = "HOADON"; 

    /**
     * Lưu thông tin hóa đơn vào bảng HOADON.
     * @param invoice Đối tượng Invoice
     * @param conn Kết nối CSDL đang mở (dùng cho Transaction)
     * @return MaHoaDon (ID tự tăng)
     */
    public int saveInvoice(Invoice invoice, Connection conn) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (MaVeBan, TongTien, DuongDanFile, TrangThai, NgayXuat) VALUES (?, ?, ?, ?, GETDATE())";
        int maHoaDon = -1;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, invoice.getMaVeBan()); 
            ps.setDouble(2, invoice.getTongTien());
            ps.setString(3, invoice.getDuongDanFile());
            ps.setString(4, invoice.getTrangThai());
            
            ps.executeUpdate();
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    maHoaDon = generatedKeys.getInt(1);
                    invoice.setMaHoaDon(maHoaDon);
                } else {
                    throw new SQLException("Lưu hóa đơn thất bại, không lấy được ID.");
                }
            }
        }
        return maHoaDon;
    }
}