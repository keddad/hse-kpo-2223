FROM gradle:jdk17 as builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar

FROM openjdk:17-jre-slim

EXPOSE 8080    
COPY --from=builder /home/gradle/src/build/libs/org.keddad.cafe-all.jar /app/
WORKDIR /app

CMD ["java", "-jar", "org.keddad.cafe-all.jar"]
