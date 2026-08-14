# ------ STAGE 1: builder ------
FROM gradle:9-jdk21-corretto-al2023 AS builder

WORKDIR /app

COPY gradlew .
COPY gradlew.bat .
COPY settings.gradle.kts .
COPY build.gradle.kts .

COPY gradle ./gradle

COPY core/domain/build.gradle.kts ./core/domain/
COPY core/application/build.gradle.kts ./core/application/
COPY core/infrastructure/build.gradle.kts ./core/infrastructure/
COPY core/presentation/build.gradle.kts ./core/presentation/
COPY core/common/build.gradle.kts ./core/common/
COPY core/bootstrap/build.gradle.kts ./core/bootstrap/

RUN sed -i 's/\r$//' ./gradlew && chmod +x ./gradlew

RUN ./gradlew dependencies --no-daemon

COPY core ./core

RUN ./gradlew :core:bootstrap:bootJar --no-daemon


# ------ STAGE 2: runtime ------
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Cài ffmpeg cho Azure Speech audio convert / streaming
# microdnf dùng cho UBI minimal
USER root

RUN apt-get update \
    && apt-get install -y ffmpeg \
    && rm -rf /var/lib/apt/lists/*

RUN groupadd spring \
    && useradd -r -g spring spring

COPY --from=builder \
    /app/core/bootstrap/build/libs/*.jar \
    app.jar

USER spring

EXPOSE 8386

ENTRYPOINT ["java", "-jar", "app.jar"]