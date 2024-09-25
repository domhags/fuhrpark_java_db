package Fahrzeug;

public class Motorrad extends Fahrzeug {
    private int hubraum;
    private int anzahlHelmhalterungen;

    // Konstruktor
    public Motorrad(int kilometerstand, int baujahr, String farbe, int hubraum, int anzahlHelmhalterungen) {
        super(kilometerstand, baujahr, farbe);
        setHubraum(hubraum);
        setAnzahlHelmhalterungen(anzahlHelmhalterungen);
    }

    // Getter für Hubraum
    public int getHubraum() {
        return hubraum;
    }

    // Setter für Hubraum
    public void setHubraum(int hubraum) {
        if (hubraum < 0) {
            throw new IllegalArgumentException("Fehler: Der Hubraum darf nicht negativ sein.");
        }
        this.hubraum = hubraum;
    }

    // Getter für Anzahl der Helmhalterungen
    public int getAnzahlHelmhalterungen() {
        return anzahlHelmhalterungen;
    }

    // Setter für Anzahl der Helmhalterungen
    public void setAnzahlHelmhalterungen(int anzahlHelmhalterungen) {
        if (anzahlHelmhalterungen <= 0) {
            throw new IllegalArgumentException("Fehler: Die Anzahl der Helmhalterungen muss positiv sein.");
        }
        this.anzahlHelmhalterungen = anzahlHelmhalterungen;
    }

    @Override
    public String toString() {
        return "Motorrad{" +
                "hubraum=" + getHubraum() +
                ", anzahlHelmhalterungen=" + getAnzahlHelmhalterungen() +
                "} " + super.toString();
    }

}
