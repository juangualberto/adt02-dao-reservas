package com.iesvdc.acceso.inventario.modelo;

import java.util.Objects;

public class Inventario {
    private int id;
    private String nombre;
    private String descripcion;
    private Estancia estancia;

    public Inventario() {
    }

    public Inventario(int id, String nombre, String descripcion, Estancia estancia) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estancia = estancia;
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

    public Estancia getEstancia() {
        return this.estancia;
    }

    public void setEstancia(Estancia estancia) {
        this.estancia = estancia;
    }

    public Inventario id(int id) {
        setId(id);
        return this;
    }

    public Inventario nombre(String nombre) {
        setNombre(nombre);
        return this;
    }

    public Inventario descripcion(String descripcion) {
        setDescripcion(descripcion);
        return this;
    }

    public Inventario estancia(Estancia estancia) {
        setEstancia(estancia);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Inventario)) {
            return false;
        }
        Inventario inventario = (Inventario) o;
        return id == inventario.id && Objects.equals(nombre, inventario.nombre)
                && Objects.equals(descripcion, inventario.descripcion) && Objects.equals(estancia, inventario.estancia);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, descripcion, estancia);
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", nombre='" + getNombre() + "'" +
                ", descripcion='" + getDescripcion() + "'" +
                ", estancia='" + getEstancia() + "'" +
                "}";
    }

}
