package projects;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Khởi chạy giao diện trên luồng Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}