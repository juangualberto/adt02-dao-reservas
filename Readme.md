# Gestión de Inventario

Aplicación que lee la información de JSON/XML y la pasa a la base de datos.

En este proyecto tenemos que crear unas clases modelo etiquetadas con JAXB para que podamos hacer marshalling y unmarshalling de JSON y los objetos que se cargan o guardan desde/hacia JSON a su vez de se guardan/leen (respectivamente) desde una base de datos MySQL.

En verdad estamos haciendo un programa para cargar y exportar ficheros JSON en una base de datos.

Con esta unidad integrada que vamos a trabajar en clase pretendemos cubrir los siguientes objetivos:

1. El desfase objeto-relacional.
2. Gestores de bases de datos embebidos e independientes.
3. Protocolos de acceso a bases de datos. Conectores.
4. Establecimiento de conexiones.
5. Definición de objetos destinados al almacenamiento del resultado de operaciones con bases de datos. Eliminación de objetos finalizada su función.
6. Ejecución de sentencias de descripción de datos.
7. Ejecución de sentencias de modificación de datos.
8. Ejecución de consultas.
9. Utilización del resultado de una consulta.
10. Ejecución de procedimientos almacenados en la base de datos.
11. Gestión de transacciones.

## Índice de contenido

1. [Creación de la BBDD y CRUD básico](./01-bbdd.md).
2. [Creación de proyectos en modo interactivo (MAVEN)](./clase01-1.md).
3. [Creación de la pila de contenedores Docker](./clase01-3.md).
4. [Patrones y estrategias de conexión](./03-patron-dao.md).
5. [Apuntes para ampliar docker](/99-docker.md).

## El desfase objeto-relacional

**El desfase objeto-relacional** en el acceso a datos se refiere a la discrepancia o incompatibilidad que puede existir entre los sistemas de gestión de bases de datos relacionales (RDBMS) y los modelos de objetos utilizados en el desarrollo de aplicaciones orientadas a objetos.

En el contexto de la programación orientada a objetos, los objetos se definen mediante clases y tienen atributos y métodos asociados. Sin embargo, las bases de datos relacionales almacenan los datos en tablas con filas y columnas, y utilizan el lenguaje SQL para manipular y consultar los datos. Esta diferencia fundamental entre los modelos de objetos y los modelos relacionales puede dar lugar a un desfase o falta de correspondencia cuando se trata de acceder a los datos de una base de datos relacional desde una aplicación orientada a objetos.

El desfase objeto-relacional puede manifestarse de diferentes maneras. Algunas de las dificultades comunes incluyen:

* Mapeo de objetos a tablas: El mapeo de objetos a tablas de bases de datos puede ser complicado debido a las diferencias en la estructura y semántica de los dos modelos. Por ejemplo, cómo se representan las relaciones entre objetos y cómo se traducen a relaciones de tablas.
* Herencia y polimorfismo: Los sistemas de gestión de bases de datos relacionales generalmente no admiten directamente conceptos de herencia y polimorfismo, que son características fundamentales de la programación orientada a objetos. Esto puede dificultar la representación de jerarquías de clases y la manipulación de objetos polimórficos en una base de datos relacional.
* Consultas y consultas complejas: La expresión de consultas complejas que involucran múltiples objetos y relaciones puede ser más difícil en un entorno objeto-relacional. La sintaxis y las operaciones disponibles en SQL pueden no ser tan expresivas o flexibles como en los lenguajes de consulta de objetos.

Para abordar este desfase, han surgido varios enfoques y tecnologías, como los mapeadores objeto-relacional (ORM) que proporcionan una capa de abstracción entre la base de datos relacional y la aplicación orientada a objetos. Estas herramientas facilitan el mapeo de objetos a tablas y proporcionan una interfaz más orientada a objetos para trabajar con los datos almacenados en la base de datos relacional. También existen bases de datos orientadas a objetos y bases de datos NoSQL que intentan superar las limitaciones de los sistemas de gestión de bases de datos relacionales en términos de soporte para la programación orientada a objetos.

Supongamos que tenemos una aplicación Java para gestionar una tienda en línea. Tenemos una clase Producto en Java que representa un producto en la tienda, con atributos como id, nombre y precio. Queremos almacenar y recuperar estos productos en una base de datos relacional utilizando SQL.

Primero, veamos cómo se definiría la clase Producto en Java:

```java
public class Producto {
    private int id;
    private String nombre;
    private double precio;

    // Constructor, getters y setters

    // Otros métodos de la clase
}
```

Ahora, para almacenar y recuperar objetos Producto en una base de datos relacional, necesitamos crear una tabla correspondiente en la base de datos y escribir consultas SQL.

Supongamos que utilizamos una base de datos MySQL y tenemos una tabla llamada productos con las columnas id, nombre y precio.

El código SQL para crear la tabla sería:

```sql
CREATE TABLE productos (
    id INT PRIMARY KEY,
    nombre VARCHAR(100),
    precio DECIMAL(10, 2)
);
```

Para almacenar un objeto Producto en la base de datos, tendríamos que traducir sus atributos a una consulta SQL de inserción:

```java
public void insertarProducto(Producto producto) {
    String sql = "INSERT INTO productos (id, nombre, precio) VALUES (?, ?, ?)";

    try (Connection conn = obtenerConexion();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, producto.getId());
        stmt.setString(2, producto.getNombre());
        stmt.setDouble(3, producto.getPrecio());

        stmt.executeUpdate();
    } catch (SQLException e) {
        // Manejo de excepciones
    }
}
```

Aquí estamos utilizando una consulta parametrizada para evitar la concatenación directa de valores en la consulta, lo cual podría conducir a vulnerabilidades de seguridad como la inyección de SQL.

Para recuperar productos de la base de datos, podríamos ejecutar una consulta SQL y luego mapear los resultados a objetos Producto en Java:

```java
public List<Producto> obtenerProductos() {
    List<Producto> productos = new ArrayList<>();
    String sql = "SELECT id, nombre, precio FROM productos";

    try (Connection conn = obtenerConexion();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            int id = rs.getInt("id");
            String nombre = rs.getString("nombre");
            double precio = rs.getDouble("precio");

            Producto producto = new Producto(id, nombre, precio);
            productos.add(producto);
        }
    } catch (SQLException e) {
        // Manejo de excepciones
    }

    return productos;
}
```

En este ejemplo, ejecutamos una consulta SELECT para obtener todos los productos de la tabla productos. Luego, iteramos sobre los resultados del conjunto de resultados (ResultSet) y creamos objetos Producto a partir de ellos.

Este ejemplo muestra cómo se realiza el mapeo entre los objetos Java y las tablas SQL en un entorno objeto-relacional. El desfase objeto-relacional se manifiesta en la necesidad de escribir código adicional para traducir entre los objetos y las consultas SQL, así como en las diferencias de sintaxis y estructura entre los modelos de objetos y los modelos relacionales.

Para aprender a desarrollar una aplicación con un lenguaje orientado a objetos que almacene información en un sistema relacional, lo vamos a combinar con lo aprendido en el tema anterior (Manejo de archivos y Marshalling de objetos) y así el objetivo de esta pieza de software será importar/exportar datos de un archivo JSON/XML a una base de datos.

## Gestores de bases de datos embebidos e independientes

Gestores de bases de datos embebidos son aquellos que se incorporan directamente en el código de la aplicación, no necesitamos un servicio, demonio o servidor para gestionar la base de datos.

Ejemplo de gestores embebidos:

* sqlite

Gestores de bases de datos independientes son aquellos que necesitan un demonio, proceso o servidor que gestione los archivos de la base de datos y al que nos conectamos desde la aplicación:

* MySQL
* Oracle
* PosgreSQL
* MariaDB
* SQL Server Express (Microsoft)
* etc.

Tenemos una lista muy interesante de sistemas de bases de datos en esta Web: <https://db-engines.com/en/ranking>.

## Protocolos de acceso a bases de datos. Conectores.

El conector es una librería o ayuda en el lenguaje de programación que nos facilita la conexión a la base de datos. 

En Java usamos la Java Database Connectivity (JDBC) es la especificación de una interfaz de programación de aplicaciones (API) que permite que los programas Java accedan a sistemas de gestión de bases de datos. La API JDBC consiste en un conjunto de interfaces y clases escribas en el lenguaje de programación Java.

La cadena de conexión obedece al patrón:

> jdbc:driver:host:puerto/basededatos

Para MySQL por ejemplo sería:

> jdbc:mysql://localhost:3306/inventario

En este ejemplo nos conectamos a un servidor local que está en el puerto 3306 escuchando y a la base de datos inventario.

Para Oracle:

> jdbc:oracle:thin:@localhost:1521/oracleservice

En este ejemplo nos conectamos a un servidor local Oracle que está en el puerto 1521 y concretamente al servicio "oracleservice".

## Establecimiento de conexiones

Como hemos dicho antes, en Java se pueden establecer conexiones a bases de datos utilizando la API JDBC (Java Database Connectivity). JDBC proporciona una interfaz estándar para interactuar con diferentes sistemas de gestión de bases de datos.


Para establecer una conexión a una base de datos MySQL en Java tenemos que seguir los siguientes pasos:
1. Importamos las clases necesarias de java.sql para trabajar con JDBC y java.sql.SQLException para manejar las excepciones relacionadas con la base de datos.
2. Definimos, al menos, la URL de la base de datos, el nombre de usuario y la contraseña.
3. Registramos el controlador JDBC correspondiente para el sistema de gestión de base de datos que estamos utilizando. En este caso, estamos utilizando el controlador JDBC para MySQL (com.mysql.cj.jdbc.Driver), pero este paso puede variar según la base de datos que estés utilizando.
4. Luego, utilizamos el método DriverManager.getConnection() para establecer la conexión proporcionando la URL, el nombre de usuario y la contraseña.
5. Una vez establecida la conexión, puedes realizar operaciones en la base de datos, como consultas, actualizaciones, inserciones, etc.
6. Finalmente, cerramos la conexión utilizando el método close().

Recuerda que debes proporcionar todos los detalles específicos de tu base de datos, no sólo la URL, el nombre de usuario y la contraseña, en el código para que la conexión se establezca correctamente.

Ejemplo de conexión:

```java

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
                
                // Registrar el controlador JDBC
                Class.forName("com.mysql.cj.jdbc.Driver");
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
}

```

Siendo el fichero de propiedades el siguiente:

```bash
user=root
password=zx76wbz7FG89k
useUnicode=yes
useJDBCCompliantTimezoneShift=true
```

## Definición de objetos destinados al almacenamiento del resultado de operaciones con bases de datos. Eliminación de objetos finalizada su función.



## Ejecución de sentencias de descripción de datos.

## Ejecución de sentencias de modificación de datos. CRUD básico.

### LEER uno (findOne)

```java
    String jsonObject="{}";
    Connection conexion;
    PreparedStatement pstm;
    String jdbcURL;

    jdbcURL = JDBC_MYSQL_GESTION_RESERVAS;

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conexion =  DriverManager.getConnection(jdbcURL, "root", "example");
        String sql = "SELECT * FROM usuario WHERE id=?";
        pstm = conexion.prepareStatement(sql);
        pstm.setInt(1, Integer.parseInt(id));
        ResultSet rs = pstm.executeQuery();
        if ( rs.next() ) {  
            String username = rs.getString("username");
            String password = rs.getString("password");
            String email = rs.getString("email");
            // String id = rs.getString("id");
            jsonObject="{"+ "\n"+
                "'id':'"+id+"',"+ "\n"+
                "'username':'"+username+"',"+ "\n"+
                "'password':'"+password+"',"+ "\n"+
                "'email':'"+email+"'"+ "\n"+
                "}";
        }
    }catch(Exception ex){
        // Gestión de la excepción
    }
    // devolvemos jsonObject
```

### LEER todos (findAll)

```java
    String jsonObject="{}";
    Connection conexion;
    PreparedStatement pstm;
    String jdbcURL;

    jdbcURL = JDBC_MYSQL_GESTION_RESERVAS;

    try {
        Persona p;
        List<Persona> pl = new ArrayList<Persona>();
        Class.forName("com.mysql.cj.jdbc.Driver");
        conexion =  DriverManager.getConnection(jdbcURL, "root", "example");
        String sql = "SELECT * FROM usuario WHERE id=?";
        pstm = conexion.prepareStatement(sql);
        pstm.setInt(1, Integer.parseInt(id));
        ResultSet rs = pstm.executeQuery();
        if ( rs.next() ) {  
            String username = rs.getString("username");
            String password = rs.getString("password");
            String email = rs.getString("email");
            // String id = rs.getString("id");
            p = new Persona(username, password, email);
            pl.add(p)
        }
    }catch(Exception ex){
        // Gestión de la excepción
    }
    // devolvemos pl
```

### Crear

```java
    Connection conexion;
    PreparedStatement pstm;
    String jdbcURL;
    jdbcURL = JDBC_MYSQL_GESTION_RESERVAS;
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conexion =  DriverManager.getConnection(jdbcURL, "root", "example");
        String sql = "INSERT INTO usuario (username,password,email) VALUES(?,?,?)";
        pstm = conexion.prepareStatement(sql);
        pstm.setString(1, username);
        pstm.setString(2, password);
        pstm.setString(3, email);
        if (pstm.executeUpdate() >0) {
            // "Usuario insertado"
        } else {
            // "No se ha podido insertar"
        }
        conexion.close();
    } catch (Exception ex) {
        // "Imposible conectar a la BBDD"
    }
```

### Actualizar

```java
    Usuario user = new Gson().fromJson(req.getReader(), Usuario.class);
    String jdbcURL = JDBC_MYSQL_GESTION_RESERVAS;
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conexion =  DriverManager.getConnection(jdbcURL, "root", "example");
        String sql = "UPDATE usuario SET username=?, password=?, email=? WHERE id=?"; 
        PreparedStatement pstm = conexion.prepareStatement(sql);
        pstm.setString(1, user.getUsername());
        pstm.setString(2, user.getPassword());
        pstm.setString(3, user.getEmail());
        pstm.setInt(4, user.getId());

        if (pstm.executeUpdate() >0) {
            resp.getWriter().println("Usuario insertado");
        } else {
            resp.getWriter().println("No se ha podido insertar");
        }

        conexion.close();
    } catch (Exception ex) {
        resp.getWriter().println(ex.getMessage());
        resp.getWriter().println(ex.getLocalizedMessage());
        // resp.getWriter().println("Imposible conectar a la BBDD");
    }
```

### Borrar

```java
    Connection conexion;
    PreparedStatement pstm;
    String jdbcURL;

    jdbcURL = JDBC_MYSQL_GESTION_RESERVAS;
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conexion =  DriverManager.getConnection(jdbcURL, "root", "example");
        String sql = "DELETE FROM usuario WHERE id=?";
        pstm = conexion.prepareStatement(sql);
        pstm.setInt(1, Integer.parseInt(id));
        if ( pstm.executeUpdate()==0 ) {  
            jsonObject="{ "+
                "'id':'"+id+"'}";
        }
    }catch(Exception ex){
        // "No se pudo eliminar"
    }
```

## Ejecución de consultas.

## Utilización del resultado de una consulta.

## Ejecución de procedimientos almacenados en la base de datos.

## Gestión de transacciones.