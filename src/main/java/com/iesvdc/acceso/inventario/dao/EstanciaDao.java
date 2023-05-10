package com.iesvdc.acceso.inventario.dao;

import java.util.List;

import com.iesvdc.acceso.inventario.modelo.Estancia;

public interface EstanciaDao {

    public boolean create(Estancia u);

    public Estancia findById(int id);

    public Estancia findByNombre(String nombre);

    public boolean update(Estancia o, Estancia n);

    public boolean update(int id, Estancia n);

    public boolean delete(Estancia u);

    public boolean delete(int id);

    public List<Estancia> findAll();

    public int count();
}
