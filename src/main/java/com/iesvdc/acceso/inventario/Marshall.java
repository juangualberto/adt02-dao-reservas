package com.iesvdc.acceso.inventario;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import java.io.File;

import org.eclipse.persistence.jaxb.MarshallerProperties;

import com.iesvdc.acceso.inventario.dao.EstanciaDao;
import com.iesvdc.acceso.inventario.daoimp.EstanciaDaoImp;
import com.iesvdc.acceso.inventario.modelo.Estancia;
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

        Estancia es = new Estancia(-1, "Aula 1.7", "Segundo de DAM");
        EstanciaDao eDao = new EstanciaDaoImp();
        eDao.create(es);
        es = eDao.findByNombre(es.getNombre());
        System.out.println("ESTANCIA: " + es.toString());

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
