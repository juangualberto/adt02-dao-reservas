package com.iesvdc.acceso.app.serviciosimpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.iesvdc.acceso.app.conexion.FactoriaConexion;
import com.iesvdc.acceso.app.entidades.Alojamiento;
import com.iesvdc.acceso.app.entidades.Cliente;
import com.iesvdc.acceso.app.entidades.Reserva;
import com.iesvdc.acceso.app.servicios.AlojamientoDao;
import com.iesvdc.acceso.app.servicios.ClienteDao;
import com.iesvdc.acceso.app.servicios.DaoException;
import com.iesvdc.acceso.app.servicios.ReservaDao;

public class ReservaDaoImpl implements ReservaDao{

    private static final String SAVE_QUERY = "INSERT INTO reserva (cliente, alojamiento, entrada, salida, pension) VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_ONE_QUERY = "SELECT * FROM reserva WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM reserva";
    private static final String FIND_BY_CLIENTE_QUERY = "SELECT * FROM reserva WHERE cliente_id = ?";
    private static final String FIND_BY_ALOJAMIENTO_QUERY = "SELECT * FROM reserva WHERE alojamiento_id = ?";
    private static final String UPDATE_QUERY = "UPDATE reserva SET cliente = ?, alojamiento = ?, entrada = ?, salida = ?, pension = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM reserva WHERE id = ?";
    private static final String COUNT_QUERY = "SELECT COUNT(*) FROM reserva";

    @Override
    public Reserva save(Reserva reserva) throws DaoException {
        try (Connection connection = FactoriaConexion.getConnection();
            PreparedStatement statement = connection.prepareStatement(SAVE_QUERY, Statement.RETURN_GENERATED_KEYS)) {

            // para la ingesta modificamos que si no existe un cliente o alojamiento se crean automágicamente
            ClienteDao cDao = new ClienteDaoImpl();
            Optional<Cliente> oc = cDao.findOne(reserva.getCliente().getId());
            if (!oc.isPresent()) {
                cDao.save(reserva.getCliente());
            }   
            AlojamientoDao aDao = new AlojamientoDaoImpl();
            Optional<Alojamiento> oa = aDao.findOne(reserva.getAlojamiento().getId());
            if (!oa.isPresent()){
                aDao.save(reserva.getAlojamiento());
            }
            
            statement.setLong(1, reserva.getCliente().getId());
            statement.setLong(2, reserva.getAlojamiento().getId());
            statement.setDate(3, new java.sql.Date(reserva.getEntrada().getTime()));
            statement.setDate(4, new java.sql.Date(reserva.getSalida().getTime()));
            statement.setString(5, reserva.getPension());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new DaoException("La inserción no tuvo éxito, no se creó ninguna reserva.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reserva.setId(generatedKeys.getLong(1));
                } else {
                    throw new DaoException("No se obtuvo el ID generado para la reserva.");
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al guardar la reserva. \n" + e.getMessage());
        } catch (DaoException de) {
            throw new DaoException("Error de integridad de datos al guardar la reserva. \n" + de.getMessage());
        }

        return reserva;
    }

    @Override
    public Optional<Reserva> findOne(long id) throws DaoException {
        try (Connection connection = FactoriaConexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ONE_QUERY)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToReserva(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al obtener la reserva por ID.", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Reserva> findAll() throws DaoException {
        List<Reserva> reservas = new ArrayList<>();

        try (Connection connection = FactoriaConexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_QUERY);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                reservas.add(mapResultSetToReserva(resultSet));
            }

        } catch (SQLException e) {
            throw new DaoException("Error al obtener todas las reservas.", e);
        }

        return reservas;
    }

    @Override
    public List<Reserva> findByCliente(Cliente cliente) throws DaoException {
        List<Reserva> reservas = new ArrayList<>();

        try (Connection connection = FactoriaConexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_CLIENTE_QUERY)) {
            statement.setLong(1, cliente.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reservas.add(mapResultSetToReserva(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al obtener las reservas por cliente.", e);
        }

        return reservas;
    }

    @Override
    public List<Reserva> findByAlojamiento(Alojamiento alojamiento) throws DaoException {
        List<Reserva> reservas = new ArrayList<>();

        try (Connection connection = FactoriaConexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ALOJAMIENTO_QUERY)) {
            statement.setLong(1, alojamiento.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reservas.add(mapResultSetToReserva(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al obtener las reservas por alojamiento.", e);
        }

        return reservas;
    }

    @Override
    public void update(Reserva reserva) throws DaoException {
        try (Connection connection = FactoriaConexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {
            statement.setLong(1, reserva.getCliente().getId());
            statement.setLong(2, reserva.getAlojamiento().getId());
            statement.setDate(3, new java.sql.Date(reserva.getEntrada().getTime()));
            statement.setDate(4, new java.sql.Date(reserva.getSalida().getTime()));
            statement.setString(5, reserva.getPension());
            statement.setLong(6, reserva.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new DaoException("La actualización no tuvo éxito, no se encontró la reserva.");
            }

        } catch (SQLException e) {
            throw new DaoException("Error al actualizar la reserva.", e);
        }
    }

    @Override
    public void delete(int id) throws DaoException {
        try (Connection connection = FactoriaConexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_QUERY)) {
            statement.setInt(1, id);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new DaoException("La eliminación no tuvo éxito, no se encontró la reserva.");
            }

        } catch (SQLException e) {
            throw new DaoException("Error al eliminar la reserva.", e);
        }
    }

    @Override
    public long count() throws DaoException {
        try (Connection connection = FactoriaConexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_QUERY);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getLong(1);
            }

        } catch (SQLException e) {
            throw new DaoException("Error al contar las reservas.", e);
        }

        return 0;
    }

    // hacemos el mapeo relacion-objeto
    private Reserva mapResultSetToReserva(ResultSet resultSet) throws SQLException {
        Reserva reserva = new Reserva();
        reserva.setId(resultSet.getLong("id"));
        ClienteDao clienteDao = new ClienteDaoImpl();
        reserva.setCliente(clienteDao.findOne(resultSet.getInt("cliente")).get()); 
        AlojamientoDao alojamientoDao = new AlojamientoDaoImpl();
        reserva.setAlojamiento(alojamientoDao.findOne(resultSet.getInt("reserva")).get()); 
        reserva.setEntrada(resultSet.getDate("entrada"));
        reserva.setSalida(resultSet.getDate("salida"));
        reserva.setPension(resultSet.getString("pension"));
        return reserva;
    }
    
}
