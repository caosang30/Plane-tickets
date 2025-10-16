package projects.models;

public class RevenueReport {
    // Trường dữ liệu cho Báo cáo Tổng thu theo Ngày & Nhân viên
    private String ngayGiaoDich;
    private int maNhanVien;
    private double tongThu;

    // Trường dữ liệu cho Báo cáo Tổng hợp/Phân loại Thanh toán (nếu cần)
    private double tongHoanTien;
    private String hinhThucThanhToan;
    private int tongGiaoDich;
    
    // ==========================================================
    // CONSTRUCTOR CHÍNH: Tổng thu theo Ngày & Nhân viên
    // ==========================================================
    public RevenueReport(String ngayGiaoDich, int maNhanVien, double tongThu) {
        this.ngayGiaoDich = ngayGiaoDich;
        this.maNhanVien = maNhanVien;
        this.tongThu = tongThu;
    }

    // Constructor cho Báo cáo Tổng hợp theo Nhân viên (Total Sales & Refund)
    public RevenueReport(int maNhanVien, double tongThu, double tongHoanTien) {
        this.maNhanVien = maNhanVien;
        this.tongThu = tongThu;
        this.tongHoanTien = tongHoanTien;
    }

    // Constructor cho Báo cáo Phân loại Thanh toán
    public RevenueReport(String ngayGiaoDich, String hinhThucThanhToan, double tongThu, int tongGiaoDich) {
        this.ngayGiaoDich = ngayGiaoDich;
        this.hinhThucThanhToan = hinhThucThanhToan;
        this.tongThu = tongThu;
        this.tongGiaoDich = tongGiaoDich;
    }
    
    // Constructor mặc định (Nếu cần)
    public RevenueReport() {
    }

    // ==========================================================
    // GETTERS VÀ SETTERS
    // ==========================================================

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

    public double getTongHoanTien() {
        return tongHoanTien;
    }

    public void setTongHoanTien(double tongHoanTien) {
        this.tongHoanTien = tongHoanTien;
    }

    public String getHinhThucThanhToan() {
        return hinhThucThanhToan;
    }

    public void setHinhThucThanhToan(String hinhThucThanhToan) {
        this.hinhThucThanhToan = hinhThucThanhToan;
    }

    public int getTongGiaoDich() {
        return tongGiaoDich;
    }

    public void setTongGiaoDich(int tongGiaoDich) {
        this.tongGiaoDich = tongGiaoDich;
    }
}