package projects.services;

import projects.dao.FlightDAO;
import projects.models.Flight;

import java.sql.SQLException;
import java.util.List;

public class FlightService {
    
    private final FlightDAO flightDAO = new FlightDAO();
    
    // Đặt ngưỡng cảnh báo (0.90) nhất quán với FlightPanel
    // Cảnh báo khi số ghế đã bán đạt 90%
    private static final double ALERT_CAPACITY_THRESHOLD = 0.90;

    /**
     * Lấy danh sách chuyến bay và kiểm tra, cập nhật cảnh báo (nếu cần).
     * @return Danh sách chuyến bay đã qua xử lý cảnh báo.
     */
    public List<Flight> getFlightListAndCheckAlerts() throws SQLException {
        // Bước 1: Lấy dữ liệu thô từ Database (sử dụng GhiChu làm TrangThai)
        List<Flight> flights = flightDAO.getAllFlightsWithCapacity();

        for (Flight flight : flights) {
            
            // Lấy trạng thái hiện tại (là GhiChu trong DB)
            String currentStatus = flight.getTrangThai() != null ? flight.getTrangThai().toUpperCase() : "";
            
            // Tính toán % ghế đã bán
            double capacity = (flight.getTongSoGhe() > 0) ? 
                                (double) flight.getGheDaBan() / flight.getTongSoGhe() : 0;
            
            // --- LOGIC CẢNH BÁO: KHI ĐẠT NGƯỠNG ĐẦY VÀ CHƯA BỊ HỦY/KHỞI HÀNH/ĐẦY ---
            
            // Kiểm tra: 1. Đã bán >= 90% AND 2. Trạng thái KHÔNG phải là FULL/CANCELED/DEPARTED
            if (capacity >= ALERT_CAPACITY_THRESHOLD && 
                !currentStatus.equals("FULL") && 
                !currentStatus.equals("CANCELED") &&
                !currentStatus.equals("DEPARTED")) {
                
                // Trường hợp 1: Chuyến bay đầy hoàn toàn (100%) -> Cập nhật sang "FULL"
                if (capacity >= 1.0) {
                    flightDAO.updateFlightStatus(flight.getMaChuyenBay(), "FULL");
                    flight.setTrangThai("FULL");
                    System.out.println("CẢNH BÁO: Chuyến bay #" + flight.getMaChuyenBay() + " đã ĐẦY (100%)!");
                } 
                // Trường hợp 2: Chuyến bay đạt ngưỡng cảnh báo (90% <= capacity < 100%) -> Cập nhật sang "ALERT"
                else if (!currentStatus.contains("ALERT")) {
                    flightDAO.updateFlightStatus(flight.getMaChuyenBay(), "ALERT");
                    flight.setTrangThai("ALERT"); // Cập nhật trong đối tượng
                    System.out.println("CẢNH BÁO: Chuyến bay #" + flight.getMaChuyenBay() + " đã đạt ngưỡng cảnh báo (>90%)!");
                }
            }
        }
        return flights;
    }
    
    // Thêm các phương thức service cơ bản khác (ví dụ: cho BookingPanel)
    
    /**
     * Lấy thông tin chi tiết một chuyến bay theo ID.
     */
    public Flight getFlightDetails(int maChuyenBay) throws SQLException {
        return flightDAO.getFlightById(maChuyenBay);
    }
    
    /**
     * Tìm kiếm chuyến bay theo ngày.
     */
    public List<Flight> searchFlightsByDate(java.util.Date date) throws SQLException {
        return flightDAO.searchFlights(date);
    }
}