# Conexión a la base de datos

## Creando los Plain Old Java Objects (POJO)

Para poder hacer el marshalling/unmarshalling de objetos, te recomendamos primero tener los objetos en Java, es lo que llamamos el modelo de datos, en algunos sitios llamados clases entidad y en Spring Java POJOs (Plain Old Java Object) .

A tal efecto creamos clases sencillas como Usuario, Instalacion, Reserva, etc. con sus correspondientes constructores, getters y setters.

Usuario.java (ten cuidado con los métodos *equals* para comparar dos usuarios):

```java
package com.iesvdc.acceso.simplecrud.model;

public class Usuario {
    
    String username;
    String email;
    String password;
    Integer id;
    
    Usuario() {

    }

    Usuario(String username, String email, String password){
        this.username = username;
        this.password = password;
        this.email = email;
    }

    Usuario(Integer id, String username, String email, String password){
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
    }


    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "{" +
            " username='" + getUsername() + "'" +
            ", email='" + getEmail() + "'" +
            ", password='" + getPassword() + "'" +
            ", id='" + getId() + "'" +
            "}";
    }

    @Override 
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Usuario)) {
            return false;
        }

        Usuario u = (Usuario) o;
        
        return u.getId() == this.id &&
            u.getUsername().compareTo(this.username)==0 &&
            u.getPassword().compareTo(this.password)==0 &&
            u.getEmail().compareTo(this.email)==0;
    }
}
```

Instalación (ten cuidado con los métodos *equals*):

```java
package com.iesvdc.acceso.simplecrud.model;

public class Instalacion {
    
    private int id;
    private String name;

    public Instalacion(){}

    // GETTERS AND SETTERS

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Instalacion)) {
            return false;
        }
        Instalacion instalacion = (Instalacion) o;
        
        return instalacion.id == this.id && 
            instalacion.name.compareTo(this.name)==0;
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", name='" + getName() + "'" +
            "}";
    }
    

}
```

Horario (ten cuidado con los métodos *equals* para comparaciones):

```java
package com.iesvdc.acceso.simplecrud.model;

import java.time.LocalTime;

public class Horario {
    Instalacion instalacion;
    LocalTime    inicio;
    LocalTime    fin;
    Long id;

    public Horario() {
    }

    

```

Reserva (ten cuidado con los métodos *equals*):

```java
package com.iesvdc.acceso.simplecrud.model;

import java.sql.Date;

public class Reserva {
    Long id;
    Usuario usuario;
    Horario horario;
    Date fecha;


    public Reserva() {
    }

    public Reserva(Usuario usuario, Horario horario, Date fecha) {
        this.usuario = usuario;
        this.horario = horario;
        this.fecha = fecha;
    }

    public Reserva(Long id, Usuario usuario, Horario horario, Date fecha) {
        this.id = id;
        this.usuario = usuario;
        this.horario = horario;
        this.fecha = fecha;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Horario getHorario() {
        return this.horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public Date getFecha() {
        return this.fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Reserva id(Long id) {
        this.id = id;
        return this;
    }

    public Reserva usuario(Usuario usuario) {
        this.usuario = usuario;
        return this;
    }

    public Reserva horario(Horario horario) {
        this.horario = horario;
        return this;
    }

    public Reserva fecha(Date fecha) {
        this.fecha = fecha;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Reserva)) {
            return false;
        }
        Reserva reserva = (Reserva) o;
        return usuario.equals(reserva.usuario) && 
            horario.equals(reserva.horario) && 
            fecha.compareTo(reserva.fecha)!=0;
    }

   

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            " usuario='" + this.getUsuario().toString() + "'" +
            ", horario='" + this.getHorario().toString() + "'" +
            ", fecha='" + getFecha().toString() + "'" +
            "}";
    }

    
}

```

Ahora ya sería posible con Gson o JAXB por ejemplo, desde el servlet directamente hacer el marshalling/unmarshalling de JSON a Java. No obstante no recomendamos esta opción porque no responde a ningún estándar como sí lo hace por ejemplo [Jersey](https://eclipse-ee4j.github.io/jersey.github.io/download.html) ([JAX-RS 2.1](https://jcp.org/en/jsr/detail?id=370), que es la especificación de la JSR 370: JavaTM API for RESTful Web Services).

## Conectando a la base de datos cargando la configuración vía JNDI 


En un entorno genérico, si por ejemplo queremos usar el patrón singleton y usar un único objeto conexión en nuestro código, podríamos hacer lo siguiente:

Abrir la conexión:

```java
public class Conexion {
    Connection conn;
    public Conexion(){
        if (conn==null)
            try {
                Class.forName("com.mysql.jdbc.Driver");
                this.conn = 
                DriverManager.getConnection("jdbc:mysql://localhost/gestion_reservas?"+
                "useUnicode=true&useJDBCCompliantTimezoneShift=true&serverTimezone=UTC"+
                "&user=root&password=example");
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            }  
    }
    public Connection getConnection() {
        return conn;
    }
}
```

Para cargar la conexión por JNDI (Java Naming and Directory Interface), es decir pedimos a Java que localice, en el contexto actual, un objeto que será la información de la conexión a la base de datos, lo haríamos así:

```java

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author juangu
 */
public class Conexion {

    Connection conn;
    Context ctx;
    DataSource ds;

    public Conexion() {
        // Vía DataSource con Contexto inyectado
        try {
            if (ctx == null)
                ctx = new InitialContext();
            if (ds == null)
                ds = (DataSource) ((Context) ctx.lookup(
                    "java:comp/env")).lookup("jdbc/gestionReservas");
            conn = ds.getConnection();
        } catch (Exception ex) {
            System.out.println("## Conexion ERROR ## " + ex.getLocalizedMessage());
            ctx = null;
            ds = null;
            conn = null;
        }
    }

    public Connection getConnection() {
        return conn;
    }
}

```

Esto implica que en el directorio *src/main/webapp/META-INF* de nuestro proyecto Maven, deberíamos tener un fichero llamado **context.xml** con el siguiente contenido:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Context path="/">
    <Resource name="jdbc/gestionReservas" global="jdbc/gestionReservas" 
              url="jdbc:mysql://localhost:33306/gestion_reservas"
              auth="Container" type="javax.sql.DataSource"
              maxTotal="100" maxIdle="30" maxWaitMillis="10000"
              username="root" password="example" driverClassName="com.mysql.cj.jdbc.Driver"
    />
</Context>
```

En este archivo podemos ver que el recurso será accesible vía JDNI bajo el nombre "jdbc/gestionReservas", que es como tener un objeto en memoria con las propiedades siguientes:

```json
 gestionReservas: {
    url:"jdbc:mysql://localhost:33306/gestion_reservas",
    auth:"Container",
    type:"javax.sql.DataSource",
    maxTotal:"100",
    maxIdle:"30",
    maxWaitMillis:"10000",
    username:"root", 
    password:"example",
    driverClassName:"com.mysql.cj.jdbc.Driver"
 }
```

## Cuando inicializar y cerrar la conexión desde un servlet

Cuando hemos de tomar la decisión de cuando conectar a la base de datos desde un servlet, nos encontramos frente cuatro opciones:

1. **Conexión por transacción**: dentro de cada método doGet, doPost, doPut o doDelete abrimos y cerramos la conexión. En el método init() del servlet cargamos el driver JDBC correspondiente (Oracle, MySQL, PostgreSQL, etc.). Éste es el modelo que seguiremos en los servicios REST, no es el más óptimo pero sí es seguro. Además cuando saltemos a JPA con Hibernate en Spring, nos resultará más fácil el cambio.
2. **Conexión dedicada**: al crear el servlet abrimos la conexión (método init() del mismo) y se cierra al descargar el servlet (método destroy()). Por tanto el driver y la conexión se cargan en el método init(). 
3. **Conexión por sesión**: cargamos el driver JDBC necesario en el método init(). No abrimos la conexión hasta el primer do(Get|Put|Delete|Post). En la sesión de usuario vamos pasando la conexión abierta de unos métodos a otros.
4. **Conexión cacheada**: Con un "pool" de conexiones. El servidor de aplicaciones (Tomcat, Jetty, Glashfish...) es el encargado de cargar el driver y abrir la conexión la primera vez que se necesita y ofrecerla a cada servlet que la necesita. Hay que crear el "pool" de conexiones en el servidor, lo que implica que tenemos acceso a su administración. Si compartimos el servidor o bien conectamos a diferentes bases de datos, puede ser peligroso tocar las configuraciones porque podemos dejar al resto de aplicaciones del servidor sin conexión a su base de datos.

Aunque lo normal es delegar en el servidor de aplicaciones la gestión de conexiones al servidor de base de datos, una receta muy común es abrir y cerrar la conexión desde los métodos init() y destroy() de los mismos. Esto es lo que se llama una **conexión dedicada**. Veámoslo en los siguientes ejemplos.

### Conexión dedicada

#### Abrir la conexión desde el método init()

```java
public class Alumno extends HttpServlet {

    Connection conn;

    @Override 
    public void init() throws ServletException {
        Conexion conexion = new Conexion(); 
        this.conn = conexion.getConnection();
    }
```

#### Cerrar la conexión desde el método destroy()

```java
@Override
    public void destroy() {
        try {
            this.conn.close();
        } catch (SQLException ex) {

        }
    }
```

## Conectando a la Base de Datos

Abrir la conexión:

```java
public class Conexion {
    Connection conn;
    public Conexion(){
        if (conn==null)
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn =
                DriverManager.getConnection("jdbc:mysql://localhost/gestion_reservas?" +
                "useUnicode=true&useJDBCCompliantTimezoneShift=true&serverTimezone=UTC"+
                "&user=root&password=example");
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            }  
    }
    public Connection getConnection() {
        return conn;
    }
}
```

## CRUD (patrón DAO)

Ya tenemos las clases base que contiene los objetos que vamos a almacenar/recuperar de la base de datos, ahora hay que hacer las interfaces para los DAO de cada uno y luego su implementación, vemos sólo el ejemplo de Instalación:

### InstalacionDao.java

```java
package com.iesvdc.acceso.simplecrud.dao;

import java.util.List;

import com.iesvdc.acceso.simplecrud.model.Instalacion;

public interface InstalacionDao {
    public boolean create(Instalacion instala);
    public Instalacion findById(Integer id);
    public List<Instalacion> findAll();
    public List<Instalacion> findByNombre(String nombre);
    public boolean update(Instalacion old_al, Instalacion new_al);
    public boolean update(Integer old_id, Instalacion new_al);
    public boolean delete(Instalacion instala);
    public boolean delete(Integer id_al);
}
```

### Leer todos/ uno (InstalacionDaoImpl.java)

```java
    @Override
    public Instalacion findById(Integer id) {
        Instalacion instala;
        try {
            Conexion conexion = new Conexion();
            Connection conn = conexion.getConnection();
            String sql = "SELECT * FROM instalacion WHERE id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            System.err.println("\nID:: " + id + "\n");
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            instala = new Instalacion(rs.getInt("id"), rs.getString("nombre"));
            conn.close();
        } catch (SQLException ex) {
            instala = null;
        }
        return instala;
    }
    
    @Override
    public List<Instalacion> findAll() {
        Instalacion instala;
        List<Instalacion> li_ins = new ArrayList<Instalacion>();
        try {
            Conexion conexion = new Conexion();
            Connection conn = conexion.getConnection();

            String sql = "SELECT * FROM instalacion";

            PreparedStatement pstmt = conn.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();
            // recorro el resultset mientras tengo datos
            while (rs.next()) {
                instala = new Instalacion(rs.getInt("id"), rs.getString("nombre"));
                li_ins.add(instala);
            }
            // cerramos la conexión
            conn.close();
        } catch (SQLException ex) {
            System.out.println("ERROR" + ex.getMessage());
            li_ins = null;
        }
        return li_ins;
    }
```

### Crear (InstalacionDaoImpl.java)

```java
    @Override
    public boolean create(Instalacion instala) {
        boolean exito = true;
        try {
            Conexion conexion = new Conexion();
            Connection conn = conexion.getConnection();
            String sql = "INSERT INTO instalacion VALUES (NULL,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, instala.getId());
            pstmt.setString(2, instala.getName());
            pstmt.executeUpdate();
            conn.close();
        } catch (SQLException ex) {
            System.out.println("ERROR:  " + ex.getMessage());
            exito = false;
        }
        return exito;
    }
```

### Actualizar (InstalacionDaoImpl.java)

```java
    /**
     * Este método actualiza uninstalaumno en la BBDD
     * 
     * @param old_al El objeto que contiene los datos antiguos delinstalaumno
     * @param new_al El objeto que contiene los datos nuevos delinstalaumno
     * @return true si se lleva a cabo correctamente <br>
     *         false si no se actualiza nada (error de conexión, no estaba
     *         elinstalaumno en la BBDD...) <br>
     */
    @Override
    public boolean update(Instalacion old_al, Instalacion new_al) {

        return update(old_al.getId(), new_al);
    }

    /**
     * Este método actualiza una instalación en la BBDD
     * 
     * @param old_id El id antiguo delinstalaumno
     * @param new_al El objeto que contieneinstalainstalaumno actualizado
     * @return true si se lleva a cabo correctamente <br>
     *         false si no se actualiza nada (error de conexión, no estaba
     *         elinstalaumno en la BBDD...) <br>
     */
    @Override
    public boolean update(Integer old_id, Instalacion new_al) {
        boolean exito = true;
        try {
            Conexion conexion = new Conexion();
            Connection conn = conexion.getConnection();
            String sql = "UPDATE instalacion SET id=?, nombre=? WHERE id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(3, old_id);
            pstmt.setInt(1, new_al.getId());
            pstmt.setString(2, new_al.getName());
            if (pstmt.executeUpdate() == 0) {
                exito = false;
            }
            conn.close();
        } catch (SQLException ex) {
            exito = false;
        }
        return exito;
    }
```

### Borrar (InstalacionDaoImpl.java)

```java
    /**
     * Este método borra de la BBDD el Alumno cuyos datos coinciden con los de el
     * objeto que se le pasa como parámetro
     * 
     * @paraminstalainstalaumno a borrar
     * @return true si borra uninstalaumno <br>
     *         false si elinstalaumno no existe o no se puede conectar a la BBDD
     *         <br>
     */
    @Override
    public boolean delete(Instalacion instala) {
        return delete(instala.getId());
    }

    @Override
    public boolean delete(Integer id_al) {
        boolean exito = true;
        try {
            Conexion conexion = new Conexion();
            Connection conn = conexion.getConnection();
            String sql = "DELETE FROM instalacion WHERE id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id_al);
            if (pstmt.executeUpdate() == 0) {
                exito = false;
            }
            conn.close();
        } catch (SQLException ex) {
            exito = false;
        }
        return exito;
    }
```
