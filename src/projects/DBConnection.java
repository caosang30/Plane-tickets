package projects;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL =
        "jdbc:sqlserver://localhost:1433;databaseName=banVeMayBay;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "sa";       // user SQL Server
    private static final String PASS = "123450";  // password SQL Server

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}