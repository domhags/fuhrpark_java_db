package Mitarbeiter;

import java.time.LocalDate;

public class Mitarbeiter {
    private String vorname;
    private String nachname;
    private LocalDate geburtsdatum;

    // Konstruktor
    public Mitarbeiter(String vorname, String nachname, LocalDate geburtsdatum) {
        setVorname(vorname);
        setNachname(nachname);
        setGeburtsdatum(geburtsdatum);
    }

    // Getter für Vorname
    public String getVorname() {
        return vorname;
    }

    // Setter für Vorname
    private void setVorname(String vorname) {
        if (vorname == null || vorname.trim().isEmpty()) {
            throw new IllegalArgumentException("Fehler: Der Vorname darf nicht leer sein.");
        }
        this.vorname = vorname;
    }

    // Getter für Nachname
    public String getNachname() {
        return nachname;
    }

    // Setter für Nachname
    private void setNachname(String nachname) {
        if (nachname == null || nachname.trim().isEmpty()) {
            throw new IllegalArgumentException("Fehler: Der Nachname darf nicht leer sein.");
        }
        this.nachname = nachname;
    }

    // Getter für das Geburtsdatum
    public LocalDate getGeburtsdatum() {
        return geburtsdatum;
    }

    // Setter für das Geburtsdatum
    public void setGeburtsdatum(LocalDate geburtsdatum) {
        if (geburtsdatum.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Fehler: Das Geburtsdatum darf nicht in der Zukunft liegen.");
        }
        this.geburtsdatum = geburtsdatum;
    }

    @Override
    public String toString() {
        return "Mitarbeiter.Mitarbeiter{" +
                "vorname='" + getVorname() + '\'' +
                ", nachname='" + getNachname() + '\'' +
                ", geburtsdatum=" + getGeburtsdatum() +
                '}';
    }
}
