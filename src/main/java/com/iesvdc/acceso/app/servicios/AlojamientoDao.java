package com.iesvdc.acceso.app.servicios;

import java.util.List;
import java.util.Optional;

import com.iesvdc.acceso.app.entidades.Alojamiento;

public interface AlojamientoDao {
    Alojamiento save(Alojamiento Alojamiento) throws DaoException;
    Optional<Alojamiento> findOne(long id) throws DaoException;
    List<Alojamiento> findAll() throws DaoException;
    void update(Alojamiento Alojamiento) throws DaoException;
    void delete(int id) throws DaoException;
    long count() throws DaoException;
}
