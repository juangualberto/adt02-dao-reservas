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
import com.iesvdc.acceso.app.entidades.Cliente;
import com.iesvdc.acceso.app.entidades.Contacto;
import com.iesvdc.acceso.app.servicios.ClienteDao;
import com.iesvdc.acceso.app.servicios.DaoException;

public class ClienteDaoImpl implements ClienteDao{

    private static final String SAVE_QUERY = "INSERT INTO cliente (nombre, email, telefono) VALUES (?, ?, ?)";
    private static final String FIND_ONE_QUERY = "SELECT * FROM cliente WHERE id = ?";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM cliente WHERE email = ?";
    private static final String FIND_BY_PHONE_QUERY = "SELECT * FROM cliente WHERE telefono = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM cliente";
    private static final String UPDATE_QUERY = "UPDATE cliente SET nombre = ?, email = ?, telefono = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM cliente WHERE id = ?";
    private static final String COUNT_QUERY = "SELECT COUNT(*) FROM cliente";

    @Override
    public Cliente save(Cliente cliente) throws DaoException {
        try (Connection connection = FactoriaConexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, cliente.getNombre());
            statement.setString(2, cliente.getContacto().getEmail());
            statement.setString(3, cliente.getContacto().getTelefono());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new DaoException("La inserción no tuvo éxito, no se creó ningún cliente.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cliente.setId(generatedKeys.getLong(1));
                } else {
                    throw new DaoException("No se obtuvo el ID generado para el cliente.");
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al guardar el cliente.", e);
        }

        return cliente;
    }

    @Override
    public Optional<Cliente> findOne(long id) throws DaoException {
        try (Connection connection = FactoriaConexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ONE_QUERY)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToCliente(resultSet));
                } 
            }
        } catch (SQLException e) {
            throw new DaoException("Error al obtener el cliente por ID.", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Cliente> findByEmail(String email) throws DaoException {
        try (Connection connection = FactoriaConexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_EMAIL_QUERY)) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToCliente(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al obtener el cliente por email.", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Cliente> findByPhone(String telefono) throws DaoException {
        try (Connection connection = FactoriaConexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_PHONE_QUERY)) {
            statement.setString(1, telefono);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToCliente(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al obtener el cliente por teléfono.", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Cliente> findAll() throws DaoException {
        List<Cliente> clientes = new ArrayList<>();

        try (Connection connection = FactoriaConexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_QUERY);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                clientes.add(mapResultSetToCliente(resultSet));
            }

        } catch (SQLException e) {
            throw new DaoException("Error al obtener todos los clientes.", e);
        }

        return clientes;
    }

    @Override
    public void update(Cliente cliente) throws DaoException {
        try (Connection connection = FactoriaConexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {
            statement.setString(1, cliente.getNombre());
            statement.setString(2, cliente.getContacto().getEmail());
            statement.setString(3, cliente.getContacto().getTelefono());
            statement.setLong(4, cliente.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new DaoException("La actualización no tuvo éxito, no se encontró el cliente.");
            }

        } catch (SQLException e) {
            throw new DaoException("Error al actualizar el cliente.", e);
        }
    }

    @Override
    public void delete(int id) throws DaoException {
        try (Connection connection = FactoriaConexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_QUERY)) {
            statement.setInt(1, id);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new DaoException("La eliminación no tuvo éxito, no se encontró el cliente.");
            }

        } catch (SQLException e) {
            throw new DaoException("Error al eliminar el cliente.", e);
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
            throw new DaoException("Error al contar los clientes.", e);
        }

        return 0;
    }

    // hacemos el mapeo relacion-objeto
    private Cliente mapResultSetToCliente(ResultSet resultSet) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(resultSet.getLong("id"));
        cliente.setNombre(resultSet.getString("nombre"));
        cliente.setContacto(
            new Contacto(
                resultSet.getString("email"),
                resultSet.getString("telefono")));
        return cliente;
    }
    
}
