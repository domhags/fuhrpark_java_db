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

    public String getVorname() {
        return vorname;
    }

    private void setVorname(String vorname) {
        if (vorname == null || vorname.trim().isEmpty()) {
            throw new IllegalArgumentException("Fehler: Der Vorname darf nicht leer sein.");
        }
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    private void setNachname(String nachname) {
        if (nachname == null || nachname.trim().isEmpty()) {
            throw new IllegalArgumentException("Fehler: Der Nachname darf nicht leer sein.");
        }
        this.nachname = nachname;
    }

    public LocalDate getGeburtsdatum() {
        return geburtsdatum;
    }

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

    public String getInsertSQLBefehl() {
        return "INSERT INTO Mitarbeiter (vorname, nachname, geburtsdatum) VALUES (?, ?, ?)";
    }
}
