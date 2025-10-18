package projects.models;

import java.util.Date;


public class Flight {
    private int maChuyenBay;
    private String diemDi;
    private String diemDen;
    private Date ngayKhoiHanh;
    private int tongSoGhe;
    private String trangThai; // Sẽ được gán bằng GhiChu từ DB
    
    // Thuộc tính tính toán
    private int gheDaBan;
    private int gheTrong;

    // --- CÁC TRƯỜNG LẤY TỪ DB QUA JOIN ---
    private String viTriDuongBay; // Lấy từ DUONGBAY.ViTri
    private String tenMayBay;      // Lấy từ MAYBAY.TenMayBay (tại sao không là mã máy bay?)
    // ------------------------------------

    public Flight() {
    }

    /**
     * Constructor chính, nhận các giá trị từ DAO đã được xử lý JOIN.
     * @param viTriDuongBay Dùng để gán cho cả diemDi và diemDen (tạm thời)
     * @param trangThai Dùng để gán GhiChu từ DB
     */
    public Flight(int maChuyenBay, String viTriDuongBay, String tenMayBay, Date ngayKhoiHanh, 
                  int tongSoGhe, String trangThai, int gheDaBan) {
        this.maChuyenBay = maChuyenBay;
        // Gán DiemDi/DiemDen từ ViTriDuongBay (vì DB không có 2 cột này)
        this.diemDi = viTriDuongBay;
        this.diemDen = viTriDuongBay;
        
        this.ngayKhoiHanh = ngayKhoiHanh;
        this.tongSoGhe = tongSoGhe;
        this.trangThai = trangThai; // Là GhiChu từ DB
        this.gheDaBan = gheDaBan;
        this.gheTrong = tongSoGhe - gheDaBan; // Tự tính toán ghế trống
        
        // Gán các trường mới
        this.viTriDuongBay = viTriDuongBay;
        this.tenMayBay = tenMayBay;
    }
    
    // Constructor cũ không còn phù hợp, nên được loại bỏ hoặc thay thế.

    // Getters and Setters...

    public int getMaChuyenBay() { return maChuyenBay; }
    public void setMaChuyenBay(int maChuyenBay) { this.maChuyenBay = maChuyenBay; }
    
    // Giữ nguyên getters, dù DiemDi/DiemDen đang được gán bằng ViTriDuongBay
    public String getDiemDi() { return diemDi; }
    public void setDiemDi(String diemDi) { this.diemDi = diemDi; }

    public String getDiemDen() { return diemDen; }
    public void setDiemDen(String diemDen) { this.diemDen = diemDen; }

    public Date getNgayKhoiHanh() { return ngayKhoiHanh; }
    public void setNgayKhoiHanh(Date ngayKhoiHanh) { this.ngayKhoiHanh = ngayKhoiHanh; }

    public int getTongSoGhe() { return tongSoGhe; }
    public void setTongSoGhe(int tongSoGhe) { this.tongSoGhe = tongSoGhe; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public int getGheDaBan() { return gheDaBan; }
    public void setGheDaBan(int gheDaBan) { 
        this.gheDaBan = gheDaBan; 
        this.gheTrong = this.tongSoGhe - gheDaBan;
    }

    public int getGheTrong() { return gheTrong; }
    
    // --- BỔ SUNG HAI PHƯƠNG THỨC KHẮC PHỤC LỖI TRONG BookingPanel ---
    public Date getNgayDi() {
        return ngayKhoiHanh;
    }

    public Date getGioBay() {
        return ngayKhoiHanh;
    }
    // -----------------------------------------------------------------
    
    // --- GETTERS & SETTERS CHO CÁC TRƯỜNG MỚI ---
    public String getViTriDuongBay() {
        return viTriDuongBay != null ? viTriDuongBay : diemDi + " - " + diemDen;
    }

    public void setViTriDuongBay(String viTriDuongBay) {
        this.viTriDuongBay = viTriDuongBay;
        // Cập nhật luôn DiemDi/DiemDen nếu có thay đổi từ ViTriDuongBay
        this.diemDi = viTriDuongBay;
        this.diemDen = viTriDuongBay;
    }

    public String getTenMayBay() {
        return tenMayBay;
    }

    public void setTenMayBay(String tenMayBay) {
        this.tenMayBay = tenMayBay;
    }
    // ----------------------------------------------
    
    @Override
    public String toString() {
        return String.format("%s - %s", getDiemDi(), getDiemDen());
    }
}