architectury {
    fabric()
}

loom {
    accessWidenerPath = project(":{{FOLDER_NAME}}:common").loom.accessWidenerPath
}

configurations {
    developmentFabric.extendsFrom common
}

dependencies {
    common(project(path: ':{{FOLDER_NAME}}:common', configuration: 'namedElements')) { transitive false }
    shadowBundle project(path: ':{{FOLDER_NAME}}:common', configuration: 'transformProductionFabric')
}
