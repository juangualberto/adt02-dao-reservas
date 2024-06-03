# Proyecto INGESTA

# Ingesta de tareas batch

Desde el sistema de reservas de Viajes El Corte Gallego cada día nos pasan una tarea **batch** con las reservas que han hecho sus agentes comerciales para confirmarlas con nosotros (nosotros somos el tour operador Expedición Mórbida).

## Instrucciones

* Nos dan el esqueleto de la aplicación, es **obligatorio** usar este esqueleto MAVEN. 
* Hay que modificar el **pom.xml** para que incluya el driver de la base de datos
* Hay que usar la configuración que viene por defecto en el docker-compose que se da (ver carpeta stack) para que  conecte con la base de datos. **No se puede modificar el docker-compose.yml**. Atención con los datos que vienen en este archivo.
* Levanta los nuevos contenedores antes de comenzar a programar.
* La base de datos se llamará **reservas**, *NO SE ADMITIRÁ OTRO NOMBRE*.
* Si la base de datos no está creada, nuestro programa debe crearla.
* Si las tablas no están creadas, nuestro programa debe crearlas.
* **Sólo hay un main** todo lo tiene que hacer el mismo programa.
* Las tablas deben tener sus respectivas claves foráneas.
* Se puede elegir hacerlo con XML o con JSON, las dos opciones valen igual.
* Hacer los DAO necesarios para todas las entidades.
* Cada clase entidad en un archivo diferente, no se pueden poner todas en el mismo archivo.
* Hay que hacer una rutina de carga del JSON a MySQL que se ejecute desde maven (ya está configurado el pom.xml). Puedes probar como funciona ejecutando ***mvn exec:java***. Antes de meter los datos en la base de datos, si no existen, por ejemplo, algunos clientes, deberán ser introducidos previamente a la inserción, o bien modificar el DAO para que arregle, que si da una excepción que no está esa clave foránea, de de alta el cliente para poder seguir (como veremos que se puede hacer con herramientas ORM).

La **tarea batch** no es más que un JSON con este formato:

**Version JSON**

```json
{
   "reservas": [
      {
         "reserva": {
            "cliente": {
               "id": 1,
               "nombre": "Juan Perez",
               "contacto": {
                  "email": "juan.perez@email.com",
                  "telefono": "+123456789"
               }
            },
               "alojamiento": {
                  "id": 2,
                  "tipo": "hotel",
                  "nombre": "Hotel Ejemplo",
                  "direccion": "Calle Principal 123, Ciudad",
                  "telefono": "+987654321"
               },
               "entrada": "2023-01-01",
               "salida": "2023-01-05",
               "pension": "pension_completa"
         }
      }
   ]
}
```

**Version XML**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<reservas>
   <reserva>
      <cliente>
         <id>1</id>
         <nombre>Juan Pérez</nombre>
         <contacto>
               <email>juan.perez@email.com</email>
               <telefono>+123456789</telefono>
         </contacto>
      </cliente>
      <alojamiento>
         <id>2</id>
         <tipo>hotel</tipo>
         <nombre>Hotel Ejemplo</nombre>
         <direccion>Calle Principal 123, Ciudad</direccion>
         <telefono>+987654321</telefono>
      </alojamiento>
      <entrada>2023-01-01</entrada>
      <salida>2023-01-05</salida>
      <pension>pension_completa</pension>
   </reserva>
</reservas>
```

Hay que implementar lo siguiente:

1) Nos piden que hagamos un programa que introduzca este JSON en la base de datos. Deberás crear, desde Java, las tablas si no existen, y además crear los clientes y otras tablas necesarias antes de las reservas.
2) Crea un disparador (o impleméntalo en JAVA) que impida que una misma persona haga reservas para fechas que ya tiene otra reserva anterior.

Puedes probar si el punto 2 funciona con esta nueva tarea batch:

**Versión JSON**:

```json
{
    "reservas": [
      {
      "reserva": {
         "cliente": {
            "id": 1,
            "nombre": "Juan Pérez",
            "contacto": {
               "email": "juan.perez@email.com",
               "telefono": "+123456789"
            }
         },          
         "alojamiento": {
               "id": 2,
               "tipo": "hotel",
               "nombre": "Hotel Ejemplo",
               "direccion": "Calle Principal 123, Ciudad",
               "telefono": "+987654321"
         },
         "entrada": "2023-01-02",
         "salida": "2023-01-04",
         "pension": "pension_completa"
      }
      }
    ]
 }
```


**Version XML**:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<reservas>
    <reserva>
        <cliente>
            <id>1</id>
            <nombre>Juan Pérez</nombre>
            <contacto>
                <email>juan.perez@email.com</email>
                <telefono>+123456789</telefono>
            </contacto>
        </cliente>
        <alojamiento>
            <id>2</id>
            <tipo>hotel</tipo>
            <nombre>Hotel Ejemplo</nombre>
            <direccion>Calle Principal 123, Ciudad</direccion>
            <telefono>+987654321</telefono>
        </alojamiento>
        <entrada>2023-01-03</entrada>
        <salida>2023-01-04</salida>
        <pension>pension_completa</pension>
    </reserva>
</reservas>
```

## Realización del proyecto

**PRIMERA PARTE:**

**Hacer un programa que introduzca XML/JSON en la base de datos. Debemos crear desde Java las tablas, si no existen, y además crear los clientes y otras tablas necesarias antes de las reservas.**

## Creación de los contenedores

Nos vamos a la carpeta stack y podemos controlar la pila de contenedores con estos comandos:

* **docker-compose up -d**: Configura, crea y levanta los contenedores. Usamos *-d* para dejarlo corriendo en segundo plano.
* **docker-compose stop**: Para los contenedores de esta pila.
* **docker-compose start**: Arranca los contenedores de esta pila.
* **docker-compose down**: Para (si están andando) y elimina los contenedores.

Ejemplo de uso:

```bash
➜  stack docker-compose up -d
Creating network "stack_default" with the default driver
Creating stack_db-reservas_1 ... done
Creating stack_adminer_1     ... done
➜  stack docker-compose stop 
Stopping stack_adminer_1     ... done
Stopping stack_db-reservas_1 ... done
➜  stack docker-compose start
Starting db-reservas ... done
Starting adminer     ... done
➜  stack docker-compose down 
Stopping stack_adminer_1     ... done
Stopping stack_db-reservas_1 ... done
Removing stack_adminer_1     ... done
Removing stack_db-reservas_1 ... done
Removing network stack_default
```

##  Preparamos el pom.xml

Es un proyecto Maven y necesitamos añadir la dependencia mysql:

```xml
    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>8.0.33</version>
    </dependency>
```

Para leer JSON ahora preparamos la dependencia GSON:

```xml
    <dependency>
      <groupId>com.google.code.gson</groupId>
      <artifactId>gson</artifactId>
      <version>2.10.1</version>
    </dependency>
```

Para leer XML añadimos la una interfaz JAXB, una implementación JAXB y binding para conectar los atributos a las etiquetas:

```xml
    <dependency>
      <groupId>org.codehaus.mojo</groupId>
      <artifactId>jaxb2-maven-plugin</artifactId>
      <version>2.5.0</version>
    </dependency>

    <dependency>
      <groupId>javax.xml.bind</groupId>
      <artifactId>jaxb-api</artifactId>
      <version>2.4.0-b180725.0427</version>
   </dependency>

    <dependency>
      <groupId>javax.activation</groupId>
      <artifactId>activation</artifactId>
      <version>1.1.1</version>
    </dependency>
```

## Conectores

Usamos un conector (JDBC) porque nos permite abstraer la lógica de la aplicación 

El uso de conectores al programar bases de datos, como JDBC (Java Database Connectivity) en el caso de Java, o ODBC en .NET ofrece varias ventajas significativas como son:

1. **Abstracción de la base de datos:**
   - **Ventaja:** Los conectores proporcionan una capa de abstracción entre la aplicación y la base de datos. Esto significa que puedes escribir código de aplicación sin tener que preocuparte por los detalles específicos de la base de datos subyacente.
   - **Beneficio:** Facilita la portabilidad del código, ya que puedes cambiar la base de datos sin tener que modificar sustancialmente tu código de aplicación.

2. **Manejo de conexiones:**
   - **Ventaja:** Los conectores como JDBC manejan la apertura, cierre y gestión de conexiones a la base de datos. También pueden gestionar conexiones de manera eficiente mediante el uso de pools de conexiones.
   - **Beneficio:** Mejora el rendimiento y la eficiencia al evitar la apertura y cierre frecuentes de conexiones, y gestiona automáticamente los recursos asociados con las conexiones.

3. **Gestión de transacciones:**
   - **Ventaja:** Los conectores suelen ofrecer funciones integradas para la gestión de transacciones, lo que permite ejecutar operaciones atómicas y consistentes en la base de datos.
   - **Beneficio:** Asegura la integridad de los datos al proporcionar control sobre las transacciones, permitiendo realizar operaciones en lotes y revertir cambios en caso de errores.

4. **Seguridad:**
   - **Ventaja:** Los conectores a menudo incluyen mecanismos de seguridad integrados para autenticación y autorización, lo que garantiza un acceso seguro a la base de datos.
   - **Beneficio:** Contribuye a proteger la base de datos contra accesos no autorizados y ayuda a garantizar la confidencialidad e integridad de los datos.

5. **Optimizaciones y Rendimiento:**
   - **Ventaja:** Los conectores suelen implementar estrategias de optimización y caché para mejorar el rendimiento de las consultas y reducir el tiempo de respuesta.
   - **Beneficio:** Aumenta la eficiencia y reduce la carga en la base de datos, mejorando así el rendimiento general de la aplicación.

6. **Soporte para múltiples bases de datos:**
   - **Ventaja:** Los conectores, como JDBC, están diseñados para ser independientes de la base de datos subyacente. Esto significa que puedes cambiar de una base de datos a otra sin tener que cambiar drásticamente tu código.
   - **Beneficio:** Facilita la adaptabilidad a diferentes entornos de base de datos según las necesidades de la aplicación.


Implementamos la conexión según el patrón factoría, es decir, una clase estática que contiene la configuración de la que crear los objetos (sin necesidad de repetir la configuración cada vez que se crean).

```java

// aquí va el package e imports...

public class FactoriaConexion {

    private static Connection conn;

    public static Connection getConnection() {
        conn = null;
        try (FileInputStream fis = new FileInputStream("db.properties")) {
            if (!createDatabase()) {
                System.out.println("--== CONEXION IMPOSIBLE ==--");
            }
            Properties prop = new Properties();
            prop.load(fis);             
            if (prop.getProperty("driver").equals("MySQL")) {
                conn = DriverManager.getConnection(
                    "jdbc:mysql://" + prop.getProperty("host") + ":" +
                        prop.getProperty("port")+"/"+prop.getProperty("database"), prop);
                }
        } catch (SQLException e) {
            System.out.println("Error de sintaxis en el código SQL");
        } catch (ClassCastException e) {
            System.out.println("Driver no encontrado");
        } catch (IOException e) {
            System.out.println("Archivo de configuración no encontrado");
        } 
        return conn;
    }

   // resto de métodos

}
```

Este método carga la configuración de un fichero de propiedades como el siguiente:

```properties
user=root
password=s83n38DGB8d72
useUnicode=yes
useJDBCCompliantTimezoneShift=true
port=33307
database=reservas
host=localhost
driver=MySQL
```

## Leer XML/JSON

Para leer JSON, nos damos cuenta que el JSON tiene un formato extraño, incluye la propiedad *reservas* y también *reserva* que, en teoría, no son necesarias. Para poder leerlo vamos a necesitar un wrapper que añada esta "etiqueta".

Wrapper para **Reserva**:

```java
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReservaWrapper {
    private Reserva reserva;
}
```

Wrapper para **Reservas**:

```java
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class ReservasWrapper {
    private List<ReservaWrapper> reservas;

    // Constructor, getters, setters, etc.
}
```

Ya podemos leer el JSON que nos dan con esta modificación así:

```java
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

```

## Impedir reservas solapadas

**SEGUNDA PARTE** 

**Crea un disparador (o impleméntalo en JAVA) que impida que una misma persona haga reservas para fechas que ya tiene otra reserva anterior.**

Para que no se solapen dos reservas de la misma persona (ojo, el mismo día que salimos puede ser que entremos en otra, o el mismo día en entramos puede acabar una reserva nueva que se almacene, eso **sí** sería válido) comprobamos las fechas de entrada y salida de la nueva reserva contra la base de datos:

```sql
DELIMITER //
DROP TRIGGER IF EXISTS `reservas_solapadas`//

CREATE TRIGGER `reservas_solapadas` 
BEFORE INSERT ON `reserva` 
FOR EACH ROW
BEGIN
    IF (SELECT COUNT(*) 
      FROM `reserva` 
      WHERE `reserva`.`cliente`= NEW.`cliente` AND ((`reserva`.`entrada` < NEW.`entrada` 
        AND `reserva`.`salida` > NEW.`entrada`) OR 
        (`reserva`.`entrada` < NEW.`salida` 
        AND `reserva`.`salida` > NEW.`salida`))) > 0 
    THEN
      SIGNAL sqlstate '45001' 
        SET message_text = 'No se permite solapar reservas por usuario.'; 
    END IF;

END//
DELIMITER ;
```