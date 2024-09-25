import Datenbank.DatenbankVerbindung;
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
                System.out.println("4. Alle Mitarbeiter anzeigen");
                System.out.println("5. Beenden");
                System.out.print("Wählen Sie eine Option (1-5): ");

                try {
                    auswahl = Integer.parseInt(scanner.nextLine());
                    if (auswahl < 1 || auswahl > 5) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    System.out.print("Ungültige Eingabe. Bitte wählen Sie eine Option (1-6): ");
                    continue;
                }

                switch (auswahl) {
                    case 1:
                        fahrzeugHinzufuegen(scanner, connection);
                        break;
                    case 2:
                        mitarbeiterHinzufuegen(scanner, connection);
                        break;
                    case 3:
                        zeigeFahrzeuge(stmt);
                        break;
                    case 4:
                        zeigeMitarbeiter(stmt);
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

    private static void fahrzeugHinzufuegen(Scanner scanner, Connection connection) {
        Fahrzeug fahrzeug = getFahrzeug(scanner);
        if (fahrzeug != null) {
            String insertSQL = "";

            if (fahrzeug instanceof Fahrrad) {
                insertSQL = "INSERT INTO Fahrrad (kilometerstand, baujahr, farbe, art, anzahl_gaenge) " +
                        "VALUES (?, ?, ?, ?, ?)";
            } else if (fahrzeug instanceof Motorrad) {
                insertSQL = "INSERT INTO Motorrad (kilometerstand, baujahr, farbe, hubraum, anzahl_helmhalterungen) " +
                        "VALUES (?, ?, ?, ?, ?)";
            } else if (fahrzeug instanceof PKW) {
                insertSQL = "INSERT INTO PKW (kilometerstand, baujahr, farbe, anzahl_sitze, kofferraumvolumen) " +
                        "VALUES (?, ?, ?, ?, ?)";
            } else if (fahrzeug instanceof LKW) {
                insertSQL = "INSERT INTO LKW (kilometerstand, baujahr, farbe, ladegewicht, anzahl_achsen) " +
                        "VALUES (?, ?, ?, ?, ?)";
            }

            try (PreparedStatement pstmt = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, fahrzeug.getKilometerstand());
                pstmt.setInt(2, fahrzeug.getBaujahr());
                pstmt.setString(3, fahrzeug.getFarbe());

                // Setze die spezifischen Attribute des Fahrzeugs, je nach Typ
                if (fahrzeug instanceof Fahrrad) {
                    Fahrrad f = (Fahrrad) fahrzeug;
                    pstmt.setString(4, f.getArt());
                    pstmt.setInt(5, f.getAnzahlGaenge());
                } else if (fahrzeug instanceof Motorrad) {
                    Motorrad m = (Motorrad) fahrzeug;
                    pstmt.setInt(4, m.getHubraum());
                    pstmt.setInt(5, m.getAnzahlHelmhalterungen());
                } else if (fahrzeug instanceof PKW) {
                    PKW p = (PKW) fahrzeug;
                    pstmt.setInt(4, p.getSitze());
                    pstmt.setInt(5, p.getKofferraumvolumen());
                } else if (fahrzeug instanceof LKW) {
                    LKW l = (LKW) fahrzeug;
                    pstmt.setFloat(4, l.getLadegewicht());
                    pstmt.setInt(5, l.getAnzahlAchsen());
                }

                pstmt.executeUpdate();
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    System.out.println("Fahrzeug erfolgreich hinzugefügt mit ID: " + id);
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Fehler beim Hinzufügen des Fahrzeugs", e);
            }
        }
    }

    private static void zeigeFahrzeuge(Statement stmt) throws SQLException {
        System.out.println("--- Alle Fahrzeuge ---");

        zeigeFahrzeugeAusTabelle(stmt, "Fahrrad");
        zeigeFahrzeugeAusTabelle(stmt, "Motorrad");
        zeigeFahrzeugeAusTabelle(stmt, "PKW");
        zeigeFahrzeugeAusTabelle(stmt, "LKW");
    }


    private static void zeigeFahrzeugeAusTabelle(Statement stmt, String tableName) throws SQLException {
        String query = "SELECT * FROM " + tableName;
        ResultSet rs = stmt.executeQuery(query);

        // Wenn keine Fahrzeuge in der Tabelle sind, eine Nachricht anzeigen
        if (!rs.isBeforeFirst()) {
            System.out.println("Keine Fahrzeuge in der Tabelle " + tableName);
            return;
        }

        while (rs.next()) {
            int id = rs.getInt(tableName.toLowerCase() + "_id");
            int kilometerstand = rs.getInt("kilometerstand");
            int baujahr = rs.getInt("baujahr");
            String farbe = rs.getString("farbe");

            System.out.println("ID: " + id + ", Kilometerstand: " + kilometerstand +
                    ", Baujahr: " + baujahr + ", Farbe: " + farbe);

            // Zeige spezifische Details für jeden Fahrzeugtyp
            switch (tableName) {
                case "Fahrrad":
                    zeigeFahrradDetails(rs);
                    System.out.println("Typ: Fahrrad");
                    break;
                case "Motorrad":
                    zeigeMotorradDetails(rs);
                    System.out.println("Typ: Motorrad");
                    break;
                case "PKW":
                    zeigePKWDetails(rs);
                    System.out.println("Typ: PKW");
                    break;
                case "LKW":
                    zeigeLKWDetails(rs);
                    System.out.println("Typ: LKW");
                    break;
            }

            System.out.println("-----------------------------");
        }
    }

    private static void zeigeFahrradDetails(ResultSet rs) throws SQLException {
        String art = rs.getString("art");
        int anzahlGaenge = rs.getInt("anzahl_gaenge");
        System.out.println("Art: " + art);
        System.out.println("Anzahl Gänge: " + anzahlGaenge);
    }

    private static void zeigeMotorradDetails(ResultSet rs) throws SQLException {
        int hubraum = rs.getInt("hubraum");
        int anzahlHelmhalterungen = rs.getInt("anzahl_helmhalterungen");
        System.out.println("Hubraum: " + hubraum + " ccm");
        System.out.println("Anzahl Helmhalterungen: " + anzahlHelmhalterungen);
    }

    private static void zeigePKWDetails(ResultSet rs) throws SQLException {
        int anzahlSitze = rs.getInt("anzahl_sitze");
        int kofferraumvolumen = rs.getInt("kofferraumvolumen");
        System.out.println("Anzahl der Sitze: " + anzahlSitze);
        System.out.println("Kofferraumvolumen: " + kofferraumvolumen + " Liter");
    }

    private static void zeigeLKWDetails(ResultSet rs) throws SQLException {
        float ladegewicht = rs.getFloat("ladegewicht");
        int anzahlAchsen = rs.getInt("anzahl_achsen");
        System.out.println("Ladegewicht: " + ladegewicht + " kg");
        System.out.println("Anzahl der Achsen: " + anzahlAchsen);
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
                logger.info("Mitarbeiter erfolgreich hinzugefügt.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Fehler beim Hinzufügen des Mitarbeiters", e);
        }
    }

    private static Fahrzeug getFahrzeug(Scanner scanner) {
        System.out.println("Wählen Sie den Fahrzeugtyp:");
        System.out.println("1. Fahrrad");
        System.out.println("2. Motorrad");
        System.out.println("3. PKW");
        System.out.println("4. LKW");
        System.out.print("Option wählen (1-4): ");

        int typ;
        try {
            typ = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Ungültige Eingabe. Bitte geben Sie eine Zahl zwischen 1 und 4 ein.");
            return null;
        }

        Fahrzeug fahrzeug;
        switch (typ) {
            case 1:
                fahrzeug = erstelleFahrrad(scanner);
                break;
            case 2:
                fahrzeug = erstelleMotorrad(scanner);
                break;
            case 3:
                fahrzeug = erstellePKW(scanner);
                break;
            case 4:
                fahrzeug = erstelleLKW(scanner);
                break;
            default:
                System.out.println("Ungültige Eingabe. Bitte wählen Sie einen gültigen Fahrzeugtyp.");
                return null;
        }
        return fahrzeug;
    }

    private static Fahrrad erstelleFahrrad(Scanner scanner) {
        System.out.print("Geben Sie den Kilometerstand ein: ");
        int kilometerstand = Integer.parseInt(scanner.nextLine());
        System.out.print("Geben Sie das Baujahr ein: ");
        int baujahr = Integer.parseInt(scanner.nextLine());
        System.out.print("Geben Sie die Farbe ein: ");
        String farbe = scanner.nextLine();
        System.out.print("Geben Sie die Art des Fahrrads ein (Mountainbike, E-Bike): ");
        String art = scanner.nextLine();
        System.out.print("Geben Sie die Anzahl der Gänge ein: ");
        int anzahlGaenge = Integer.parseInt(scanner.nextLine());

        return new Fahrrad(kilometerstand, baujahr, farbe, art, anzahlGaenge);
    }


    private static Motorrad erstelleMotorrad(Scanner scanner) {
        System.out.print("Geben Sie den Kilometerstand ein: ");
        int kilometerstand = Integer.parseInt(scanner.nextLine());
        System.out.print("Geben Sie das Baujahr ein: ");
        int baujahr = Integer.parseInt(scanner.nextLine());
        System.out.print("Geben Sie die Farbe ein: ");
        String farbe = scanner.nextLine();
        System.out.print("Geben Sie den Hubraum ein: ");
        int hubraum = Integer.parseInt(scanner.nextLine());
        System.out.print("Geben Sie die Anzahl der Helmhalterungen ein: ");
        int anzahlHelmhalterungen = Integer.parseInt(scanner.nextLine());

        return new Motorrad(kilometerstand, baujahr, farbe, hubraum, anzahlHelmhalterungen);
    }

    private static PKW erstellePKW(Scanner scanner) {
        System.out.print("Geben Sie den Kilometerstand ein: ");
        int kilometerstand = Integer.parseInt(scanner.nextLine());
        System.out.print("Geben Sie das Baujahr ein: ");
        int baujahr = Integer.parseInt(scanner.nextLine());
        System.out.print("Geben Sie die Farbe ein: ");
        String farbe = scanner.nextLine();
        System.out.print("Geben Sie die Anzahl der Sitze ein: ");
        int anzahlSitze = Integer.parseInt(scanner.nextLine());
        System.out.print("Geben Sie das Kofferraumvolumen ein: ");
        int kofferraumvolumen = Integer.parseInt(scanner.nextLine());

        return new PKW(kilometerstand, baujahr, farbe, anzahlSitze, kofferraumvolumen);
    }

    private static LKW erstelleLKW(Scanner scanner) {
        System.out.print("Geben Sie den Kilometerstand ein: ");
        int kilometerstand = Integer.parseInt(scanner.nextLine());
        System.out.print("Geben Sie das Baujahr ein: ");
        int baujahr = Integer.parseInt(scanner.nextLine());
        System.out.print("Geben Sie die Farbe ein: ");
        String farbe = scanner.nextLine();
        System.out.print("Geben Sie das Ladegewicht ein: ");
        float ladegewicht = Float.parseFloat(scanner.nextLine());
        System.out.print("Geben Sie die Anzahl der Achsen ein: ");
        int anzahlAchsen = Integer.parseInt(scanner.nextLine());

        return new LKW(kilometerstand, baujahr, farbe, ladegewicht, anzahlAchsen);
    }

    private static Mitarbeiter getMitarbeiter(Scanner scanner) {
        System.out.print("Geben Sie den Vornamen des Mitarbeiters ein: ");
        String vorname = scanner.nextLine();
        System.out.print("Geben Sie den Nachnamen des Mitarbeiters ein: ");
        String nachname = scanner.nextLine();
        System.out.print("Geben Sie das Geburtsdatum des Mitarbeiters ein (YYYY-MM-DD): ");
        LocalDate geburtsdatum = LocalDate.parse(scanner.nextLine());

        return new Mitarbeiter(vorname, nachname, geburtsdatum);
    }

    private static void zeigeMitarbeiter(Statement stmt) throws SQLException {
        System.out.println("--- Alle Mitarbeiter ---");
        String query = "SELECT * FROM Mitarbeiter"; // Angenommen, die Tabelle heißt "Mitarbeiter"
        ResultSet rs = stmt.executeQuery(query);

        // Wenn keine Mitarbeiter in der Tabelle sind, eine Nachricht anzeigen
        if (!rs.isBeforeFirst()) {
            System.out.println("Keine Mitarbeiter in der Tabelle.");
            return;
        }

        while (rs.next()) {
            int id = rs.getInt("mitarbeiter_id"); // Angenommen, die ID-Spalte heißt "mitarbeiter_id"
            String vorname = rs.getString("vorname");
            String nachname = rs.getString("nachname");
            Date geburtsdatum = rs.getDate("geburtsdatum");

            System.out.println("ID: " + id + ", Vorname: " + vorname + ", Nachname: " + nachname + ", Geburtsdatum: " + geburtsdatum);
        }
        System.out.println("-----------------------------");
    }

}
