package com.iesvdc.acceso.app.servicios;

import java.util.List;
import java.util.Optional;

import com.iesvdc.acceso.app.entidades.Alojamiento;
import com.iesvdc.acceso.app.entidades.Cliente;
import com.iesvdc.acceso.app.entidades.Reserva;

public interface ReservaDao {
    Reserva save(Reserva reserva) throws DaoException;
    Optional<Reserva> findOne(long id) throws DaoException;
    List<Reserva> findAll() throws DaoException;
    List<Reserva> findByCliente(Cliente c) throws DaoException;
    List<Reserva> findByAlojamiento(Alojamiento a) throws DaoException;
    void update(Reserva reserva) throws DaoException;
    void delete(int id) throws DaoException;
    long count() throws DaoException;
}

