package com.iesvdc.acceso.inventario.modelo;

import java.util.Objects;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="usuario")
public class Usuario {
    int id;
    String username;
    String password;
    String tipo;
    String email;

    public Usuario() {
    }

    public Usuario(int id, String username, String password, String tipo, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.tipo = tipo;
        this.email = email;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTipo() {
        return this.tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Usuario id(int id) {
        setId(id);
        return this;
    }

    public Usuario username(String username) {
        setUsername(username);
        return this;
    }

    public Usuario password(String password) {
        setPassword(password);
        return this;
    }

    public Usuario tipo(String tipo) {
        setTipo(tipo);
        return this;
    }

    public Usuario email(String email) {
        setEmail(email);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Usuario)) {
            return false;
        }
        Usuario usuario = (Usuario) o;
        return id == usuario.id && Objects.equals(username, usuario.username)
                && Objects.equals(password, usuario.password) && Objects.equals(tipo, usuario.tipo)
                && Objects.equals(email, usuario.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, tipo, email);
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", username='" + getUsername() + "'" +
                ", password='" + getPassword() + "'" +
                ", tipo='" + getTipo() + "'" +
                ", email='" + getEmail() + "'" +
                "}";
    }

}
