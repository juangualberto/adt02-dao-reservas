\pagebreak

# Gestión de Inventario

Con este proyecto vamos a aprender a crear aplicaciones que gestionen información almacenada en bases de datos relacionales. Vamos a construir una aplicación que lee la información de JSON/XML (importa archivos JSON) y la pasa a la base de datos. Dejaremos propuesto el proceso contrario: exportar la base de datos o parte de ella a JSON.

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
     * Constructor, lee archivo de propiedades y abre 
     * la conexión. 
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

## Definición de objetos destinados al almacenamiento del resultado de operaciones con bases de datos. 

La definición y manipulación de objetos destinados al almacenamiento del resultado de operaciones con bases de datos es una parte fundamental de la mayoría de los lenguajes de programación. Estos objetos, generalmente conocidos como "objetos de dominio" o **"entidades"**, representan los datos recuperados de la base de datos y se utilizan para interactuar con esos datos en la aplicación.

Aquí hay algunas buenas prácticas y conceptos clave relacionados con la definición de objetos destinados al almacenamiento del resultado de operaciones con bases de datos:

1. **Modelado de Datos**: Antes de definir los objetos de dominio, es fundamental comprender la estructura de los datos en la base de datos. Esto incluye tablas, columnas, relaciones y tipos de datos.

2. **Clases de Entidades**: Cada tabla en la base de datos debe tener una clase de entidad correspondiente en Java. Esta clase contendrá propiedades que representan las columnas de la tabla.

3. **Mapeo Objeto-Relacional (ORM)**: El uso de herramientas ORM, como Hibernate o JPA, simplifica la definición de objetos de dominio y la interacción con la base de datos. Estas herramientas mapean automáticamente las tablas de la base de datos a las clases de entidad en Java.

4. **Encapsulación de Datos**: Las propiedades de una clase de entidad deben ser privadas y se acceden a través de métodos getter y setter para garantizar la encapsulación de datos. Esto permite el control sobre cómo se acceden y modifican los datos.

5. **Constructores Personalizados**: Además de los constructores predeterminados, es útil proporcionar constructores personalizados para inicializar objetos de entidad de manera coherente y segura.

6. **Validación de Datos**: Agregar lógica de validación en los métodos setter o en métodos específicos de validación para garantizar que los datos cumplan con las restricciones y reglas de negocio.

7. **Sobrescritura de Métodos**: Es útil sobrescribir los métodos `equals()`, `hashCode()`, y `toString()` para facilitar la comparación de objetos, trabajar con colecciones y depurar.

8. **Relaciones entre Entidades**: Si existen relaciones entre tablas en la base de datos, estas relaciones deben reflejarse en las clases de entidad. Por ejemplo, una relación uno a uno, uno a muchos o muchos a muchos.

9. **Serializable**: Si es necesario, implementar la interfaz `Serializable` para permitir la serialización de objetos, como en el caso de almacenamiento en caché o transferencia a través de la red.

10. **Auditoría y Registro**: Puede ser beneficioso incluir campos de auditoría, como fecha de creación, fecha de modificación y el usuario responsable, para el seguimiento y la auditoría de los datos.

11. **Pruebas Unitarias**: Asegúrate de que las clases de entidad se puedan probar de manera aislada utilizando pruebas unitarias para garantizar su correcto funcionamiento.

La definición adecuada de objetos de dominio es esencial para el diseño y el rendimiento de aplicaciones que interactúan con bases de datos. También es importante tener en cuenta las prácticas de diseño de bases de datos para lograr una integración eficiente y coherente.

### Eliminación de objetos finalizada su función.

Una vez hemos realizado las operaciones necesarias contra la base de datos, es necesaria la liberación de recursos y la eliminación de objetos una vez que han cumplido su propósito y ya no son necesarios en la aplicación. Aunque este concepto es más amplio y no se limita únicamente a conexiones de bases de datos, las conexiones de bases de datos son un ejemplo común en el contexto de la eliminación de objetos.

De hecho, un fallo muy común es no liberar correctamente objetos y/o recursos y esto es explotado por usuarios maliciosos para acceder a información que no deberían tener. Puedes ver más intormación en la Web de MITRE: <https://cwe.mitre.org/top25/index.html>.

En el caso de conexiones de bases de datos, es esencial administrarlas adecuadamente para evitar problemas de rendimiento, fugas de recursos y bloqueos. Aquí hay algunos puntos clave relacionados con la eliminación de conexiones de bases de datos:

1. **Cierre de Conexiones**: Cada vez que se abre una conexión a una base de datos, es fundamental cerrarla una vez que ya no se necesite. Esto se logra llamando al método `close()` en la conexión. No cerrar conexiones puede agotar los recursos de la base de datos y afectar negativamente el rendimiento.

2. **Uso de Bloques `try-with-resources`**: En Java, se recomienda utilizar la estructura `try-with-resources` al trabajar con conexiones de bases de datos. Esto garantiza que las conexiones se cierren automáticamente al salir del bloque `try`, incluso en caso de excepciones.

   ```java
   try (Connection connection = DriverManager.getConnection(url, username, password)) {
       // Trabajar con la conexión
   } catch (SQLException e) {
       // Manejo de excepciones
   }
   // La conexión se cierra automáticamente al salir del bloque try
   ```

3. **Pooling de Conexiones**: En aplicaciones empresariales, es común utilizar pooling de conexiones. En lugar de abrir y cerrar conexiones de manera individual, se mantienen en un pool y se reutilizan. Esto mejora la eficiencia y el rendimiento, ya que se evita el costo de abrir y cerrar conexiones repetidamente.

4. **Administración de Transacciones**: Es importante administrar las transacciones de manera adecuada. Una transacción debe comprometerse (`commit`) o deshacerse (`rollback`) según sea necesario antes de cerrar la conexión.

5. **Monitorización de Recursos**: Es útil monitorear el uso de recursos, como conexiones de bases de datos, para identificar posibles problemas de fugas de recursos o bloqueos.

Si bien la eliminación de conexiones de bases de datos es un aspecto crítico, la eliminación de objetos finalizados su función se aplica a muchos otros recursos en una aplicación, como archivos, sockets, recursos de memoria y más. La gestión adecuada de estos recursos es esencial para mantener la eficiencia, la estabilidad y la seguridad de la aplicación.

## Ejecución de sentencias de descripción de datos.

La Ejecución de Sentencias de Descripción de Datos (DDL, por sus siglas en inglés, Data Definition Language) se refiere a un conjunto de comandos utilizados en sistemas de gestión de bases de datos (DBMS) para definir, modificar y eliminar la estructura de la base de datos. Estas sentencias no se utilizan para manipular los datos en sí, sino para definir la estructura de la base de datos y sus objetos. Algunas de las tareas comunes realizadas mediante DDL incluyen la creación de tablas, la definición de restricciones de integridad, la modificación de esquemas y la eliminación de objetos de la base de datos.

Aquí hay algunas de las sentencias DDL más comunes y su función:

1. **CREATE**: La sentencia `CREATE` se utiliza para crear objetos de la base de datos, como tablas, índices, vistas, procedimientos almacenados, funciones y más. Por ejemplo, `CREATE TABLE` se utiliza para crear una tabla en la base de datos.

2. **ALTER**: La sentencia `ALTER` se utiliza para modificar la estructura de la base de datos existente. Puede usarse para agregar, modificar o eliminar columnas en una tabla, cambiar el nombre de objetos, agregar restricciones y más. Por ejemplo, `ALTER TABLE` se utiliza para modificar una tabla.

3. **DROP**: La sentencia `DROP` se utiliza para eliminar objetos de la base de datos, como tablas, vistas, índices u otros objetos. Por ejemplo, `DROP TABLE` se utiliza para eliminar una tabla.

4. **TRUNCATE**: La sentencia `TRUNCATE` se utiliza para eliminar todos los datos de una tabla, pero no la estructura de la tabla en sí. Es más eficiente que la eliminación de filas una por una.

5. **COMMENT**: La sentencia `COMMENT` se utiliza para agregar comentarios o descripciones a objetos de la base de datos, como tablas, columnas o vistas.

6. **RENAME**: Algunos sistemas de gestión de bases de datos permiten la sentencia `RENAME` para cambiar el nombre de un objeto existente, como una tabla.

7. **GRANT y REVOKE**: Aunque técnicamente son sentencias DCL (Data Control Language), `GRANT` y `REVOKE` se utilizan para otorgar o revocar permisos y privilegios a usuarios y roles en la base de datos. Esto afecta la seguridad y el acceso a los objetos de la base de datos.

Las sentencias DDL son esenciales para diseñar y administrar bases de datos. Los administradores de bases de datos y los desarrolladores utilizan estas sentencias para crear, modificar y mantener la estructura de la base de datos, lo que incluye definir las tablas y sus relaciones, aplicar restricciones de integridad, definir índices y otros elementos esenciales. La ejecución de DDL tiene un impacto directo en la estructura de la base de datos y, por lo tanto, debe realizarse con precaución y consideración.

## Ejecución de sentencias de modificación de datos. CRUD básico.

La Ejecución de Sentencias de Modificación de Datos (DML, por sus siglas en inglés, Data Manipulation Language) se refiere a un conjunto de comandos utilizados en sistemas de gestión de bases de datos (DBMS) para realizar operaciones que modifican los datos almacenados en la base de datos. Estas operaciones se conocen comúnmente como operaciones CRUD, que representan las siguientes acciones:

1. **Create (Crear)**: Insertar nuevos registros en una tabla.
2. **Read (Leer)**: Recuperar datos de una tabla.
3. **Update (Actualizar)**: Modificar registros existentes en una tabla.
4. **Delete (Eliminar)**: Eliminar registros de una tabla.

A continuación, te proporcionaré un ejemplo de un CRUD básico en Java utilizando el lenguaje SQL para realizar operaciones de modificación de datos en una tabla de una base de datos ficticia. Ten en cuenta que necesitarás una base de datos real o una base de datos en memoria (como H2) para ejecutar este ejemplo.

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CrudExample {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/tu_base_de_datos";
        String usuario = "tu_usuario";
        String contraseña = "tu_contraseña";

        try (Connection connection = DriverManager.getConnection(url, usuario, contraseña)) {
            // Create (Insert)
            insertarRegistro(connection, "EjemploNombre", "EjemploApellido");

            // Read (Select)
            consultarRegistros(connection);

            // Update (Update)
            actualizarRegistro(connection, 1, "NuevoNombre", "NuevoApellido");

            // Read (Select)
            consultarRegistros(connection);

            // Delete (Delete)
            eliminarRegistro(connection, 1);

            // Read (Select)
            consultarRegistros(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertarRegistro(Connection connection, String nombre, String apellido) throws SQLException {
        String insertQuery = "INSERT INTO tabla_ejemplo (nombre, apellido) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, nombre);
            preparedStatement.setString(2, apellido);
            int filasAfectadas = preparedStatement.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Registro insertado con éxito.");
            }
        }
    }

    private static void consultarRegistros(Connection connection) throws SQLException {
        String selectQuery = "SELECT * FROM tabla_ejemplo";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre");
                String apellido = resultSet.getString("apellido");
                System.out.println("ID: " + id + ", Nombre: " + nombre + ", Apellido: " + apellido);
            }
        }
    }

    private static void actualizarRegistro(Connection connection, int id, String nuevoNombre, String nuevoApellido) throws SQLException {
        String updateQuery = "UPDATE tabla_ejemplo SET nombre = ?, apellido = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, nuevoNombre);
            preparedStatement.setString(2, nuevoApellido);
            preparedStatement.setInt(3, id);
            int filasAfectadas = preparedStatement.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Registro actualizado con éxito.");
            }
        }
    }

    private static void eliminarRegistro(Connection connection, int id) throws SQLException {
        String deleteQuery = "DELETE FROM tabla_ejemplo WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setInt(1, id);
            int filasAfectadas = preparedStatement.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Registro eliminado con éxito.");
            }
        }
    }
}
```

Este ejemplo de CRUD en Java utiliza JDBC (Java Database Connectivity) para interactuar con una base de datos MySQL. Puedes adaptar el código para que se ajuste a tu base de datos y requerimientos específicos. El CRUD incluye la inserción de registros, consulta de registros, actualización de registros y eliminación de registros en una tabla ficticia llamada "tabla_ejemplo".

### LEER uno (findOne)

Veamos otro ejemplo más concreto de la operación **findOne** también referida en muchas herramientas como **findById** (buscar por la clave): 

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

Veamos ahora un ejemplo detallado de buscar todos los registros de la base de datos:

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

Sea un objeto **usuario** del que queremos guardar los datos:

* *id*: Identificador único del usuario.
* *username*: Nombre de usuario.
* *password*: Contraseña.
* *email*: Correo electrónico.

La siguiente porción de código Java explica cómo podríamos insertar en la tabla *ususario* sus datos:

```java
    Connection conexion;
    PreparedStatement pstm;
    String jdbcURL = "jdbc:mysql://localhost:33306/inventario";
    jdbcURL = JDBC_MYSQL_GESTION_RESERVAS;
    try {
        Class.forName("com.mysql.jdbc.Driver");
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

En operaciones que impliquen pedir datos al usuario, como por ejemplo aquí, siempre que sea posible será preferible usar **PreparedStatement** frente a la ejecución directa de la consulta para prevenir frente a ataques de inyección de código. Como se ve en el ejemplo, cada interrogante posteriormente se sustituye por la variable correspondiente, no permitiendo la introducción de otros caracteres maliciosos que puedan dar lugar a la inyección.

### Actualizar

En el siguiente ejemplo vamos a ver una sentencia de actualización. Suponemos que lo único que no cambia es el ID de usuario luego el resto de campos son susceptibles de ser actualizados:

```java
    
    User user = new User(1, "obijuan", "Secreto123, "obijuan@star.wars" );

    String jdbcURL = "jdbc:mysql://localhost:33306/inventario";
    
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
            // println("Usuario insertado");
        } else {
            // println("No se ha podido insertar");
        }

        conexion.close();
    } catch (Exception ex) {
        // gestión de excepciones
    }
```

### Borrar

A continuación vemos un ejemplo de cómo borrar un registro de la base de datos:

```java
    Connection conexion;
    PreparedStatement pstm;
    String jdbcURL;

    jdbcURL = "jdbc:mysql://localhost:33306/inventario";
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conexion =  DriverManager.getConnection(jdbcURL, "root", "example");
        String sql = "DELETE FROM usuario WHERE id=?";
        pstm = conexion.prepareStatement(sql);
        pstm.setInt(1, Integer.parseInt(id));
        if ( pstm.executeUpdate()==0 ) {  
            // no se ha encontrado el ID en la base de datos
        } else {
            // usuario eliminado correctamente
        }
    }catch(Exception ex){
        // "No se pudo eliminar"
    }
```

## Ejecución de consultas.

Resumiendo lo visto hasta ahora, la ejecución de consultas de una base de datos en Java implica el uso de sentencias SQL para recuperar datos de una base de datos. Java proporciona una API estándar llamada JDBC (Java Database Connectivity) que facilita la conexión a bases de datos y la ejecución de consultas SQL. Aquí hay un resumen de cómo ejecutar consultas de bases de datos en Java:

1. **Establecer una Conexión a la Base de Datos**:
   - Utiliza la clase `java.sql.Connection` para establecer una conexión a tu base de datos. Esto generalmente se hace proporcionando la URL de conexión, el nombre de usuario y la contraseña.

   ```java
   Connection connection = DriverManager.getConnection(url, usuario, contraseña);
   ```

2. **Preparar la Consulta**:
   - Utiliza objetos de tipo `PreparedStatement` o `Statement` para preparar tus consultas SQL. Los `PreparedStatement` son preferibles debido a su seguridad y mejor rendimiento.

   ```java
   String sql = "SELECT * FROM tabla WHERE condicion = ?";
   PreparedStatement preparedStatement = connection.prepareStatement(sql);
   preparedStatement.setString(1, valorCondicion);
   ```

3. **Ejecutar la Consulta**:
   - Utiliza el método `executeQuery()` para consultas `SELECT` que devuelven un conjunto de resultados, y `executeUpdate()` para consultas `INSERT`, `UPDATE` o `DELETE` que modifican la base de datos.

   ```java
   ResultSet resultSet = preparedStatement.executeQuery();
   ```

4. **Recuperar y Procesar Resultados**:
   - Si la consulta devuelve resultados, utiliza un objeto `ResultSet` para acceder a los datos recuperados. Puedes iterar a través del `ResultSet` para obtener los valores de las columnas.

   ```java
   while (resultSet.next()) {
       String columna1 = resultSet.getString("nombre_columna1");
       int columna2 = resultSet.getInt("nombre_columna2");
       // Realiza operaciones con los datos
   }
   ```

5. **Cerrar Recursos**:
   - Es importante cerrar todos los recursos, como la conexión, el `PreparedStatement` y el `ResultSet`, cuando hayas terminado de usarlos. Esto se puede hacer en un bloque `try-with-resources` para garantizar que se cierren adecuadamente.

   ```java
   try (Connection connection = DriverManager.getConnection(url, usuario, contraseña);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery()) {
       // Operaciones de consulta y procesamiento de resultados
   } catch (SQLException e) {
       e.printStackTrace();
   }
   ```

6. **Manejo de Excepciones**:
   - Las operaciones de base de datos pueden generar excepciones de tipo `SQLException`. Es importante manejar estas excepciones adecuadamente, ya sea registrándolas, notificando al usuario o tomando medidas específicas en caso de error.

7. **Seguridad**:
   - Evita concatenar valores directamente en las consultas SQL para prevenir ataques de inyección SQL. En su lugar, utiliza parámetros o `PreparedStatement` para asignar valores de manera segura en las consultas.

8. **Optimización de Consultas**:
   - Para consultas que se ejecutan con frecuencia, considera la indexación de columnas relevantes para mejorar el rendimiento. También puedes utilizar técnicas de optimización de consultas, como la limitación de resultados y la paginación.

9. **Pruebas Unitarias**:
   - Realiza pruebas unitarias para garantizar que tus consultas funcionen correctamente y produzcan los resultados esperados.

Java ofrece una gran flexibilidad y potencia para interactuar con bases de datos a través de JDBC. Los desarrolladores pueden trabajar con una variedad de bases de datos relacionales y no relacionales, y ejecutar consultas complejas para satisfacer las necesidades de sus aplicaciones.

## Utilización del resultado de una consulta.

El resultado de una consulta SQL se recupera generalmente en forma de un conjunto de resultados, que es representado en Java por un objeto de tipo `ResultSet`. Puedes utilizar el `ResultSet` para acceder a los datos recuperados de la base de datos y realizar diversas operaciones con ellos. Aquí hay algunas formas comunes de utilizar el resultado de una consulta:

1. **Recuperar Datos**: Utiliza métodos como `getString()`, `getInt()`, `getDouble()`, etc., del objeto `ResultSet` para recuperar los valores de las columnas del conjunto de resultados. Puedes acceder a los datos por nombre de columna o por posición.

   ```java
   ResultSet resultSet = preparedStatement.executeQuery();
   while (resultSet.next()) {
       String nombre = resultSet.getString("nombre");
       int edad = resultSet.getInt("edad");
       // Realiza operaciones con los datos recuperados
   }
   ```

2. **Iterar a través de Resultados**: Utiliza un bucle `while` o `for` para iterar a través de los resultados del `ResultSet`. Esto te permite procesar todos los registros recuperados por la consulta.

   ```java
   while (resultSet.next()) {
       // Procesar cada registro
   }
   ```

3. **Almacenar en Estructuras de Datos**: Puedes almacenar los resultados en estructuras de datos de Java, como listas, mapas o arreglos, para su posterior procesamiento o presentación.

   ```java
   List<Registro> registros = new ArrayList<>();
   while (resultSet.next()) {
       String nombre = resultSet.getString("nombre");
       int edad = resultSet.getInt("edad");
       registros.add(new Registro(nombre, edad));
   }
   ```

4. **Realizar Cálculos o Transformaciones**: Puedes realizar cálculos, transformaciones o filtrar los datos recuperados antes de usarlos. Esto es útil cuando necesitas manipular los datos antes de mostrarlos o almacenarlos.

   ```java
   while (resultSet.next()) {
       double precio = resultSet.getDouble("precio");
       double descuento = precio * 0.1; // Aplicar un descuento del 10%
       // Realizar otras operaciones
   }
   ```

5. **Presentar los Datos**: Los datos recuperados de la base de datos se pueden utilizar para mostrar información al usuario. Esto puede incluir la presentación de datos en una interfaz de usuario, informes, gráficos u otros medios.

6. **Actualizar o Modificar Datos**: En algunas situaciones, es posible que desees actualizar o modificar los datos recuperados y guardar los cambios en la base de datos. Para ello, necesitas construir y ejecutar sentencias SQL de modificación de datos (DML).

7. **Realizar Operaciones de Negocio**: Los datos recuperados se pueden utilizar para realizar operaciones de negocio, como cálculos, generación de informes, toma de decisiones, etc.

8. **Manejar Errores y Excepciones**: Es importante manejar adecuadamente los errores y excepciones que puedan surgir al trabajar con los resultados de una consulta. Esto puede incluir la comprobación de valores nulos, la gestión de excepciones de SQL y la notificación adecuada al usuario en caso de problemas.

En resumen, el resultado de una consulta se utiliza para acceder y procesar los datos de la base de datos, lo que te permite realizar una amplia variedad de operaciones según los requisitos de tu aplicación. La manipulación de datos recuperados es esencial en la mayoría de las aplicaciones, ya que permite interactuar con la información almacenada en la base de datos y utilizarla de manera significativa.

## Ejecución de procedimientos almacenados en la base de datos.

La ejecución de procedimientos almacenados en una base de datos es una técnica que permite a los desarrolladores encapsular lógica de negocio en la base de datos misma. Un procedimiento almacenado es una colección de instrucciones SQL que se almacenan en la base de datos y se pueden invocar mediante un nombre o identificador. Esto ofrece varias ventajas:

1. **Reutilización de Código**: Los procedimientos almacenados permiten reutilizar lógica de negocio en múltiples partes de una aplicación. Esto evita la duplicación de código y garantiza la consistencia en la aplicación.

2. **Mejora del Rendimiento**: Los procedimientos almacenados se pueden compilar y optimizar en la base de datos, lo que puede resultar en un mejor rendimiento en comparación con la ejecución de consultas SQL individuales desde la aplicación.

3. **Seguridad**: Los procedimientos almacenados pueden controlar el acceso a los datos al definir quién tiene permiso para ejecutarlos. Esto ayuda a aplicar políticas de seguridad de manera centralizada.

4. **Mantenibilidad**: Si se necesita cambiar la lógica de negocio, se puede modificar el procedimiento almacenado sin necesidad de actualizar la aplicación. Esto facilita el mantenimiento a largo plazo.

5. **Transacciones**: Los procedimientos almacenados pueden agrupar un conjunto de operaciones en una transacción, lo que garantiza la atomicidad de las operaciones y evita problemas de consistencia de datos.

6. **Abstracción de Datos**: Los procedimientos almacenados permiten abstraer detalles de implementación en la base de datos. Los cambios en la estructura de las tablas no afectan a las aplicaciones que utilizan procedimientos almacenados.

Para ejecutar un procedimiento almacenado desde una aplicación Java, se pueden seguir estos pasos:

1. **Preparar la Llamada al Procedimiento**: Debes preparar una llamada al procedimiento almacenado utilizando una instancia de `CallableStatement`. Puedes pasar parámetros al procedimiento almacenado y especificar cualquier valor de retorno.

   ```java
   CallableStatement callableStatement = connection.prepareCall("{call NombreDelProcedimiento(?, ?, ?)}");
   callableStatement.setInt(1, parametro1);
   callableStatement.setString(2, parametro2);
   callableStatement.registerOutParameter(3, Types.INTEGER); // Ejemplo de parámetro de salida
   ```

2. **Ejecutar el Procedimiento Almacenado**: Utiliza el método `execute()` o `executeQuery()` del `CallableStatement` para ejecutar el procedimiento almacenado.

   ```java
   callableStatement.execute();
   ```

3. **Recuperar Resultados**: Si el procedimiento almacenado devuelve valores, puedes utilizar el `CallableStatement` para recuperarlos.

   ```java
   int valorRetorno = callableStatement.getInt(3); // Ejemplo de recuperación de valor de retorno
   ```

4. **Cerrar Recursos**: Al igual que con otras operaciones de base de datos, es importante cerrar adecuadamente los recursos cuando hayas terminado de utilizarlos.

   ```java
   callableStatement.close();
   ```

Es importante tener en cuenta que la sintaxis y la forma de invocar procedimientos almacenados pueden variar según la base de datos que estés utilizando. Además, debes asegurarte de que la base de datos y el controlador JDBC sean compatibles con la ejecución de procedimientos almacenados.

En resumen, la ejecución de procedimientos almacenados es una práctica común en el desarrollo de aplicaciones empresariales, ya que proporciona ventajas significativas en términos de reutilización, rendimiento y seguridad. Permite separar la lógica de negocio de la capa de acceso a datos y ofrece una forma eficaz de interactuar con una base de datos desde una aplicación Java.

## Gestión de transacciones.

La gestión de transacciones en bases de datos es fundamental para garantizar la integridad y la consistencia de los datos en aplicaciones. En Java, puedes gestionar transacciones utilizando la API JDBC (Java Database Connectivity). Aquí tienes una descripción general de la gestión de transacciones en Java y un ejemplo con MySQL:

**Conceptos Clave de la Gestión de Transacciones:**

1. **Inicio de Transacción**: Debes iniciar explícitamente una transacción antes de ejecutar operaciones que involucren cambios en la base de datos. Esto se hace generalmente llamando al método `setAutoCommit(false)` en la conexión, lo que deshabilita el modo de autocommit.

2. **Commit**: Cuando todas las operaciones dentro de una transacción se han ejecutado con éxito y deseas que los cambios se confirmen de manera permanente en la base de datos, debes llamar al método `commit()` de la conexión.

3. **Rollback**: Si ocurre un error o una excepción durante una transacción y deseas deshacer todos los cambios realizados dentro de esa transacción, debes llamar al método `rollback()` de la conexión.

4. **AutoCommit**: Si `setAutoCommit(true)` está habilitado (modo de autocommit), cada sentencia SQL se confirma automáticamente como una transacción independiente. Si está deshabilitado (`setAutoCommit(false)`), debes llamar explícitamente a `commit()` o `rollback()`.

5. **Puntos de Guardado (Savepoints)**: Puedes establecer puntos de guardado dentro de una transacción para poder volver a un estado anterior si ocurre un error. Esto se logra mediante `Savepoint` y los métodos `setSavepoint()` y `rollbackToSavepoint()`.

**Ejemplo de Gestión de Transacciones en Java con MySQL:**

A continuación, te proporciono un ejemplo de gestión de transacciones en Java utilizando MySQL y JDBC:

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class TransactionExample {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/tu_base_de_datos";
        String usuario = "tu_usuario";
        String contraseña = "tu_contraseña";

        try (Connection connection = DriverManager.getConnection(url, usuario, contraseña)) {
            // Deshabilitar el modo de autocommit
            connection.setAutoCommit(false);

            // Operaciones dentro de la transacción
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO tabla_ejemplo (nombre, edad) VALUES ('Juan', 30)");
            statement.executeUpdate("INSERT INTO tabla_ejemplo (nombre, edad) VALUES ('María', 28)");

            // Confirmar la transacción
            connection.commit();
        } catch (SQLException e) {
            // En caso de error, realizar un rollback para deshacer los cambios
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        }
    }
}
```

En este ejemplo, se deshabilita el modo de autocommit (`setAutoCommit(false)`) antes de realizar las operaciones. Si ocurre una excepción durante las operaciones, se llama a `rollback()` para deshacer los cambios. Si las operaciones tienen éxito, se confirman con `commit()`. La gestión de transacciones es esencial para garantizar que los cambios en la base de datos sean consistentes y seguros.

\pagebreak
