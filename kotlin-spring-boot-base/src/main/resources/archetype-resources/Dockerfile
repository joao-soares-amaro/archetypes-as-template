FROM maven:3.8.1-jdk-11-openj9 AS build

WORKDIR /compiling

COPY src src
COPY pom.xml pom.xml

RUN --mount=type=cache,target=/root/.m2/repository mvn spring-aot:test-generate test
RUN --mount=type=cache,target=/root/.m2/repository mvn clean package spring-aot:generate -DskipTests

FROM adoptopenjdk/openjdk11-openj9:alpine-jre

WORKDIR /application

COPY datadog/dd-java-agent.jar dd-java-agent.jar
COPY --from=build /compiling/target/* .
COPY --from=build /compiling/target/${artifactId}*.jar ${artifactId}.jar

# TODO review exposed port
EXPOSE 8081 8080

CMD ["java","-javaagent:dd-java-agent.jar","-jar","${artifactId}.jar"]
