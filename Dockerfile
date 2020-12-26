FROM openjdk:8-jdk-alpine as builder
WORKDIR application
ADD ./target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM openjdk:8-jdk-alpine
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT exec java $JAVA_OPTS $JAVA_DEBUG_OPTS org.springframework.boot.loader.JarLauncher
EXPOSE 8886