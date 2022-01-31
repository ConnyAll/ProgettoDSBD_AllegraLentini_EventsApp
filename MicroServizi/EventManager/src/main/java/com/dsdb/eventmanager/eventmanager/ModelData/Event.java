package com.dsdb.eventmanager.eventmanager.ModelData;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idEvent;

    @NotBlank
    @Column(unique = true) //deve essere univoco
    private String codice;

    @NotBlank
    private String nomeEvento;

    @NotBlank
    private String descrizione;

    @NotNull
    private Integer postiMax;

    @NotNull
    private Integer postiDisp;

    @NotNull
    private Double prezzoBiglietto;

    public Event() {
    }

    public Event(String codice, String nomeEvento, String descrizione, Integer postiMax, Integer postiDisp, Double prezzoBiglietto) {
        this.codice = codice;
        this.nomeEvento = nomeEvento;
        this.descrizione = descrizione;
        this.postiMax = postiMax;
        this.postiDisp = postiDisp;
        this.prezzoBiglietto = prezzoBiglietto;
    }

    public Integer getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(Integer idEvent) {
        this.idEvent = idEvent;
    }

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public String getNomeEvento() {
        return nomeEvento;
    }

    public void setNomeEvento(String nomeEvento) {
        this.nomeEvento = nomeEvento;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Integer getPostiMax() {
        return postiMax;
    }

    public void setPostiMax(Integer postiMax) {
        this.postiMax = postiMax;
    }

    public Integer getPostiDisp() {
        return postiDisp;
    }

    public void setPostiDisp(Integer postiDisp) {
        this.postiDisp = postiDisp;
    }

    public Double getPrezzoBiglietto() {
        return prezzoBiglietto;
    }

    public void setPrezzoBiglietto(Double prezzoBiglietto) {
        this.prezzoBiglietto = prezzoBiglietto;
    }

    @Override
    public String toString() {
        return "Event{" +
                "idEvent=" + idEvent +
                ", codice='" + codice + '\'' +
                ", nomeEvento='" + nomeEvento + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", postiMax=" + postiMax +
                ", postiDisp=" + postiDisp +
                ", prezzoBiglietto=" + prezzoBiglietto +
                '}';
    }


}
