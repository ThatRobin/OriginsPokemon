plugins {
    id("java")
    id("fabric-loom") version("1.5-SNAPSHOT")
    kotlin("jvm") version ("1.8.10")
}

val minecraftVersion = project.properties["minecraft_version"] as String
val modid = "cobblemorigins"

repositories {
    mavenCentral()
    maven(url = "https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://maven.impactdev.net/repository/development/")

    maven ("https://maven.tterrag.com/")
    maven("https://maven.ladysnake.org/releases")
    maven ("https://maven.cafeteria.dev")
    maven ("https://maven.jamieswhiteshirt.com/libs-release")
    maven ("https://jitpack.io")
    maven ("https://maven.shedaniel.me/")
    maven ("https://maven.terraformersmc.com/")
    exclusiveContent {
        forRepository {
            maven ("https://api.modrinth.com/maven")
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

loom {
    runs {
        // This adds a new gradle task that runs the datagen API: "gradlew runDatagen"
        register("datagen") {
            server()
            name("Data Generation")
            runDir("build/datagen")
            property("fabric-api.datagen")
            property("fabric-api.datagen.modid", modid)
            property("fabric-api.datagen.output-dir", project.file("src/main/generated").toString())
            property("fabric-api.datagen.strict-validation", "false")
        }
    }
}

sourceSets {
    main {
        resources {
            srcDir("src/main/generated")
            exclude(".cache/")
        }
    }
}


dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${project.properties["minecraft_version"]}")
    mappings("net.fabricmc:yarn:${project.properties["yarn_mappings"]}:v2")
    modImplementation ("net.fabricmc:fabric-loader:${project.properties["loader_version"]}")

    modApi ("com.github.apace100:origins-fabric:${project.properties["origins_version"]}")

    modApi ("com.github.apace100:apoli:v${project.properties["apoli_version"]}")
    include ("com.github.apace100:apoli:v${project.properties["apoli_version"]}")

    modImplementation("com.cobblemon:fabric:1.4.0+1.20.1-SNAPSHOT")
    modApi("net.fabricmc:fabric-language-kotlin:1.10.17+kotlin.1.9.22")
    modRuntimeOnly("com.jozufozu.flywheel:flywheel-fabric-$minecraftVersion:${project.properties["flywheel_fabric_version"]}")

    //modImplementation 'com.cobblemon:fabric:1.4.1+1.20.1'
    // Fabric API. This is technically optional, but you probably want it anyway.
    include("com.github.llamalad7.mixinextras:mixinextras-fabric:${project.properties["mixinextras_version"]}")
    implementation("com.github.llamalad7.mixinextras:mixinextras-fabric:${project.properties["mixinextras_version"]}")
    annotationProcessor("com.github.llamalad7.mixinextras:mixinextras-fabric:${project.properties["mixinextras_version"]}")
    modImplementation ("net.fabricmc.fabric-api:fabric-api:${project.properties["fabric_version"]}")
}

tasks.getByName<Test>("test") {
useJUnitPlatform()
}
