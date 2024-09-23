package Fahrzeug;

public class Fahrrad extends Fahrzeug {
    private String art;
    private int anzahlGaenge;

    // Konstruktor
    public Fahrrad(int kilometerstand, int baujahr, String farbe, String art, int anzahlGaenge) {
        super(kilometerstand, baujahr, farbe);
        setArt(art.trim());
        setAnzahlGaenge(anzahlGaenge);
    }

    // Getter für Art des Fahrrads
    public String getArt() {
        return art;
    }

    // Setter für Art des Fahrrads
    public void setArt(String art) {
        if (art == null || art.trim().isEmpty()) {
            throw new IllegalArgumentException("Fehler: Art vom Fahrzeug.Fahrzeug.Fahrrad darf nicht leer sein.");
        }
        this.art = art;
    }

    // Getter für Anzahl der Gänge
    public int getAnzahlGaenge() {
        return anzahlGaenge;
    }

    // Setter für Anzahl der Gänge
    public void setAnzahlGaenge(int anzahlGaenge) {
        if (anzahlGaenge < 0) {
            throw new IllegalArgumentException("Fehler: Die Anzahl der Gänge muss positiv sein.");
        } else {
            this.anzahlGaenge = anzahlGaenge;
        }
    }

    @Override
    public String toString() {
        return "Fahrrad [Kilometerstand=" + getKilometerstand() + ", Baujahr=" + getBaujahr() + ", Farbe=" + getFarbe() +
                ", Art=" + getArt() + ", Anzahl der Gänge=" + getAnzahlGaenge() + "]";
    }

    @Override
    public String getInsertSQLBefehl() {
        return "INSERT INTO fahrzeug (kilometerstand, baujahr, farbe) VALUES (" +
                getKilometerstand() + ", " + getBaujahr() + ", '" + getFarbe() + "')";
    }

    @Override
    public String getSpezifischenInsertSQLBefehl(int fahrzeugID) {
        return "INSERT INTO fahrrad (id, art, anzahl_gaenge) VALUES (" +
                fahrzeugID + ", '" + getArt() + "', " + getAnzahlGaenge() + ")";
    }
}
