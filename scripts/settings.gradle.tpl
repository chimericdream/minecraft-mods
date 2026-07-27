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

// chimeric-lib is a foundational library that other mods now depend on as project() dependencies.
// Loom eagerly resolves each mod's classpath during that mod's afterEvaluate; if a consumer resolves
// chimeric-lib before it has finished configuring, the resolve fails with "project components has not
// been calculated yet". Hoisting chimeric-lib to the front makes its parent project the first mod
// evaluated; together with evaluationDependsOnChildren() in chimeric-lib/build.gradle (which pulls in
// its common/fabric/neoforge children at that point) this guarantees chimeric-lib's subprojects are
// fully configured before any consumer resolves them. Both pieces are required.
def orderedList = projectList.contains('chimeric-lib') \
    ? (['chimeric-lib'] + projectList.findAll { it != 'chimeric-lib' }) \
    : projectList

orderedList.each {
    include ":$it"
    include ":$it:common"
    include ":$it:fabric"
    include ":$it:neoforge"
}
