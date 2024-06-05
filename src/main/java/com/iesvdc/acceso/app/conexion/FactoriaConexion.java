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

    /**
     * Me devuelve una conexión en función de lo encontrado en 
     * el fichero db.properties en el raíz del proyecto. 
     * 
     * Ejemplo de fichero:
     * 
     *  user=root 
     *  password=s83n38DGB8d72
     *  useUnicode=yes
     *  useJDBCCompliantTimezoneShift=true
     *  port=33307
     *  database=reservas
     *  host=localhost
     *  driver=MySQL
     * 
     * @return el objeto con la conexión a la BBDD o 
     * null si no soporta el driver indicado.
     */
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
            } else {
                System.out.println("Driver no soportado");
            }
        } catch (SQLException e) {
            System.out.println("FactoriaConexion::Error de sintaxis en el código SQL");
        } catch (ClassCastException e) {
            System.out.println("FactoriaConexion::Driver no encontrado");
        } catch (IOException e) {
            System.out.println("FactoriaConexion::Archivo de configuración no encontrado");
        }
        return conn;
    }

    /**
     * Cierra la conexión
     */
    public static void destroy() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Intenta crear la base de datos que le indicamos en el 
     * fichero db.properties.
     * @return true si no da error, false en caso contrario
     */
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

                String sql = "CREATE DATABASE IF NOT EXISTS `"
                    +prop.getProperty("database")+"`";

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

    /**
     * Intenta borrar la base de datos que le indicamos en el 
     * fichero db.properties.
     * @return true si no da error, false en caso contrario
     */
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

                String sql = "DROP DATABASE IF EXISTS `"
                    +prop.getProperty("database")+"`";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.executeUpdate();
                }
                conn.close();
            }
        } catch (Exception e) {
            solucion = false;
        }
        return solucion;
    }

    /**
     * Carga un fichero SQL y lo ejecuta. Esto es útil para 
     * inicializar la base de datos, por ejemplo. Cada instrucción
     * del fichero SQL siempre tiene que acabar en ";" y nueva línea
     * después del punto y coma para que funcione.
     * @param filename El nombre del fichero con el código SQL a cargar.
     */
    public static void initializeDatabase(String filename) {
        StringBuilder scriptSql = new StringBuilder();
        String linea="";

        try (FileInputStream fis = new FileInputStream("db.properties")) {
            // Obtener el script SQL desde el archivo
            conn = getConnection();
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filename)));
            
            while ((linea = reader.readLine()) != null) {
                // Concatenar las líneas del script
                scriptSql.append(linea.trim());
                if (linea.endsWith(";")) {                    
                    // Si la línea termina con ';', ejecuta la instrucción SQL
                    try (Statement statement = conn.createStatement()) {
                        statement.executeUpdate(scriptSql.toString());
                    }
                    // Reinicia el StringBuilder para la próxima instrucción
                    scriptSql.setLength(0);
                } else {
                    // Si la línea no termina con ';', 
                    // agrega un espacio para separar las
                    // instrucciones
                    scriptSql.append(" ");
                }
            }
            reader.close();
            System.out.println("Base de datos inicializada correctamente.");
            conn.close();
        } catch (Exception e) {        
            e.printStackTrace();
            System.out.println("Imposible inicializar la base de datos");
            System.err.println("LINEA: "+ linea);
            System.err.println("StringBuilder: "+scriptSql.toString());
        }
    }

    /**
     * Crea los disparadores necesarios para el buen funcionamiento
     * de la base de datos. Los creamos desde Java con "statement.execute()"
     */
    public static void createTriggers() {
        try (Connection connection = getConnection();
                Statement statement = connection.createStatement()) {

            // Código SQL para crear el trigger
            String sql = "DROP TRIGGER IF EXISTS `reservas_solapadas`";
            statement.execute(sql);

            sql = "CREATE TRIGGER `reservas_solapadas` \n" + //
                "BEFORE INSERT ON `reserva` \n" + //
                "FOR EACH ROW\n" + //
                "BEGIN\n" + //
                "    IF (SELECT COUNT(*) \n" + //
                "      FROM `reserva` \n" + //
                "      WHERE `reserva`.`cliente` = NEW.`cliente` AND \n" + //
                "        (`reserva`.`entrada` < NEW.`salida` \n" + //
                "        AND `reserva`.`salida` > NEW.`salida`)) > 0 \n" + //
                "    THEN\n" + //
                "      SIGNAL sqlstate '45001' \n" + //
                "        SET message_text = 'La fecha de salida se solapa con otra reserva.'; \n" + //
                "    END IF;\n" + //
                "\n" + //
                "    IF (SELECT COUNT(*) \n" + //
                "      FROM `reserva` \n" + //
                "      WHERE `reserva`.`cliente` = NEW.`cliente` AND \n" + //
                "        (`reserva`.`entrada` < NEW.`entrada` \n" + //
                "        AND `reserva`.`salida` > NEW.`entrada`)) > 0 \n" + //
                "    THEN\n" + //
                "      SIGNAL sqlstate '45002' \n" + //
                "        SET message_text = 'La fecha de entrada se solapa con otra reserva.'; \n" + //
                "    END IF;\n" + //
                "\n" + //
                "    IF (SELECT COUNT(*) \n" + //
                "      FROM `reserva` \n" + //
                "      WHERE `reserva`.`cliente` = NEW.`cliente` AND \n" + //
                "        (`reserva`.`entrada` > NEW.`entrada` \n" + //
                "        AND `reserva`.`salida` < NEW.`salida`)) > 0 \n" + //
                "    THEN\n" + //
                "      SIGNAL sqlstate '45002' \n" + //
                "        SET message_text = 'Las fechas indicadas contienen otra reserva.'; \n" + //
                "    END IF;\n" + //
                "\n" + //
                "END";
            statement.execute(sql);

            System.out.println("Disparadores creados correctamente.");

        } catch (SQLException e) {
            System.out.println("Error al crear los disparadores. "+ e.getMessage());
        }
    }
}
