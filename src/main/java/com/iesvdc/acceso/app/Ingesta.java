package com.iesvdc.acceso.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iesvdc.acceso.app.conexion.FactoriaConexion;
import com.iesvdc.acceso.app.entidades.LocalDateTimeTypeAdapter;
import com.iesvdc.acceso.app.entidades.LocalDateTypeAdapter;
import com.iesvdc.acceso.app.entidades.LocalTimeTypeAdapter;
import com.iesvdc.acceso.app.entidades.Reserva;
import com.iesvdc.acceso.app.entidades.ReservaWrapper;
import com.iesvdc.acceso.app.entidades.Reservas;
import com.iesvdc.acceso.app.entidades.ReservasWrapper;
import com.iesvdc.acceso.app.servicios.DaoException;
import com.iesvdc.acceso.app.servicios.ReservaDao;
import com.iesvdc.acceso.app.serviciosimpl.ReservaDaoImpl;

/**
 * Este programa acepta como parámetro un archivo XML o JSON 
 * con la información a añadir a la base de datos.
 */
public class Ingesta 
{
    public static void main( String[] args )
    {
        if(args.length!=1) {
            printUsage();
        } else {
            creaBBDD();
            if (args[0].endsWith(".xml")) {
                System.out.println("Realizando ingesta de XML:");
                loadXml(args[0]);
            } else {
                if(args[0].endsWith(".json")){
                    System.out.println("Realizando ingesta de JSON:");
                    loadGson(args[0]);
                } else {
                    printUsage();
                }
            }
        }
        
    }

    private static void printUsage(){
        printline();
        System.out.println("Este programa importa información de un archivo XML o JSON a la base de datos.");
        System.out.println("Uso del programa: ");
        System.out.println(" programa archivo.json|archivo.xml");
        printline();
    }

    private static void creaBBDD(){
        printline();
        System.out.println("Preparando la base de datos para la ingesta...");
        if (FactoriaConexion.dropDatabase()) {
            if (FactoriaConexion.createDatabase()) {
                    FactoriaConexion.initializeDatabase("init_db.sql");
                    FactoriaConexion.createTriggers();
            } else {
                System.err.println("Error:: No pude crear la base de datos.");
            }
        } else {
            System.err.println("No pude eliminar la base de datos inicial.");
        }
        printline();
    }

    private static void loadGson(String filename) {
        // configuramos Gson
        Gson gson = new GsonBuilder(). 
                registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter()).
                registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter()).
                registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter()).
                setPrettyPrinting().
                create();
        // leemos        

        try (Reader reader = new FileReader(filename)) {      
            ReservasWrapper reservasWrapper = gson.fromJson(reader, ReservasWrapper.class);
            // Obtener la lista de reservas
            List<ReservaWrapper> reservas = reservasWrapper.getReservas();
            ReservaDao rDao = new ReservaDaoImpl();
            printline();
            for (ReservaWrapper rw : reservas) {
                System.out.println("Guardando reserva: " + rw.getReserva().toString());
                rDao.save(rw.getReserva());
            }
            printline();
        } catch (FileNotFoundException fne) {
            System.out.println("Archivo no encontrado.");
        } catch (IOException ioe){
            System.out.println("Error de entrada salida.");
        } catch (DaoException de) {
            System.out.println("Error al ejecutar las consultas: "+de.getMessage());
        }
    }

    private static void loadXml(String filename){
        JAXBContext jaxbContext;
        try {
            // RESERVAS
            jaxbContext = JAXBContext.newInstance(Reservas.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Reservas reservas = (Reservas) unmarshaller.unmarshal(new File(filename));
            printline();
            ReservaDao rDao = new ReservaDaoImpl();
            for (Reserva reserva : reservas.getReservas()) {
                System.out.println("Guardando reserva: "+reserva.toString());
                rDao.save(reserva);
            }
            printline();
            // saveJSON("data/reservas.json", reservas);
        } catch (JAXBException e) {
            System.out.println("JAXB: error procesando el XML");            
        } catch (DaoException de) {
            System.out.println("Error al ejecutar las consultas: "+de.getMessage());
        }
    }

    private static void saveJSON(String filename, Reservas r){
        try (PrintWriter printWriter = new PrintWriter(new File(filename)) ){
            Gson gson = new GsonBuilder().
                registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter()).
                registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter()).
                registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter()).
                setPrettyPrinting().
                create();
            printWriter.write(gson.toJson(r));
        } catch (FileNotFoundException fnfe) {
            System.out.println("Archivo no encontrado: "+ filename);
        }

    }

    private static void printline(){
        System.out.println( "\n ····----++++===******===++++----·····\n");
    }
}
