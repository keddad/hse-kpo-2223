plugins {
    id("java")
}

group = "edu.keddad.stasi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    implementation(files("lib/jade.jar"))
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")

    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
}

val fatJar = task("fatJar", type = Jar::class) {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Implementation-Title"] = "Stasi"
        attributes["Implementation-Version"] = archiveVersion
        attributes["Main-Class"] = "edu.keddad.stasi.Main"
    }
    from(configurations.runtimeClasspath.get().map({ if (it.isDirectory) it else zipTree(it) }))
    with(tasks.jar.get() as CopySpec)
    destinationDirectory.set(file("."))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}