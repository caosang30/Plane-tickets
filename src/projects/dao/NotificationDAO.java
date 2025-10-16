package projects.dao;

import projects.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NotificationDAO {

    /**
     * Lưu nhật ký thông báo (Transactional).
     * SỬ DỤNG TÊN TRƯỜNG KHỚP VỚI CSDL CỦA BẠN (MaNhanVien, NoiDung, ThoiGian).
     * Bảng THONGBAO CỦA BẠN THIẾU CỘT MaVeBan, nên ta không thể lưu MaVeBan vào trực tiếp.
     * Ta sẽ lưu MaNhanVien và NoiDung.
     * * @param maVeBan Chỉ được dùng để đưa vào NoiDung (vì bảng thiếu cột MaVeBan)
     * @param noiDung Nội dung thông báo
     * @param maNhanVien Mã nhân viên thực hiện giao dịch
     * @param conn Connection đang chạy trong transaction
     */
    // Sửa chữ ký phương thức để loại bỏ maVeBan khỏi SQL INSERT
    public void logConfirmation(int maVeBan, String noiDung, int maNhanVien, Connection conn) throws SQLException {
        
        // Cập nhật câu lệnh SQL: Loại bỏ MaVeBan khỏi danh sách cột INSERT
        // Giả sử MaThongBao là ID tự tăng.
        // Cột MaVeBan KHÔNG CÓ trong bảng THONGBAO của bạn.
        String sql = "INSERT INTO THONGBAO (MaNhanVien, NoiDung, ThoiGian) VALUES (?, ?, GETDATE())";
        
        // Thêm MaVeBan vào đầu nội dung để dễ dàng tìm kiếm sau này
        String finalNoiDung = String.format("[VÉ #%d] %s", maVeBan, noiDung);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Tham số 1: MaNhanVien
            ps.setInt(1, maNhanVien); 
            
            // Tham số 2: NoiDung (đã bao gồm MaVeBan)
            ps.setString(2, finalNoiDung); 
            
            ps.executeUpdate();
        }
    }
    
    // Phương thức quá tải tự mở/đóng Connection (Không dùng trong giao dịch đặt vé)
    public void logConfirmation(int maVeBan, String noiDung, int maNhanVien) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            logConfirmation(maVeBan, noiDung, maNhanVien, conn);
        }
    }
}