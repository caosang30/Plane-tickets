package projects.dao;

import projects.DBConnection; 
import projects.models.Flight;
import projects.models.TicketDetail;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class FlightDAO {

    public List<Flight> getAllFlightsWithCapacity() throws SQLException {
        List<Flight> flights = new ArrayList<>();
        
        String sql = "SELECT " +
                     "    CB.MaChuyenBay, " +
                     "    MB.SoCho AS TongSoGhe, " +
                     "    DB.ViTri AS ViTriDuongBay, " +
                     "    CB.GhiChu AS TrangThai, " +
                     "    CONVERT(DATETIME, CONVERT(VARCHAR, CB.NgayDi, 120) + ' ' + CONVERT(VARCHAR, CB.GioBay, 8)) AS NgayKhoiHanh, " +
                     "    ISNULL(SUM(CASE WHEN VB.MaChuyenBay IS NOT NULL THEN VB.SoLuongBan ELSE 0 END), 0) AS GheDaBan, " +
                     "    MB.TenMayBay " + 
                     "FROM " +
                     "    CHUYENBAY CB " +
                     "INNER JOIN MAYBAY MB ON CB.MaMayBay = MB.MaMayBay " +
                     "INNER JOIN DUONGBAY DB ON CB.MaDuongBay = DB.MaDuongBay " +
                     "LEFT JOIN VEBAN VB ON CB.MaChuyenBay = VB.MaChuyenBay " +
                     "GROUP BY " +
                     "    CB.MaChuyenBay, MB.SoCho, DB.ViTri, CB.GhiChu, CB.NgayDi, CB.GioBay, MB.TenMayBay " +
                     "ORDER BY " + 
                     "    NgayKhoiHanh DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                flights.add(createFlightFromResultSet(rs));
            }
        }
        return flights;
    }

    /**
     * PHƯƠNG THỨC TIỆN ÍCH ĐÃ ĐƯỢC SỬA ĐỂ KHỚP VỚI CONSTRUCTOR MỚI
     */
    private Flight createFlightFromResultSet(ResultSet rs) throws SQLException {
        
        String viTriDuongBay = rs.getString("ViTriDuongBay");
        String tenMayBay = rs.getString("TenMayBay"); 
        
        Flight flight = new Flight(
            rs.getInt("MaChuyenBay"),
            viTriDuongBay, 
            tenMayBay, 
            rs.getTimestamp("NgayKhoiHanh"), 
            rs.getInt("TongSoGhe"),
            rs.getString("TrangThai"), 
            rs.getInt("GheDaBan")
        );
        
        return flight;
    }

    public List<Flight> searchFlights(Date date) throws SQLException {
        List<Flight> flights = new ArrayList<>();
        
        String sql = "SELECT " +
                     "    CB.MaChuyenBay, " +
                     "    MB.SoCho AS TongSoGhe, " + 
                     "    DB.ViTri AS ViTriDuongBay, " + 
                     "    CB.GhiChu AS TrangThai, " + 
                     "    CONVERT(DATETIME, CONVERT(VARCHAR, CB.NgayDi, 120) + ' ' + CONVERT(VARCHAR, CB.GioBay, 8)) AS NgayKhoiHanh, " + 
                     "    ISNULL(SUM(CASE WHEN VB.MaChuyenBay IS NOT NULL THEN VB.SoLuongBan ELSE 0 END), 0) AS GheDaBan, " +
                     "    MB.TenMayBay " + 
                     "FROM " +
                     "    CHUYENBAY CB " +
                     "INNER JOIN MAYBAY MB ON CB.MaMayBay = MB.MaMayBay " + 
                     "INNER JOIN DUONGBAY DB ON CB.MaDuongBay = DB.MaDuongBay " + 
                     "LEFT JOIN VEBAN VB ON CB.MaChuyenBay = VB.MaChuyenBay " +
                     "WHERE " + 
                     "    CB.NgayDi = ? " +
                     "GROUP BY " +
                     "    CB.MaChuyenBay, MB.SoCho, DB.ViTri, CB.GhiChu, CB.NgayDi, CB.GioBay, MB.TenMayBay " +
                     "ORDER BY " + 
                     "    NgayKhoiHanh ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, new java.sql.Date(date.getTime()));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    flights.add(createFlightFromResultSet(rs)); 
                }
            }
        }
        return flights;
    }

    /**
     * ĐÃ SỬA: Lấy chi tiết vé (MaVe, DonGia, TenLoaiVe, TongSoLuong, SoLuongCon)
     */
    public List<TicketDetail> loadTicketDetails(int maChuyenBay) throws SQLException {
        List<TicketDetail> details = new ArrayList<>();
        
        // TTCV: THONGTINCHITIETVE, LV: LOAIVE (Giả định vẫn cần JOIN)
        // VB: VEBAN
        String sql = "SELECT " +
                     "   TTCV.MaVe, " +
                     "   TTCV.Gia, " +
                     "   TTCV.LoaiVe AS TenLoaiVe, " + // Lấy LoaiVe trực tiếp từ TTCV
                     "   TTCV.SoLuong AS TongSoLuong, " + // Sử dụng TTCV.SoLuong làm tổng
                     "   TTCV.SoLuong - ISNULL(SUM(VB.SoLuongBan), 0) AS SoLuongCon " + // Tính toán số lượng còn lại
                     "FROM THONGTINCHITIETVE TTCV " +
                     "LEFT JOIN VEBAN VB ON TTCV.MaChuyenBay = VB.MaChuyenBay AND TTCV.MaVe = VB.MaVe " +
                     "WHERE TTCV.MaChuyenBay = ? " +
                     "GROUP BY TTCV.MaVe, TTCV.Gia, TTCV.LoaiVe, TTCV.SoLuong";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maChuyenBay);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TicketDetail detail = new TicketDetail(
                        rs.getInt("MaVe"),
                        rs.getString("TenLoaiVe"), // TTCV.LoaiVe (đã alias thành TenLoaiVe)
                        rs.getDouble("Gia"), 
                        rs.getInt("TongSoLuong"), 
                        rs.getInt("SoLuongCon") 
                    );
                    details.add(detail);
                }
            }
        }
        return details;
    }
    
    public Flight getFlightById(int maChuyenBay) throws SQLException {
        Flight flight = null;
        
        String sql = "SELECT " +
                     "    CB.MaChuyenBay, " +
                     "    MB.SoCho AS TongSoGhe, " + 
                     "    DB.ViTri AS ViTriDuongBay, " + 
                     "    CB.GhiChu AS TrangThai, " + 
                     "    CONVERT(DATETIME, CONVERT(VARCHAR, CB.NgayDi, 120) + ' ' + CONVERT(VARCHAR, CB.GioBay, 8)) AS NgayKhoiHanh, " + 
                     "    ISNULL(SUM(CASE WHEN VB.MaChuyenBay IS NOT NULL THEN VB.SoLuongBan ELSE 0 END), 0) AS GheDaBan, " +
                     "    MB.TenMayBay " + 
                     "FROM " +
                     "    CHUYENBAY CB " +
                     "INNER JOIN MAYBAY MB ON CB.MaMayBay = MB.MaMayBay " + 
                     "INNER JOIN DUONGBAY DB ON CB.MaDuongBay = DB.MaDuongBay " + 
                     "LEFT JOIN VEBAN VB ON CB.MaChuyenBay = VB.MaChuyenBay " +
                     "WHERE " + 
                     "    CB.MaChuyenBay = ? " +
                     "GROUP BY " +
                     "    CB.MaChuyenBay, MB.SoCho, DB.ViTri, CB.GhiChu, CB.NgayDi, CB.GioBay, MB.TenMayBay";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maChuyenBay);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    flight = createFlightFromResultSet(rs); 
                }
            }
        }
        return flight;
    }

    public void updateFlightStatus(int maChuyenBay, String newStatus) throws SQLException {
        String sql = "UPDATE CHUYENBAY SET GhiChu = ? WHERE MaChuyenBay = ?"; 
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, maChuyenBay);
            ps.executeUpdate();
        }
    }
}