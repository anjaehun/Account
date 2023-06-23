plugins {
    java
    id("org.springframework.boot") version "2.7.12"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
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
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("com.h2database:h2")


    // Mockito 추가
    testImplementation("org.mockito:mockito-core:3.10.0")
    // redis client
    implementation("org.redisson:redisson:3.17.1")
    // embedded redis
        implementation("it.ozimov:embedded-redis:0.7.3") {
            exclude(group = "org.slf4j", module = "slf4j-simple")
        }
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
