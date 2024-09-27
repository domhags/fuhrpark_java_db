package Datenbank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatenbankVerbindung {
    private static final String URL = "jdbc:mysql://localhost:3307/fahrzeugverwaltung"; // URL zur Fahrzeugverwaltung-Datenbank
    private static final String USER = "root"; // Benutzername für die Datenbank
    private static final String PASSWORD = ""; // Passwort für die Datenbank (leer)

    // Methode zur Herstellung einer Datenbankverbindung
    public static Connection connect() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD); // Verbindung herstellen
            System.out.println("Verbindung zur Datenbank erfolgreich!"); // Erfolgsnachricht
        } catch (SQLException e) {
            System.out.println("Verbindung zur Datenbank fehlgeschlagen: " + e.getMessage()); // Fehlermeldung ausgeben
        }
        return connection; // Verbindung zurückgeben
    }
}
