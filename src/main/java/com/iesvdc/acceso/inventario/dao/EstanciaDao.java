package com.iesvdc.acceso.inventario.dao;

import java.util.List;

import com.iesvdc.acceso.inventario.modelo.Estancia;

public interface EstanciaDao {

    public boolean create(Estancia u);

    /**
     * Busca en la base de datos estancias por su identificador.
     * 
     * @param id número entero positivo
     * @return null si no está ese id, la Estancia en caso contrario.
     */
    public Estancia findById(int id);

    public List<Estancia> findAll();

    public Estancia findByNombre(String nombre);

    public boolean update(Estancia o, Estancia n);

    public boolean update(int id, Estancia n);

    public boolean delete(Estancia u);

    public boolean delete(int id);

    public int count();
}
