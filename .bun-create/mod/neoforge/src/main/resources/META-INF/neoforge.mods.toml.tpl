modLoader = "javafml"
loaderVersion = "[4,)"
#issueTrackerURL = ""
license = "MIT"

[[mods]]
modId = "{{MOD_ID}}"
version = "${version}"
displayName = "{{MOD_NAME}}"
authors = "chimericdream"
description = '''
{{MOD_DESC}}
'''
#logoFile = ""

[[dependencies.{{MOD_ID}}]]
modId = "neoforge"
type = "required"
versionRange = "[21.0,)"
ordering = "NONE"
side = "BOTH"

[[dependencies.{{MOD_ID}}]]
modId = "minecraft"
type = "required"
versionRange = "[1.21,)"
ordering = "NONE"
side = "BOTH"

[[dependencies.{{MOD_ID}}]]
modId = "architectury"
type = "required"
versionRange = "[13.0.2,)"
ordering = "AFTER"
side = "BOTH"

[[dependencies.{{MOD_ID}}]]
modId = "chimericlib"
type = "required"
versionRange = "[2.1.0,)"
ordering = "AFTER"
side = "BOTH"

[[dependencies.{{MOD_ID}}]]
modId = "yet_another_config_lib_v3"
type = "required"
versionRange = "[3.6.1,)"
ordering = "AFTER"
side = "BOTH"

[[mixins]]
config = "{{MOD_ID}}.mixins.json"

[[accessTransformers]]
file = "META-INF/accesstransformer.cfg"
