import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.google.devtools.ksp") version "1.7.22-1.0.8"
    kotlin("jvm") version "1.7.20"
    kotlin("plugin.serialization") version "1.7.20"
    id("dev.schlaubi.mikbot.gradle-plugin") version "2.6.3"
}

group = "net.stckoverflw"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    mikbot("dev.schlaubi", "mikbot-api", "3.10.0-SNAPSHOT")
    ksp("dev.schlaubi", "mikbot-plugin-processor", "2.3.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "18"
}

mikbotPlugin {
    description.set("Simple Bansystem Plugin")
    provider.set("StckOverflw")
    license.set("MIT")
}
