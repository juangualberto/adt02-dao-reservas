package com.iesvdc.acceso.app.servicios;

import java.util.List;
import java.util.Optional;

import com.iesvdc.acceso.app.entidades.Cliente;

public interface ClienteDao {
    Cliente save(Cliente Cliente) throws DaoException;
    Optional<Cliente> findOne(long id) throws DaoException;
    Optional<Cliente> findByEmail(String email) throws DaoException;
    Optional<Cliente> findByPhone(String telefono) throws DaoException;
    List<Cliente> findAll() throws DaoException;
    void update(Cliente Cliente) throws DaoException;
    void delete(int id) throws DaoException;
    long count() throws DaoException;
}
