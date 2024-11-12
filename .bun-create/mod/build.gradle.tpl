allprojects {
    group = project(":{{FOLDER_NAME}}").maven_group
    version = project(":{{FOLDER_NAME}}").mod_version
}

subprojects {
    base {
        archivesName = project(":{{FOLDER_NAME}}").archives_name + "-" + project.name
    }
}
