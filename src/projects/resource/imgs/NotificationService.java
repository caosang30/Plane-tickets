package projects.resource.imgs;

import projects.dao.NotificationDAO;
import projects.models.Customer;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class NotificationService {
    
    private final NotificationDAO notificationDAO;

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
    }

    /**
     * Mô phỏng việc gửi email/SMS xác nhận và ghi log sự kiện.
     * * @param component Component cha để hiển thị hộp thoại (thường là BookingPanel)
     * @param maVeBan Mã Vé Bán mới được tạo
     * @param maNhanVien Mã nhân viên thực hiện giao dịch
     * @param customer Khách hàng đã đặt vé
     */
    public void sendConfirmation(Component component, int maVeBan, int maNhanVien, Customer customer) {
        
        // 1. Chuẩn bị nội dung thông báo
        String subject = String.format("[XÁC NHẬN ĐẶT VÉ] Mã vé: %d", maVeBan);
        
        String body = String.format(
            "Kính chào quý khách %s,\n\n" +
            "Chúc mừng! Quý khách đã đặt vé thành công.\n" +
            "Mã Vé Bán của quý khách là: %d.\n" +
            "Chúng tôi đã gửi chi tiết vé điện tử vào email và tin nhắn (nếu có).\n" +
            "\nXin cảm ơn quý khách!",
            customer.getTenKhachHang(), maVeBan
        );

        // 2. Hiển thị mô phỏng gửi thông báo
        JOptionPane.showMessageDialog(
            component,
            subject + "\n\n" + body + 
            "\n\n[Mô phỏng: Đã GỬI EMAIL/SMS đến SĐT: " + customer.getSoDienThoai() + "]",
            "Gửi Xác nhận Thành công",
            JOptionPane.INFORMATION_MESSAGE
        );

        // 3. Ghi log vào CSDL (Bảng THONGBAO)
        // Việc ghi log phải được thực hiện BÊN NGOÀI BookingService để tránh vòng lặp phụ thuộc (dependency loop)
        // và để log được lưu kể cả khi giao dịch chính đã commit.
        try {
            // Nội dung log (tóm tắt)
            String logNoiDung = "Đã gửi XN đặt vé #" + maVeBan + " cho KH " + customer.getTenKhachHang();
            
            // Gọi DAO để lưu log. (Sử dụng phương thức không có Connection)
            notificationDAO.logConfirmation(maVeBan, logNoiDung, maNhanVien); 
            
        } catch (SQLException e) {
            // Log lỗi ra console nếu không lưu được log thông báo
            System.err.println("Lỗi SQL khi lưu log thông báo: " + e.getMessage());
        }
    }
}