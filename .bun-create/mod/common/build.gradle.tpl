architectury {
    common project(':{{FOLDER_NAME}}').enabled_platforms.split(',')
}

loom {
    accessWidenerPath = file("src/main/resources/{{MOD_ID}}.accesswidener")
}
