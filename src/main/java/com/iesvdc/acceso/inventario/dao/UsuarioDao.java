package com.iesvdc.acceso.inventario.dao;

import java.util.List;

import com.iesvdc.acceso.inventario.modelo.Usuario;

public interface UsuarioDao {
    
    public boolean create(Usuario u);
    public Usuario findById(int id);
    public Usuario findByUsername(String username);
    public Usuario findByUsernameAndPassword(String username, String password);
    public boolean update(Usuario o, Usuario n);
    public boolean update(int id, Usuario n);
    public boolean delete(Usuario u);
    public boolean delete(int id);
    /**
     * Devuelve la lista de todos los usuarios de la base de datos
     */
    public List<Usuario> findAll();
    public int count();
}
