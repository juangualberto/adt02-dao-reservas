package com.iesvdc.acceso.inventario;

import java.io.File;

import org.eclipse.persistence.jaxb.UnmarshallerProperties;

import com.iesvdc.acceso.inventario.dao.UsuarioDao;
import com.iesvdc.acceso.inventario.daoimp.UsuarioDaoImp;
import com.iesvdc.acceso.inventario.modelo.Usuario;
import com.iesvdc.acceso.inventario.modelo.Usuarios;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

/**
 * Hello world!
 *
 */
public class DataBase {
        public static void main(String[] args) {

                JAXBContext jaxbContext;
                Usuarios usuarios = new Usuarios();
                try {
                        System.setProperty("javax.xml.bind.JAXBContextFactory",
                                        "org.eclipse.persistence.jaxb.JAXBContextFactory");

                        jaxbContext = JAXBContext.newInstance(usuarios.getClass());
                        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

                        jaxbUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
                        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, true);

                        usuarios = (Usuarios) jaxbUnmarshaller.unmarshal(new File("usuarios.json"));
                        if (usuarios != null) {
                                System.out.println(usuarios.toString());
                                UsuarioDao uDao = new UsuarioDaoImp();
                                for (Usuario u : usuarios.getUsuarios()) {
                                        uDao.create(u);
                                        Usuario u2 = uDao.findByUsername(u.getUsername());
                                        System.out.println(u2.toString());
                                }
                        } else {
                                System.out.println("Error al cargar el JSON.");
                        }

                } catch (JAXBException e) {
                        e.printStackTrace();
                }

        }
}
