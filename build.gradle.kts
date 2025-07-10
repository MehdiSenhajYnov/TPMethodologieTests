import info.solidsoft.gradle.pitest.PitestPluginExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    id("jacoco")
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.spring") version "1.8.22"
    id("info.solidsoft.pitest") version "1.15.0"
}

group = "com.jicay"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

jacoco {
    toolVersion = "0.8.13"
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

testing {
    suites {
        val testIntegration by registering(JvmTestSuite::class) {
            sources {
                kotlin {
                    setSrcDirs(listOf("src/testIntegration/kotlin"))
                }
                compileClasspath += sourceSets.main.get().output
                runtimeClasspath += sourceSets.main.get().output
            }
        }
    }
}

val testIntegrationImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.liquibase:liquibase-core")
    implementation("org.postgresql:postgresql")
    implementation("org.springdoc:springdoc-openapi-starter-common:2.1.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")

    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.kotest:kotest-property:5.9.1")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("info.solidsoft.gradle.pitest:gradle-pitest-plugin:1.15.0")
    testImplementation("io.kotest.extensions:kotest-extensions-pitest:1.2.0")

    testIntegrationImplementation("io.mockk:mockk:1.13.8")
    testIntegrationImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testIntegrationImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testIntegrationImplementation("com.ninja-squad:springmockk:4.0.2")
    testIntegrationImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
    }
    testIntegrationImplementation("org.testcontainers:postgresql:1.19.1")
    testIntegrationImplementation("org.testcontainers:jdbc-test:1.12.0")
    testIntegrationImplementation("org.testcontainers:testcontainers:1.19.1")
    testIntegrationImplementation("info.solidsoft.gradle.pitest:gradle-pitest-plugin:1.15.0")
    testIntegrationImplementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")
    testIntegrationImplementation("io.kotest.extensions:kotest-extensions-testcontainers:2.0.2")
    testIntegrationImplementation("io.kotest.extensions:kotest-extensions-pitest:1.2.0")

    implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.21")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register<JacocoReport>("jacocoFullReport") {
    executionData(tasks.named("test").get())
    sourceSets(sourceSets["main"])

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

configure<PitestPluginExtension> {
    targetClasses.set(listOf("com.jicay.bookmanagement.*"))
}

pitest {
    targetClasses.add("com.jicay.bookmanagement.*")
    junit5PluginVersion = "1.0.0"
    avoidCallsTo.set(setOf("kotlin.jvm.internal"))
    mutators.set(setOf("STRONGER"))
    threads.set(Runtime.getRuntime().availableProcessors())
    testSourceSets.addAll(sourceSets["test"])
    mainSourceSets.addAll(sourceSets["main"])
    outputFormats.addAll("XML", "HTML")
    excludedClasses.add("**BookManagementApplication")
}
