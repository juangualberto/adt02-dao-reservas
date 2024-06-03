package com.iesvdc.acceso.app.conexion;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Patrón Factoría:
 * 
 */
public class FactoriaConexion {

    private static Connection conn;

    private FactoriaConexion() {
        // Constructor privado para evitar instancias
    }

    public static Connection getConnection() {
        conn = null;
        try (FileInputStream fis = new FileInputStream("db.properties")) {
            if (!createDatabase()) {
                System.out.println("--== CONEXION IMPOSIBLE ==--");
            }
            Properties prop = new Properties();
            prop.load(fis);
            if (prop.getProperty("driver").equals("MySQL")) {
                conn = DriverManager.getConnection(
                        "jdbc:mysql://" + prop.getProperty("host") + ":" +
                                prop.getProperty("port") + 
                                "/" + prop.getProperty("database"),
                        prop);
            }
        } catch (SQLException e) {
            System.out.println("Error de sintaxis en el código SQL");
        } catch (ClassCastException e) {
            System.out.println("Driver no encontrado");
        } catch (IOException e) {
            System.out.println("Archivo de configuración no encontrado");
        }
        return conn;
    }

    public static void destroy() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean createDatabase() {
        boolean solucion = true;
        try (FileInputStream fis = new FileInputStream("db.properties")) {
            Properties prop = new Properties();
            prop.load(fis);
            if (prop.getProperty("driver").equals("MySQL")) {
                conn = DriverManager.getConnection(
                        "jdbc:mysql://" + prop.getProperty("host") + ":" +
                                prop.getProperty("port"),
                        prop);

                String sql = "CREATE DATABASE IF NOT EXISTS `reservas`";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    solucion = ps.executeUpdate() == 1;
                }
                conn.close();
            }
        } catch (Exception e) {
            solucion = false;
        }
        return solucion;
    }

    public static boolean dropDatabase() {
        boolean solucion = true;
        try (FileInputStream fis = new FileInputStream("db.properties")) {
            Properties prop = new Properties();
            prop.load(fis);
            if (prop.getProperty("driver").equals("MySQL")) {
                conn = DriverManager.getConnection(
                        "jdbc:mysql://" + prop.getProperty("host") + ":" +
                                prop.getProperty("port"),
                        prop);

                String sql = "DROP DATABASE `reservas`";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    solucion = ps.executeUpdate() == 1;
                }
                conn.close();
            }
        } catch (Exception e) {
            solucion = false;
        }
        return solucion;
    }

    public static void initializeDatabase(String filename) {
        try (FileInputStream fis = new FileInputStream("db.properties")) {
            // Obtener el script SQL desde el archivo
            conn = getConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
            StringBuilder scriptSql = new StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null) {
                // Concatenar las líneas del script
                scriptSql.append(linea.trim());
                if (!linea.endsWith(";")) {
                    // Si la línea no termina con ';', agrega un espacio para separar las
                    // instrucciones
                    scriptSql.append(" ");
                } else {
                    // Si la línea termina con ';', ejecuta la instrucción SQL
                    try (Statement statement = conn.createStatement()) {
                        statement.executeUpdate(scriptSql.toString());
                    }
                    // Reinicia el StringBuilder para la próxima instrucción
                    scriptSql.setLength(0);
                }
            }
            reader.close();
            System.out.println("Base de datos inicializada correctamente.");
            conn.close();
        } catch (Exception e) {
            System.out.println("Imposible inicializar la base de datos");
        }
    }

    public static void createTriggers() {
        try (Connection connection = getConnection();
                Statement statement = connection.createStatement()) {

            // Código SQL para crear el trigger
            String sql = "DROP TRIGGER IF EXISTS `reservas_solapadas`";
            statement.execute(sql);

            sql = "CREATE TRIGGER `reservas_solapadas` " +
                    "BEFORE INSERT ON `reserva` " +
                    "FOR EACH ROW " +
                    "BEGIN " +
                    "    IF (SELECT COUNT(*) " +
                    "      FROM `reserva` " +
                    "      WHERE `reserva`.`cliente` = NEW.`cliente` AND "+
                    "        ((`reserva`.`entrada` < NEW.`entrada` " +
                    "        AND `reserva`.`salida` > NEW.`entrada`) OR " +
                    "        (`reserva`.`entrada` < NEW.`salida` " +
                    "        AND `reserva`.`salida` > NEW.`salida`))) > 0 " +
                    "    THEN " +
                    "      SIGNAL sqlstate '45001' " +
                    "        SET message_text = 'No se permite solapar reservas por usuario.'; " +
                    "    END IF; " +
                    "END";
            statement.execute(sql);

            System.out.println("Disparadores creados correctamente.");

        } catch (SQLException e) {
            System.out.println("Error al crear los disparadores. "+ e.getMessage());
        }
    }
}
