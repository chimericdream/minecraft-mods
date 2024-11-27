pluginManagement {
    repositories {
        maven { url "https://maven.fabricmc.net/" }
        maven { url "https://maven.architectury.dev/" }
        maven { url "https://files.minecraftforge.net/maven/" }
        gradlePluginPortal()
    }
}

rootProject.name = 'chimeric-mods'

def projectList = {{PROJECT_LIST}}

projectList.each {
    include ":$it"
    include ":$it:common"
    include ":$it:fabric"
    include ":$it:neoforge"
}
