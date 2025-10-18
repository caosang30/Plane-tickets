package projects.models;

public class Airplane { 
    private int maMayBay; // MaMayBay (PK) đổi về string
    private String tenMayBay; // TenMayBay
    private String hangSanXuat; // HangSanXuat
    private String kichThuoc; // KichThuoc chưa rõ
    private int soCho; // SoCho 

    // rong
    public Airplane(){}
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

// setters
    public void setmaMayBay(int m){
        maMayBay=m;
    }
    public void setTenMayBay(String s){
        tenMayBay=s;
    }
    public void setHangSanXuat(String s){
        hangSanXuat = s;
    }
    public void setKichThuoc(String k){
        kichThuoc=k;
    }
    public void setSoCho(int s){
        soCho=s;
    }


}
    