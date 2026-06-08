# ------ STAGE 1: builder ------
# Nhiệm vụ là gradle build => bootJar => app.jar
FROM gradle:9-jdk21-corretto-al2023 AS builder

WORKDIR /app

# do đã set workdir = /app nên
# thực tế là copy các file bên trái vào /app
COPY gradlew .
COPY gradlew.bat .
COPY settings.gradle.kts .
COPY build.gradle.kts .

# ./ sẽ tương đương với /app
COPY gradle ./gradle

COPY core/domain/build.gradle.kts ./core/domain/
COPY core/application/build.gradle.kts ./core/application/
COPY core/infrastructure/build.gradle.kts ./core/infrastructure/
COPY core/presentation/build.gradle.kts ./core/presentation/
COPY core/common/build.gradle.kts ./core/common/
COPY core/bootstrap/build.gradle.kts ./core/bootstrap/

# preload dependencies
RUN ./gradlew dependencies --no-daemon

# copy source code vào /app/core
COPY core ./core

# sử dụng ./gradlew để không phụ thuộc gradle cài trên máy
# nó tự kiểm tra gradle version và download nếu thiếu
# run build
# kết quả sẽ ra /app/core/bootstrap/build/libs/{tên file}.jar
RUN ./gradlew :core:bootstrap:bootJar --no-daemon

# ------ STAGE 2: runtime ------
FROM eclipse-temurin:21.0.11_10-jre-ubi10-minimal

WORKDIR /app

# groupadd: tạo group trong Linux
# spring là tên group
# useradd: tạo user trong Linux
# -r = system account
# -g = group name (spring)
# spring (ở cuối) là tên user gán vào group tên spring
RUN groupadd spring \
    && useradd -r -g spring spring

# copy file jar từ stage builder vào file /app/app.jar
# dấu "\" để nối dòng
COPY --from=builder \
     /app/core/bootstrap/build/libs/*.jar \
     app.jar

USER spring

EXPOSE 8386

# docker sẽ chạy câu lệnh: java -jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
