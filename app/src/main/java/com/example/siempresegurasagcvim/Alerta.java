package com.example.siempresegurasagcvim;

public class Alerta {
    String contacto, imagen, mensaje, usuaria;

    public Alerta() {
    }

    public Alerta(String contacto, String imagen, String mensaje, String usuaria) {
        this.contacto = contacto;
        this.imagen = imagen;
        this.mensaje = mensaje;
        this.usuaria = usuaria;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getUsuaria() {
        return usuaria;
    }

    public void setUsuaria(String usuaria) {
        this.usuaria = usuaria;
    }
}
