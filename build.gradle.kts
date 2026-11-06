plugins {
    id("java")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "ru.akkyne13.storage"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.0")
}

tasks.test {
    useJUnitPlatform()
}

javafx {
    version = "26.0.2"
    modules = listOf("javafx.controls")
}
