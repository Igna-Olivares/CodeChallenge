# CodeChallenge

To start the API you should biuld the project with gradle and execute the generated .jar
It contains a dockerfile, to simplify the API to start.

URl Swagger: http://localhost:8080/swagger-ui.html

Suposiciones sobre los requisitos:

- Se ha asumido la existendia de la Entidad "Cuenta Bancaria", para el control sobre el balance que dejan las transacciones
- La referencia autogenerada para las transferencias ha sido elegida como una cadena alfanumerica de 6 posiciones para evitar colisiones al generarlas.
- Se ha restringido el formato de fecha al dado. Cuando la fecha no esta presente se toma la del instante de creación de la transacción
- En el endpoint de búsqueda, se ha asumido que tanto el filtrado como la ordenacion son opcionales.


Posibles mejoras:

- LLevar todos los textos repetitivos de información a ficheros .properties y poner internacionalización.
- Mejora de test de integración, hay cosas que se pueden mejorar.
- Mejora de la documentación en swagger.