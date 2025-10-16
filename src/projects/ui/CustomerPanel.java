package projects.ui;

import projects.dao.CustomerDAO;
import projects.models.Customer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

public class CustomerPanel extends JPanel {

    private final CustomerDAO customerDAO;
    private final DefaultTableModel customerModel;

    // Components
    private JTextField txtMaKH, txtName, txtCCCD, txtPhone, txtAddress, txtSearch;
    private JTable tblCustomers;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    public CustomerPanel() {
        this.customerDAO = new CustomerDAO();
        
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- CẤU HÌNH BẢNG KHÁCH HÀNG ---
        customerModel = new DefaultTableModel(new String[]{"Mã KH", "Họ tên", "CCCD/CMND", "SĐT", "Địa chỉ"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblCustomers = new JTable(customerModel);
        
        // Listener: Khi chọn một dòng, đổ dữ liệu lên form
        tblCustomers.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedCustomerToForm();
            }
        });

        // --- CẤU HÌNH CÁC PANEL CHỨC NĂNG ---
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setOpaque(false);
        
        mainPanel.add(createSearchPanel(), BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(tblCustomers), BorderLayout.CENTER);
        mainPanel.add(createFormAndButtonPanel(), BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Tải dữ liệu lần đầu
        loadCustomers();
        resetForm();
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(new TitledBorder("Tìm kiếm Khách hàng"));
        panel.setOpaque(false);

        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.addActionListener(this::searchCustomersAction);
        
        panel.add(new JLabel("Tên/CCCD/SĐT:"));
        panel.add(txtSearch);
        panel.add(btnSearch);
        
        return panel;
    }

    private JPanel createFormAndButtonPanel() {
        JPanel container = new JPanel(new BorderLayout(15, 15));
        container.setOpaque(false);
        
        // 1. Panel Form
        JPanel formPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        formPanel.setBorder(new TitledBorder("Thông tin Khách hàng"));
        formPanel.setOpaque(false);

        txtMaKH = new JTextField();
        txtMaKH.setEditable(false);
        txtName = new JTextField();
        txtCCCD = new JTextField();
        txtPhone = new JTextField();
        txtAddress = new JTextField();
        
        formPanel.add(new JLabel("Mã KH:"));
        formPanel.add(txtMaKH);
        formPanel.add(new JLabel("Họ tên:"));
        formPanel.add(txtName);
        
        formPanel.add(new JLabel("CCCD/CMND:"));
        formPanel.add(txtCCCD);
        formPanel.add(new JLabel("SĐT:"));
        formPanel.add(txtPhone);

        formPanel.add(new JLabel("Địa chỉ:"));
        formPanel.add(txtAddress);
        formPanel.add(new JPanel()); // Placeholder
        formPanel.add(new JPanel()); // Placeholder

        // 2. Panel Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        
        btnAdd = new JButton("Thêm mới");
        btnUpdate = new JButton("Cập nhật");
        btnDelete = new JButton("Xóa");
        btnClear = new JButton("Làm mới Form");

        btnAdd.addActionListener(this::addCustomerAction);
        btnUpdate.addActionListener(this::updateCustomerAction);
        btnDelete.addActionListener(this::deleteCustomerAction);
        btnClear.addActionListener(e -> resetForm());
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        
        container.add(formPanel, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);
        
        return container;
    }

    // =======================================================
    // --- LOGIC DỮ LIỆU VÀ HÀNH ĐỘNG ---
    // =======================================================
    
    private void resetForm() {
        txtMaKH.setText("");
        txtName.setText("");
        txtCCCD.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        txtSearch.setText("");
        tblCustomers.clearSelection();
        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    private Customer getCustomerFromForm(boolean isNew) {
        // Kiểm tra validation
        String name = txtName.getText().trim();
        String cccd = txtCCCD.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();
        
        if (name.isEmpty() || cccd.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Họ tên, CCCD/CMND và SĐT.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int maKH = isNew ? 0 : Integer.parseInt(txtMaKH.getText());
        return new Customer(maKH, name, address, phone, cccd);
    }
    
    private void loadCustomers() {
        customerModel.setRowCount(0);
        try {
            List<Customer> customers = customerDAO.getAllCustomers();
            for (Customer c : customers) {
                customerModel.addRow(new Object[]{
                    c.getMaKhachHang(),
                    c.getTenKhachHang(),
                    c.getCccd(),
                    c.getSoDienThoai(),
                    c.getDiaChi()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu khách hàng: " + ex.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void loadSelectedCustomerToForm() {
        int selectedRow = tblCustomers.getSelectedRow();
        if (selectedRow != -1) {
            try {
                int maKH = (int) customerModel.getValueAt(selectedRow, 0);
                String name = (String) customerModel.getValueAt(selectedRow, 1);
                String cccd = (String) customerModel.getValueAt(selectedRow, 2);
                String phone = (String) customerModel.getValueAt(selectedRow, 3);
                String address = (String) customerModel.getValueAt(selectedRow, 4);
                
                txtMaKH.setText(String.valueOf(maKH));
                txtName.setText(name);
                txtCCCD.setText(cccd);
                txtPhone.setText(phone);
                txtAddress.setText(address);
                
                btnAdd.setEnabled(false);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu khách hàng lên form: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchCustomersAction(ActionEvent e) {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadCustomers();
            return;
        }

        customerModel.setRowCount(0);
        try {
            List<Customer> customers = customerDAO.searchCustomers(keyword);
            for (Customer c : customers) {
                 customerModel.addRow(new Object[]{
                    c.getMaKhachHang(),
                    c.getTenKhachHang(),
                    c.getCccd(),
                    c.getSoDienThoai(),
                    c.getDiaChi()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm khách hàng: " + ex.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void addCustomerAction(ActionEvent e) {
        Customer newCustomer = getCustomerFromForm(true);
        if (newCustomer == null) return;

        try {
            int maKH = customerDAO.addCustomer(newCustomer);
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công! Mã KH: " + maKH, "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadCustomers();
            resetForm();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm khách hàng: " + ex.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void updateCustomerAction(ActionEvent e) {
        Customer updatedCustomer = getCustomerFromForm(false);
        if (updatedCustomer == null) return;

        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn cập nhật thông tin khách hàng này?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            customerDAO.updateCustomer(updatedCustomer);
            JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadCustomers();
            resetForm();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật khách hàng: " + ex.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void deleteCustomerAction(ActionEvent e) {
        if (txtMaKH.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int maKH = Integer.parseInt(txtMaKH.getText());

        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa khách hàng #" + maKH + " không? (Cần đảm bảo khách hàng không còn giao dịch)", "Xác nhận Xóa", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            customerDAO.deleteCustomer(maKH);
            JOptionPane.showMessageDialog(this, "Xóa khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadCustomers();
            resetForm();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi xóa khách hàng: " + ex.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}