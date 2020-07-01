# CodeChallenge

To start the API you should biuld the project with gradle and execute the generated .jar
It contains a dockerfile, to simplify the API to start.

For gradle build run this commands on the root of the project
- "gradlew build" on Windows or "./gradlew build" on Unix
- run the jar on "build/libs/code-challenge-docker-1.0.0.jar"

For docker run this commands on the root of the project
- "docker build --build-arg JAR_FILE=build/libs/*.jar -t spring/code-challenge-docker ." 
- "docker run -p 8080:8080 docker.io/spring/code-challenge-docker"

URl Swagger: http://localhost:8080/swagger-ui.html

Suposiciones sobre los requisitos:

- Se ha asumido la existendia de la Entidad "Cuenta Bancaria", para el control sobre el balance que dejan las transacciones
- La referencia autogenerada para las transferencias ha sido elegida como una cadena alfanumerica de 6 posiciones para evitar colisiones al generarlas.
- Se ha restringido el formato de fecha al dado. Cuando la fecha no esta presente se toma la del instante de creaci�n de la transacci�n
- En el endpoint de b�squeda, se ha asumido que tanto el filtrado como la ordenacion son opcionales.


Posibles mejoras:

- LLevar todos los textos repetitivos de informaci�n a ficheros .properties y poner internacionalizaci�n.
- Mejora de test de integraci�n, hay cosas que se pueden mejorar.
- Mejora de la documentaci�n en swagger.