plugins {
    id 'com.github.johnrengelman.shadow'
}

loom {
    accessWidenerPath = project(":{{FOLDER_NAME}}:common").loom.accessWidenerPath
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

configurations {
    common {
        canBeResolved = true
        canBeConsumed = false
    }
    compileClasspath.extendsFrom common
    runtimeClasspath.extendsFrom common
    developmentNeoForge.extendsFrom common

    // Files in this configuration will be bundled into your mod using the Shadow plugin.
    // Don't use the `shadow` configuration from the plugin itself as it's meant for excluding files.
    shadowBundle {
        canBeResolved = true
        canBeConsumed = false
    }
}

repositories {
    maven {
        name = 'NeoForged'
        url = 'https://maven.neoforged.net/releases'
    }
}

dependencies {
    neoForge "net.neoforged:neoforge:$rootProject.neoforge_version"

    // Architectury API. This is optional, and you can comment it out if you don't need it.
    modImplementation "dev.architectury:architectury-neoforge:$rootProject.architectury_api_version"

    common(project(path: ':{{FOLDER_NAME}}:common', configuration: 'namedElements')) { transitive false }
    shadowBundle project(path: ':{{FOLDER_NAME}}:common', configuration: 'transformProductionNeoForge')

    modImplementation "com.chimericdream.lib:chimericlib-neoforge:${rootProject.chimericlib_version}"

    modImplementation "dev.isxander:yet-another-config-lib:${rootProject.yacl_version}-neoforge"

    forgeRuntimeLibrary(runtimeOnly("org.quiltmc.parsers:json:0.2.1"))
    forgeRuntimeLibrary(runtimeOnly("org.quiltmc.parsers:gson:0.2.1"))
}

processResources {
    inputs.property 'version', project.version

    filesMatching('META-INF/neoforge.mods.toml') {
        expand version: project.version
    }
}

shadowJar {
    configurations = [project.configurations.shadowBundle]
    archiveClassifier = 'dev-shadow'
}

remapJar {
    atAccessWideners.add("{{MOD_ID}}.accesswidener")
    input.set shadowJar.archiveFile
}
