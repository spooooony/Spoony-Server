FROM eclipse-temurin:21-jdk-alpine

ENV TZ=Asia/Seoul
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone

WORKDIR /app
COPY build/libs/*.jar spoony-dev.jar
EXPOSE 8080

CMD ["java", "-Duser.timezone=Asia/Seoul", "-jar", "spoony-dev.jar"]