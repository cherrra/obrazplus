package services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseService {
    private static DatabaseService instance;

    private DatabaseService() throws SQLException {
        //конструктор для паттерна Singleton
    }

    public static synchronized DatabaseService getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }
    //соединение бд
    public Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found.", e);
        }

        String url = "jdbc:mysql://localhost:3306/obraz_db";
        String user = "root";
        String password = "";

        //каждый раз новое соединение
        return DriverManager.getConnection(url, user, password);
    }

    //заглушка
    public void close() {
        System.out.println("DatabaseService: Метод close() вызван");
    }
}
