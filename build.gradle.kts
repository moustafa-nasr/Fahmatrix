plugins {
    java
    `maven-publish`
    id("org.sonarqube") version "6.2.0.5505"
}

group = "com.fahmatrix" // Replace with your group
version = "0.1.5"

sonar {
  properties {
    property("sonar.projectKey", "com:fahmatrix")
    property("sonar.projectName", "Fahmatrix")
    property("sonar.host.url", "http://localhost:9000")
    property("sonar.token", System.getenv("SONAR_TOKEN_FAHMATRIX"))
  }
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withSourcesJar()  // ← Recommended
    withJavadocJar()  // ← Recommended
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            
            groupId = "com.fahmatrix"
            artifactId = "fahmatrix" // replace with your actual artifact name
            version = "0.1.5"
        }
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