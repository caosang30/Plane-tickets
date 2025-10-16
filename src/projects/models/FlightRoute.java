package projects.models;

public class FlightRoute {
    private int maDuongBay; // MaDuongBay (PK)
    private String viTri; // ViTri (Ví dụ: Sân bay đi - Sân bay đến)
    private double chieuDai; // ChieuDai
    private double chieuRong; // ChieuRong
    private String tinhTrang; // TinhTrang

    // Constructor full
    public FlightRoute(int maDuongBay, String viTri, double chieuDai, double chieuRong, String tinhTrang) {
        this.maDuongBay = maDuongBay;
        this.viTri = viTri;
        this.chieuDai = chieuDai;
        this.chieuRong = chieuRong;
        this.tinhTrang = tinhTrang;
    }

    // Getters
    public int getMaDuongBay() { return maDuongBay; }
    public String getViTri() { return viTri; }
    public double getChieuDai() { return chieuDai; }
    
    // --- BỔ SUNG GETTERS THIẾU ---
    public double getChieuRong() { 
        return chieuRong; 
    }
    
    public String getTinhTrang() { 
        return tinhTrang; 
    }
}