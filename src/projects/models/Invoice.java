package projects.models;

import java.util.Date;

public class Invoice {
    private int maHoaDon;
    private int maVeBan;
    private double tongTien;
    private Date ngayXuat;
    private String duongDanFile;
    private String trangThai; 

    // Constructor cho việc tạo mới hóa đơn (chưa có MaHD và NgayXuat)
    public Invoice(int maVeBan, double tongTien, String duongDanFile, String trangThai) {
        this.maVeBan = maVeBan;
        this.tongTien = tongTien;
        this.duongDanFile = duongDanFile;
        this.trangThai = trangThai;
    }
    
    // Constructor đầy đủ (dùng khi đọc từ CSDL)
    public Invoice(int maHoaDon, int maVeBan, double tongTien, Date ngayXuat, String duongDanFile, String trangThai) {
        this.maHoaDon = maHoaDon;
        this.maVeBan = maVeBan;
        this.tongTien = tongTien;
        this.ngayXuat = ngayXuat;
        this.duongDanFile = duongDanFile;
        this.trangThai = trangThai;
    }

    // --- Getters và Setters ---
    public int getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(int maHoaDon) { this.maHoaDon = maHoaDon; }
    
    public int getMaVeBan() { return maVeBan; }
    public void setMaVeBan(int maVeBan) { this.maVeBan = maVeBan; }

    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }

    public Date getNgayXuat() { return ngayXuat; }
    public void setNgayXuat(Date ngayXuat) { this.ngayXuat = ngayXuat; }
    
    public String getDuongDanFile() { return duongDanFile; }
    public void setDuongDanFile(String duongDanFile) { this.duongDanFile = duongDanFile; }
    
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}