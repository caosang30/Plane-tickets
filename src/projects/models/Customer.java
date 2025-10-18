package projects.models;

// Ánh xạ bảng KHACHHANG

public class Customer {
    private String maKhachHang; // MaKhachHang (PK)không có nhập tay
    private String tenKhachHang; // TenKhachHang
    private String diaChi; // DiaChi có thể có nhiều
    private String soDienThoai; // SoDienThoai có thể có nhiều
    private String cccd; // CCCD

    public Customer() {
    }
    // Constructor cho việc lấy dữ liệu từ CSDL setters
    public Customer( String tenKhachHang, String diaChi, String soDienThoai, String cccd) {
        this.tenKhachHang = tenKhachHang;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
        this.cccd = cccd;
    }

    // Constructor cho việc thêm mới (chưa có mã) (không cần tại mã sẽ tự động tạo mà)
    
    
    // Getters and Setters
    public String getMaKhachHang() { return maKhachHang; }
    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }
}