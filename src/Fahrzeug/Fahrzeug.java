package Fahrzeug;

public abstract class Fahrzeug {
    private int kilometerstand;
    private int baujahr;
    private String farbe;

    // Konstruktor
    protected Fahrzeug(int kilometerstand, int baujahr, String farbe) {
        setKilometerstand(kilometerstand);
        setBaujahr(baujahr);
        setFarbe(farbe);
    }

    // Getter für Kilometerstand
    public int getKilometerstand() {
        return kilometerstand;
    }

    // Setter für Kilometerstand
    public void setKilometerstand(int kilometerstand) {
        if (kilometerstand < 0) {
            throw new IllegalArgumentException("Fehler: Der Kilometerstand darf nicht negativ sein.");
        } else {
            this.kilometerstand = kilometerstand;
        }
    }

    // Getter für Baujahr
    public int getBaujahr() {
        return baujahr;
    }

    // Setter für Baujahr
    public void setBaujahr(int baujahr) {
        if (baujahr < 1885) {
            throw new IllegalArgumentException("Fehler: Das Baujahr darf nicht vor 1885 liegen.");
        } else {
            this.baujahr = baujahr;
        }
    }

    // Getter für Farbe
    public String getFarbe() {
        return farbe;
    }

    // Setter für Farbe
    public void setFarbe(String farbe) {
        if (farbe == null || farbe.trim().isEmpty()) {
            throw new IllegalArgumentException("Fehler: Die Farbe darf nicht leer sein.");
        } else {
            this.farbe = farbe;
        }
    }

    @Override
    public String toString() {
        return "Fahrzeug{" +
                "kilometerstand=" + getKilometerstand() +
                ", baujahr=" + getBaujahr() +
                ", farbe='" + getFarbe() + '\'' +
                '}';
    }

}
