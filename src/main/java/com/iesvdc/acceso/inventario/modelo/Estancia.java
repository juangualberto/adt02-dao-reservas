package com.iesvdc.acceso.inventario.modelo;

import java.util.Objects;

public class Estancia {

    private int id;
    private String nombre;
    private String descripcion;

    public Estancia() {
    }

    public Estancia(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Estancia id(int id) {
        setId(id);
        return this;
    }

    public Estancia nombre(String nombre) {
        setNombre(nombre);
        return this;
    }

    public Estancia descripcion(String descripcion) {
        setDescripcion(descripcion);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Estancia)) {
            return false;
        }
        Estancia estancia = (Estancia) o;
        return id == estancia.id && Objects.equals(nombre, estancia.nombre)
                && Objects.equals(descripcion, estancia.descripcion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, descripcion);
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", nombre='" + getNombre() + "'" +
                ", descripcion='" + getDescripcion() + "'" +
                "}";
    }

}
