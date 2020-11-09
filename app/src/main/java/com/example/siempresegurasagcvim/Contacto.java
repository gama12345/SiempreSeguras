package com.example.siempresegurasagcvim;

public class Contacto {
    String nombre, telefono;
    boolean seleccionado;

    public Contacto() {
    }

    public Contacto(String nombre, String telefono, boolean seleccionado) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.seleccionado = seleccionado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }
}
