plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.31"
    `java-library`
}

repositories {
    // Use JCenter for resolving dependencies.
    jcenter()
}

tasks {
    named<Test>("test") {
        useJUnitPlatform()
    }

    withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).all {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_11.toString()
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.1.0")
    testImplementation("org.assertj:assertj-core:3.8.0")
}
