plugins {
    java
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
    id("com.google.cloud.tools.jib") version "3.4.4"
}
val springCloudVersion by extra("2024.0.0")

group = "org.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("com.github.tomakehurst:wiremock-jre8:3.0.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    runtimeOnly("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

jib {
    from {
        image = "eclipse-temurin:21-jdk-alpine"

        platforms {
            platform {
                architecture = "arm64"
                os = "linux"
            }
        }
    }
    to {
        image = "registry.hub.docker.com/${System.getenv("DOCKER_USERNAME")}/expense-tracker-ft"
        auth {
            username = System.getenv("DOCKER_USERNAME") ?: "<default-username>"
            password = System.getenv("DOCKER_PASSWORD") ?: "<default-password>"
        }
    }
    container {
        jvmFlags = listOf("-Xms512m", "-Xmx1024m")
        ports = listOf("8080")
        mainClass = "org.example.expensetracker.ExpenseTrackerApplication"
    }
}


tasks.withType<Test> {
    useJUnitPlatform()
}
