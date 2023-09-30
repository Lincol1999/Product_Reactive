
FROM openjdk:11-jdk-slim as builder
LABEL maintainer="{YOUR_EMAIL}"

WORKDIR /app

COPY /.mvn ./.mvn
COPY ./mvnw .
COPY ./pom.xml .

RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target/
# RUN ./mvnw dependency:go-offline

COPY ./src ./src/

RUN ./mvnw clean package -DskipTests

#===========================================================================================
FROM adoptopenjdk/openjdk11:alpine
WORKDIR /app
ARG TARGET_FOLDER=/app/target
COPY --from=builder ${TARGET_FOLDER}/api-products-reactivo-0.0.1-SNAPSHOT.jar .
ARG PORT_APP=8001
ENV PORT ${PORT_APP}

EXPOSE $PORT

CMD ["java","-jar","api-products-reactivo-0.0.1-SNAPSHOT.jar"]

