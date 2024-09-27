package Datenbank;

import Mitarbeiter.Mitarbeiter;

import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MitarbeiterDAO {
    private static final Logger logger = Logger.getLogger(MitarbeiterDAO.class.getName());

    // Zeigt alle Mitarbeiter aus der Datenbank an
    public static void zeigeMitarbeiter(Statement stmt) throws SQLException {
        System.out.println("--- Alle Mitarbeiter ---");
        String query = "SELECT * FROM Mitarbeiter";
        ResultSet rs = stmt.executeQuery(query);

        // Prüft, ob die Tabelle leer ist
        if (!rs.isBeforeFirst()) {
            System.out.println("Keine Mitarbeiter in der Tabelle.");
            return;
        }

        // Iteriert durch die den ResultSet
        while (rs.next()) {
            int id = rs.getInt("mitarbeiter_id");
            String vorname = rs.getString("vorname");
            String nachname = rs.getString("nachname");
            Date geburtsdatum = rs.getDate("geburtsdatum");

            // Gibt die Mitarbeiterdetails aus
            System.out.println("ID: " + id + ", Vorname: " + vorname + ", Nachname: " + nachname + ", Geburtsdatum: " + geburtsdatum);
        }
        System.out.println("-----------------------------");
    }

    // Fügt einen neuen Mitarbeiter in die Datenbank hinzu
    public static void mitarbeiterHinzufuegen(Scanner scanner, Connection connection) {
        try {
            // Holt die Mitarbeiterdaten vom Benutzer
            Mitarbeiter mitarbeiter = getMitarbeiter(scanner);
            String insertSQL = "INSERT INTO Mitarbeiter (vorname, nachname, geburtsdatum) VALUES (?, ?, ?)";

            // Bereitet das SQL-Insert-Statement vor
            try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                pstmt.setString(1, mitarbeiter.getVorname());
                pstmt.setString(2, mitarbeiter.getNachname());
                pstmt.setDate(3, java.sql.Date.valueOf(mitarbeiter.getGeburtsdatum()));

                pstmt.executeUpdate(); // Führt das Insert-Statement aus
                logger.info("Mitarbeiter erfolgreich hinzugefügt.");
            }
        } catch (SQLException e) {
            // Loggt eventuelle SQL-Fehler
            logger.log(Level.SEVERE, "Fehler beim Hinzufügen des Mitarbeiters", e);
        }
    }

    // Fragt den Benutzer nach den Mitarbeiterdaten
    private static Mitarbeiter getMitarbeiter(Scanner scanner) {
        System.out.print("Geben Sie den Vornamen des Mitarbeiters ein: ");
        String vorname = scanner.nextLine();
        System.out.print("Geben Sie den Nachnamen des Mitarbeiters ein: ");
        String nachname = scanner.nextLine();
        System.out.print("Geben Sie das Geburtsdatum des Mitarbeiters ein (YYYY-MM-DD): ");
        LocalDate geburtsdatum = LocalDate.parse(scanner.nextLine());

        // Erstellt ein Mitarbeiter-Objekt mit den eingegebenen Daten
        return new Mitarbeiter(vorname, nachname, geburtsdatum);
    }
}
