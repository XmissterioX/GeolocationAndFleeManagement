package com.example.missterio.stage.models;

public class Alerte {
    String  Date;
    String AlertType;
    String Adresse;
    String Vitesse;
    String Matricule;

    public Alerte(String date, String alertType, String adresse, String vitesse) {
        Date = date;
        AlertType = alertType;
        Adresse = adresse;
        Vitesse = vitesse;
    }

    public Alerte(String date, String alertType, String adresse, String vitesse, String matricule) {
        Date = date;
        AlertType = alertType;
        Adresse = adresse;
        Vitesse = vitesse;
        Matricule = matricule;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getAlertType() {
        return AlertType;
    }

    public void setAlertType(String alertType) {
        AlertType = alertType;
    }

    public String getAdresse() {
        return Adresse;
    }

    public void setAdresse(String adresse) {
        Adresse = adresse;
    }

    public String getVitesse() {
        return Vitesse;
    }

    public void setVitesse(String vitesse) {
        Vitesse = vitesse;
    }

    public String getMatricule() {
        return Matricule;
    }

    public void setMatricule(String matricule) {
        Matricule = matricule;
    }
}
