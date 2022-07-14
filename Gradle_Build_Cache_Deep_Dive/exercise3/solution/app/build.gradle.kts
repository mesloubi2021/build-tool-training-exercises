/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java application project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/7.4.2/userguide/building_java_projects.html
 */

import kotlin.random.Random

plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use JUnit Jupiter for testing.
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:30.1.1-jre")
}

application {
    // Define the main class for the application.
    mainClass.set("com.gradle.lab.App")
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

/*
This just guarantees that everyone running the lab pushes their own changes
into the remote cache, rather than getting hits from someone else's run.
 */
tasks.register("generateLocalUniqueValue") {
    val outputFile = layout.projectDirectory.file("local.txt").asFile
    onlyIf {
        !outputFile.exists()
    }
    outputs.file(outputFile)
    doLast {
        val bytes = ByteArray(20)
        Random.nextBytes(bytes)
        outputFile.writeBytes(bytes)
    }
}
listOf("compileJava", "compileTestJava", "test").forEach{
    tasks.named(it) {
        inputs.files(tasks.named("generateLocalUniqueValue"))
                .withPathSensitivity(PathSensitivity.NONE)
                .withPropertyName("uniqueValue")
    }
}

tasks.register<Zip>("zipUniqueValue") {
    // Inputs
    from(tasks.named("generateLocalUniqueValue"))

    // Outputs
    destinationDirectory.set(layout.buildDirectory)
    archiveFileName.set("unique.zip")
    // Enable working with cache.
    outputs.cacheIf { true }
}

tasks.register<Exec>("helloFile") {
    workingDir = layout.buildDirectory.asFile.get()
    commandLine("bash", "-c", "person=`cat ../name.txt`; echo \"hello \$person\" > hello.txt")

    inputs.file(layout.projectDirectory.file("name.txt"))
            .withPropertyName("helloInput")
            .withPathSensitivity(PathSensitivity.RELATIVE)

    outputs.file(layout.buildDirectory.file("hello.txt"))
            .withPropertyName("helloOutput")

    outputs.cacheIf { true }
}

