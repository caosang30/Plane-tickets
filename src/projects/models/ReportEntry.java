package projects.models;

public class ReportEntry { // là gì?
    private String ngayGiaoDich;
    private int maNhanVien; // Mặc định là 0 nếu chỉ báo cáo theo ngày
    private double tongThu;

    // Constructors (nếu cần)

    // Getters and Setters
    public String getNgayGiaoDich() {
        return ngayGiaoDich;
    }

    public void setNgayGiaoDich(String ngayGiaoDich) {
        this.ngayGiaoDich = ngayGiaoDich;
    }

    public int getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(int maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public double getTongThu() {
        return tongThu;
    }

    public void setTongThu(double tongThu) {
        this.tongThu = tongThu;
    }
}