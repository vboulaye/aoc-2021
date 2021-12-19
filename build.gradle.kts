plugins {
    kotlin("jvm") version "1.6.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:multik-api:0.1.1")
    implementation("org.jetbrains.kotlinx:multik-default:0.1.1")
    testImplementation(kotlin("test"))
}

tasks {
    sourceSets {
        main {
            java.srcDirs("src")
        }
        test {
            java.srcDirs("test")
        }
    }

    wrapper {
        gradleVersion = "7.3"
    }

    test {
        useJUnitPlatform()
    }
}
