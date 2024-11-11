# Usa una imagen base de Java 21
FROM eclipse-temurin:21-jdk

# Establece el directorio de trabajo en el contenedor
WORKDIR /app

RUN apt update && apt -y install graphviz  

# Copia el archivo JAR de tu aplicación al contenedor
COPY . .
COPY ./repository /root/.m2/repository
RUN ./mvnw install

# COPY target/rdevs.webtexteditor.backend-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto en el que tu aplicación se ejecuta
EXPOSE 8092

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "target/rdevs.webtexteditor.backend-0.0.1-SNAPSHOT.jar"]
