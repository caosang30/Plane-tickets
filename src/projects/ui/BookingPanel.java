package projects.ui;

import projects.dao.FlightDAO;
import projects.models.Customer;
import projects.models.Flight;
import projects.models.Ticket;
import projects.models.TicketDetail;
import projects.resource.imgs.NotificationService;
import projects.services.BookingService;
import projects.utils.TicketPrinter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects; 
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookingPanel extends JPanel {
    
    private static final Logger LOGGER = Logger.getLogger(BookingPanel.class.getName());

    // Màu sắc theme
    private static final Color PRIMARY_COLOR = new Color(25, 118, 210);
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private static final Color WARNING_COLOR = new Color(255, 152, 0);
    private static final Color DANGER_COLOR = new Color(244, 67, 54);
    private static final Color LIGHT_BG = new Color(250, 250, 250);
    private static final Color BORDER_COLOR = new Color(220, 220, 220);

    private final int maNhanVien;
    private final FlightDAO flightDAO;
    private final BookingService bookingService;
    private final TicketPrinter ticketPrinter;     
    private final NotificationService notificationService; 
    
    private final DefaultTableModel flightsModel;
    
    // Components
    private JTextField txtCustomerName, txtCCCD, txtPhone;
    private JComboBox<TicketDetail> cbTicketType;
    private JSpinner spnQuantity;
    private JTable tblFlights;
    private JSpinner spnDate;
    
    // Components cho chức năng Đổi vé
    private JTextField txtExchangeTicketId; 
    
    // Biến lưu trữ chuyến bay hiện tại (đã chọn)
    private Flight selectedFlight = null;
    
    public BookingPanel(int maNhanVien) {
        this.maNhanVien = maNhanVien;
        this.flightDAO = new FlightDAO();
        this.bookingService = new BookingService();
        this.ticketPrinter = new TicketPrinter();     
        this.notificationService = new NotificationService();
        
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Cấu hình bảng chuyến bay
        flightsModel = new DefaultTableModel(
            new String[]{"Mã CB", "Ngày Đi", "Giờ Bay", "Tuyến Bay", "Máy Bay", "Ghế Trống"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblFlights = new JTable(flightsModel);
        styleTable(tblFlights);
        
        // Panel chính
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(Color.WHITE);
        
        mainPanel.add(createSearchPanel(), BorderLayout.NORTH);
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        
        // Chia panel dưới thành 2 phần: Đặt/Hủy và Đổi vé
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(createBookingFormPanel());
        bottomPanel.add(createExchangePanel());
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Listener: Khi chọn một dòng chuyến bay
        tblFlights.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedFlightDetails();
            }
        });
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblTitle = new JLabel("TÌM KIẾM CHUYẾN BAY");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(PRIMARY_COLOR);
        
        JLabel lblDate = new JLabel("Ngày đi:");
        lblDate.setFont(new Font("Arial", Font.PLAIN, 13));
        
        spnDate = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnDate, "dd/MM/yyyy");
        spnDate.setEditor(dateEditor);
        spnDate.setPreferredSize(new Dimension(150, 35));
        spnDate.setFont(new Font("Arial", Font.PLAIN, 13));
        
        JButton btnSearch = createStyledButton("Tìm Chuyến Bay", PRIMARY_COLOR);
        btnSearch.setPreferredSize(new Dimension(150, 35));
        btnSearch.addActionListener(this::searchFlightsAction);
        
        panel.add(lblTitle);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(lblDate);
        panel.add(spnDate);
        panel.add(btnSearch);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createCompoundBorder(
        new LineBorder(BORDER_COLOR, 1, true),
        new EmptyBorder(10, 10, 10, 10)
    ));
    
    JLabel lblTitle = new JLabel("DANH SÁCH CHUYẾN BAY");
    lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
    lblTitle.setForeground(PRIMARY_COLOR);
    lblTitle.setBorder(new EmptyBorder(5, 5, 10, 5));
    
    JScrollPane scrollPane = new JScrollPane(tblFlights);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    
    // THÊM CÁC DÒNG NÀY ĐỂ KHẮC PHỤC CUỘN CHUỘT
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
    scrollPane.setWheelScrollingEnabled(true);
    
    panel.add(lblTitle, BorderLayout.NORTH);
    panel.add(scrollPane, BorderLayout.CENTER);
    
    return panel;
}
    
    private JPanel createBookingFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(SUCCESS_COLOR, 2, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Tiêu đề
        JLabel lblTitle = new JLabel("ĐẶT VÉ & HỦY VÉ");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(SUCCESS_COLOR);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Thông tin khách hàng
        JPanel customerPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        customerPanel.setBackground(Color.WHITE);
        customerPanel.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(BORDER_COLOR, 1),
            "Thông tin Khách hàng",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            new Color(80, 80, 80)
        ));
        
        txtCustomerName = createStyledTextField();
        txtCCCD = createStyledTextField();
        txtPhone = createStyledTextField();
        
        customerPanel.add(createLabel("Họ và tên:"));
        customerPanel.add(txtCustomerName);
        customerPanel.add(createLabel("CCCD/CMND:"));
        customerPanel.add(txtCCCD);
        customerPanel.add(createLabel("Số điện thoại:"));
        customerPanel.add(txtPhone);
        
        // Chi tiết vé
        JPanel ticketPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        ticketPanel.setBackground(Color.WHITE);
        ticketPanel.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(BORDER_COLOR, 1),
            "Chi tiết Vé",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            new Color(80, 80, 80)
        ));

        cbTicketType = new JComboBox<>();
        cbTicketType.setFont(new Font("Arial", Font.PLAIN, 13));
        cbTicketType.setPreferredSize(new Dimension(0, 35));
        
        spnQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        spnQuantity.setFont(new Font("Arial", Font.PLAIN, 13));
        ((JSpinner.DefaultEditor) spnQuantity.getEditor()).getTextField().setHorizontalAlignment(JTextField.CENTER);
        
        ticketPanel.add(createLabel("Loại vé:"));
        ticketPanel.add(cbTicketType);
        ticketPanel.add(createLabel("Số lượng:"));
        ticketPanel.add(spnQuantity);
        
        // Nút hành động
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton btnBook = createStyledButton("ĐẶT VÉ", SUCCESS_COLOR);
        JButton btnCancel = createStyledButton("HỦY VÉ", DANGER_COLOR);
        
        btnBook.addActionListener(this::bookTicketAction);
        btnCancel.addActionListener(this::cancelTicketAction);
        
        buttonPanel.add(btnBook);
        buttonPanel.add(btnCancel);
        
        // Sắp xếp
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(customerPanel, BorderLayout.NORTH);
        contentPanel.add(ticketPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        formPanel.add(lblTitle, BorderLayout.NORTH);
        formPanel.add(contentPanel, BorderLayout.CENTER);
        
        return formPanel;
    }
    
    private JPanel createExchangePanel() {
        JPanel exchangePanel = new JPanel(new BorderLayout(10, 10));
        exchangePanel.setBackground(Color.WHITE);
        exchangePanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(WARNING_COLOR, 2, true),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // Tiêu đề
        JLabel lblTitle = new JLabel("ĐỔI VÉ");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(WARNING_COLOR);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        // Input Mã vé cũ
        JPanel findPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        findPanel.setBackground(Color.WHITE);
        
        JLabel lblOldTicket = new JLabel("Mã Vé Cũ:");
        lblOldTicket.setFont(new Font("Arial", Font.BOLD, 13));
        
        txtExchangeTicketId = createStyledTextField();
        txtExchangeTicketId.setPreferredSize(new Dimension(120, 35));
        
        JButton btnFindOldTicket = createStyledButton("Tìm Vé", PRIMARY_COLOR);
        btnFindOldTicket.setPreferredSize(new Dimension(100, 35));
        btnFindOldTicket.addActionListener(e -> findOldTicketAction());

        findPanel.add(lblOldTicket);
        findPanel.add(txtExchangeTicketId);
        findPanel.add(btnFindOldTicket);

        // Hướng dẫn
        JTextArea instruction = new JTextArea(
            "HƯỚNG DẪN ĐỔI VÉ:\n\n" +
            "1. Nhập Mã Vé Cũ và nhấn 'Tìm Vé'\n" +
            "2. Chọn chuyến bay MỚI trong bảng trên\n" +
            "3. Chọn loại vé MỚI và Số lượng\n" +
            "4. Cập nhật thông tin khách hàng (nếu cần)\n" +
            "5. Nhấn 'THỰC HIỆN ĐỔI VÉ' để hoàn tất\n\n" +
            "Lưu ý: Phí đổi vé 5% sẽ được áp dụng"
        );
        instruction.setEditable(false);
        instruction.setLineWrap(true);
        instruction.setWrapStyleWord(true);
        instruction.setFont(new Font("Arial", Font.PLAIN, 12));
        instruction.setBackground(new Color(255, 248, 225));
        instruction.setBorder(new EmptyBorder(10, 10, 10, 10));
        instruction.setForeground(new Color(80, 80, 80));

        // Nút thực hiện Đổi vé
        JButton btnExchange = createStyledButton("THỰC HIỆN ĐỔI VÉ", WARNING_COLOR);
        btnExchange.setPreferredSize(new Dimension(200, 40));
        btnExchange.setFont(new Font("Arial", Font.BOLD, 14));
        btnExchange.addActionListener(this::exchangeTicketAction);
        
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southPanel.setBackground(Color.WHITE);
        southPanel.add(btnExchange);
        
        exchangePanel.add(lblTitle, BorderLayout.NORTH);
        exchangePanel.add(findPanel, BorderLayout.NORTH);
        exchangePanel.add(instruction, BorderLayout.CENTER);
        exchangePanel.add(southPanel, BorderLayout.SOUTH);

        return exchangePanel;
    }

    // =======================================================
    // --- UTILITY METHODS ---
    // =======================================================
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(0, 40));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 13));
        textField.setPreferredSize(new Dimension(0, 35));
        textField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        return textField;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        label.setForeground(new Color(60, 60, 60));
        return label;
    }
    
    private void styleTable(JTable table) {
    table.setFont(new Font("Arial", Font.PLAIN, 13));
    table.setRowHeight(40);
    table.setSelectionBackground(new Color(179, 229, 252));
    table.setSelectionForeground(new Color(13, 71, 161));
    table.setShowGrid(true);
    table.setGridColor(new Color(189, 189, 189));
    table.setIntercellSpacing(new Dimension(1, 1));
    table.setBackground(Color.WHITE);
    
    // Header styling - màu xanh đậm nổi bật với chữ đen
    JTableHeader header = table.getTableHeader();
    header.setFont(new Font("Arial", Font.BOLD, 14));
    header.setBackground(new Color(21, 101, 192));
    header.setForeground(Color.BLACK);
    header.setPreferredSize(new Dimension(header.getPreferredSize().width, 45));
    header.setReorderingAllowed(false);
    header.setBorder(BorderFactory.createEmptyBorder());
    
    // Center align với màu chữ đen
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(JLabel.CENTER);
    centerRenderer.setBackground(Color.WHITE);
    centerRenderer.setForeground(Color.BLACK);
    
    for (int i = 0; i < table.getColumnCount(); i++) {
        table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
    }
    
    // Thiết lập độ rộng cột để hiển thị đầy đủ nội dung
    if (table.getColumnCount() >= 6) {
        table.getColumnModel().getColumn(0).setPreferredWidth(80);  // Mã CB
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // Ngày Đi
        table.getColumnModel().getColumn(2).setPreferredWidth(80);  // Giờ Bay
        table.getColumnModel().getColumn(3).setPreferredWidth(200); // Tuyến Bay
        table.getColumnModel().getColumn(4).setPreferredWidth(150); // Máy Bay
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Ghế Trống
    }
}

    private String getPaymentMethod() {
        String[] options = {"Tiền Mặt", "Chuyển Khoản", "Thẻ Tín Dụng"};
        JComboBox<String> cbPayment = new JComboBox<>(options);
        cbPayment.setFont(new Font("Arial", Font.PLAIN, 13));
        
        int result = JOptionPane.showConfirmDialog(this, cbPayment, 
                "Chọn Hình thức Thanh toán:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String selectedMethod = (String) cbPayment.getSelectedItem();
            if (selectedMethod != null && !selectedMethod.trim().isEmpty()) {
                return selectedMethod.trim();
            }
        }
        return null;
    }
    
    // =======================================================
    // --- LOGIC HÀNH ĐỘNG ---
    // =======================================================

    private void searchFlightsAction(ActionEvent e) {
        java.util.Date selectedUtilDate = (java.util.Date) spnDate.getValue();
        Date sqlDate = new Date(selectedUtilDate.getTime());

        flightsModel.setRowCount(0);
        selectedFlight = null;

        try {
            List<Flight> flights = flightDAO.searchFlights(sqlDate); 
            
            if (flights.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy chuyến bay nào vào ngày này.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            for (Flight f : flights) {
                flightsModel.addRow(new Object[]{
                    f.getMaChuyenBay(),
                    f.getNgayDi().toString(),
                    timeFormat.format(f.getGioBay()),
                    f.getViTriDuongBay(),
                    f.getTenMayBay(),
                    f.getGheTrong()
                });
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi truy vấn CSDL khi tìm chuyến bay", ex);
            JOptionPane.showMessageDialog(this, "Lỗi truy vấn CSDL: " + ex.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSelectedFlightDetails() {
        int selectedRow = tblFlights.getSelectedRow();
        cbTicketType.removeAllItems();
        selectedFlight = null;

        if (selectedRow != -1) {
            try {
                int maChuyenBay = (int) flightsModel.getValueAt(selectedRow, 0);
                
                selectedFlight = flightDAO.getFlightById(maChuyenBay);
                
                List<TicketDetail> details = flightDAO.loadTicketDetails(maChuyenBay);
                
                for (TicketDetail td : details) {
                    cbTicketType.addItem(td);
                }
                
                if (details.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Chuyến bay này chưa có chi tiết loại vé.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Lỗi tải chi tiết loại vé", ex);
                JOptionPane.showMessageDialog(this, "Lỗi tải chi tiết loại vé: " + ex.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void bookTicketAction(ActionEvent e) {
        if (selectedFlight == null || cbTicketType.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến bay và loại vé.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = txtCustomerName.getText().trim();
        String cccd = txtCCCD.getText().trim();
        String phone = txtPhone.getText().trim();
        int quantity = (int) spnQuantity.getValue();
        
        if (name.isEmpty() || cccd.isEmpty() || phone.isEmpty() || quantity <= 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin khách hàng và số lượng vé.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        TicketDetail selectedTicketDetail = (TicketDetail) cbTicketType.getSelectedItem();
        int maChuyenBay = selectedFlight.getMaChuyenBay();

        if (quantity > selectedTicketDetail.getSoLuongCon()) {
            JOptionPane.showMessageDialog(this, "Số lượng vé yêu cầu vượt quá số ghế còn trống (" + selectedTicketDetail.getSoLuongCon() + ").", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (selectedFlight.getGheTrong() < quantity) {
            JOptionPane.showMessageDialog(this, "Số lượng vé yêu cầu vượt quá tổng số ghế trống còn lại của chuyến bay (" + selectedFlight.getGheTrong() + ").", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String hinhThucThanhToan = getPaymentMethod();
        if (hinhThucThanhToan == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Hình thức Thanh toán.", "Hủy giao dịch", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Customer newCustomerInfo = new Customer(name, "", phone, cccd); 
        
        Ticket newTicket = new Ticket(
            selectedTicketDetail.getMaVe(),
            this.maNhanVien,
            0,
            maChuyenBay,
            quantity,
            selectedTicketDetail.getLoaiVe(),
            selectedTicketDetail.getGia()
        );

        try {
            int maVeBanMoi = bookingService.bookTicket(newTicket, newCustomerInfo, this.maNhanVien, hinhThucThanhToan);
            
            JOptionPane.showMessageDialog(this, 
                String.format("Đặt vé thành công!\nMã Vé Bán: %d\n(Đang tiến hành in vé điện tử và gửi xác nhận...)", maVeBanMoi), 
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
            Customer confirmedCustomer = ticketPrinter.printE_Ticket(this, maVeBanMoi); 
            
            if (confirmedCustomer != null) {
                notificationService.sendConfirmation(this, maVeBanMoi, this.maNhanVien, confirmedCustomer);
            }
            
            searchFlightsAction(null); 
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi SQL khi đặt vé", ex);
            JOptionPane.showMessageDialog(this, "Lỗi giao dịch đặt vé: " + ex.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Lỗi nghiệp vụ/không xác định khi đặt vé", ex);
            JOptionPane.showMessageDialog(this, "Lỗi nghiệp vụ/không xác định: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cancelTicketAction(ActionEvent e) {
        String input = JOptionPane.showInputDialog(this, "Nhập Mã Vé Bán cần HỦY:");
        if (input == null || input.trim().isEmpty()) return;

        try {
            int maVeBan = Integer.parseInt(input.trim());
            
            String lyDoHuy = JOptionPane.showInputDialog(this, "Lý do hủy vé (bắt buộc):");
            lyDoHuy = Objects.requireNonNullElseGet(lyDoHuy, () -> ""); 
            
            if (lyDoHuy.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lý do hủy không được để trống.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String hinhThucHoanTien = getPaymentMethod();
            if (hinhThucHoanTien == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn Hình thức Hoàn tiền.", "Hủy giao dịch", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn hủy vé #"+maVeBan+"? (Sẽ áp dụng phí hủy 10%)", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            double refundAmount = bookingService.cancelTicket(maVeBan, this.maNhanVien, lyDoHuy, hinhThucHoanTien);
            
            JOptionPane.showMessageDialog(this, 
                String.format("Hủy vé #%d thành công.\nSố tiền hoàn lại: %,.0f VNĐ.", maVeBan, refundAmount), 
                "Thành công", JOptionPane.INFORMATION_MESSAGE);

            searchFlightsAction(null); 
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mã Vé Bán không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi SQL khi hủy vé", ex);
            JOptionPane.showMessageDialog(this, "Lỗi giao dịch hủy vé: " + ex.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Lỗi nghiệp vụ/không xác định khi hủy vé", ex);
            JOptionPane.showMessageDialog(this, "Lỗi nghiệp vụ/không xác định: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void findOldTicketAction() {
        String input = txtExchangeTicketId.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã Vé Cũ.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int maVeBan = Integer.parseInt(input);
            Ticket oldTicket = bookingService.getTicketDAO().getTicketSaleDetails(maVeBan);
            
            if (oldTicket != null && oldTicket.getSoLuongBan() > 0) {
                JOptionPane.showMessageDialog(this, 
                    String.format("Tìm thấy Vé Cũ #%d:\n- Chuyến bay cũ: %d\n- Loại vé: %s\n- Số lượng: %d\n- Giá trị: %,.0f VNĐ", 
                        maVeBan, oldTicket.getMaChuyenBay(), oldTicket.getLoaiVe(), oldTicket.getSoLuongBan(), oldTicket.getThanhTien()), 
                    "Tìm Vé Cũ Thành Công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy vé bán hợp lệ có Mã: " + maVeBan, "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mã Vé Bán không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi CSDL khi tìm vé cũ", ex);
            JOptionPane.showMessageDialog(this, "Lỗi CSDL khi tìm vé cũ: " + ex.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exchangeTicketAction(ActionEvent e) {
        if (selectedFlight == null || cbTicketType.getSelectedItem() == null) { 
            JOptionPane.showMessageDialog(this, "Vui lòng CHỌN chuyến bay MỚI và loại vé MỚI trong bảng trên.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String maVeBanCuStr = txtExchangeTicketId.getText().trim();
        if (maVeBanCuStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã Vé Cũ cần đổi.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int maVeBanCu = Integer.parseInt(maVeBanCuStr);
            
            String name = txtCustomerName.getText().trim();
            String cccd = txtCCCD.getText().trim();
            String phone = txtPhone.getText().trim();
            int quantity = (int) spnQuantity.getValue();
            
            if (name.isEmpty() || cccd.isEmpty() || phone.isEmpty() || quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin khách hàng và số lượng vé mới.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            TicketDetail selectedTicketDetail = (TicketDetail) cbTicketType.getSelectedItem();
            int maChuyenBayMoi = selectedFlight.getMaChuyenBay();

            if (quantity > selectedTicketDetail.getSoLuongCon()) {
                JOptionPane.showMessageDialog(this, "Số lượng vé MỚI yêu cầu vượt quá số ghế còn trống.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String hinhThucThanhToan = getPaymentMethod();
            if (hinhThucThanhToan == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn Hình thức Thanh toán cho giao dịch chênh lệch.", "Hủy giao dịch", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Customer newCustomerInfo = new Customer(name, "", phone, cccd);
            Ticket newTicket = new Ticket(
                selectedTicketDetail.getMaVe(), 
                this.maNhanVien,
                0,
                maChuyenBayMoi,
                quantity,
                selectedTicketDetail.getLoaiVe(),
                selectedTicketDetail.getGia()
            );

            int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn đổi vé #"+maVeBanCu+" sang chuyến bay #"+maChuyenBayMoi+" không? (Phí đổi 5% áp dụng trên vé mới)", 
                "Xác nhận Đổi vé", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            double difference = bookingService.exchangeTicket(maVeBanCu, newTicket, newCustomerInfo, this.maNhanVien, hinhThucThanhToan);
            
            String message;
            if (difference > 0) {
                message = String.format("Đổi vé thành công! Khách hàng cần trả thêm: %,.0f VNĐ (Hình thức: %s)", difference, hinhThucThanhToan);
            } else if (difference < 0) {
                message = String.format("Đổi vé thành công! Khách hàng được hoàn lại: %,.0f VNĐ (Hình thức: %s)", Math.abs(difference), hinhThucThanhToan);
            } else {
                message = "Đổi vé thành công! Không có chênh lệch giá.";
            }

            JOptionPane.showMessageDialog(this, message, "Đổi vé Thành công", JOptionPane.INFORMATION_MESSAGE);
            
            searchFlightsAction(null); 
            txtExchangeTicketId.setText("");
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mã Vé Cũ không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi SQL khi đổi vé", ex);
            JOptionPane.showMessageDialog(this, "Lỗi giao dịch đổi vé: " + ex.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Lỗi nghiệp vụ/không xác định khi đổi vé", ex);
            JOptionPane.showMessageDialog(this, "Lỗi nghiệp vụ/không xác định: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}