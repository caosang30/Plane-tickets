package projects.dao;

import projects.DBConnection;
import projects.models.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    /**
     * Thêm khách hàng mới.
     * Phương thức này được quá tải: tự mở/đóng Connection khi gọi độc lập (từ CustomerPanel).
     * @return MaKhachHang (ID tự tăng)
     */
    public int addCustomer(Customer customer) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            // Gọi phương thức Transactional
            return addCustomer(customer, conn);
        }
    }

    /**
     * Thêm khách hàng mới (Sử dụng Connection đã có - Dùng trong BookingService Transaction).
     * @return MaKhachHang (ID tự tăng)
     */
    public int addCustomer(Customer customer, Connection conn) throws SQLException {
        // Sử dụng Connection truyền vào (từ BookingService)
        String sql = "INSERT INTO KHACHHANG (TenKhachHang, DiaChi, SoDienThoai, CCCD) VALUES (?, ?, ?, ?)";
        int maKhachHang = -1;
        
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, customer.getTenKhachHang());
            ps.setString(2, customer.getDiaChi());
            ps.setString(3, customer.getSoDienThoai());
            ps.setString(4, customer.getCccd());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Thêm khách hàng thất bại, không hàng nào bị ảnh hưởng.");
            }
            
            // Lấy ID tự tăng
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    maKhachHang = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Thêm khách hàng thất bại, không lấy được ID.");
                }
            }
        }
        return maKhachHang;
    }

    /**
     * Tìm kiếm khách hàng theo CCCD hoặc SĐT (Sử dụng trong BookingService Transaction).
     * Phương thức này không tự đóng Connection.
     */
    public Customer findCustomerByIdentity(String cccd, String sdt) throws SQLException {
        String sql = "SELECT MaKhachHang, TenKhachHang, DiaChi, SoDienThoai, CCCD FROM KHACHHANG WHERE CCCD = ? OR SoDienThoai = ?";
        
        // Sử dụng try-with-resources để tự động đóng Connection
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cccd);
            ps.setString(2, sdt);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                        rs.getInt("MaKhachHang"),
                        rs.getString("TenKhachHang"),
                        rs.getString("DiaChi"),
                        rs.getString("SoDienThoai"),
                        rs.getString("CCCD")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Lấy tất cả khách hàng (Dùng cho CustomerPanel).
     */
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT MaKhachHang, TenKhachHang, DiaChi, SoDienThoai, CCCD FROM KHACHHANG";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                customers.add(new Customer(
                    rs.getInt("MaKhachHang"),
                    rs.getString("TenKhachHang"),
                    rs.getString("DiaChi"),
                    rs.getString("SoDienThoai"),
                    rs.getString("CCCD")
                ));
            }
        }
        return customers;
    }

    /**
     * Cập nhật thông tin khách hàng.
     */
    public void updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE KHACHHANG SET TenKhachHang = ?, DiaChi = ?, SoDienThoai = ?, CCCD = ? WHERE MaKhachHang = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, customer.getTenKhachHang());
            ps.setString(2, customer.getDiaChi());
            ps.setString(3, customer.getSoDienThoai());
            ps.setString(4, customer.getCccd());
            ps.setInt(5, customer.getMaKhachHang());
            
            ps.executeUpdate();
        }
    }

    /**
     * Xóa khách hàng (Lưu ý: có thể bị lỗi khóa ngoại nếu khách hàng đã đặt vé).
     */
    public void deleteCustomer(int maKhachHang) throws SQLException {
        String sql = "DELETE FROM KHACHHANG WHERE MaKhachHang = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maKhachHang);
            
            ps.executeUpdate();
        }
    }

    /**
     * Tìm kiếm khách hàng theo tên, CCCD hoặc SĐT (Dùng cho CustomerPanel).
     */
    public List<Customer> searchCustomers(String keyword) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT MaKhachHang, TenKhachHang, DiaChi, SoDienThoai, CCCD FROM KHACHHANG " +
                     "WHERE TenKhachHang LIKE ? OR CCCD LIKE ? OR SoDienThoai LIKE ?";
        
        String pattern = "%" + keyword + "%";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    customers.add(new Customer(
                        rs.getInt("MaKhachHang"),
                        rs.getString("TenKhachHang"),
                        rs.getString("DiaChi"),
                        rs.getString("SoDienThoai"),
                        rs.getString("CCCD")
                    ));
                }
            }
        }
        return customers;
    }
    public Customer getCustomerById(int maKhachHang) throws SQLException {
    String sql = "SELECT MaKhachHang, TenKhachHang, DiaChi, SoDienThoai, CCCD FROM KHACHHANG WHERE MaKhachHang = ?";
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, maKhachHang);
        
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new Customer(
                    rs.getInt("MaKhachHang"),
                    rs.getString("TenKhachHang"),
                    rs.getString("DiaChi"),
                    rs.getString("SoDienThoai"),
                    rs.getString("CCCD")
                );
            }
        }
    }
    return null;
}
    private final TicketDAO ticketDAO = new TicketDAO(); // Khởi tạo tại đây

public TicketDAO getTicketDAO() { 
    return this.ticketDAO; 
}
}