package Fahrzeug;

public class LKW extends Fahrzeug {
    private float ladegewicht;
    private int anzahlAchsen;

    // Konstruktor
    public LKW(int kilometerstand, int baujahr, String farbe, float ladegewicht, int anzahlAchsen) {
        super(kilometerstand, baujahr, farbe);
        setLadegewicht(ladegewicht); // Ladegewicht setzen
        setAnzahlAchsen(anzahlAchsen); // Achsen setzen
    }

    // Getter für Ladegewicht
    public float getLadegewicht() {
        return ladegewicht;
    }

    // Setter für Ladegewicht
    public void setLadegewicht(float ladegewicht) {
        if (ladegewicht <= 0) {
            throw new IllegalArgumentException("Fehler: Das Ladegewicht muss positiv sein.");
        }
        this.ladegewicht = ladegewicht;
    }

    // Getter für Achsen
    public int getAnzahlAchsen() {
        return anzahlAchsen;
    }

    // Setter für Achsen
    public void setAnzahlAchsen(int anzahlAchsen) {
        if (anzahlAchsen < 2) {
            throw new IllegalArgumentException("Fehler: Ein Lkw muss mindestens 2 Achsen haben.");
        }
        this.anzahlAchsen = anzahlAchsen;
    }

    @Override
    public String toString() {
        return "LKW{" +
                "ladegewicht=" + getLadegewicht() +
                ", anzahlAchsen=" + getAnzahlAchsen() +
                "} " + super.toString();
    }

    @Override
    public String getInsertSQLBefehl() {
        return "INSERT INTO fahrzeug (kilometerstand, baujahr, farbe) VALUES (" +
                getKilometerstand() + ", " + getBaujahr() + ", '" + getFarbe() + "')";
    }

    @Override
    public String getSpezifischenInsertSQLBefehl(int fahrzeugID) {
        return "INSERT INTO lkw (id, ladegewicht, anzahl_achsen) VALUES (" +
                fahrzeugID + ", " + getLadegewicht() + ", " + getAnzahlAchsen() + ")";
    }
}
