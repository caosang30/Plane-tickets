package projects;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

// Import Panel đã hoàn thành
import projects.ui.BookingPanel;
import projects.ui.CustomerPanel;
import projects.ui.PaymentPanel;
import projects.ui.ReportPanel;
import projects.ui.FlightPanel;
// import projects.LoginFrame; // Bỏ comment dòng này nếu cần

public class EmployeeFrame extends JFrame {

    private final int maNV;
    private final String tenNV;
    private final String chucVu;

    // Màu sắc chuẩn
    private static final Color PRIMARY_COLOR = new Color(25, 118, 210);
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private static final Color INFO_COLOR = new Color(33, 150, 243);
    private static final Color WARNING_COLOR = new Color(255, 152, 0);
    private static final Color DANGER_COLOR = new Color(220, 53, 69); // Màu đỏ cho chữ và viền
    private static final Color PURPLE_COLOR = new Color(156, 39, 176);

    // Class Helper cho Background
    static class BackgroundPanel extends JPanel {
        private final Image backgroundImage;

        public BackgroundPanel() {
            Image loadedImage = null;
            try {
                URL imageUrl = BackgroundPanel.class.getResource("/projects/resources/images/bg_airplane.jpg");
                if (imageUrl != null) {
                    loadedImage = ImageIO.read(imageUrl);
                } else {
                    System.err.println("LỖI PATH: Không tìm thấy ảnh nền EmployeeFrame.");
                }
            } catch (IOException e) {
                System.err.println("Lỗi tải ảnh nền cục bộ: " + e.getMessage());
            }
            this.backgroundImage = loadedImage;
            this.setLayout(new BorderLayout(0, 0));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g2d.setColor(new Color(255, 255, 255, 40));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            } else {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(240, 248, 255), 0, getHeight(), new Color(230, 240, 250));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    public EmployeeFrame(int maNV, String tenNV, String chucVu) {
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.chucVu = chucVu;

        setTitle("Hệ thống Bán vé Máy bay - " + chucVu);
        setSize(1400, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        BackgroundPanel mainPanel = new BackgroundPanel();
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.add(createHeader(), BorderLayout.NORTH);
        mainPanel.add(createTabbedPane(), BorderLayout.CENTER);
        mainPanel.add(createStatusBar(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel titleWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titleWrapper.setBackground(new Color(255, 255, 255, 220));
        titleWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true),
            new EmptyBorder(10, 30, 10, 30)
        ));
        JLabel titleLabel = new JLabel("HỆ THỐNG QUẢN LÝ BÁN VÉ MÁY BAY");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleWrapper.add(titleLabel);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        infoPanel.setBackground(new Color(255, 255, 255, 200));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(5, 15, 5, 15)
        ));

        JLabel userLabel = new JLabel("Nhân viên: " + tenNV);
        userLabel.setFont(new Font("Arial", Font.BOLD, 13));
        userLabel.setForeground(new Color(60, 60, 60));

        JLabel roleLabel = new JLabel("| Chức vụ: " + chucVu);
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        roleLabel.setForeground(new Color(100, 100, 100));

        infoPanel.add(userLabel);
        infoPanel.add(roleLabel);

        // ==================== BẮT ĐẦU CODE ĐĂNG XUẤT (ĐÃ SỬA) ====================

        JButton logoutButton = new JButton("Đăng xuất");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setFocusPainted(false);

        // --- THAY ĐỔI Ở ĐÂY ---
        // Đặt nền trắng, chữ đỏ để dễ nhìn hơn
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setForeground(DANGER_COLOR);
        // --- KẾT THÚC THAY ĐỔI ---
        
        logoutButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DANGER_COLOR, 1), // Viền màu đỏ
            new EmptyBorder(5, 15, 5, 15)
        ));

        logoutButton.addActionListener(e -> {
            int confirmed = JOptionPane.showConfirmDialog(EmployeeFrame.this,
                "Bạn có chắc chắn muốn đăng xuất không?", "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

            if (confirmed == JOptionPane.YES_OPTION) {
                dispose();
                // Mở lại cửa sổ đăng nhập
                SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
            }
        });

        infoPanel.add(Box.createHorizontalStrut(10));
        infoPanel.add(logoutButton);

        // ===================== KẾT THÚC CODE ĐĂNG XUẤT =====================

        headerPanel.add(titleWrapper, BorderLayout.CENTER);
        headerPanel.add(infoPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JTabbedPane createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setOpaque(true);

        tabbedPane.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color[] tabColors = {SUCCESS_COLOR, INFO_COLOR, PURPLE_COLOR, WARNING_COLOR, DANGER_COLOR};
                Color tabColor = tabColors[tabIndex % tabColors.length];
                if (isSelected) {
                    g2d.setColor(tabColor);
                } else {
                    g2d.setColor(new Color(tabColor.getRed(), tabColor.getGreen(), tabColor.getBlue(), 100));
                }
                g2d.fillRoundRect(x, y, w, h, 10, 10);
            }

            @Override
            protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) {
                g.setFont(font);
                g.setColor(isSelected ? Color.WHITE : new Color(80, 80, 80));
                g.drawString(title, textRect.x, textRect.y + metrics.getAscent());
            }
        });

        addStyledTab(tabbedPane, "Đặt/Hủy vé", new BookingPanel(maNV));
        addStyledTab(tabbedPane, "Thanh toán", new PaymentPanel());
        addStyledTab(tabbedPane, "Khách hàng", new CustomerPanel());
        addStyledTab(tabbedPane, "Chuyến bay", new FlightPanel());
        addStyledTab(tabbedPane, "Báo cáo", new ReportPanel());

        return tabbedPane;
    }

    private void addStyledTab(JTabbedPane tabbedPane, String title, JPanel panel) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        wrapper.add(scrollPane, BorderLayout.CENTER);
        tabbedPane.addTab(title, wrapper);
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(245, 245, 245));
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(200, 200, 200)),
            new EmptyBorder(10, 15, 10, 15)
        ));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);
        JLabel statusDot = new JLabel("\u25CF");
        statusDot.setFont(new Font("Arial", Font.PLAIN, 16));
        statusDot.setForeground(SUCCESS_COLOR);
        JLabel systemStatus = new JLabel("Hệ thống hoạt động bình thường");
        systemStatus.setFont(new Font("Arial", Font.PLAIN, 12));
        systemStatus.setForeground(new Color(60, 60, 60));
        leftPanel.add(statusDot);
        leftPanel.add(systemStatus);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        JLabel empIdLabel = new JLabel("Mã NV: " + maNV);
        empIdLabel.setFont(new Font("Arial", Font.BOLD, 12));
        empIdLabel.setForeground(new Color(60, 60, 60));
        empIdLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(3, 10, 3, 10)
        ));
        empIdLabel.setBackground(new Color(255, 255, 255));
        empIdLabel.setOpaque(true);
        JLabel separator1 = new JLabel("|");
        separator1.setForeground(new Color(200, 200, 200));
        JLabel empNameLabel = new JLabel(tenNV);
        empNameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        empNameLabel.setForeground(new Color(80, 80, 80));
        JLabel separator2 = new JLabel("|");
        separator2.setForeground(new Color(200, 200, 200));
        JLabel empRoleLabel = new JLabel(chucVu);
        empRoleLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        empRoleLabel.setForeground(new Color(120, 120, 120));
        rightPanel.add(empIdLabel);
        rightPanel.add(separator1);
        rightPanel.add(empNameLabel);
        rightPanel.add(separator2);
        rightPanel.add(empRoleLabel);

        statusBar.add(leftPanel, BorderLayout.WEST);
        statusBar.add(rightPanel, BorderLayout.EAST);

        return statusBar;
    }
}