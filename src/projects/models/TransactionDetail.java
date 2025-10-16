package projects.models;

import java.util.Date;

public class TransactionDetail {
    private int maGiaoDich;
    private int maVeBan; // Mã vé liên quan đến giao dịch
    private int maNhanVien;
    private String loaiGiaoDich; // BOOK, CANCEL, EXCHANGE_PAY, ...
    private double soTienLienQuan;
    private Date thoiGian;
    private String hinhThucThanhToan;
    
    // Thông tin bổ sung (nếu cần)
    private String tenKhachHang; 

    // Constructor (Bạn có thể thêm các trường khác nếu cần)
    public TransactionDetail(int maGiaoDich, int maVeBan, int maNhanVien, String loaiGiaoDich, double soTienLienQuan, Date thoiGian, String hinhThucThanhToan) {
        this.maGiaoDich = maGiaoDich;
        this.maVeBan = maVeBan;
        this.maNhanVien = maNhanVien;
        this.loaiGiaoDich = loaiGiaoDich;
        this.soTienLienQuan = soTienLienQuan;
        this.thoiGian = thoiGian;
        this.hinhThucThanhToan = hinhThucThanhToan;
    }

    // Constructor mặc định (nếu cần)
    public TransactionDetail() {}

    // Getters and Setters
    public int getMaGiaoDich() { return maGiaoDich; }
    public void setMaGiaoDich(int maGiaoDich) { this.maGiaoDich = maGiaoDich; }
    public int getMaVeBan() { return maVeBan; }
    public void setMaVeBan(int maVeBan) { this.maVeBan = maVeBan; }
    public int getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(int maNhanVien) { this.maNhanVien = maNhanVien; }
    public String getLoaiGiaoDich() { return loaiGiaoDich; }
    public void setLoaiGiaoDich(String loaiGiaoDich) { this.loaiGiaoDich = loaiGiaoDich; }
    public double getSoTienLienQuan() { return soTienLienQuan; }
    public void setSoTienLienQuan(double soTienLienQuan) { this.soTienLienQuan = soTienLienQuan; }
    public Date getThoiGian() { return thoiGian; }
    public void setThoiGian(Date thoiGian) { this.thoiGian = thoiGian; }
    public String getHinhThucThanhToan() { return hinhThucThanhToan; }
    public void setHinhThucThanhToan(String hinhThucThanhToan) { this.hinhThucThanhToan = hinhThucThanhToan; }
    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }
}