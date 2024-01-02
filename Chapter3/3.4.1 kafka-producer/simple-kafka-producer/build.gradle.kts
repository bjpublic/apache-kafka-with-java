plugins {
    id("java")
    kotlin("jvm") version Dependency.kotlinVersion
}

group = "com.example"
version = "1.0"

java.sourceCompatibility = JavaVersion.toVersion(Dependency.targetJvmVersion)
java.targetCompatibility = JavaVersion.toVersion(Dependency.targetJvmVersion)

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.kafka:kafka-clients:2.5.0")
    implementation("org.slf4j:slf4j-simple:1.7.30")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}
kotlin {
//    jvmToolchain(17)
}