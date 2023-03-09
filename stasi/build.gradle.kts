plugins {
    id("java")
}

group = "edu.keddad.stasi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation(files("lib/jade.jar"))
}
tasks.jar {
    manifest {
        attributes["Main-Class"] = "edu.keddad.stasi.Main"
        attributes["Class-Path"] = configurations
                .runtimeClasspath
                .get()
                .joinToString(separator = " ") { file ->
                    "lib/${file.name}"
                }
    }

    destinationDirectory.set(file("."))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}