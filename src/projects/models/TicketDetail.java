package projects.models;

// Ánh xạ bảng LOAIVE và tính toán số lượng còn lại
public class TicketDetail {
    private final int maVe; // MaVe (PK)
    private final String loaiVe; // LoaiVe
    private final double gia; // Gia (money)
    
    // --- BỔ SUNG TRƯỜNG THIẾU TỪ TRUY VẤN SQL ---
    private final int tongSoLuong; // Tổng số lượng vé loại này của chuyến bay
    // -------------------------------------------
    
    private final int soLuongCon; // SoLuongCon (Ghế trống của loại này)

    /**
     * CONSTRUCTOR ĐÃ CẬP NHẬT (5 tham số) ĐỂ KHẮC PHỤC LỖI TRONG FLIGHTDAO
     */
    public TicketDetail(int maVe, String loaiVe, double gia, int tongSoLuong, int soLuongCon) {
        this.maVe = maVe;
        this.loaiVe = loaiVe;
        this.gia = gia;
        this.tongSoLuong = tongSoLuong; // Gán giá trị cho trường mới
        this.soLuongCon = soLuongCon;
    }
    
    // Getters
    public int getMaVe() {
        return maVe;
    }

    public String getLoaiVe() {
        return loaiVe;
    }

    public double getGia() {
        return gia;
    }

    // --- GETTER MỚI ---
    public int getTongSoLuong() {
        return tongSoLuong;
    }
    // ------------------

    public int getSoLuongCon() {
        return soLuongCon;
    }

    /**
     * Quan trọng: Phương thức này hiển thị thông tin trong JComboBox 
     * và được sử dụng để hiển thị Loại vé trong BookingPanel.
     */
    @Override
    public String toString() {
        // Định dạng giá có dấu phân cách hàng nghìn
        return String.format("%s (Giá: %,.0f VNĐ | Còn: %d/%d)", loaiVe, gia, soLuongCon, tongSoLuong);
    }
}