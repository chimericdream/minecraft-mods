import org.gradle.jvm.tasks.Jar

plugins {
    id 'com.gradleup.shadow'
}

architectury {
    neoForge()
}

loom {
    accessWidenerPath = project(":{{FOLDER_NAME}}:common").loom.accessWidenerPath

    neoForge {
        convertAccessWideners(tasks.named("shadowJar", Jar), "{{MOD_ID}}.accesswidener")
    }
}

configurations {
    developmentNeoForge.extendsFrom common
}

dependencies {
    common(project(path: ':{{FOLDER_NAME}}:common')) { transitive false }
    shadowBundle project(path: ':{{FOLDER_NAME}}:common', configuration: 'transformProductionNeoForge')
}
