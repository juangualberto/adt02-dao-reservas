# Gestión de Inventario

Aplicación que lee la información de JSON/XML y la pasa a la base de datos.

## Creación de la BBDD

Instalamos Docker si no estuviese:

```bash
sudo apt install docker.io docker-registry docker-compose 
```

Para no tener que usar "sudo" debemos añadir el usuario al grupo de Docker:

En el fichero /etc/group buscamos una línea similar a:

> docker:x:136

Y le añadimos nuestro username:

> docker:x:136:usuario

Ahora o bien lanzamos los servicios con systemctl o bien reiniciamos la máquina, lo que más rápido sea.

Creamos un archivo docker-compose.yml para la base de datos MySQL:

```yml
version: '3.1'

services:

  db-inventario:
    image: mysql
    # NOTE: use of "mysql_native_password" is not recommended: https://dev.mysql.com/doc/refman/8.0/en/upgrading-from-previous-series.html#upgrade-caching-sha2-password
    # (this is just an example, not intended to be a production configuration)
    command: --default-authentication-plugin=mysql_native_password
    restart: "no"
    environment:
      MYSQL_ROOT_PASSWORD: zx76wbz7FG89k

  adminer:
    image: adminer
    restart: "no"
    ports:
      - 8181:8080
```

Para crear y levantar el servicio la primera vez:

> docker-compose -f docker-compose.yml up -d

