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
import com.iesvdc.acceso.app.servicios.AlojamientoDao;
import com.iesvdc.acceso.app.servicios.DaoException;

public class AlojamientoDaoImpl implements AlojamientoDao{

    
    private static final String SAVE_QUERY = "INSERT INTO alojamiento (tipo, nombre, direccion, telefono) VALUES (?, ?, ?, ?)";
    private static final String FIND_ONE_QUERY = "SELECT * FROM alojamiento WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM alojamiento";
    private static final String UPDATE_QUERY = "UPDATE alojamiento SET tipo = ?, nombre = ?, direccion = ?, telefono = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM alojamiento WHERE id = ?";
    private static final String COUNT_QUERY = "SELECT COUNT(*) FROM alojamiento";

    @Override
    public Alojamiento save(Alojamiento alojamiento) throws DaoException {
        try (Connection connection = FactoriaConexion.getConnection();
            PreparedStatement statement = connection.prepareStatement(SAVE_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, alojamiento.getTipo());
            statement.setString(2, alojamiento.getNombre());
            statement.setString(3, alojamiento.getDireccion());
            statement.setString(4, alojamiento.getTelefono());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new DaoException("La inserción no tuvo éxito, no se creó ningún alojamiento.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    alojamiento.setId(generatedKeys.getLong(1));
                } else {
                    throw new DaoException("No se obtuvo el ID generado para el alojamiento.");
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al guardar el alojamiento.", e);
        }

        return alojamiento;
    }

    @Override
    public Optional<Alojamiento> findOne(long id) throws DaoException {
        try (Connection connection = FactoriaConexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ONE_QUERY)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToAlojamiento(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al obtener el alojamiento por ID.", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Alojamiento> findAll() throws DaoException {
        List<Alojamiento> alojamientos = new ArrayList<>();

        try (Connection connection = FactoriaConexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_QUERY);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                alojamientos.add(mapResultSetToAlojamiento(resultSet));
            }

        } catch (SQLException e) {
            throw new DaoException("Error al obtener todos los alojamientos.", e);
        }

        return alojamientos;
    }

    @Override
    public void update(Alojamiento alojamiento) throws DaoException {
        try (Connection connection = FactoriaConexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {
            statement.setString(1, alojamiento.getTipo());
            statement.setString(2, alojamiento.getNombre());
            statement.setString(3, alojamiento.getDireccion());
            statement.setString(4, alojamiento.getTelefono());
            statement.setLong(5, alojamiento.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new DaoException("La actualización no tuvo éxito, no se encontró el alojamiento.");
            }

        } catch (SQLException e) {
            throw new DaoException("Error al actualizar el alojamiento.", e);
        }
    }

    @Override
    public void delete(int id) throws DaoException {
        try (Connection connection = FactoriaConexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_QUERY)) {
            statement.setInt(1, id);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new DaoException("La eliminación no tuvo éxito, no se encontró el alojamiento.");
            }

        } catch (SQLException e) {
            throw new DaoException("Error al eliminar el alojamiento.", e);
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
            throw new DaoException("Error al contar los alojamientos.", e);
        }

        return 0;
    }

    // hacemos el mapeo relacion-objeto
    private Alojamiento mapResultSetToAlojamiento(ResultSet resultSet) throws SQLException {
        Alojamiento alojamiento = new Alojamiento();
        alojamiento.setId(resultSet.getLong("id"));
        alojamiento.setTipo(resultSet.getString("tipo"));
        alojamiento.setNombre(resultSet.getString("nombre"));
        alojamiento.setDireccion(resultSet.getString("direccion"));
        alojamiento.setTelefono(resultSet.getString("telefono"));
        return alojamiento;
    }
}
