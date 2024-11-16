modLoader = "javafml"
loaderVersion = "[4,)"
issueTrackerURL = "https://github.com/chimericdream/minecraft-mods/issues"
license = "MIT"

[[mods]]
modId = "${mod_id}"
version = "${version}"
displayName = "${mod_name}"
authors = "chimericdream"
description = '''
${mod_description}
'''
#logoFile = ""

[[dependencies.${mod_id}]]
modId = "neoforge"
type = "required"
versionRange = "[21.0,)"
ordering = "NONE"
side = "BOTH"

[[dependencies.${mod_id}]]
modId = "minecraft"
type = "required"
versionRange = "[${minecraft_compat},)"
ordering = "NONE"
side = "BOTH"

[[dependencies.${mod_id}]]
modId = "architectury"
type = "required"
versionRange = "[${architectury_compat},)"
ordering = "AFTER"
side = "BOTH"

[[dependencies.${mod_id}]]
modId = "chimericlib"
type = "required"
versionRange = "[${chimericlib_compat},)"
ordering = "AFTER"
side = "BOTH"

[[dependencies.${mod_id}]]
modId = "yet_another_config_lib_v3"
type = "required"
versionRange = "[${yacl_compat},)"
ordering = "AFTER"
side = "BOTH"

[[mixins]]
config = "{{MOD_ID}}.mixins.json"