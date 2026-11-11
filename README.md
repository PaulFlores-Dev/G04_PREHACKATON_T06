# Gestión de Eventos y Participantes

Aplicación JavaFX integrada, con un solo proyecto Maven en la raíz y un punto de entrada: com.team_04.MainApp.
Abre inicialmente Eventos. Los botones superiores permiten ir a Participantes y volver sin cerrar la ventana.
Las pantallas se conservan al navegar, incluidos formularios y filtros. Usa «Actualizar listado» en Participantes para volver a consultar MySQL.

## Abrir en IntelliJ

La integración está en la rama feature/integracion-eventos-participantes, basada en origin/develop (6feac66).
Carpeta del worktree:

    C:\Users\marti\Desktop\APRAQ-PROYECTO\G04_INTEGRACION_EVENTOS_PARTICIPANTES

Abrir esa carpeta o su pom.xml como proyecto Maven. Configurar Project SDK y Maven Runner con Java 17.
El proyecto original G04_PREHACKATON_T06 sigue en main; conserva su .idea local.

## Iniciar en esta computadora (PowerShell)

El MySQL existente gestion_eventos-mysql-1 usa 127.0.0.1:3309.
La configuración privada db.properties ya quedó creada en este worktree, fuera de Git.

    Set-Location 'C:\Users\marti\Desktop\APRAQ-PROYECTO\G04_INTEGRACION_EVENTOS_PARTICIPANTES'
    $env:JAVA_HOME = 'C:\Users\marti\.jdks\corretto-17.0.16'
    $env:Path = "$env:JAVA_HOME\bin;$env:Path"
    docker start gestion_eventos-mysql-1
    .\mvnw.cmd -B verify
    .\mvnw.cmd javafx:run

Cerrar la ventana termina la aplicación. No se necesita Maven instalado: se incluye Maven Wrapper.
También se puede ejecutar MainApp desde IntelliJ; el directorio de trabajo debe ser la raíz del proyecto.

## Configuración para otra computadora

Requiere JDK 17, Docker con MySQL accesible y acceso a Maven Central para la primera descarga.
Copiar db.properties.example como db.properties y completar una contraseña local.
Ambos módulos y el reporte Jasper usan esa misma configuración.

Orden de resolución: variables DB_URL, DB_USER, DB_PASSWORD sobre las claves del archivo.
El archivo se busca en la raíz; como compatibilidad se acepta gestion_eventos/db.properties.
Para otra ubicación usar la variable DB_CONFIG con la ruta completa.
Las contraseñas y demás valores del archivo siguen la sintaxis Java Properties (por ejemplo, una barra invertida se escribe doble).
No se incluye ninguna contraseña funcional en archivos versionados.

Para crear el contenedor en una computadora nueva, crear gestion_eventos/.env con
MYSQL_ROOT_PASSWORD y MYSQL_PASSWORD, y ejecutar:

    docker compose --env-file .\gestion_eventos\.env -f .\gestion_eventos\compose.yaml up -d

No cambiar el nombre del proyecto Compose ni eliminar el volumen de un contenedor existente.
Las variables de inicialización de MySQL no cambian contraseñas de un volumen ya creado.
El puerto 3307 pertenece a APRAQ y la aplicación integrada no lo utiliza.

## Esquema y conservación de datos

sql/02_integracion.sql crea gestion_eventos y las tablas faltantes eventos, participantes e inscripciones
con IF NOT EXISTS. No ejecuta DROP, DELETE, TRUNCATE ni reemplaza tablas existentes.
En esta computadora ya se aplicó y se repitió con éxito; los dos participantes originales quedaron idénticos.
El respaldo previo está en .local-backups/participantes-before.sql, ignorado por Git.

Para aplicar el script en el contenedor existente (sin exponer contraseñas):

    Get-Content -Raw .\sql\02_integracion.sql |
        docker exec -i gestion_eventos-mysql-1 sh -c 'export MYSQL_PWD=$MYSQL_PASSWORD; exec mysql -u$MYSQL_USER gestion_eventos'

script.sql conserva el esquema histórico BD_T04 y gestion_eventos/01_participantes.sql el esquema original
de Participantes; ambos ahora toleran tablas ya existentes. Para la integración usar sql/02_integracion.sql.
No se trasladaron registros de BD_T04 ni se accedió a la base de APRAQ.
Si existen eventos históricos en otro servidor, deben importarse después de revisar sus identificadores
y relaciones; esta integración no mezcla bases automáticamente.

## Inspección y cambios

- Antes: Eventos en la raíz, Java/JavaFX 21, JDBC fijo a 3307, JasperReports 6.21.0.
  Participantes en gestion_eventos, Java/JavaFX 17, JDBC externo y reporte HTML sin biblioteca adicional.
- Ahora: un solo pom.xml, Java 17, JavaFX Controls/FXML 17.0.12, MySQL Connector/J 8.4.0.
  El código y FXML de Participantes se trasladaron a src/main conservando sus paquetes y lógica.
  Se retiraron su POM, descriptor modular y los lanzadores alternativos.
- Eventos conserva registro, listado, edición, eliminación, modalidades y estados:
  Programado, En Curso, Finalizado y Cancelado.
- Participantes conserva registro, listado, edición, eliminación con confirmación, búsqueda
  por DNI/nombre/apellidos/correo, filtro por institución y reporte de los resultados filtrados.
- JasperReports 6.21.3 mantiene la plantilla JRXML existente y resuelve OpenPDF 1.3.32 desde Maven Central.
  También utiliza ECJ para compilar las expresiones del reporte y Swing para JasperViewer.
  Se cierran conexión y plantilla después de llenar el reporte; el visor se abre en el hilo Swing.
  El reporte también funciona sin eventos y permite exportar PDF desde JasperViewer.
- El reporte de Participantes vuelve a consultar MySQL, genera HTML escapado y permite imprimir
  o guardar PDF desde el navegador. No requiere Jasper ni JavaFX Web.
- Se movió Maven Wrapper a la raíz. .idea y target dejaron de versionarse;
  .env, db.properties, respaldos y resultados de validación quedan ignorados.

## Validación reproducible

Pruebas de reportes sin base de datos:

    .\mvnw.cmd -B verify

Prueba adicional con JavaFX real y MySQL configurado (requiere escritorio; solo consulta datos):

    .\mvnw.cmd -B '-DintegrationTests=true' test

Se ejecutaron tres pruebas con JDK 17: todas pasaron.
La prueba integrada abre App, acciona los botones de navegación en ambos sentidos, comprueba
que los listados coincidan con cada tabla y genera ambos reportes desde MySQL.
Resultados reales: 0 eventos y 2 participantes.
Archivos generados: target/verification/eventos.pdf y target/verification/participantes.html.
Las otras pruebas comprueban el escape HTML, el botón de impresión y la compilación/llenado/exportación PDF
de la plantilla Jasper con un evento de ejemplo y su estado.
Los reportes HTML contienen datos locales y no se suben a Git.

También se comprobó el arranque con mvnw.cmd javafx:run, cuyo punto de entrada es MainApp.

Pendiente de revisión manual: registrar/editar/eliminar datos reales con ambos formularios, cancelar y confirmar
la eliminación de Participantes, combinar búsquedas y filtros, revisar visualmente JasperViewer y el HTML
en el navegador, y completar el diálogo de impresión o impresión física.
No se borraron ni editaron participantes reales para probar esas operaciones.

## Revisión antes del merge

Los cambios están en la rama feature local. Revisar el diff y los resultados antes de publicar o fusionar.
No se hizo push ni merge a develop.