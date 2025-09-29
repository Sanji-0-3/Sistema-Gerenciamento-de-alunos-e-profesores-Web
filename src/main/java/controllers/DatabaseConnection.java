package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost/escola"; 
    private static final String USER = "root";
    private static final String PASSWORD = "paulo3255"; 

    public static Connection getConnection() throws SQLException {
        try {
            // Registra o driver do MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            throw new SQLException("Erro ao conectar com o banco de dados", e);
        }
    }
}
