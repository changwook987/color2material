plugins {
    kotlin("jvm") version "1.6.10"
}

group = "io.github.changwook987"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

dependencies {
    implementation(kotlin("stdlib"))
    // https://mvnrepository.com/artifact/io.github.monun/kommand-api
    implementation("io.github.monun:kommand-api:2.10.0")
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
}

project.extra["packageName"] = project.name.replace("-", "")
project.extra["pluginName"] = project.name.split("-").joinToString("") { it.capitalize() }

tasks {
    processResources {
        filesMatching("**/*.yml") {
            expand(project.properties)
            expand(project.extra.properties)
        }
    }

    create<Jar>("paper") {
        from(sourceSets["main"].output)
        archiveBaseName.set(project.extra.properties["pluginName"].toString())
        archiveVersion.set("")

        doLast {
            copy {
                from(archiveFile)
                into(File(rootDir, "server/plugins"))
            }
        }
    }
}