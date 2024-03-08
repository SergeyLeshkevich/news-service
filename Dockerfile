FROM openjdk:17-alpine

ADD /build/libs/news-service-0.0.1-SNAPSHOT.jar /app/

CMD ["java", "-Xmx200m", "-jar", "/app/news-service-0.0.1-SNAPSHOT.jar"]

EXPOSE 8086