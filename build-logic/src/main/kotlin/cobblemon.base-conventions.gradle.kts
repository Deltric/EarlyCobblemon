/*
 *
 *  * Copyright (C) 2023 Cobblemon Contributors
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val accessWidenerFile = "src/main/resources/cobblemon-common.accesswidener"

plugins {
    java
    `java-library`
    id("org.cadixdev.licenser")
    id("dev.architectury.loom")
    id("architectury-plugin")
    kotlin("jvm")
}

repositories {
    mavenCentral()
    //JEI
    maven("https://maven.blamejared.com/")
    maven("https://maven.tterrag.com/")
}

license {
    header(rootProject.file("HEADER"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

architectury {
    minecraft = project.property("mc_version").toString()
}

loom {
    silentMojangMappingsLicense()
    accessWidenerPath.set(project(":common").file(ACCESS_WIDENER))

    mixin {
        defaultRefmapName.set("cobblemon-${project.name}-refmap.json")
    }
}

dependencies {
    minecraft("net.minecraft:minecraft:${rootProject.property("mc_version")}")
    mappings("net.fabricmc:yarn:${rootProject.property("yarn_version")}:v2")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    withType<Jar> {
        from(rootProject.file("LICENSE"))
    }
}
