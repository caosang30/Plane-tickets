package projects.models;

import java.sql.Timestamp;

// Sử dụng bảng THONGBAO để ghi lại Lịch sử Giao dịch
public class TransactionLog {
    private int maThongBao; // MaThongBao (PK)
    private int maNhanVien; // MaNhanVien (FK)
    private String noiDung; // NoiDung (Chi tiết giao dịch)
    private Timestamp thoiGian; // ThoiGian

    // Constructor
    public TransactionLog(int maThongBao, int maNhanVien, String noiDung, Timestamp thoiGian) {
        this.maThongBao = maThongBao;
        this.maNhanVien = maNhanVien;
        this.noiDung = noiDung;
        this.thoiGian = thoiGian;
    }

    // --- Getters BỔ SUNG để loại bỏ cảnh báo "value is not used" ---

    public int getMaThongBao() {
        return maThongBao;
    }

    public int getMaNhanVien() {
        return maNhanVien;
    }

    // --- Getters đã có ---
    public String getNoiDung() { 
        return noiDung; 
    }
    
    public Timestamp getThoiGian() { 
        return thoiGian; 
    }
}