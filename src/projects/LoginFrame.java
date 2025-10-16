package projects;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private JTextField userText;
    private JPasswordField passwordText;
    private JComboBox<String> roleComboBox;

    // lưu image icon để dùng lại
    private Image appIcon;

    // Class 1: BackgroundPanel với overlay
    static class BackgroundPanel extends JPanel {
        private final Image backgroundImage;

        public BackgroundPanel(Image backgroundImage) {
            this.backgroundImage = backgroundImage;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            if (backgroundImage != null) {
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

                // Overlay tối để text nổi bật
                g2d.setColor(new Color(0, 0, 0, 80));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            } else {
                // Gradient backup
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(13, 71, 161),
                        0, getHeight(), new Color(25, 118, 210)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    // Class 2: RoundedPanel với shadow
    static class RoundedPanel extends JPanel {
        private final int cornerRadius = 30;

        public RoundedPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Shadow effect
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fill(new RoundRectangle2D.Float(4, 4, getWidth() - 4, getHeight() - 4, cornerRadius, cornerRadius));

            // Main panel
            g2.setColor(new Color(255, 255, 255, 245));
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 8, getHeight() - 8, cornerRadius, cornerRadius));

            g2.dispose();
        }
    }

    public LoginFrame() {
        setTitle("Hệ thống Quản lý Bán vé Máy bay");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Image bgImage = null;
        try {
            URL imageUrl = LoginFrame.class.getResource("/projects/resource/imgs/bg_airplane.jpg");

            if (imageUrl != null) {
                bgImage = ImageIO.read(imageUrl);
                System.out.println("DEBUG: Tải ảnh nền thành công từ: " + imageUrl);
            } else {
                System.err.println("LỖI PATH: Không tìm thấy ảnh nền.");
            }
        } catch (IOException e) {
            System.err.println("Lỗi tải ảnh nền: " + e.getMessage());
        }

        // --- Tải icon ứng dụng (có fallback nếu không tìm thấy) ---
        this.appIcon = loadIconFromResources(new String[] {
                "/projects/resource/imgs/airplane.png",
                "/projects/resource/imgs/logo.png",
                "/projects/resource/imgs/icon.png"
        }, 64, 64);

        // Nếu tìm được icon thì set lên title bar luôn
        if (this.appIcon != null) {
            setIconImage(this.appIcon);
        } else {
            System.out.println("DEBUG: Không tìm thấy icon ứng dụng (sử dụng emoji thay thế).");
        }

        BackgroundPanel mainPanel = new BackgroundPanel(bgImage);
        mainPanel.setLayout(new GridBagLayout());

        RoundedPanel loginPanel = createLoginPanel();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(30, 30, 30, 30);
        mainPanel.add(loginPanel, gbc);

        setContentPane(mainPanel);
    }

    /**
     * Thử tải một trong các đường dẫn icon; trả về Image đã scale nếu thành công, ngược lại null.
     */
    private Image loadIconFromResources(String[] paths, int w, int h) {
        for (String p : paths) {
            try {
                URL url = LoginFrame.class.getResource(p);
                if (url != null) {
                    BufferedImage img = ImageIO.read(url);
                    if (img != null) {
                        return img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
                    }
                }
            } catch (IOException ex) {
                // tiếp tục thử path kế tiếp
            }
        }
        return null;
    }

    private RoundedPanel createLoginPanel() {
        RoundedPanel panel = new RoundedPanel(new BorderLayout(0, 25));
        panel.setPreferredSize(new Dimension(450, 580));
        panel.setBorder(new EmptyBorder(40, 45, 40, 45));

        // --- HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout(0, 15));
        headerPanel.setOpaque(false);

        // Icon hoặc Logo: nếu có image thì hiển thị image, không thì dùng emoji
        JLabel iconLabel;
        if (this.appIcon != null) {
            iconLabel = new JLabel(new ImageIcon(this.appIcon));
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            iconLabel.setBorder(new EmptyBorder(0, 0, 5, 0));
        } else {
            iconLabel = new JLabel("✈", SwingConstants.CENTER);
            iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 60));
            iconLabel.setForeground(new Color(25, 118, 210));
        }

        JLabel titleLabel = new JLabel("ĐĂNG NHẬP HỆ THỐNG", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(33, 33, 33));

        JLabel subtitleLabel = new JLabel("Quản lý Bán vé Hàng không", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));

        JPanel titlePanel = new JPanel(new BorderLayout(0, 5));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        headerPanel.add(iconLabel, BorderLayout.NORTH);
        headerPanel.add(titlePanel, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);

        // --- FORM (CENTER) ---
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        // 1. Tên đăng nhập (có icon)
        userText = createStyledTextField();
        formPanel.add(createModernInputPanelWithIcon("Tên đăng nhập", userText, "/projects/resource/imgs/user.png"));
        formPanel.add(Box.createVerticalStrut(20));

        // 2. Mật khẩu (có icon)
        passwordText = new JPasswordField();
        stylePasswordField(passwordText);
        formPanel.add(createModernInputPanelWithIcon("Mật khẩu", passwordText, "/projects/resource/imgs/ticket.png"));
        formPanel.add(Box.createVerticalStrut(20));

        // 3. Chức vụ (có icon)
        roleComboBox = createStyledComboBox();
        formPanel.add(createModernInputPanelWithIcon("Chức vụ", roleComboBox, "/projects/resource/imgs/airplane.png"));
        formPanel.add(Box.createVerticalStrut(30));

        // 4. Nút Đăng nhập
        JButton loginButton = createStyledButton("ĐĂNG NHẬP");
        loginButton.addActionListener(this::loginAction);

        JPanel buttonWrapper = new JPanel(new BorderLayout());
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(loginButton, BorderLayout.CENTER);
        formPanel.add(buttonWrapper);

        panel.add(formPanel, BorderLayout.CENTER);

        // --- FOOTER ---
        JLabel footerLabel = new JLabel("© 2025 Hệ thống Quản lý Bán vé Hàng không", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        footerLabel.setForeground(new Color(120, 120, 120));
        panel.add(footerLabel, BorderLayout.SOUTH);

        return panel;
    }

    // private JPanel createModernInputPanel(String labelText, JComponent inputComponent) {
    //     JPanel panel = new JPanel(new BorderLayout(0, 8));
    //     panel.setOpaque(false);
    //     panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

    //     JLabel label = new JLabel(labelText);
    //     label.setFont(new Font("Segoe UI", Font.BOLD, 14));
    //     label.setForeground(new Color(60, 60, 60));

    //     panel.add(label, BorderLayout.NORTH);
    //     panel.add(inputComponent, BorderLayout.CENTER);

    //     return panel;
    // }

    /**
     * Tương tự createModernInputPanel nhưng có icon bên trái input.
     * labelText: tiêu đề (không kèm emoji) để giao diện nhất quán.
     * iconPath: đường dẫn trong resources, ví dụ "/projects/resource/imgs/user.png"
     */
    private JPanel createModernInputPanelWithIcon(String labelText, JComponent inputComponent, String iconPath) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(60, 60, 60));
        panel.add(label, BorderLayout.NORTH);

        JPanel inputWithIcon = new JPanel(new BorderLayout());
        inputWithIcon.setOpaque(false);

        // Tải icon 
        try {
            URL url = getClass().getResource(iconPath);
            if (url != null) {
                Image img = ImageIO.read(url).getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                JLabel iconLabel = new JLabel(new ImageIcon(img));
                iconLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
                inputWithIcon.add(iconLabel, BorderLayout.WEST);
            }
        } catch (IOException e) {
            System.err.println("Không tải được icon: " + iconPath);
        }

        inputWithIcon.add(inputComponent, BorderLayout.CENTER);
        panel.add(inputWithIcon, BorderLayout.CENTER);

        return panel;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        textField.setPreferredSize(new Dimension(0, 45));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(5, 15, 5, 15)
        ));

        // Focus effect
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(25, 118, 210), 2),
                        new EmptyBorder(5, 15, 5, 15)
                ));
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        new EmptyBorder(5, 15, 5, 15)
                ));
            }
        });

        return textField;
    }

    private void stylePasswordField(JPasswordField passwordField) {
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passwordField.setPreferredSize(new Dimension(0, 45));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(5, 15, 5, 15)
        ));

        // Focus effect
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(25, 118, 210), 2),
                        new EmptyBorder(5, 15, 5, 15)
                ));
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        new EmptyBorder(5, 15, 5, 15)
                ));
            }
        });
    }

    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> comboBox = new JComboBox<>(new String[]{"Nhân Viên", "Quản Lý", "Admin"});
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        comboBox.setPreferredSize(new Dimension(0, 45));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(0, 10, 0, 10)
        ));
        return comboBox;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(new Color(25, 118, 210));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(0, 50));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(21, 101, 192));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(25, 118, 210));
            }
        });

        return button;
    }

    // --- LOGIC ĐĂNG NHẬP (Giữ nguyên) ---
    private void loginAction(ActionEvent e) {
        String username = userText.getText().trim();
        String password = new String(passwordText.getPassword());
        String selectedRole = (String) roleComboBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ thông tin đăng nhập.",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT MaNhanVien, TenNhanVien, ChucVu FROM NHANVIEN WHERE TenDangNhap = ? AND MatKhau = ? AND ChucVu = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.setString(3, selectedRole);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int maNV = rs.getInt("MaNhanVien");
                        String tenNV = rs.getString("TenNhanVien");
                        String chucVu = rs.getString("ChucVu");

                        EmployeeFrame emp = new EmployeeFrame(maNV, tenNV, chucVu);
                        emp.setVisible(true);
                        this.dispose();

                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Tên đăng nhập hoặc mật khẩu hoặc chức vụ không đúng.",
                                "Đăng nhập thất bại",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi kết nối database: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
