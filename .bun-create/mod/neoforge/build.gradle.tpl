architectury {
    neoForge()
}

loom {
    accessWidenerPath = project(":{{FOLDER_NAME}}:common").loom.accessWidenerPath
}

configurations {
    developmentNeoForge.extendsFrom common
}

dependencies {
    common(project(path: ':{{FOLDER_NAME}}:common', configuration: 'namedElements')) { transitive false }
    shadowBundle project(path: ':{{FOLDER_NAME}}:common', configuration: 'transformProductionNeoForge')
}

remapJar {
    atAccessWideners.add("{{MOD_ID}}.accesswidener")
}
