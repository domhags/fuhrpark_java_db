package Datenbank;

import Fahrzeug.*;

import java.sql.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class FahrzeugDAO {
    private static final Logger logger = Logger.getLogger(FahrzeugDAO.class.getName());

    // Zeigt alle Fahrzeuge aus verschiedenen Tabellen an
    public static void zeigeFahrzeuge(Statement stmt) throws SQLException {
        System.out.println("--- Alle Fahrzeuge ---");

        zeigeFahrzeugeAusTabelle(stmt, "Fahrrad");
        zeigeFahrzeugeAusTabelle(stmt, "Motorrad");
        zeigeFahrzeugeAusTabelle(stmt, "PKW");
        zeigeFahrzeugeAusTabelle(stmt, "LKW");
    }

    // Holt und zeigt Fahrzeugdetails aus der angegebenen Tabelle
    private static void zeigeFahrzeugeAusTabelle(Statement stmt, String tableName) throws SQLException {
        String query = "SELECT * FROM " + tableName;
        ResultSet rs = stmt.executeQuery(query);

        // Wenn keine Fahrzeuge in der Tabelle sind, eine Nachricht anzeigen
        if (!rs.isBeforeFirst()) {
            System.out.println("Keine Fahrzeuge in der Tabelle " + tableName);
            return;
        }

        //Kopfzeilen
        if (tableName.equals("Fahrrad")) {
            System.out.format("%-5s %-5s %-15s %-10s %-10s %-15s %-15s\n", "ID", "Typ", "Kilometerstand", "Baujahr", "Farbe", "Art", "Anzahl Gänge");
            System.out.println("------------------------------------------------------------------------------------------------");
        } else if (tableName.equals("Motorrad")) {
            System.out.format("%-5s %-5s %-15s %-10s %-10s %-10s %-15s\n", "ID", "Typ", "Kilometerstand", "Baujahr", "Farbe", "Hubraum", "Helmhalterungen");
            System.out.println("------------------------------------------------------------------------------------------------");
        } else if (tableName.equals("PKW")) {
            System.out.format("%-5s %-5s %-15s %-10s %-10s %-15s %-15s\n", "ID", "Typ", "Kilometerstand", "Baujahr", "Farbe", "Sitze", "Kofferraumvolumen");
            System.out.println("------------------------------------------------------------------------------------------------");
        } else if (tableName.equals("LKW")) {
            System.out.format("%-5s %-5s %-15s %-10s %-10s %-15s %-15s\n", "ID", "Typ", "Kilometerstand", "Baujahr", "Farbe", "Ladegewicht", "Anzahl Achsen");
            System.out.println("------------------------------------------------------------------------------------------------");
        }

        while (rs.next()) {
            // Zeige spezifische Details für jeden Fahrzeugtyp
            switch (tableName) {
                case "Fahrrad":
                    zeigeFahrradDetails(rs);
                    break;
                case "Motorrad":
                    zeigeMotorradDetails(rs);
                    break;
                case "PKW":
                    zeigePKWDetails(rs);
                    break;
                case "LKW":
                    zeigeLKWDetails(rs);
                    break;
            }

            System.out.println("-----------------------------");
        }
    }

    // Fügt ein neues Fahrzeug basierend auf dem Typ zur Datenbank hinzu
    public static void fahrzeugHinzufuegen(Scanner scanner, Connection connection) {
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

            // Setzt die Standard-Werte des Fahrzeugs
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

                pstmt.executeUpdate(); // Führt das SQL-Statement aus
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1); // Holt die generierte ID vom Fahrzeug
                    System.out.println("Fahrzeug erfolgreich hinzugefügt mit ID: " + id);
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Fehler beim Hinzufügen des Fahrzeugs", e);
            }
        }
    }

    // Fragt nach Benutzereingaben für Fahrzeugdetails
    public static Fahrzeug getFahrzeug(Scanner scanner) {
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

    // Erstellt ein Fahrrad und sammelt Details vom Benutzer
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

    // Erstellt ein Motorrad und sammelt Details vom Benutzer
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

    // Erstellt ein PKW und sammelt Details vom Benutzer
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

    // Erstellt ein LKW und sammelt Details vom Benutzer
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


    private static void zeigeFahrradDetails(ResultSet rs) throws SQLException {
        // Liest die Fahrrad-Daten aus dem ResultSet
        int id = rs.getInt("fahrrad_id");
        int kilometerstand = rs.getInt("kilometerstand");
        int baujahr = rs.getInt("baujahr");
        String farbe = rs.getString("farbe");
        String art = rs.getString("art");
        int anzahlGaenge = rs.getInt("anzahl_gaenge");

        // Gibt die Fahrrad-Daten formatiert aus
        System.out.format("%-5d %-8s %-15d %-10d %-10s %-15s %-10d\n", id, "Fahrrad", kilometerstand, baujahr, farbe, art, anzahlGaenge);
    }

    private static void zeigeMotorradDetails(ResultSet rs) throws SQLException {
        // Liest die Motorrad-Daten aus dem ResultSet
        int id = rs.getInt("motorrad_id");
        int kilometerstand = rs.getInt("kilometerstand");
        int baujahr = rs.getInt("baujahr");
        String farbe = rs.getString("farbe");
        int hubraum = rs.getInt("hubraum");
        int anzahlHelmhalterungen = rs.getInt("anzahl_helmhalterungen");

        // Gibt die Motorrad-Daten formatiert aus
        System.out.format("%-5d %-8s %-15d %-10d %-10s %-10d %-10d\n", id, "Motorrad", kilometerstand, baujahr, farbe, hubraum, anzahlHelmhalterungen);
    }

    private static void zeigePKWDetails(ResultSet rs) throws SQLException {
        // Liest die PKW-Daten aus dem ResultSet
        int id = rs.getInt("pkw_id");
        int kilometerstand = rs.getInt("kilometerstand");
        int baujahr = rs.getInt("baujahr");
        String farbe = rs.getString("farbe");
        int anzahlSitze = rs.getInt("anzahl_sitze");
        int kofferraumvolumen = rs.getInt("kofferraumvolumen");

        // Gibt die PKW-Daten formatiert aus
        System.out.format("%-5d %-8s %-15d %-10d %-10s %-10d %-15d\n", id, "PKW", kilometerstand, baujahr, farbe, anzahlSitze, kofferraumvolumen);
    }

    private static void zeigeLKWDetails(ResultSet rs) throws SQLException {
        // Liest die LKW-Daten aus dem ResultSet
        int id = rs.getInt("lkw_id");
        int kilometerstand = rs.getInt("kilometerstand");
        int baujahr = rs.getInt("baujahr");
        String farbe = rs.getString("farbe");
        float ladegewicht = rs.getFloat("ladegewicht");
        int anzahlAchsen = rs.getInt("anzahl_achsen");

        // Gibt die LKW-Daten formatiert aus
        System.out.format("%-5d %-8s %-15d %-10d %-10s %-15.2f %-10d\n", id, "LKW", kilometerstand, baujahr, farbe, ladegewicht, anzahlAchsen);
    }
}
