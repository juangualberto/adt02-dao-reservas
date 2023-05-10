package com.iesvdc.acceso.inventario;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import java.io.File;

import org.eclipse.persistence.jaxb.MarshallerProperties;

import com.iesvdc.acceso.inventario.modelo.TipoUsuario;
import com.iesvdc.acceso.inventario.modelo.Usuario;

public class Marshall {

    public static void main(String[] args) {
        Usuario u = new Usuario(
                0,
                "pepe",
                "Secreto123",
                TipoUsuario.ADMIN.toString(),
                "pepe@sincorreo.com");

        JAXBContext jaxbContext;

        try {
            System.setProperty("javax.xml.bind.JAXBContextFactory", "org.eclipse.persistence.jaxb.JAXBContextFactory");

            jaxbContext = JAXBContext.newInstance(u.getClass());
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Para JSON
            jaxbMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
            jaxbMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);

            jaxbMarshaller.marshal(u, new File("usuario.json"));

        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }
}
