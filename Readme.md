# Gestión de Inventario

Aplicación que lee la información de JSON/XML y la pasa a la base de datos.

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

## El problema

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

