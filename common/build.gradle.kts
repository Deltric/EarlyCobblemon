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
    id("cobblemon.base-conventions")
    id("cobblemon.publish-conventions")
}

architectury {
    common("fabric")
}

repositories {
    maven(url = "${rootProject.projectDir}/deps")
    maven(url = "https://api.modrinth.com/maven")
    mavenLocal()
}

dependencies {
    implementation(libs.stdlib)
    implementation(libs.reflect)

    modImplementation(libs.fabricLoader)
    //Flywheel has no common dep so just pick one and don't use any platform specific code in common
//    modCompileOnly(libs.flywheelFabric)
    modApi(libs.molang)
    //compileOnlyApi(libs.jeiApi)
    //modCompileOnly(libs.adornFabric)

    // For Showdown
    modCompileOnly(libs.graal)

    //shadowCommon group: 'commons-io', name: 'commons-io', version: '2.6'

    // For datastore
    modCompileOnly(libs.mongoDriverCore)
    modCompileOnly(libs.mongoDriverSync)

    testRuntimeOnly(libs.junitEngine)
    testImplementation(libs.junitApi)
    testImplementation(libs.junitParams)
    testImplementation(libs.mockito)
    testImplementation(libs.mockk)
    testImplementation(libs.classgraph)
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        setEvents(listOf("failed"))
        setExceptionFormat("full")
    }
}