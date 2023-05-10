package com.iesvdc.acceso.inventario;

import java.io.File;

import org.eclipse.persistence.jaxb.UnmarshallerProperties;

import com.iesvdc.acceso.inventario.dao.UsuarioDao;
import com.iesvdc.acceso.inventario.daoimp.UsuarioDaoImp;
import com.iesvdc.acceso.inventario.modelo.Usuario;

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
                Usuario u = new Usuario(0, null, null, null, null);
                try {
                        System.setProperty("javax.xml.bind.JAXBContextFactory",
                                        "org.eclipse.persistence.jaxb.JAXBContextFactory");

                        jaxbContext = JAXBContext.newInstance(u.getClass());

                        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                        // Para JSON
                        jaxbUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE,
                                        "application/json");
                        jaxbUnmarshaller.setProperty(
                                        UnmarshallerProperties.JSON_INCLUDE_ROOT, true);

                        Object objeto = jaxbUnmarshaller.unmarshal(new File("usuario.json"));
                        u = (Usuario) objeto;
                        System.out.println(u.toString());

                        UsuarioDao uDao = new UsuarioDaoImp();
                        uDao.create(u);
                        u = uDao.findByUsername(u.getUsername());
                        System.out.println(u.toString());

                } catch (JAXBException e) {
                        e.printStackTrace();
                }

                /*
                 * System.out.println("CRUD USUARIO");
                 * Usuario u = new Usuario(10, "pepito",
                 * "Secreto123", "admin", "pepe@sincorreo.com");
                 * System.out.println("CREATE:");
                 * System.out.println("Creando usuario: " + u.toString());
                 * UsuarioDao uDao = new UsuarioDaoImp();
                 * uDao.create(u);
                 * System.out.println("READ");
                 * List<Usuario> listaUsuarios = uDao.findAll();
                 * System.out.println("Lista de usuarios: " + listaUsuarios.toString());
                 * u = listaUsuarios.get(0);
                 * u.setEmail("nuevoemail@sincorreo.com");
                 * u.setUsername("newusername");
                 * System.out.println("UPDATE");
                 * uDao.update(u.getId(), u);
                 * System.out.println("Usuario actualizado (comprobación): \n" +
                 * uDao.findById(u.getId()).toString());
                 * System.out.println("DELETE");
                 * u = listaUsuarios.get(listaUsuarios.size() - 1);
                 * uDao.delete(u);
                 * System.out.println("Listado con el último borrado: \n" +
                 * uDao.findAll().toString());
                 * 
                 * System.out.println("\n\n\n");
                 * System.out.println("Comprobamos ESTANCIAS");
                 * Estancia e = new Estancia(
                 * 0, "aula 112", "Aula de 2º de DAM");
                 * EstanciaDao estanciaDao = new EstanciaDaoImp();
                 * estanciaDao.create(e);
                 * System.out.println("El número de estancias es: " + estanciaDao.count());
                 */
        }
}
