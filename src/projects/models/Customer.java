package projects.models;

// Ánh xạ bảng KHACHHANG
public class Customer {
    private int maKhachHang; // MaKhachHang (PK)
    private String tenKhachHang; // TenKhachHang
    private String diaChi; // DiaChi
    private String soDienThoai; // SoDienThoai
    private String cccd; // CCCD

    // Constructor cho việc lấy dữ liệu từ CSDL
    public Customer(int maKhachHang, String tenKhachHang, String diaChi, String soDienThoai, String cccd) {
        this.maKhachHang = maKhachHang;
        this.tenKhachHang = tenKhachHang;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
        this.cccd = cccd;
    }

    // Constructor cho việc thêm mới (chưa có ID)
    public Customer(String tenKhachHang, String diaChi, String soDienThoai, String cccd) {
        this(0, tenKhachHang, diaChi, soDienThoai, cccd);
    }
    
    // Getters and Setters
    public int getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(int maKhachHang) { this.maKhachHang = maKhachHang; }
    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }
}