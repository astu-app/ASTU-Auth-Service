val ktor_version: String by project
val koin_ktor: String by project
val kotlin_version: String by project
val logback_version: String by project

val prometeus_version: String by project
plugins {
    kotlin("jvm") version "1.9.10"
    id("io.ktor.plugin") version "2.3.5"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10"
}

group = "org.astu.auth.api"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-client-core-jvm")
    implementation("io.ktor:ktor-client-apache-jvm")
    implementation("io.ktor:ktor-server-sessions-jvm")
    implementation("io.ktor:ktor-server-default-headers-jvm")
    implementation("io.ktor:ktor-server-forwarded-header-jvm")
    implementation("io.ktor:ktor-server-http-redirect-jvm")
    implementation("io.ktor:ktor-server-metrics-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-client-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-metrics-micrometer-jvm")
    implementation("io.micrometer:micrometer-registry-prometheus:$prometeus_version")
    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-status-pages-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-cors:$ktor_version")

    implementation("ch.qos.logback:logback-classic:$logback_version")

    implementation("dev.whyoleg.cryptography:cryptography-core:0.2.0")
    implementation("dev.whyoleg.cryptography:cryptography-provider-jdk:0.2.0")

    implementation("io.ktor:ktor-client-logging-jvm:2.3.5")
    implementation("io.ktor:ktor-client-cio-jvm:2.3.5")
    implementation("io.ktor:ktor-client-okhttp-jvm:2.3.5")
    implementation("io.ktor:ktor-client-apache:2.3.5")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:2.3.5")

    implementation("org.jetbrains.exposed:exposed-core:0.44.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.44.1")

    implementation("org.postgresql:postgresql:42.2.2")

    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

    // Koin for Ktor
    implementation("io.insert-koin:koin-ktor:$koin_ktor")
}


tasks.register("buildDockerCompose") {
    group = "docker"
    dependsOn("buildFatJar")
    doFirst {
        exec {
            executable = "docker"
            args = listOf("compose", "down")
        }
        exec {
            executable = "docker"
            args = listOf("compose", "build")
        }
    }
}
tasks.register("runDockerCompose") {
    group = "docker"
    doFirst {
        exec {
            executable = "docker"
            args = listOf("compose", "up", "-d")
        }
    }
}
tasks.register("buildAndRunDockerCompose") {
    group = "docker"
    dependsOn("buildDockerCompose")
    dependsOn("runDockerCompose")
}

