plugins {
    java
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
    id("com.google.cloud.tools.jib") version "3.4.4"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
}

extra["springCloudVersion"] = "2024.0.0-RC1"

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("io.netty:netty-resolver-dns-native-macos:4.1.95.Final:osx-aarch_64")

    implementation("io.jsonwebtoken:jjwt-api:0.11.1")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.1")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.1")
    implementation("org.projectlombok:lombok")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
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
        image = "registry.hub.docker.com/${System.getenv("DOCKER_USERNAME")}/cloud-gateway-ft"
        auth {
            username = System.getenv("DOCKER_USERNAME") ?: "<default-username>"
            password = System.getenv("DOCKER_PASSWORD") ?: "<default-password>"
        }
    }
    container {
        jvmFlags = listOf("-Xms512m", "-Xmx1024m")
        ports = listOf("9090")
        mainClass = "org.example.cloudgateway.CloudGatewayApplication"
    }
}


tasks.withType<Test> {
    useJUnitPlatform()
}
