import org.gradle.jvm.tasks.Jar

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
    common(project(path: ':{{FOLDER_NAME}}:common')) { transitive false }
    shadowBundle project(path: ':{{FOLDER_NAME}}:common', configuration: 'transformProductionNeoForge')
}

jar {
    atAccessWideners.add("{{MOD_ID}}.accesswidener")
}
