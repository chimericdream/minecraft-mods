{
    "schemaVersion": 1,
    "id": "${mod_id}",
    "version": "${version}",
    "name": "${mod_name}",
    "description": "${mod_description}",
    "authors": [
        "chimericdream"
    ],
    "contact": {
        "homepage": "https://fabricmc.net/",
        "sources": "https://github.com/FabricMC/fabric-example-mod"
    },
    "license": "MIT",
    "icon": "assets/{{MOD_ID}}/icon.png",
    "environment": "*",
    "entrypoints": {
        "main": [
            "com.chimericdream.{{MOD_ID}}.fabric.{{CLASS_NAME}}Fabric"
        ],
        "client": [
            "com.chimericdream.{{MOD_ID}}.fabric.client.{{CLASS_NAME}}FabricClient"
        ]
    },
    "mixins": [
        "{{MOD_ID}}.mixins.json"
    ],
    "depends": {
        "fabricloader": ">=${fabric_compat}",
        "minecraft": "~${minecraft_compat}",
        "java": ">=21",
        "fabric-api": "*",
        "architectury": ">=${architectury_compat}",
        "chimericlib": ">=${chimericlib_compat}",
        "yet_another_config_lib_v3": ">=${yacl_compat}"
    },
    "recommends": {
        "modmenu": ">=${modmenu_compat}"
    }
}