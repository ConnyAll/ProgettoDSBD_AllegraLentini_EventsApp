package com.dsdb.eventsapp.usermanager.ModelData;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idUser;

    //@NotNull(message='the username cannot be black')
    @NotBlank
    @Column(unique = true) //deve essere univoco
    private String userName;

    @NotBlank
    @Column(unique = true) //deve essere univoco
    private String email;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(unique = true) //deve essere univoco
    private String codice;

    @NotNull
    private Integer NprenotazioniFatte=0;



    public User() {}

    public User(Integer idUser, String userName, String email, String password, String codice, Integer nprenotazioniFatte) {
        this.idUser = idUser;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.codice = codice;
        NprenotazioniFatte = nprenotazioniFatte;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer id) {
        this.idUser = idUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public Integer getNprenotazioniFatte() {
        return NprenotazioniFatte;
    }

    public void setNprenotazioniFatte(Integer nprenotazioniFatte) {
        NprenotazioniFatte = nprenotazioniFatte;
    }

    @Override
    public String toString() {
        return "User{" +
                "idUser=" + idUser +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", codice='" + codice + '\'' +
                ", NprenotazioniFatte=" + NprenotazioniFatte +
                '}';
    }
}
