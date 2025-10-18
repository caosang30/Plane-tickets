package projects.models;

// Ánh xạ bảng VEBAN
public class Ticket {
    private int maVeBan; 
    private final int maVe; 
    private final int maNhanVien; 
    private final int maKhachHang; // Trường này đã có, không cần thay đổi
    private final int maChuyenBay; 
    private final int soLuongBan; // nên để là số lượng ( == số chỗ) , phụ thuộc vào máy bay
    
    // Các trường phụ trợ (dùng cho logic tính toán và hiển thị)
    private final String loaiVe;// loại vé thương gia đồ đồ đúng không , nếu cho nhâp tay rồi lỡ như tui nhập giá vé thương gia thấp hơn giá vé bth đc k?
    private final double donGia; // Trường này đã có, không cần thay đổi

    // Constructor cho việc Đặt vé MỚI
    public Ticket(int maVe, int maNhanVien, int maKhachHang, int maChuyenBay, int soLuongBan, String loaiVe, double donGia) {
        this.maVeBan = 0; // Khi thêm mới thì ID là 0
        this.maVe = maVe;
        this.maNhanVien = maNhanVien;
        this.maKhachHang = maKhachHang;
        this.maChuyenBay = maChuyenBay;
        this.soLuongBan = soLuongBan;
        this.loaiVe = loaiVe;
        this.donGia = donGia;
    }

    // Constructor cho việc Lấy dữ liệu từ CSDL (đã có MaVeBan)
    public Ticket(int maVeBan, int maVe, int maNhanVien, int maKhachHang, int maChuyenBay, int soLuongBan, String loaiVe, double donGia) {
        this.maVeBan = maVeBan;
        this.maVe = maVe;
        this.maNhanVien = maNhanVien;
        this.maKhachHang = maKhachHang;
        this.maChuyenBay = maChuyenBay;
        this.soLuongBan = soLuongBan;
        this.loaiVe = loaiVe;
        this.donGia = donGia;
    }
    
    // Getters
    public int getMaVeBan() { return maVeBan; }
    public int getMaVe() { return maVe; }
    public int getMaNhanVien() { return maNhanVien; }
    public int getMaKhachHang() { return maKhachHang; } // <-- OK
    public int getMaChuyenBay() { return maChuyenBay; }
    public int getSoLuongBan() { return soLuongBan; }
    public String getLoaiVe() { return loaiVe; }
    public double getDonGia() { return donGia; } // <-- OK

    // Phương thức tính toán: Tổng tiền
    public double getThanhTien() {
        return this.donGia * this.soLuongBan; 
    }
    
    // Setter (chỉ cho MaVeBan vì nó không final)
    public void setMaVeBan(int maVeBan) { this.maVeBan = maVeBan; }
}