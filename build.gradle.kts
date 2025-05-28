plugins {
    java
}

group = "com.fahmatrix" // Replace with your group
version = "0.1.4"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}


repositories {
    mavenCentral()
}

dependencies {
    // Add your dependencies here
    // testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    // testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    
    // Example implementation dependency:
    // implementation("com.google.guava:guava:31.1-jre")
}

tasks.test {
    useJUnitPlatform()
}

tasks.javadoc {
    exclude("com/fahmatrix/Importers/**")
    exclude("com/fahmatrix/Exporters/**")
    exclude("com/fahmatrix/Helpers/**")
    exclude("examples/**")
}