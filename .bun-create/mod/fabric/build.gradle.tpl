plugins {
    id 'com.github.johnrengelman.shadow'
}

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    accessWidenerPath = project(":{{FOLDER_NAME}}:common").loom.accessWidenerPath
}

configurations {
    common {
        canBeResolved = true
        canBeConsumed = false
    }
    compileClasspath.extendsFrom common
    runtimeClasspath.extendsFrom common
    developmentFabric.extendsFrom common

    // Files in this configuration will be bundled into your mod using the Shadow plugin.
    // Don't use the `shadow` configuration from the plugin itself as it's meant for excluding files.
    shadowBundle {
        canBeResolved = true
        canBeConsumed = false
    }
}

dependencies {
    modImplementation "net.fabricmc:fabric-loader:$rootProject.fabric_loader_version"

    // Fabric API. This is technically optional, but you probably want it anyway.
    modImplementation "net.fabricmc.fabric-api:fabric-api:$rootProject.fabric_api_version"

    // Architectury API. This is optional, and you can comment it out if you don't need it.
    modImplementation "dev.architectury:architectury-fabric:$rootProject.architectury_api_version"

    common(project(path: ':{{FOLDER_NAME}}:common', configuration: 'namedElements')) { transitive false }
    shadowBundle project(path: ':{{FOLDER_NAME}}:common', configuration: 'transformProductionFabric')

    modImplementation "com.chimericdream.lib:chimericlib-fabric:${rootProject.chimericlib_version}"

    modImplementation("com.terraformersmc:modmenu:${rootProject.mod_menu_version}")

    modImplementation "dev.isxander:yet-another-config-lib:${rootProject.yacl_version}-fabric"
}

processResources {
    inputs.property 'version', project.version

    filesMatching('fabric.mod.json') {
        expand version: project.version
    }
}

shadowJar {
    configurations = [project.configurations.shadowBundle]
    archiveClassifier = 'dev-shadow'
}

remapJar {
    input.set shadowJar.archiveFile
}
