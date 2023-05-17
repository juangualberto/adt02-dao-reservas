package com.iesvdc.acceso.inventario.dao;

import java.util.List;

import com.iesvdc.acceso.inventario.modelo.Estancia;
import com.iesvdc.acceso.inventario.modelo.Inventario;

public interface InventarioDao {

    public boolean create(Inventario i);

    public Inventario findById(int id);

    public Inventario findByNombre(String nombre);

    public Inventario findByEstancia(Estancia e);

    public boolean update(Inventario o, Inventario n);

    public boolean update(int id, Inventario n);

    public boolean delete(Inventario i);

    public boolean delete(int id);

    public List<Inventario> findAll();

    public int count();
}
