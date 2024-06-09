FROM eclipse-temurin:19-jre-alpine

RUN mkdir /app
COPY ./build/libs/*-all.jar /app/ktor-server.jar
#COPY keystore.jks /./keystore.jks
ENTRYPOINT ["java", "-jar", "/app/ktor-server.jar"]