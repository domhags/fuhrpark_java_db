package Datenbank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatenbankVerbindung {
    private static final String URL = "jdbc:mysql://localhost:3307/fahrzeugverwaltung";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection connect() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Verbindung zur Datenbank erfolgreich!");
        } catch (SQLException e) {
            System.out.println("Verbindung zur Datenbank fehlgeschlagen: " + e.getMessage());
        }
        return connection;
    }
}
