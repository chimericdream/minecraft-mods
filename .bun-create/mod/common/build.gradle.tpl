architectury {
    common project(':{{FOLDER_NAME}}').enabled_platforms.split(',')
}

loom {
    accessWidenerPath = file("src/main/resources/{{MOD_ID}}.accesswidener")
}

dependencies {
    // We depend on Fabric Loader here to use the Fabric @Environment annotations,
    // which get remapped to the correct annotations on each platform.
    // Do NOT use other classes from Fabric Loader.
    modImplementation "net.fabricmc:fabric-loader:$rootProject.fabric_loader_version"

    // Architectury API. This is optional, and you can comment it out if you don't need it.
    modImplementation "dev.architectury:architectury:$rootProject.architectury_api_version"

    modImplementation "com.chimericdream.lib:chimericlib-common:${rootProject.chimericlib_version}"

    modImplementation "dev.isxander:yet-another-config-lib:${rootProject.yacl_version}-fabric"
}
