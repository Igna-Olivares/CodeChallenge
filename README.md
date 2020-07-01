# CodeChallenge

To start the API you should build the project with gradle and execute the generated .jar
It contains a dockerfile, to simplify the API to start.

For gradle build run this commands on the root of the project
- "gradlew build" on Windows or "./gradlew build" on Unix
- run the jar on "build/libs/code-challenge-docker-1.0.0.jar"

For docker run this commands on the root of the project
- "gradlew build" on Windows or "./gradlew build" on Unix
- "docker build --build-arg JAR_FILE=build/libs/*.jar -t spring/code-challenge-docker ." 
- "docker run -p 8080:8080 docker.io/spring/code-challenge-docker"

URl Swagger: http://localhost:8080/swagger-ui.html