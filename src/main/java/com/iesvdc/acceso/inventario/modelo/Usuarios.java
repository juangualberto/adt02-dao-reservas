package com.iesvdc.acceso.inventario.modelo;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Usuarios {

    private List<Usuario> listaUsuarios;

    public Usuarios() {
        this.listaUsuarios = new ArrayList<Usuario>();
    }

    public Usuarios(List<Usuario> usuarios) {
        this.listaUsuarios = usuarios;
    }

    public void add(Usuario u) {
        this.listaUsuarios.add(u);
    }

    public List<Usuario> getUsuarios() {
        return this.listaUsuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.listaUsuarios = usuarios;
    }

    public Usuarios usuarios(List<Usuario> usuarios) {
        setUsuarios(usuarios);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Usuarios)) {
            return false;
        }
        Usuarios usuarios = (Usuarios) o;
        return Objects.equals(usuarios, usuarios.listaUsuarios);
    }

    @Override
    public String toString() {
        return "{" +
                " usuarios='" + getUsuarios() + "'" +
                "}";
    }

}
