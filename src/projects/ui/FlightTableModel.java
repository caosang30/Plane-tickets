package projects.ui;

import projects.models.Flight;
import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.text.SimpleDateFormat;

public class FlightTableModel extends AbstractTableModel {
    private List<Flight> flights;
    private final String[] columnNames = new String[]{
        "Mã Chuyến", "Điểm Đi", "Điểm Đến", "Ngày Khởi Hành", 
        "Giờ Khởi Hành", "Tổng Ghế", "Đã Bán", "Còn Trống", "Tình Trạng"
    };
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public FlightTableModel(List<Flight> flights) {
        this.flights = flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
        fireTableDataChanged();
    }

    public Flight getFlightAt(int rowIndex) {
        return flights.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return flights.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Flight flight = flights.get(rowIndex);
        switch (columnIndex) {
            case 0: return flight.getMaChuyenBay();
            case 1: return flight.getDiemDi();
            case 2: return flight.getDiemDen();
            case 3: return dateFormat.format(flight.getNgayKhoiHanh()); // Sử dụng getNgayKhoiHanh()
            case 4: return timeFormat.format(flight.getNgayKhoiHanh()); // Sử dụng getNgayKhoiHanh()
            case 5: return flight.getTongSoGhe();
            case 6: return flight.getGheDaBan();
            case 7: return flight.getGheTrong();
            case 8: return flight.getTrangThai();
            default: return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0 || columnIndex == 5 || columnIndex == 6 || columnIndex == 7) {
            return Integer.class;
        }
        return String.class;
    }
}