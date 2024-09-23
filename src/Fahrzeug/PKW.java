package Fahrzeug;

public class PKW extends Fahrzeug {
    private int sitze;
    private int kofferraumvolumen;

    // Konstruktor
    public PKW(int kilometerstand, int baujahr, String farbe, int sitze, int kofferraumvolumen) {
        super(kilometerstand, baujahr, farbe);
        setSitze(sitze);
        setKofferraumvolumen(kofferraumvolumen);
    }

    // Getter für Sitze
    public int getSitze() {
        return sitze;
    }

    // Setter für Sitze
    public void setSitze(int sitze) {
        if (sitze < 2) {
            throw new IllegalArgumentException("Fehler: Ein Pkw muss mindestens 2 Sitze besitzen.");
        }
        this.sitze = sitze;
    }

    // Getter für Kofferraumvolumen
    public int getKofferraumvolumen() {
        return kofferraumvolumen;
    }

    // Setter für Kofferraumvolumen
    public void setKofferraumvolumen(int kofferraumvolumen) {
        if (kofferraumvolumen <= 0) {
            throw new IllegalArgumentException("Fehler: Das Kofferraumvolumen muss positiv sein.");
        }
        this.kofferraumvolumen = kofferraumvolumen;
    }

    @Override
    public String toString() {
        return "PKW [Kilometerstand=" + getKilometerstand() + ", Baujahr=" + getBaujahr() + ", Farbe=" + getFarbe() +
                ", Sitze=" + getSitze() + ", Kofferraumvolumen=" + getKofferraumvolumen() + "]";
    }

    @Override
    public String getInsertSQLBefehl() {
        return "INSERT INTO fahrzeug (kilometerstand, baujahr, farbe) VALUES (" +
                getKilometerstand() + ", " + getBaujahr() + ", '" + getFarbe() + "')";
    }

    @Override
    public String getSpezifischenInsertSQLBefehl(int fahrzeugID) {
        return "INSERT INTO pkw (id, anzahl_sitze, kofferraumvolumen) VALUES (" +
                fahrzeugID + ", " + getSitze() + ", " + getKofferraumvolumen() + ")";
    }
}
