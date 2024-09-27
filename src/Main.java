import Datenbank.*;

import java.sql.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        Connection connection = DatenbankVerbindung.connect(); // Verbindung zur Datenbank

        if (connection != null) {
            try {
                startMenu(connection); // Hauptmenü starten
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Ein Fehler ist aufgetreten", e);
            } finally {
                try {
                    if (!connection.isClosed()) {
                        connection.close(); // Verbindung schließen
                    }
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Fehler beim Schließen der Datenbankverbindung", ex);
                }
            }
        } else {
            logger.warning("Verbindung zur Datenbank konnte nicht hergestellt werden.");
        }
    }

    public static void startMenu(Connection connection) {
        int auswahl;
        MitarbeiterDAO mitarbeiterDAO = new MitarbeiterDAO(); // DAO für Mitarbeiter
        FahrzeugDAO fahrzeugDAO = new FahrzeugDAO(); // DAO für Fahrzeuge

        try (Scanner scanner = new Scanner(System.in);
             Statement stmt = connection.createStatement()) {

            while (true) {
                // Menüoptionen anzeigen
                System.out.println("\n--- Fahrzeugverwaltung ---");
                System.out.println("1. Fahrzeug hinzufügen");
                System.out.println("2. Mitarbeiter hinzufügen");
                System.out.println("3. Alle Fahrzeuge anzeigen");
                System.out.println("4. Alle Mitarbeiter anzeigen");
                System.out.println("5. Beenden");
                System.out.print("Wählen Sie eine Option (1-5): ");

                try {
                    auswahl = Integer.parseInt(scanner.nextLine()); // Benutzerauswahl einlesen
                    if (auswahl < 1 || auswahl > 5) {
                        throw new NumberFormatException(); // Eingabe validieren
                    }
                } catch (NumberFormatException e) {
                    System.out.print("Ungültige Eingabe. Bitte wählen Sie eine Option (1-6): ");
                    continue; // Fehlerbehandlung für ungültige Eingaben
                }

                switch (auswahl) {
                    case 1:
                        fahrzeugDAO.fahrzeugHinzufuegen(scanner, connection);
                        break;
                    case 2:
                        mitarbeiterDAO.mitarbeiterHinzufuegen(scanner, connection);
                        break;
                    case 3:
                        fahrzeugDAO.zeigeFahrzeuge(stmt);
                        break;
                    case 4:
                        mitarbeiterDAO.zeigeMitarbeiter(stmt);
                        break;
                    case 5:
                        System.out.println("Programm wird beendet.");
                        return;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ein Fehler ist aufgetreten", e);
        }
    }
}
