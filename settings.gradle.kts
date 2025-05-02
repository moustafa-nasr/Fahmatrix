rootProject.name = "fahmatrix"

// build.gradle.kts
plugins {
    java
}

group = "com.fahmatrix"
version = "0.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}