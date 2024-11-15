plugins {
    id 'com.github.johnrengelman.shadow'
}

allprojects {
    group = project(":{{FOLDER_NAME}}").maven_group
    version = project(":{{FOLDER_NAME}}").mod_version
}

subprojects {
    apply plugin: 'com.github.johnrengelman.shadow'

    base {
        archivesName = project(":{{FOLDER_NAME}}").archives_name + "-" + project.name
    }

    architectury {
        platformSetupLoomIde()
    }

    processResources {
        inputs.property 'version', project.version

        filesMatching(['fabric.mod.json', 'META-INF/neoforge.mods.toml']) {
            expand version: project.version,
                architectury_compat: project.architectury_compat,
                chimericlib_compat: project.chimericlib_compat,
                fabric_compat: project.fabric_compat,
                minecraft_compat: project.minecraft_compat,
                mod_id: project.mod_id,
                mod_name: project.mod_name,
                mod_description: project.mod_description,
                modmenu_compat: project.modmenu_compat,
                yacl_compat: project.yacl_compat
        }
    }

    shadowJar {
        configurations = [project.configurations.shadowBundle]
        archiveClassifier = 'dev-shadow'
    }

    remapJar {
        input.set shadowJar.archiveFile
    }
}
