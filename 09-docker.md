# Introducción a Docker

Docker es un sistema de gestión de contenedores, para automatizar el despliegue de aplicaciones dentro de contenedores de software, proporcionando una capa adicional de abstracción y automatización de virtualización de aplicaciones en múltiples sistemas operativos.

En vez de usar máquinas virtuales (más pesadas), docker usa el espacio de Kernel de Linux y comparte partes con él, aislando y simplificando las aplicaciones. 

Si usamos docker en una máquina con MAC OS o Microsoft Windows, será necesario tener una máquina virtual Linux que será la que nos ofrezca el soporte Linux. Así las últimas versiones de Windows incorporan el WSL (Windows Subsystem for Linux) por ejemplo, con las que se hace mucho más sencillo utilizar Docker.

## Comandos útiles de docker

Listado de contenedores:

```bash
$ docker ps
```

Parar un contenedor:

```bash
$ docker stop [contenedor]
```

Borrar un contenedor:

```bash
$ docker rm [contenedor]
```

Listado de imágenes:

```bash
$ docker image ls
```

Borrar una imagen:

```bash
$ docker rmi [imagen]
```

Listado de volúmenes:

```bash
$ docker volume ls
```

Borrar todos los volúmenes que no estén usados por ningún contenedor:

```bash
$ docker volume prune
```

Borrar un volumen concreto:

```bash
$ docker volume rm [volumen]
```

Borrar todo lo relacionado con un docker-compose.yml

```bash
$ docker-compose -f file down
```

## Instalación de Docker

Para instalar en sistemas Linux basados en Debian:

$ sudo apt update
$ sudo apt install docker.io docker-compose

Para que el usuario local, por ejemplo, matinal o tarde puedan usar docker, crear contenedores, manipularlos, etc. deberemos añadir nuestros usuarios al grupo docker
con el comando:

> $ sudo nano /etc/group

ó

> $ sudo vim /etc/group

modificamos la última línea:

> docker:x:131:matinal

Ahora cerramos sesión y volvemos a entrar para que coja los cambios.

Para asegurar que está todo andando, reiniciamos el servicio:

> sudo systemctl restart docker

## Cómo crear un contenedor desde la imagen de Ubuntu

> $ git pull ubuntu
> $ docker run --name lamp-server-plantilla -it ubuntu:latest bash

Ahora nos abre una terminal dentro del nuevo contenedor:

```bash
# apt update
# apt install net-tools
# apt install apache2
# apt install mysql-server
# apt install php
# apt-get install libapache2-mod-php php-mysql
```

## Copias de los contenedores

En este capítulo vamos a aprender cómo llevarnos los datos de nuestros contenedores sin tener que estar guardando imágenes completas.

La idea es hacer copias de seguridad solamente de los volúmenes de datos.
https://docs.docker.com/storage/volumes/

Como es un poco lioso se puede usar el contenedor (loomchild/volume-backup) que contiene un script que facilita la creación/restauración de volúmenes.

https://github.com/loomchild/volume-backup

https://hub.docker.com/r/loomchild/volume-backup

Por ejemplo, joomla https://hub.docker.com/r/bitnami/joomla/

Se crean dos contenedores con dos volúmenes (uno para webserver y otro mariadb):

> $ mkdir joomla-docker
> $ cd joomla-docker
> $ curl -sSL https://raw.githubusercontent.com/bitnami/bitnami-docker-joomla/master/docker-compose.yml > docker-compose.yml
> $ docker-compose up -d
> $ cat docker-compose.yml

```yaml
version: '2'
services:
  mariadb:
    image: 'bitnami/mariadb:latest'
    environment:
      - MARIADB_USER=bn_joomla
      - MARIADB_DATABASE=bitnami_joomla
      - ALLOW_EMPTY_PASSWORD=yes
    volumes:
      - 'mariadb_data:/bitnami'
  joomla:
    image: 'bitnami/joomla:latest'
    environment:
      - MARIADB_HOST=mariadb
      - MARIADB_PORT_NUMBER=3306
      - JOOMLA_DATABASE_USER=bn_joomla
      - JOOMLA_DATABASE_NAME=bitnami_joomla
      - ALLOW_EMPTY_PASSWORD=yes
    labels:
      kompose.service.type: nodeport
    ports:
      - '80:80'
      - '443:443'
    volumes:
      - 'joomla_data:/bitnami'
    depends_on:
      - mariadb
volumes:
  mariadb_data:
    driver: local
  joomla_data:
    driver: local
```

Se han creado los dos volúmenes (se han concatenado a los nombres de volumen
 el directorio desde donde se ha ejecutado):

> $  docker volume ls
> DRIVER VOLUME NAME
> local joomla-docker_joomla_data
> local joomla-docker_mariadb_data

IMPORTANTE: Antes de hacer backup hay que parar los contenedores

> $ docker-compose stop

Backup volúmenes

> $ docker run -v joomla-docker_joomla_data:/volume --rm loomchild/volume-backup backup - > joomla-docker_joomla_data.tar.bz2
> $ docker run -v joomla-docker_mariadb_data:/volume --rm loomchild/volume-backup backup - > joomla-docker_mariadb_data.bz2
> $ ls joomla-docker_joomla_data.tar.bz2 joomla-docker_mariadb_data.bz2

Para hacer la prueba, he eliminado los volúmenes para luego restaurarlos desde los ficheros.

> $ docker volume rm joomla-docker_joomla_data joomla-docker_mariadb_data

Restaurar volúmenes:

> $ cat joomla-docker_joomla_data.tar.bz2 | docker run -i -v joomla-docker_joomla_data:/volume --rm loomchild/volume-backup restore -
> $ cat joomla-docker_mariadb_data.bz2 |  docker run -i -v joomla-docker_mariadb_data:/volume --rm loomchild/volume-backup restore -

Se vuelven a iniciar los contenedores:

$ docker-compose up -d

## Apache2 como contenedor

Instalación del servidor

A continuación vamos a ver los pasos a seguir para crear el contenedor (Docker) a partir de una imagen personalizada. 

Pimero preparamos los archivos de la Web que contendrá el servidor:

$ mkdir public-html

$ cat > ./public-html/index.html

```html
<!DOCTYPE html>
<html lang="es">
  <head>
    <meta charset="utf-8" />
    <title>Apache2</title>
  </head>
  <body>
    <h1>Hola Mundo</h1>
  </body>
</html>
```

Ahora creamos el fichero "Dockerfile" con este contenido:

cat > Dockerfile

```Dockerfile
FROM httpd:2.4
COPY ./public-html/ /usr/local/apache2/htdocs/ (CTRL+C)
```

El siguiente paso es generar la imagen que nos servirá de plantilla para nuestros contenedores:

$ docker build -t mi-imagen-apache2 .

Ahora podemos crear un contenedor "vivo", lo creamos y lanzamos con:

$ docker run -dit --name mi-container-apache -p 8080:80 mi-imagen-apache2

Configuración

El servidor escuchará el puerto 8008, NO eliminar el puerto 80.

Rehacer la imagen (cada vez que hagamos cambios):

$ docker build -t mi-imagen-apache2 .

Parar el contenedor (para poder borrarlo):

$ docker stop mi-container-apache

Borrar el contenedor:

$ docker rm mi-container-apache

Crear de nuevo el contenedor y lanzarlo:

$ docker run -dit --name mi-container-apache -p 8080:80 mi-imagen-apache2

Copiar el archivo de configuración del contenedor a la máquina física (se hace en la misma carpeta donde está el Dockerfile):

$ docker cp mi-container-apache:/usr/local/apache2/conf/httpd.conf .

Modificamos el Dockerfile:

```Dockerfile
FROM httpd:2.4
COPY ./public-html/ /usr/local/apache2/htdocs/
COPY ./httpd.conf /usr/local/apache2/conf/
```

Creación del fichero htpasswd

En el directorio de trabajo (donde está el Dockerfile) ejecuto:

$ htpasswd -c .htpasswd usuario

Si no lo tengo instalado:

$ sudo apt install apache2-utils

Añadir el .htpassword a la imagen

Modifico el Dockerfile para que quede así:

```Dockerfile
FROM httpd:2.4
COPY ./public-html/ /usr/local/apache2/htdocs/
COPY ./httpd.conf /usr/local/apache2/conf/
COPY .htpasswd /usr/local/apache2/conf/
```

Añadimos la autenticación a Apache

```conf
<Location "/dos"> 
    AuthType basic
    AuthName "DOS: Autenticación Básica"
    AuthUserFile /usr/local/apache2/conf/.htpasswd
    Require valid-user
</Location>
```

Creo un script para automatizar la actualización de los contenedores

(fichero reload.sh)

```bash
#!/bin/bash
echo "INICIO"
echo "Parando el contenedor: "
docker stop mi-container-apache
echo "Eliminando el contenedor: "
docker rm mi-container-apache
echo "Generando la nueva imagen: "
docker build -t mi-imagen-apache2 .
echo "Creando el contenedor y poniéndolo en marcha"
docker run -dit --name mi-container-apache -p 8080:80 mi-imagen-apache2
echo "TERMINADO!!!"
```bash

Autenticación DIGEST

Primero: creamos el archivo de contraseñas:

htdigest -c .htdigest restringido pepe

Segundo: modificamos el Dokcerfile:

```Dockerfile
FROM httpd:2.4
COPY ./public-html/ /usr/local/apache2/htdocs/
COPY ./httpd.conf /usr/local/apache2/conf/
COPY .htpasswd /usr/local/apache2/conf/
COPY .htdigest /usr/local/apache2/conf/
```

Tercero: Modifcamos el httpd.conf añadiendo al final:

```conf
<Location "/tres"> 
    AuthType digest
    AuthName "restringido"
    AuthDigestProvider file
    AuthUserFile /usr/local/apache2/conf/.htdigest
    Require valid-user
</Location> 
```

Añadiendo seguridad SSL al contenedor:

Modificamos el script de generación de imágenes como sigue:

```bash
#!/bin/bash
echo "INICIO"
echo "Parando el contenedor: "
docker stop mi-container-apache
echo "Eliminando el contenedor: "
docker rm mi-container-apache
echo "Eliminando certificados antiguos"
rm -fr ssl
echo "Creando certificados nuevos:"
mkdir ssl 
openssl genrsa -out ssl/server.key 1024
openssl req -new -key ssl/server.key -out ssl/server.csr
openssl x509 -req -days 365 -in ssl/server.csr -signkey ssl/server.key -out ssl/server.crt
echo "Generando la nueva imagen: "
docker build -t mi-imagen-apache2 .
echo "Creando el contenedor y poniéndolo en marcha"
docker run -dit --name mi-container-apache -p 8080:80 mi-imagen-apache2
docker run -dit --name mi-container-apache mi-imagen-apache2 
sleep 4
docker ps
echo "TERMINADO!!!"
```

Modificamos el Dockerfile:

```Dockerfile
FROM httpd:2.4
COPY ./public-html/ /usr/local/apache2/htdocs/
COPY ./httpd.conf /usr/local/apache2/conf/
COPY ./httpd-ssl.conf /usr/local/apache2/conf/extra/
COPY .htpasswd /usr/local/apache2/conf/
COPY .htdigest /usr/local/apache2/conf/
COPY ./ssl/ /usr/local/apache2/conf/
COPY ./extra/ /usr/local/apache2/conf/extra/
```

Modificamos el httpd.conf:

```conf
LoadModule ssl_module modules/mod_ssl.so

Listen 443
<VirtualHost *:443>
    ServerName localhost
    SSLEngine on
    SSLCertificateFile "/usr/local/apache2/conf/server.crt"
    SSLCertificateKeyFile "/usr/local/apache2/conf/server.key"
</VirtualHost>

#<IfModule ssl_module>
#SSLRandomSeed startup builtin
#SSLRandomSeed connect builtin
#</IfModule>
```

Ejecutamos el script "reload" y ¡listo!
