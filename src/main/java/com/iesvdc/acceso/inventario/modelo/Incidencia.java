package com.iesvdc.acceso.inventario.modelo;

import java.util.Objects;

public class Incidencia {

    private int id;
    private String asunto;
    private String descripcion;
    Inventario inventario;
    Usuario usuario;
    Usuario operario;

    public Incidencia() {
    }

    public Incidencia(int id, String asunto, String descripcion, Inventario inventario, Usuario usuario,
            Usuario operario) {
        this.id = id;
        this.asunto = asunto;
        this.descripcion = descripcion;
        this.inventario = inventario;
        this.usuario = usuario;
        this.operario = operario;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAsunto() {
        return this.asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Inventario getInventario() {
        return this.inventario;
    }

    public void setInventario(Inventario inventario) {
        this.inventario = inventario;
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getOperario() {
        return this.operario;
    }

    public void setOperario(Usuario operario) {
        this.operario = operario;
    }

    public Incidencia id(int id) {
        setId(id);
        return this;
    }

    public Incidencia asunto(String asunto) {
        setAsunto(asunto);
        return this;
    }

    public Incidencia descripcion(String descripcion) {
        setDescripcion(descripcion);
        return this;
    }

    public Incidencia inventario(Inventario inventario) {
        setInventario(inventario);
        return this;
    }

    public Incidencia usuario(Usuario usuario) {
        setUsuario(usuario);
        return this;
    }

    public Incidencia operario(Usuario operario) {
        setOperario(operario);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Incidencia)) {
            return false;
        }
        Incidencia incidencia = (Incidencia) o;
        return id == incidencia.id && Objects.equals(asunto, incidencia.asunto)
                && Objects.equals(descripcion, incidencia.descripcion)
                && Objects.equals(inventario, incidencia.inventario) && Objects.equals(usuario, incidencia.usuario)
                && Objects.equals(operario, incidencia.operario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, asunto, descripcion, inventario, usuario, operario);
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", asunto='" + getAsunto() + "'" +
                ", descripcion='" + getDescripcion() + "'" +
                ", inventario='" + getInventario() + "'" +
                ", usuario='" + getUsuario() + "'" +
                ", operario='" + getOperario() + "'" +
                "}";
    }

}
