package com.iesvdc.acceso.inventario.daoimp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.iesvdc.acceso.inventario.conexion.Conexion;
import com.iesvdc.acceso.inventario.dao.EstanciaDao;
import com.iesvdc.acceso.inventario.modelo.Estancia;

public class EstanciaDaoImp implements EstanciaDao {

    public boolean create(Estancia e) {
        Conexion conexion = new Conexion();
        boolean resultado = false;
        String sql = "INSERT INTO `estancia` (`nombre`, `descripcion`)" +
                " VALUES (?, ?);";
        Connection con = conexion.getConnection();
        PreparedStatement ps;
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getDescripcion());
            // ps.executeUpdate() devuelve un número entero que representa
            // el número de filas afectadas (con cambios).
            if (ps.executeUpdate() > 0) {
                resultado = true;
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        conexion.destroy();
        return resultado;
    }

    public Estancia findById(int id) {
        Conexion conexion = new Conexion();
        Estancia resultado = null;
        String sql = "SELECT * from `estancia` WHERE `id`= ?;";
        Connection con = conexion.getConnection();
        PreparedStatement ps;
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                resultado = new Estancia(
                        id,
                        rs.getString("nombre"),
                        rs.getString("descripcion"));
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        conexion.destroy();
        return resultado;
    }

    public Estancia findByNombre(String nombre) {
        Conexion conexion = new Conexion();
        Estancia resultado = null;
        String sql = "SELECT * from `estancia` WHERE `nombre`= ?;";
        Connection con = conexion.getConnection();
        PreparedStatement ps;
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                resultado = new Estancia(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"));
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        conexion.destroy();
        return resultado;
    }

    public boolean update(Estancia o, Estancia n) {
        return update(o.getId(), n);
    }

    public boolean update(int id, Estancia e) {
        Conexion conexion = new Conexion();
        boolean resultado = false;
        String sql = "UPDATE `estancia` SET `nombre`= ? , `descripcion`= ?" +
                " WHERE `id`= ?;";
        Connection con = conexion.getConnection();
        PreparedStatement ps;
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getDescripcion());
            ps.setInt(3, id);
            if (ps.executeUpdate() > 0) {
                resultado = true;
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        conexion.destroy();
        return resultado;
    }

    public boolean delete(Estancia u) {
        return delete(u.getId());
    }

    public boolean delete(int id) {
        Conexion conexion = new Conexion();
        boolean resultado = false;
        String sql = "DELETE FROM `estancia` WHERE `id`= ?;";
        Connection con = conexion.getConnection();
        PreparedStatement ps;
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                resultado = true;
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        conexion.destroy();
        return resultado;
    }

    public List<Estancia> findAll() {
        Conexion conexion = new Conexion();
        List<Estancia> resultado = new ArrayList<Estancia>();
        String sql = "SELECT * from `estancia`";
        Connection con = conexion.getConnection();
        PreparedStatement ps;
        try {
            ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                resultado.add(new Estancia(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")));
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        conexion.destroy();
        return resultado;
    }

    public int count() {
        Conexion conexion = new Conexion();
        int resultado = -1;
        String sql = "SELECT count(*) from `estancia`";
        Connection con = conexion.getConnection();
        PreparedStatement ps;
        try {
            ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                resultado = rs.getInt(1);
                // resultado = rs.getInt("count(*)")
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        conexion.destroy();
        return resultado;
    }
}
