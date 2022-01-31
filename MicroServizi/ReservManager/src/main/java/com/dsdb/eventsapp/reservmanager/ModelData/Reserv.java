package com.dsdb.eventsapp.reservmanager.ModelData;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Reserv {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idPrenotazione;

    //@NotNull(message='the username cannot be black')
    @NotNull
    private Integer idEvent;

    @NotNull
    private Integer idUser;

    @NotNull
    private Integer Nbiglietti;



    public Reserv() {

    }

    public Reserv(Integer idEvent, Integer idUser, Integer nbiglietti, String status) {
        this.idEvent = idEvent;
        this.idUser = idUser;
        Nbiglietti = nbiglietti;
    }

    public Integer getIdPrenotazione() {
        return idPrenotazione;
    }

    public void setIdPrenotazione(Integer idPrenotazione) {
        this.idPrenotazione = idPrenotazione;
    }

    public Integer getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(Integer idEvent) {
        this.idEvent = idEvent;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public Integer getNbiglietti() {
        return Nbiglietti;
    }

    public void setNbiglietti(Integer nbiglietti) {
        Nbiglietti = nbiglietti;
    }



    @Override
    public String toString() {
        return "Reserv{" +
                "idPrenotazione=" + idPrenotazione +
                ", idEvent=" + idEvent +
                ", idUser=" + idUser +
                ", Nbiglietti=" + Nbiglietti +
                '}';
    }
}
