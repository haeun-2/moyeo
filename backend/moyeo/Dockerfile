# Build stage

FROM bellsoft/liberica-openjdk-alpine:17 AS builder

WORKDIR /app

COPY . .

RUN ./gradlew clean build -x test


# Run stage
FROM bellsoft/liberica-openjdk-alpine:17

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar
COPY ./pinpoint-agent-2.2.2 /app/pinpoint

EXPOSE 8080

ENTRYPOINT ["java", "-jar","-javaagent:./pinpoint/pinpoint-bootstrap-2.2.2.jar", "-Dpinpoint.agentId=adventcalendarDev","-Dpinpoint.applicationName=adventcalendar","-Dpinpoint.config=./pinpoint/pinpoint-root.config","-Dspring.profiles.active=${SERVER_MODE}","-Duser.timezone=Asia/Seoul", "/app.jar"]