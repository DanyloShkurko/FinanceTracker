plugins {
	java
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.google.cloud.tools.jib") version "3.4.4"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "2024.0.0"

dependencies {
	implementation("org.springframework.cloud:spring-cloud-config-server")
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}
jib{
	from {
		image = "openjdk:21-jdk-slim"
	}
	to {
		image = "registry.hub.docker.com/"+System.getenv("DOCKER_USERNAME")+"/config-server-ft"
		auth {
			username = System.getenv("DOCKER_USERNAME") ?: "<default-username>"
			password = System.getenv("DOCKER_PASSWORD") ?: "<default-password>"
		}

	}
}
tasks.withType<Test> {
	useJUnitPlatform()
}
