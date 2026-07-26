plugins {
    java
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.naho"
version = "1.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.14")
    }
}

dependencies {
    // project dependencies
    implementation(project(":core:domain"))
    implementation(project(":core:application"))
    implementation(project(":core:common"))

    // lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // flyway
    implementation("org.flywaydb:flyway-mysql:11.20.0")
    implementation("org.flywaydb:flyway-core")

    // spring security
    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // google
    implementation("com.google.api-client:google-api-client:2.8.1")
    implementation("com.google.oauth-client:google-oauth-client:1.39.0")

    // jwt
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // Azure Speech SDK
    implementation("com.microsoft.cognitiveservices.speech:client-sdk:1.49.1@jar")

    // MapStructs
    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.apache.commons:commons-pool2:2.13.1")

    // s3
    implementation("software.amazon.awssdk:s3:2.46.0")

    // Sudachi
    implementation("com.worksap.nlp:sudachi:0.7.5")

    // Apache POI for Excel
    implementation("org.apache.poi:poi-ooxml:5.2.3")

    // other
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.5")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mybatis.spring.boot:mybatis-spring-boot-starter-test:3.0.5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}