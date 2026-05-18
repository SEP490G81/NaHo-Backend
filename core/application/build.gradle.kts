plugins {
    id("java")
}

group = "org.naho"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    // project implementation
    implementation(project(":core:domain"))

    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}