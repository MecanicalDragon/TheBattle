import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.2.5.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("org.flywaydb.flyway") version "6.3.0"
    kotlin("jvm") version "1.3.70"
    kotlin("plugin.spring") version "1.3.70"
    kotlin("plugin.jpa") version "1.3.70"
    kotlin("plugin.allopen") version "1.3.70"
    kotlin("plugin.noarg") version "1.3.70"
}

group = "net.medrag"
version = "0.98.5"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")

//    websockets
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-integration")

//    kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.springframework.integration:spring-integration-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.flywayMigrate{
    url = "jdbc:h2:file:$rootDir/database/the_battle"
    user = "the_battle"
    password = "best_pw_ever"
}

tasks.register("buildResources") {
    group = "build"
    dependsOn("yarnInstall", "yarnRunBuild")
}

tasks.register<Exec>("yarnRunBuild") {
    group = "yarn"
    if (System.getProperty("os.name").toLowerCase().contains("windows")) {
        commandLine("cmd", "/c", "yarn", "run", "build")
    } else {
        commandLine("yarn", "run", "build")
    }
    doLast {
        copy {
            from("src/res")
            into("src/main/resources/static/res")
        }
    }
}

tasks.register<Exec>("yarnInstall") {
    group = "yarn"
    if (System.getProperty("os.name").toLowerCase().contains("windows")) {
        commandLine("cmd", "/c", "yarn", "install")
    } else {
        commandLine("yarn", "install")
    }
}

tasks.register<Exec>("yarnStartDev") {
    group = "yarn"
    if (System.getProperty("os.name").toLowerCase().contains("windows")) {
        commandLine("cmd", "/c", "yarn", "start-dev")
    } else if (System.getProperty("os.name").toLowerCase().contains("linux")) {
        //  that is useless while yarn has no sudo privileges, cause it can not refresh the view, but anyway...
        commandLine("yarn", "start-dev")
//        commandLine("bash", "-c", "echo 'password' | sudo -kS yarn start-dev")
    } else {
        commandLine("yarn", "start-dev")
    }
}

tasks.register<Exec>("yarnKillDev") {
    group = "yarn"
    if (System.getProperty("os.name").toLowerCase().contains("linux")) {
        commandLine("bash", "-c", "kill $(lsof -i -P -n | grep 9095 | grep node)")
    } else {
        logger.error("This feature for your OS is not maintained yet.")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

