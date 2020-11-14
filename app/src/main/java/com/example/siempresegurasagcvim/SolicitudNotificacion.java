package com.example.siempresegurasagcvim;

public class SolicitudNotificacion {
    String token;

    public SolicitudNotificacion() {
    }

    public SolicitudNotificacion(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
