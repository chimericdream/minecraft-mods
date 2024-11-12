{
    "schemaVersion": 1,
    "id": "{{MOD_ID}}",
    "version": "${version}",
    "name": "{{MOD_NAME}}",
    "description": "{{MOD_DESC}}",
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
    "accessWidener": "{{MOD_ID}}.accesswidener",
    "depends": {
        "fabricloader": ">=0.16.5",
        "minecraft": "~1.21",
        "java": ">=21",
        "architectury": ">=13.0.2",
        "fabric-api": "*",
        "chimericlib": ">=2",
        "yet_another_config_lib_v3": ">=3.6.1"
    },
    "recommends": {
        "modmenu": ">=11.0.0"
    }
}
