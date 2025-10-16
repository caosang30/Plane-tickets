package projects.ui;

import projects.dao.FlightDAO;
import projects.models.Flight;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

public class FlightPanel extends JPanel {

    private JTable flightTable;
    private FlightTableModel tableModel; // Cần đảm bảo FlightTableModel có thể xử lý các cột mới
    private FlightDAO flightDAO;
    private JButton refreshButton;
    
    // Đặt ngưỡng cảnh báo tại đây
    private static final double ALERT_CAPACITY_THRESHOLD = 0.90; 

    public FlightPanel() {
        flightDAO = new FlightDAO();
        initializeUI();
        loadFlights();
        
        // --- ĐÃ SỬA: Thiết lập Timer để tự động refresh và kiểm tra cảnh báo sau mỗi 60 giây ---
        // Sử dụng lambda và truyền null để phân biệt với sự kiện nhấn nút
        Timer timer = new Timer(60000, e -> checkAndAlertFlights(null)); 
        timer.start();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // --- 1. Tiêu đề ---
        JLabel titleLabel = new JLabel("QUẢN LÝ VÀ THEO DÕI CHUYẾN BAY", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // --- 2. Bảng hiển thị dữ liệu ---
        tableModel = new FlightTableModel(List.of());
        flightTable = new JTable(tableModel);
        flightTable.setRowHeight(25);
        flightTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(flightTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. Panel cho các nút chức năng ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        refreshButton = new JButton("Làm Mới Dữ Liệu");
        refreshButton.addActionListener(e -> loadFlights());
        
        JButton alertButton = new JButton("Kiểm Tra Cảnh Báo Ngay");
        alertButton.addActionListener(this::checkAndAlertFlights); // Nút nhấn vẫn truyền ActionEvent e

        buttonPanel.add(refreshButton);
        buttonPanel.add(alertButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Tải danh sách chuyến bay và cập nhật JTable.
     */
    public void loadFlights() {
        try {
            List<Flight> flights = flightDAO.getAllFlightsWithCapacity();
            tableModel.setFlights(flights);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu chuyến bay: " + ex.getMessage(), 
                "Lỗi Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Kiểm tra trạng thái ghế và đưa ra cảnh báo nếu chuyến bay gần đầy.
     * Đồng thời, cập nhật trạng thái chuyến bay nếu cần.
     */
    private void checkAndAlertFlights(ActionEvent e) {
        try {
            List<Flight> flights = flightDAO.getAllFlightsWithCapacity();
            int alertCount = 0;
            StringBuilder alertMessage = new StringBuilder();
            alertMessage.append("CẢNH BÁO CHUYẾN BAY GẦN ĐẦY:\n");

            for (Flight flight : flights) {
                // Kiểm tra phân chia cho 0
                if (flight.getTongSoGhe() <= 0) continue; 
                
                double capacity = (double) flight.getGheDaBan() / flight.getTongSoGhe();
                String currentStatus = flight.getTrangThai() != null ? flight.getTrangThai().toUpperCase() : "";
                
                // Tiêu chí cảnh báo: Đã bán trên 90% TỔNG GHẾ VÀ trạng thái KHÔNG phải là CANCELED/DEPARTED
                if (capacity >= ALERT_CAPACITY_THRESHOLD && 
                    !currentStatus.equals("CANCELED") && 
                    !currentStatus.equals("DEPARTED")) {
                    
                    alertCount++;
                    
                    // Thử cập nhật trạng thái nếu cần 
                    // Chỉ cập nhật nếu trạng thái hiện tại không phải là "ALERT"
                    if (!currentStatus.contains("ALERT")) {
                        // Sửa lỗi: Cập nhật trạng thái mới (chuyển chữ hoa thành chữ thường)
                        String baseStatus = currentStatus.startsWith("ALERT: ") ? currentStatus.substring(7) : currentStatus;
                        flightDAO.updateFlightStatus(flight.getMaChuyenBay(), "ALERT: " + baseStatus); 
                    }

                    // Hiển thị ViTriDuongBay và TenMayBay
                    alertMessage.append(String.format(
                        " - Chuyến #%d (ĐB: %s | MB: %s): Đã bán %d/%d ghế (%.0f%%) - Trạng thái hiện tại: %s\n", 
                        flight.getMaChuyenBay(), flight.getViTriDuongBay(), flight.getTenMayBay(),
                        flight.getGheDaBan(), flight.getTongSoGhe(), capacity * 100, flight.getTrangThai()));
                }
            }

            // Luôn tải lại dữ liệu sau khi kiểm tra (để hiển thị trạng thái "ALERT" nếu có)
            loadFlights(); 
            
            if (alertCount > 0) {
                // Hiển thị cảnh báo cho nhân viên
                JOptionPane.showMessageDialog(this, alertMessage.toString(), 
                    "CẢNH BÁO ĐẦY CHỖ", JOptionPane.WARNING_MESSAGE);
            } else {
                // CHỈ HIỂN THỊ thông báo này khi nhấn nút (e != null)
                if (e != null) { 
                    JOptionPane.showMessageDialog(this, "Không có chuyến bay nào cần cảnh báo lúc này.", 
                        "Kiểm Tra Hoàn Tất", JOptionPane.INFORMATION_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi kiểm tra cảnh báo: " + ex.getMessage(), 
                "Lỗi Database", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}