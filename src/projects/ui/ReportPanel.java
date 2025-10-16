package projects.ui;

import projects.models.RevenueReport;
import projects.services.ReportingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ReportPanel extends JPanel {

    private final ReportingService reportingService;
    private JTable reportTable;

    public ReportPanel() {
        this.reportingService = new ReportingService();
        setLayout(new BorderLayout(10, 10)); 

        // 1. Tạo tiêu đề
        JLabel titleLabel = new JLabel("BÁO CÁO TỔNG THU THEO NGÀY VÀ NHÂN VIÊN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(30, 144, 255));
        add(titleLabel, BorderLayout.NORTH);

        // 2. Tạo Bảng (JTable)
        initializeTable();
        add(new JScrollPane(reportTable), BorderLayout.CENTER);

        // 3. Tải dữ liệu khi Panel được khởi tạo
        loadReportData();
    }

    private void initializeTable() {
        // Định nghĩa cột cho JTable
        String[] columnNames = {"Ngày Giao Dịch", "Mã Nhân Viên", "Tổng Thu (VNĐ)"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép sửa cell
            }
        };
        reportTable = new JTable(model);
        reportTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reportTable.setRowHeight(25);
    }

    private void loadReportData() {
        DefaultTableModel model = (DefaultTableModel) reportTable.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        // ĐÃ SỬA: Dùng Locale.forLanguageTag để tránh cảnh báo Deprecated
        Locale vn = Locale.forLanguageTag("vi-VN"); 
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(vn);
        
        try {
            // Gọi Service để lấy dữ liệu
            List<RevenueReport> reportList = reportingService.getEmployeeDailyRevenueReport();
            
            for (RevenueReport report : reportList) {
                // Lấy giá trị tổng thu và định dạng nó
                String formattedRevenue = currencyFormatter.format(report.getTongThu());
                
                // Thêm hàng vào bảng
                model.addRow(new Object[]{
                    report.getNgayGiaoDich(),
                    report.getMaNhanVien(),
                    formattedRevenue
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi tải báo cáo: " + e.getMessage(), 
                "Lỗi CSDL", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}