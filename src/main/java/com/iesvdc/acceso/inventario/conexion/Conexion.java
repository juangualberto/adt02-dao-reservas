/*
 */
package com.iesvdc.acceso.inventario.conexion;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

import com.mysql.cj.xdevapi.Statement;

/**
 *
 * @author juangu
 */
public class Conexion {

    Connection conn;
    Properties prop;

    /**
     * 
     */
    public Conexion() {
        // Vía JDBC
        if (conn == null) {
            try (FileInputStream fis = new FileInputStream("db.properties")) {
                // Class.forName("com.mysql.jdbc.Driver");
                prop = new Properties();
                prop.load(fis);
                this.conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:33306/inventario",
                        prop);
            } catch (SQLException | ClassCastException | IOException e) {
                Logger.getLogger(Conexion.class.getName()).severe(e.getLocalizedMessage());
            }
        }
    }

    public Connection getConnection() {
        return conn;
    }

    public void destroy() {
        if (this.conn != null) {
            try {
                this.conn.close();
            } catch (SQLException e) {

            }
        }
    }

    public boolean createDatabase() {
        boolean solucion = true;
        try {
            String sql = "CREATE DATABASE `inventario`";
            PreparedStatement ps = this.conn.prepareStatement(sql);
            ps.executeUpdate();
        } catch (Exception e) {
            solucion = false;
        }
        return solucion;
    }

    public boolean dropDatabase() {
        boolean solucion = true;
        try {
            String sql = "DROP DATABASE `inventario`";
            PreparedStatement ps = this.conn.prepareStatement(sql);
            ps.executeUpdate();
        } catch (Exception e) {
            solucion = false;
        }
        return solucion;
    }
}
