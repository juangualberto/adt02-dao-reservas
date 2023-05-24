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

1. [Teoría del tema](./01-introduccion.md).
2. [Creación de la BBDD y CRUD básico](./02-bbdd.md).
3. [Patrones y estrategias de conexión](./03-patron-dao.md).
4. [Apuntes para ampliar docker](/09-docker.md).