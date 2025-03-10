# Dockerfile
FROM nvidia/cuda:12.1.1-base-ubuntu22.04

# FFMPEG 설정
RUN apt-get update && apt-get install -y \
    ffmpeg \
    && apt-get clean

VOLUME /tmp
ARG JAR_FILE=build/libs/postvue-api-server.jar
COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]