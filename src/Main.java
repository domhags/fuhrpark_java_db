import Datenbank.*;
import Mitarbeiter.Mitarbeiter;
import Fahrzeug.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        Connection connection = DatenbankVerbindung.connect();

        if (connection != null) {
            try {
                startMenu(connection);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Ein Fehler ist aufgetreten", e);
            } finally {
                try {
                    if (!connection.isClosed()) {
                        connection.close();
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
        try (Scanner scanner = new Scanner(System.in);
             Statement stmt = connection.createStatement()) {

            while (true) {
                System.out.println("\n--- Fahrzeugverwaltung ---");
                System.out.println("1. Fahrzeug hinzufügen");
                System.out.println("2. Mitarbeiter hinzufügen");
                System.out.println("3. Alle Fahrzeuge anzeigen");
                System.out.println("4. Beenden");
                System.out.print("Wählen Sie eine Option (1-4): ");

                try {
                    auswahl = Integer.parseInt(scanner.nextLine());
                    if (auswahl < 1 || auswahl > 4) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    System.out.print("Ungültige Eingabe. Bitte wählen Sie eine Option (1-3): ");
                    continue;
                }

                switch (auswahl) {
                    case 1:
                        fahrzeugHinzufuegen(scanner, stmt);
                        break;
                    case 2:
                        mitarbeiterHinzufuegen(scanner, stmt.getConnection());
                        break;
                    case 3:
                        zeigeFahrzeuge(stmt);
                        break;
                    case 4:
                        System.out.println("Programm wird beendet.");
                        return;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ein Fehler ist aufgetreten", e);
        }
    }

    private static void fahrzeugHinzufuegen(Scanner scanner, Statement stmt) throws SQLException {
        Fahrzeug fahrzeug = getFahrzeug(scanner);
        if (fahrzeug != null) {
            String insertSQL = fahrzeug.getInsertSQLBefehl();
            stmt.executeUpdate(insertSQL, Statement.RETURN_GENERATED_KEYS);
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                String specificSQL = fahrzeug.getSpezifischenInsertSQLBefehl(id);
                stmt.executeUpdate(specificSQL);
                System.out.println("Fahrzeug erfolgreich hinzugefügt!");
            }
        }
    }

    private static void zeigeFahrzeuge(Statement stmt) throws SQLException {
        String query = "SELECT Fahrzeug.fahrzeug_id, Fahrzeug.kilometerstand, Fahrzeug.baujahr, Fahrzeug.farbe, " +
                "Fahrrad.art, Fahrrad.anzahl_gaenge, Motorrad.hubraum, Motorrad.anzahl_helmhalterungen, PKW.anzahl_sitze, " +
                "PKW.kofferraumvolumen, LKW.ladegewicht, LKW.anzahl_achsen " +
                "FROM Fahrzeug " +
                "LEFT JOIN Fahrrad ON Fahrzeug.fahrzeug_id = Fahrrad.id " +
                "LEFT JOIN Motorrad ON Fahrzeug.fahrzeug_id = Motorrad.id " +
                "LEFT JOIN PKW ON Fahrzeug.fahrzeug_id = PKW.id " +
                "LEFT JOIN LKW ON Fahrzeug.fahrzeug_id = LKW.id";
        ResultSet rs = stmt.executeQuery(query);

        // Extrahieren und Anzeigen der Daten
        while (rs.next()) {
            int id = rs.getInt("fahrzeug_id");
            int fahrzeugKilometerstand = rs.getInt("kilometerstand");
            int fahrzeugBaujahr = rs.getInt("baujahr");
            String fahrzeugFarbe = rs.getString("farbe");

            System.out.println("ID: " + id + ", Kilometerstand: " + fahrzeugKilometerstand + ", Baujahr: " + fahrzeugBaujahr +
                    ", Farbe: " + fahrzeugFarbe);

            // Überprüfen des Fahrzeugtyps und Anzeigen von Details
            if (rs.getString("art") != null) {
                System.out.println("Typ: Fahrrad, Art: " + rs.getString("art"));
            } else if (rs.getInt("anzahl_gaenge") > 0) {
                System.out.println("Typ: Fahrrad, Anzahl Gänge: " + rs.getInt("anzahl_gaenge"));
            } else if (rs.getInt("hubraum") > 0) {
                System.out.println("Typ: Motorrad, Hubraum: " + rs.getInt("hubraum") + " ccm");
            } else if (rs.getInt("anzahl_helmhalterungen") > 0) {
                System.out.println("Typ: Motorrad, Anzahl Helmhalterungen: " + rs.getInt("anzahl_helmhalterungen"));
            } else if (rs.getInt("anzahl_sitze") > 0) {
                System.out.println("Typ: PKW, Anzahl der Sitze: " + rs.getInt("anzahl_sitze"));
            } else if (rs.getInt("kofferraumvolumen") > 0) {
                System.out.println("Typ: PKW, Kofferraumvolumen: " + rs.getInt("kofferraumvolumen"));
            } else if (rs.getFloat("ladegewicht") > 0) {
                System.out.println("Typ: LKW, Ladegewicht: " + rs.getFloat("ladegewicht") + " kg");
            } else if (rs.getInt("anzahl_achsen") > 0) {
                System.out.println("Typ: LKW, Anzahl der Achsen: " + rs.getInt("anzahl_achsen"));
            }
            System.out.println("-----------------------------");
        }
    }


    private static void mitarbeiterHinzufuegen(Scanner scanner, Connection connection) {
        try {
            Mitarbeiter mitarbeiter = getMitarbeiter(scanner);
            String insertSQL = mitarbeiter.getInsertSQLBefehl();
            try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                pstmt.setString(1, mitarbeiter.getVorname());
                pstmt.setString(2, mitarbeiter.getNachname());
                pstmt.setDate(3, java.sql.Date.valueOf(mitarbeiter.getGeburtsdatum()));

                pstmt.executeUpdate();
                logger.info("Mitarbeiter erfolgreich hinzugefügt: " + mitarbeiter.getVorname() + " " + mitarbeiter.getNachname());
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Fehler beim Hinzufügen des Mitarbeiters", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unerwarteter Fehler", e);
        }
    }

    private static Mitarbeiter getMitarbeiter(Scanner scanner) {
        System.out.print("Vorname des Mitarbeiters: ");
        String vorname = scanner.nextLine();
        System.out.print("Nachname des Mitarbeiters: ");
        String nachname = scanner.nextLine();
        System.out.print("Geburtsdatum des Mitarbeiters (YYYY-MM-DD): ");
        LocalDate geburtsdatum;

        while (true) {
            try {
                geburtsdatum = LocalDate.parse(scanner.nextLine());
                break;
            } catch (Exception e) {
                System.out.print("Ungültiges Datum. Bitte geben Sie das Geburtsdatum im Format YYYY-MM-DD ein: ");
            }
        }
        return new Mitarbeiter(vorname, nachname, geburtsdatum);
    }

    private static Fahrzeug getFahrzeug(Scanner scanner) {
        System.out.println("Fahrzeugtypen: ");
        System.out.println("1. Fahrrad");
        System.out.println("2. Motorrad");
        System.out.println("3. PKW");
        System.out.println("4. LKW");
        System.out.print("Wählen Sie einen Typ: ");
        int typ;

        while (true) {
            try {
                typ = Integer.parseInt(scanner.nextLine());
                if (typ < 1 || typ > 4) {
                    System.out.println("Ungültiger Typ. Bitte wählen Sie einen Typ (1-4): ");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.print("Ungültige Eingabe. Bitte wählen Sie einen Typ (1-4): ");
            }
        }

        System.out.print("Kilometerstand des Fahrzeugs: ");
        int kilometerstand = getInteger(scanner);
        System.out.print("Baujahr des Fahrzeugs: ");
        int baujahr = getInteger(scanner);
        System.out.print("Farbe des Fahrzeugs: ");
        String farbe = scanner.nextLine().trim();

        switch (typ) {
            case 1:
                return erstelleFahrrad(scanner, kilometerstand, baujahr, farbe);
            case 2:
                return erstelleMotorrad(scanner, kilometerstand, baujahr, farbe);
            case 3:
                return erstellePkw(scanner, kilometerstand, baujahr, farbe);
            case 4:
                return erstelleLkw(scanner, kilometerstand, baujahr, farbe);
            default:
                throw new IllegalArgumentException("Ungültiger Fahrzeugtyp.");
        }
    }

    private static int getInteger(Scanner scanner) {
        int value;
        while (true) {
            try {
                value = Integer.parseInt(scanner.nextLine());
                if (value < 0) {
                    throw new NumberFormatException();
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.print("Bitte geben Sie eine positive Zahl ein: ");
            }
        }
    }

    private static Fahrrad erstelleFahrrad(Scanner scanner, int kilometerstand, int baujahr, String farbe) {
        System.out.print("Geben Sie den Typ des Fahrrads ein (zB E-Bike): ");
        String art = scanner.nextLine().trim();
        System.out.print("Geben Sie die Anzahl der Gänge vom Fahrrad ein: ");
        int anzahlGaenge = Integer.parseInt(scanner.nextLine().trim());
        return new Fahrrad(kilometerstand, baujahr, farbe, art, anzahlGaenge);
    }

    private static Motorrad erstelleMotorrad(Scanner scanner, int kilometerstand, int baujahr, String farbe) {
        System.out.print("Hubraum des Motorrads: ");
        int hubraum = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Geben Sie die Anzahl der Helmhalterungen für das Motorrad ein: ");
        int anzahlHelmhalterungen = Integer.parseInt(scanner.nextLine().trim());
        return new Motorrad(kilometerstand, baujahr, farbe, hubraum, anzahlHelmhalterungen);
    }

    private static PKW erstellePkw(Scanner scanner, int kilometerstand, int baujahr, String farbe) {
        System.out.print("Anzahl der Sitze im PKW: ");
        int anzahlSitze = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Geben Sie das Kofferraumvolumen ein: ");
        int kofferraumvolumen = Integer.parseInt(scanner.nextLine().trim());
        return new PKW(kilometerstand, baujahr, farbe, anzahlSitze, kofferraumvolumen);
    }

    private static LKW erstelleLkw(Scanner scanner, int kilometerstand, int baujahr, String farbe) {
        System.out.print("Ladegewicht des LKW in kg: ");
        float ladegewicht = Float.parseFloat(scanner.nextLine().trim());
        System.out.print("Geben Sie die Anzahl der Achsen ein: ");
        int anzahlAchsen = Integer.parseInt(scanner.nextLine().trim());
        return new LKW(kilometerstand, baujahr, farbe, ladegewicht, anzahlAchsen);
    }
}
