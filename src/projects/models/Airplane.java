package projects.models;

public class Airplane {
    private int maMayBay; // MaMayBay (PK)
    private String tenMayBay; // TenMayBay
    private String hangSanXuat; // HangSanXuat
    private String kichThuoc; // KichThuoc
    private int soCho; // SoCho

    // Constructor full
    public Airplane(int maMayBay, String tenMayBay, String hangSanXuat, String kichThuoc, int soCho) {
        this.maMayBay = maMayBay;
        this.tenMayBay = tenMayBay;
        this.hangSanXuat = hangSanXuat;
        this.kichThuoc = kichThuoc;
        this.soCho = soCho;
    }

    // Getters
    public int getMaMayBay() { return maMayBay; }
    public String getTenMayBay() { return tenMayBay; }
    
    // --- BỔ SUNG GETTERS THIẾU ---
    public String getHangSanXuat() { return hangSanXuat; }
    public String getKichThuoc() { return kichThuoc; }
    // ----------------------------
    
    public int getSoCho() { return soCho; }
}