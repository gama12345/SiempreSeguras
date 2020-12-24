package com.example.siempresegurasagcvim;

public class Alerta {
    String contacto, imagen, mensaje, usuaria, estado;

    public Alerta() {
    }

    public Alerta(String contacto, String imagen, String mensaje, String usuaria, String estado) {
        this.contacto = contacto;
        this.imagen = imagen;
        this.mensaje = mensaje;
        this.usuaria = usuaria;
        this.estado = estado;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
