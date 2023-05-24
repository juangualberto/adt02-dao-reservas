package com.iesvdc.acceso.inventario;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import java.io.File;

import org.eclipse.persistence.jaxb.MarshallerProperties;

// import com.iesvdc.acceso.inventario.dao.EstanciaDao;
// import com.iesvdc.acceso.inventario.daoimp.EstanciaDaoImp;
// import com.iesvdc.acceso.inventario.modelo.Estancia;
import com.iesvdc.acceso.inventario.modelo.TipoUsuario;
import com.iesvdc.acceso.inventario.modelo.Usuario;
import com.iesvdc.acceso.inventario.modelo.Usuarios;

public class Marshall {

    public static void main(String[] args) {

        JAXBContext jaxbContext;

        Usuarios listaUsuarios = new Usuarios();
        listaUsuarios.add(
                new Usuario(
                        1,
                        "pepe1",
                        "Secreto123",
                        TipoUsuario.ADMIN.toString(),
                        "pepe1@sincorreo.com"));
        listaUsuarios.add(
                new Usuario(
                        2,
                        "pepe2",
                        "Secreto123",
                        TipoUsuario.OPERARIO.toString(),
                        "pepe2@sincorreo.com"));
        listaUsuarios.add(
                new Usuario(
                        3,
                        "pepe3",
                        "Secreto123",
                        TipoUsuario.USUARIO.toString(),
                        "pepe3@sincorreo.com"));

        try {
            System.setProperty("javax.xml.bind.JAXBContextFactory", "org.eclipse.persistence.jaxb.JAXBContextFactory");

            jaxbContext = JAXBContext.newInstance(listaUsuarios.getClass());
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Para JSON
            jaxbMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
            jaxbMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);

            jaxbMarshaller.marshal(listaUsuarios, new File("usuarios.json"));

        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }
}
