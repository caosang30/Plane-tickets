package projects.models;

import java.util.Date;

/**
 * Lớp model đại diện cho một bản ghi trong bảng LICHSUGIAODICH (Lịch Sử Giao Dịch).
 */
public class Transaction {

    private int maGiaoDich;
    private int maVeBan; 	     // MaThamChieu trong CSDL, dùng để tham chiếu MaVeBan
    private int maNhanVien;
    private String loaiGiaoDich; // Ví dụ: BAN_VE, HOAN_TIEN, THU_PHI
    private double soTienLienQuan;
    private Date thoiGian; 	     // Sẽ được set bằng GETDATE() trong CSDL
    private String hinhThucThanhToan; // Ví dụ: TienMat, TheTinDung, ChuyenKhoan

    // <<< ĐÃ THÊM CONSTRUCTOR MẶC ĐỊNH (KHÔNG THAM SỐ) ĐỂ KHẮC PHỤC LỖI ReportingService >>>
    public Transaction() {
        // Constructor mặc định cần thiết cho việc tạo đối tượng trước khi set giá trị bằng setters.
    }
    
    // Constructor cơ bản cho việc ghi log (Khi ID tự tăng chưa có)
    public Transaction(int maVeBan, int maNhanVien, String loaiGiaoDich, double soTienLienQuan, String hinhThucThanhToan) {
        this.maVeBan = maVeBan;
        this.maNhanVien = maNhanVien;
        this.loaiGiaoDich = loaiGiaoDich;
        this.soTienLienQuan = soTienLienQuan;
        this.hinhThucThanhToan = hinhThucThanhToan;
    }
    
    // Constructor đầy đủ (Cho việc đọc dữ liệu từ CSDL)
    public Transaction(int maGiaoDich, int maVeBan, int maNhanVien, String loaiGiaoDich, double soTienLienQuan, Date thoiGian, String hinhThucThanhToan) {
        this.maGiaoDich = maGiaoDich;
        this.maVeBan = maVeBan;
        this.maNhanVien = maNhanVien;
        this.loaiGiaoDich = loaiGiaoDich;
        this.soTienLienQuan = soTienLienQuan;
        this.thoiGian = thoiGian;
        this.hinhThucThanhToan = hinhThucThanhToan;
    }

    // --- Getters ---

    public int getMaGiaoDich() {
        return maGiaoDich;
    }

    public int getMaVeBan() {
        return maVeBan;
    }

    public int getMaNhanVien() {
        return maNhanVien;
    }

    public String getLoaiGiaoDich() {
        return loaiGiaoDich;
    }

    public double getSoTienLienQuan() {
        return soTienLienQuan;
    }

    public Date getThoiGian() {
        return thoiGian;
    }

    public String getHinhThucThanhToan() {
        return hinhThucThanhToan;
    }

    // --- Setters ---

    public void setMaGiaoDich(int maGiaoDich) {
        this.maGiaoDich = maGiaoDich;
    }
    
    public void setMaVeBan(int maVeBan) {
        this.maVeBan = maVeBan;
    }

    public void setMaNhanVien(int maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public void setLoaiGiaoDich(String loaiGiaoDich) {
        this.loaiGiaoDich = loaiGiaoDich;
    }

    public void setSoTienLienQuan(double soTienLienQuan) {
        this.soTienLienQuan = soTienLienQuan;
    }

    public void setThoiGian(Date thoiGian) {
        this.thoiGian = thoiGian;
    }

    public void setHinhThucThanhToan(String hinhThucThanhToan) {
        this.hinhThucThanhToan = hinhThucThanhToan;
    }
}