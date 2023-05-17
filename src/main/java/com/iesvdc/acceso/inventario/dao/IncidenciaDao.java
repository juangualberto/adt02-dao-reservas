package com.iesvdc.acceso.inventario.dao;

import java.util.List;

import com.iesvdc.acceso.inventario.modelo.Estancia;
import com.iesvdc.acceso.inventario.modelo.Incidencia;
import com.iesvdc.acceso.inventario.modelo.Usuario;

public interface IncidenciaDao {

    public boolean create(Incidencia i);

    public Incidencia findById(int id);

    public Incidencia findByNombre(String nombre);

    public Incidencia findByEstancia(Estancia e);

    public Incidencia findByOperario(Usuario o);

    public Incidencia findByUsuario(Usuario u);

    public boolean update(Incidencia o, Incidencia n);

    public boolean update(int id, Incidencia n);

    public boolean delete(Incidencia i);

    public boolean delete(int id);

    public List<Incidencia> findAll();

    public int count();
}
