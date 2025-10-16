package projects.ui;

import projects.models.RevenueReport;
import projects.models.TransactionDetail; 
import projects.services.ReportingService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale; 

public class PaymentPanel extends JPanel {

    private final ReportingService reportingService;
    private JTable paymentTable;

    // Biến instance để lưu thông tin giao dịch đang được chọn
    private TransactionDetail selectedTransaction = null;
    // Biến instance để lưu thông tin nhân viên đang đăng nhập (MaNV)
    private final int currentMaNV = 1; 


    public PaymentPanel() {
        this.reportingService = new ReportingService();
        setLayout(new BorderLayout(15, 15));
        
        JLabel titleLabel = new JLabel("QUẢN LÝ THANH TOÁN VÀ PHÂN PHỐI DOANH THU", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(255, 69, 0)); 
        
        JPanel reportSection = createPaymentDistributionSection();
        JPanel detailSection = createDetailManagementSection();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, reportSection, detailSection); 
        splitPane.setResizeWeight(0.5);

        add(titleLabel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        
        loadPaymentDistributionData();
    }
    
    private JPanel createPaymentDistributionSection() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel header = new JLabel("PHÂN LOẠI THANH TOÁN THEO HÌNH THỨC", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(header, BorderLayout.NORTH);
        
        String[] columnNames = {"Ngày", "Hình Thức", "Tổng Tiền Thu (VNĐ)", "Số Lượng G.D"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        paymentTable = new JTable(model);
        paymentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        paymentTable.setRowHeight(25);
        
        panel.add(new JScrollPane(paymentTable), BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createDetailManagementSection() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // 1. Panel Tìm kiếm Giao dịch
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder("TRA CỨU VÀ XỬ LÝ GIAO DỊCH ĐIỀU CHỈNH"));

        // >>> SỬA: CHỈ CẦN MÃ GIAO DỊCH <<<
        JLabel searchLabel = new JLabel("Mã Giao Dịch:");
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("Tìm Kiếm");
        searchButton.setBackground(new Color(30, 144, 255)); 
        searchButton.setForeground(Color.WHITE);

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        panel.add(searchPanel, BorderLayout.NORTH);

        // 2. Khu vực Hiển thị Chi tiết Giao dịch
        JTextArea detailArea = new JTextArea("Chi tiết giao dịch sẽ hiển thị tại đây sau khi tìm kiếm...");
        detailArea.setEditable(false);
        detailArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane detailScrollPane = new JScrollPane(detailArea);
        detailScrollPane.setBorder(BorderFactory.createTitledBorder("THÔNG TIN GIAO DỊCH LIÊN QUAN"));

        panel.add(detailScrollPane, BorderLayout.CENTER);

        // 3. Panel Xử lý Hoàn tiền
        JPanel refundPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        refundPanel.setBorder(BorderFactory.createTitledBorder("GHI NHẬN HOÀN TIỀN / THU PHÍ"));

        JLabel refundAmountLabel = new JLabel("Số Tiền Hoàn/Thu (Âm nếu Hoàn):");
        JTextField refundAmountField = new JTextField("0", 10);

        JLabel typeLabel = new JLabel("Loại Giao Dịch:");
        
        String[] displayTypes = {"Hủy Vé", "Hoàn Đổi Vé", "Thu Phí Khác", "Điều Chỉnh"};
        JComboBox<String> typeComboBox = new JComboBox<>(displayTypes);
        
        // MÃ CSDL RÚT GỌN (Giúp tránh lỗi Truncated Value)
        final String[] dbTypes = {"HUY_VE", "HOAN_DOI", "THU_PHI", "DIEU_CHINH"}; 

        JButton refundButton = new JButton("Ghi Nhận Giao Dịch");
        refundButton.setBackground(new Color(220, 20, 60));
        refundButton.setForeground(Color.WHITE);
        refundButton.setEnabled(false);

        refundPanel.add(typeLabel);
        refundPanel.add(typeComboBox);
        refundPanel.add(refundAmountLabel);
        refundPanel.add(refundAmountField);
        refundPanel.add(refundButton);

        panel.add(refundPanel, BorderLayout.SOUTH);

        // --- LOGIC XỬ LÝ NÚT BẤM (CHỈ DÙNG MÃ GIAO DỊCH) ---
        
        searchButton.addActionListener(e -> {
            selectedTransaction = null; 
            refundButton.setEnabled(false);
            String query = searchField.getText().trim();
            if (query.isEmpty()) {
                detailArea.setText("Vui lòng nhập Mã Giao Dịch.");
                return;
            }

            try {
                int id = Integer.parseInt(query);
                // GỌI SERVICE: CHỈ TRUYỀN MỘT ID (Mã Giao Dịch)
                TransactionDetail detail = reportingService.getTransactionDetails(id);
                
                if (detail != null) {
                    // Chỉ cho phép điều chỉnh (hoàn tiền/thu phí) trên giao dịch BOOK
                    if (detail.getLoaiGiaoDich().equals("BOOK") || detail.getLoaiGiaoDich().equals("THU_PHI") || detail.getLoaiGiaoDich().equals("HUY_VE")) {
                        selectedTransaction = detail;
                        detailArea.setText(formatTransactionDetail(detail));
                        refundButton.setEnabled(true);
                        // Thiết lập số tiền hoàn tiền mặc định (âm số tiền gốc)
                        refundAmountField.setText(String.valueOf(-Math.round(detail.getSoTienLienQuan()))); 
                    } else {
                        detailArea.setText("Chỉ có thể điều chỉnh giao dịch gốc (BOOK) hoặc giao dịch điều chỉnh trước đó.");
                        refundAmountField.setText("0");
                    }
                } else {
                    detailArea.setText("Không tìm thấy giao dịch với mã: " + query);
                    refundAmountField.setText("0");
                }
            } catch (NumberFormatException ex) {
                detailArea.setText("Mã nhập vào không hợp lệ (phải là số).");
                refundAmountField.setText("0");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi CSDL khi tra cứu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                detailArea.setText("Lỗi CSDL.");
                refundAmountField.setText("0");
            }
        });
        
        // Action Xử lý Giao dịch (Hoàn tiền / Thu phí)
        refundButton.addActionListener(e -> {
            if (selectedTransaction == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng tìm kiếm giao dịch trước khi xử lý.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double amount = Double.parseDouble(refundAmountField.getText());
                
                int selectedIndex = typeComboBox.getSelectedIndex();
                String dbType = dbTypes[selectedIndex]; 

                int confirm = JOptionPane.showConfirmDialog(this, 
                    String.format("Xác nhận ghi nhận giao dịch '%s' cho vé %d với số tiền %,.0f VNĐ?", typeComboBox.getSelectedItem(), selectedTransaction.getMaVeBan(), amount), 
                    "Xác Nhận Nghiệp Vụ", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = reportingService.processRefundOrFee(
                        selectedTransaction.getMaVeBan(), 
                        currentMaNV, 
                        dbType, 
                        amount
                    );
                    
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Ghi nhận giao dịch điều chỉnh thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        loadPaymentDistributionData(); 
                        selectedTransaction = null;
                        searchField.setText("");
                        detailArea.setText("Chi tiết giao dịch sẽ hiển thị tại đây sau khi tìm kiếm...");
                        refundAmountField.setText("0");
                        refundButton.setEnabled(false);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ. Vui lòng nhập số.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi CSDL khi ghi nhận giao dịch: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private String formatTransactionDetail(TransactionDetail detail) {
        Locale vn = Locale.of("vi", "VN"); 
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(vn);

        return String.format("Mã GD: %d\n", detail.getMaGiaoDich()) +
               String.format("Mã Vé Bán: %d\n", detail.getMaVeBan()) +
               String.format("Loại GD Gốc: %s\n", detail.getLoaiGiaoDich()) +
               String.format("Hình Thức Gốc: %s\n", detail.getHinhThucThanhToan()) +
               String.format("Thời Gian GD: %s\n", detail.getThoiGian().toString()) +
               String.format("Mã NV GD: %d\n", detail.getMaNhanVien()) +
               String.format("Số Tiền Gốc: %s\n", currencyFormatter.format(detail.getSoTienLienQuan()));
    }

    private void loadPaymentDistributionData() {
        DefaultTableModel model = (DefaultTableModel) paymentTable.getModel();
        model.setRowCount(0);

        Locale vn = Locale.of("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(vn);
        
        try {
            List<RevenueReport> reportList = reportingService.getPaymentDistributionReport();
            
            for (RevenueReport report : reportList) {
                String formattedAmount = currencyFormatter.format(report.getTongThu());
                
                model.addRow(new Object[]{
                    report.getNgayGiaoDich(),
                    report.getHinhThucThanhToan(),
                    formattedAmount,
                    report.getTongGiaoDich()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi tải báo cáo phân loại thanh toán: " + e.getMessage(), 
                "Lỗi CSDL", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
