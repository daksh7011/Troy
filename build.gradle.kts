import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.0"
    kotlin("plugin.serialization") version "1.7.0"
    id("org.jmailen.kotlinter") version "3.4.5"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "in.technowolf"
version = "1.1.2"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven {
        name = "Kotlin Discord"
        url = uri("https://maven.kotlindiscord.com/repository/maven-public/")
    }
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.kotlindiscord.kord.extensions:kord-extensions:1.5.1-RC1")
    implementation("com.kotlindiscord.kord.extensions:time4j:1.5.1-RC1")
    implementation("org.slf4j:slf4j-simple:1.7.36")
    implementation("net.dean.jraw:JRAW:1.1.0")
    implementation("io.getunleash:unleash-client-java:5.1.0")
    implementation("dev.kord.x:emoji:0.5.0")
    implementation("com.github.discordbotlist:dbl-java-library:2.0.1")
    implementation("org.litote.kmongo:kmongo-coroutine:4.6.1")
    implementation("com.gitlab.technowolf:links-detektor:1.0.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "16"
}

tasks.withType(KotlinCompile::class).all {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
}

tasks.check {
    dependsOn("installKotlinterPrePushHook")
}

kotlinter {
    ignoreFailures = false
    indentSize = 4
    reporters = arrayOf("checkstyle", "plain")
    experimentalRules = false
    disabledRules = arrayOf("no-wildcard-imports", "import-ordering", "indent")
}
