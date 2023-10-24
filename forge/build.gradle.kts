/*
 *
 *  * Copyright (C) 2023 Cobblemon Contributors
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 */

plugins {
    id("cobblemon.platform-conventions")
    id("cobblemon.publish-conventions")
}

architectury {
    platformSetupLoomIde()
    forge()
}

loom {
    forge {
        convertAccessWideners.set(true)

        mixinConfig("mixins.cobblemon-forge.json")
        mixinConfig("mixins.cobblemon-common.json")
    }
}

repositories {
    maven(url = "${rootProject.projectDir}/deps")
    maven(url = "https://thedarkcolour.github.io/KotlinForForge/")
    maven(url = "https://api.modrinth.com/maven")
    mavenLocal()
}

dependencies {
    forge(libs.forge)
    //Because of the JEI mapping issues if we want
    //a forge launch we gotta do some wacky stuff
    //modImplementation(libs.jeiForge)
    //shadowCommon group: 'commons-io', name: 'commons-io', version: '2.6'
//    modImplementation(libs.flywheelForge)
//    include(libs.flywheelForge)
    modCompileOnly(libs.adornForge)

    implementation(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    implementation(libs.kotlinForForge)
    "developmentForge"(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    bundle(project(path = ":common", configuration = "transformProductionForge")) {
        isTransitive = false
    }
    testImplementation(project(":common", configuration = "namedElements"))

    listOf(
        libs.graal,
        libs.molang
    ).forEach {
        forgeRuntimeLibrary(it)
        bundle(it)
    }
}

tasks {
    shadowJar {
        exclude("architectury-common.accessWidener")
        exclude("architectury.common.json")

        relocate ("com.ibm.icu", "com.cobblemon.mod.relocations.ibm.icu")
    }

    processResources {
        inputs.property("version", rootProject.version)
        inputs.property("minecraft_version", rootProject.property("mc_version").toString())

        filesMatching("META-INF/mods.toml") {
            expand(
                "version" to rootProject.version,
                "minecraft_version" to rootProject.property("mc_version").toString()
            )
        }
    }
}

tasks {
    sourcesJar {
        val depSources = project(":common").tasks.sourcesJar
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        dependsOn(depSources)
        from(depSources.get().archiveFile.map { zipTree(it) }) {
            exclude("architectury.accessWidener")
        }
    }
}


//jar {
//    classifier("dev")
//    manifest {
//        attributes(
//                "Specification-Title" to rootProject.mod_id,
//                "Specification-Vendor" to "Cable MC",
//                "Specification-Version" to "1",
//                "Implementation-Title" to rootProject.mod_id,
//                "Implementation-Version" to project.version,
//                "Implementation-Vendor" to "Cable MC",
//        )
//    }
//}
//
//sourcesJar {
//    def commonSources = project(":common").sourcesJar
//    dependsOn commonSources
//    from commonSources.archiveFile.map { zipTree(it) }
//}
//
//components.java {
//    withVariantsFromConfiguration(project.configurations.shadowRuntimeElements) {
//        skip()
//    }
//}
