# Gestión de Eventos y Participantes

Aplicación JavaFX para administrar eventos y participantes desde una sola ventana.

- Eventos: registrar, listar, editar, eliminar, gestionar estados y generar reportes PDF.
- Participantes: registrar, listar, editar, eliminar con confirmación, buscar, filtrar por institución y generar reportes imprimibles.

## Requisitos

- JDK 17 y JAVA_HOME configurado.
- Docker con MySQL en el puerto 3309.
- IntelliJ IDEA (opcional). Abrir el pom.xml de la raíz como proyecto Maven.

## Configuración

1. Crear gestion_eventos/.env con MYSQL_ROOT_PASSWORD y MYSQL_PASSWORD.
2. Iniciar MySQL desde la raíz del proyecto:

       docker compose --env-file .\gestion_eventos\.env -f .\gestion_eventos\compose.yaml up -d

3. Copiar db.properties.example como db.properties y completar las credenciales.
   La conexión debe usar la base gestion_eventos en 127.0.0.1:3309.
4. Crear las tablas sin borrar los registros existentes:

       Get-Content -Raw .\sql\02_integracion.sql |
           docker exec -i gestion_eventos-mysql-1 sh -c 'export MYSQL_PWD=$MYSQL_PASSWORD; exec mysql -u$MYSQL_USER gestion_eventos'

Los archivos .env y db.properties son locales y no se suben a Git.
Si MySQL y la configuración ya existen, basta con iniciar el contenedor.

## Ejecutar en PowerShell

Desde la raíz del proyecto:

    docker start gestion_eventos-mysql-1
    .\mvnw.cmd javafx:run

Punto de entrada: com.team_04.MainApp.
Usar los botones Eventos y Participantes para cambiar de pantalla.

## Comprobar el proyecto

    .\mvnw.cmd -B verify

Prueba adicional de navegación y consultas, con MySQL activo y escritorio disponible:

    .\mvnw.cmd -B '-DintegrationTests=true' test
