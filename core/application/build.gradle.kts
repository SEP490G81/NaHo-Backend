plugins {
    id("java")
    id("jacoco")
}

group = "org.naho"
version = "1.0"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

jacoco {
    toolVersion = "0.8.12"
}

dependencies {
    // project implementation
    implementation(project(":core:domain"))
    implementation(project(":core:common"))

    // jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")

    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/test/html"))
    }
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/command/**",
                    "**/result/**",
                    "**/exception/**",
                    "**/constant/**",
                    "**/event/**",
                    "**/mapper/**"
                )
            }
        })
    )
}