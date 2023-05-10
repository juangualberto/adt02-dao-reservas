package com.iesvdc.acceso.inventario.daoimp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.iesvdc.acceso.inventario.conexion.Conexion;
import com.iesvdc.acceso.inventario.dao.UsuarioDao;
import com.iesvdc.acceso.inventario.modelo.TipoUsuario;
import com.iesvdc.acceso.inventario.modelo.Usuario;
import com.mysql.cj.PreparedQuery;

public class UsuarioDaoImp implements UsuarioDao {


    
    public List<Usuario> findAll(){
        List<Usuario> listaUsuarios = new ArrayList<Usuario>();
        try {
            String sql = "select * from usuario";
            Conexion con = new Conexion();
            Connection conexion = con.getConnection();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                listaUsuarios.add(new Usuario(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("tipo"),
                    rs.getString("email")
                ));
            }
            conexion.close();
        } catch (Exception e) {
            return null;
        }
        return listaUsuarios;
    }

    /**
     * Método para crear un usuario en la BBDD
     * 
     * @param Usuario u: el usuario a dar de alta en la
     *                base de datos
     * @return verdadero si éxito
     */
    public boolean create(Usuario u) {
        boolean resultado = true;

        try {
            Conexion con = new Conexion();
            Connection connection = con.getConnection();
            String sql = "INSERT INTO " +
                    " usuario(id, username, password, tipo, email) " +
                    " VALUES(NULL, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getTipo().toString());
            ps.setString(4, u.getEmail());
            if (ps.executeUpdate() == 0)
                resultado = false;
            connection.close();
        } catch (Exception e) {
            System.out.println("::UsuarioDaoImp::CREATE:: Error: " + e.getLocalizedMessage());
            resultado = false;
        }

        return resultado;
    }

    /**
     * Busca un usuario por su identificador
     * 
     * @param int el id del usuario a buscar
     * @return false si no está el ID o no conecta
     */
    public Usuario findById(int id) {
        Usuario u = null;
        try {
            Conexion con = new Conexion();
            Connection connection = con.getConnection();
            String sql = "SELECT * FROM usuario WHERE id=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                u = new Usuario(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        String.valueOf(rs.getString("tipo")),
                        rs.getString("email"));
            } else {
                // no encontrado, usuario NULL
                u = null;
            }
            connection.close();
        } catch (Exception e) {
            System.out.println("::UsuarioDaoImp::findById:: Error: " + e.getLocalizedMessage());
        }
        return u;
    }

    public Usuario findByUsername(String username) {
        Usuario u = null;
        try {
            Conexion con = new Conexion();
            Connection connection = con.getConnection();
            String sql = "SELECT * FROM usuario WHERE username=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                u = new Usuario(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        String.valueOf(rs.getString("tipo")),
                        rs.getString("email"));

            } else {
                // no encontrado, usuario NULL
                u = null;
            }
            connection.close();
        } catch (Exception e) {
            System.out.println("::UsuarioDaoImp::findByUsername:: Error: " + e.getLocalizedMessage());
        }
        return u;
    }

    public Usuario findByUsernameAndPassword(String username, String password) {
        Usuario u = null;
        try {
            Conexion con = new Conexion();
            Connection connection = con.getConnection();
            String sql = "SELECT * FROM usuario WHERE username=? and password=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                u = new Usuario(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        String.valueOf(rs.getString("tipo")),
                        rs.getString("email"));

            } else {
                // TODO: Borrar estas líneas
                // no encontrado, usuario NULL
            }
            connection.close();
        } catch (Exception e) {
            System.out.println("::UsuarioDaoImp::findByUsernameAndPassword:: Error: " + e.getLocalizedMessage());
        }
        return u;
    }

    public boolean update(Usuario o, Usuario n) {
        return update(o.getId(), n);
    }

    public boolean update(int id, Usuario n) {
        boolean resultado = true;
        try {
            Conexion con = new Conexion();
            Connection connection = con.getConnection();
            String sql = "UPDATE usuario " +
                    "SET username = ?, password = ?, email=?, tipo=? " +
                    "WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, n.getUsername());
            ps.setString(2, n.getPassword());
            ps.setString(3, n.getPassword());
            ps.setString(4, n.getTipo().toString());
            ps.setInt(5, id);

            if (ps.executeUpdate() == 0)
                resultado = false;

            connection.close();
        } catch (Exception e) {
            System.out.println("::UsuarioDaoImp::update:: Error: " + e.getLocalizedMessage());
            resultado = false;
        }
        return resultado;
    }

    public boolean delete(Usuario u) {
        return delete(u.getId());
    }

    public boolean delete(int id) {
        boolean resultado = true;
        try {
            Conexion con = new Conexion();
            Connection connection = con.getConnection();
            String sql = "DELETE FROM usuario WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);

            if (ps.executeUpdate() == 0)
                resultado = false;

            connection.close();
        } catch (Exception e) {
            System.out.println("::UsuarioDaoImp::delete:: Error: " + e.getLocalizedMessage());
            resultado = false;
        }
        return resultado;
    }
    
    public int count() {
        int resultado = 0;
        try {
            Conexion con = new Conexion();
            Connection connection = con.getConnection();
            String sql = "SELECT count(*) FROM usuario";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            connection.close();
        } catch (Exception e) {
            System.out.println("::UsuarioDaoImp::count:: Error: " 
                    + e.getLocalizedMessage());
            resultado = -1;
        }
        return resultado;
    }
}
