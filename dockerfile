# Dockerfile
FROM openjdk:17-jdk-slim

# FFMPEG 설정
RUN apt-get update && apt-get install -y \
    ffmpeg \
    libx264-dev \
    && apt-get clean

VOLUME /tmp
ARG JAR_FILE=build/libs/postvue-api-server.jar
COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]