#!/bin/sh

echo "Enter the details for the mod"
read -p "Mod name (e.g. Banner Tweaks): " modname
read -p "Mod description: " moddesc
read -p "Mod ID (e.g. bannertweaks; will also be the Java package name): " modid
read -p "Folder (e.g. banner-tweaks): " foldername
read -p "Main Java class, without \`*Mod\` (e.g. BannerTweaks): " classname

bun create mod "./$foldername" --no-install --no-git

mv "./$foldername/common/src/main/java/com/chimericdream/{{MOD_ID}}" "./$foldername/common/src/main/java/com/chimericdream/$modid"
mv "./$foldername/common/src/main/resources/assets/{{MOD_ID}}" "./$foldername/common/src/main/resources/assets/$modid"
mv "./$foldername/fabric/src/main/java/com/chimericdream/{{MOD_ID}}" "./$foldername/fabric/src/main/java/com/chimericdream/$modid"
mv "./$foldername/neoforge/src/main/java/com/chimericdream/{{MOD_ID}}" "./$foldername/neoforge/src/main/java/com/chimericdream/$modid"

mv "./$foldername/common/src/main/resources/{{MOD_ID}}.accesswidener" "./$foldername/common/src/main/resources/$modid.accesswidener"
mv "./$foldername/common/src/main/resources/{{MOD_ID}}.mixins.json.tpl" "./$foldername/common/src/main/resources/$modid.mixins.json.tpl"
mv "./$foldername/neoforge/src/main/java/com/chimericdream/$modid/neoforge/{{CLASS_NAME}}NeoForge.java.tpl" "./$foldername/neoforge/src/main/java/com/chimericdream/$modid/neoforge/${classname}NeoForge.java.tpl"
mv "./$foldername/fabric/src/main/java/com/chimericdream/$modid/fabric/{{CLASS_NAME}}Fabric.java.tpl" "./$foldername/fabric/src/main/java/com/chimericdream/$modid/fabric/${classname}Fabric.java.tpl"
mv "./$foldername/fabric/src/main/java/com/chimericdream/$modid/fabric/client/{{CLASS_NAME}}FabricClient.java.tpl" "./$foldername/fabric/src/main/java/com/chimericdream/$modid/fabric/client/${classname}FabricClient.java.tpl"
mv "./$foldername/common/src/main/java/com/chimericdream/$modid/{{CLASS_NAME}}Mod.java.tpl" "./$foldername/common/src/main/java/com/chimericdream/$modid/${classname}Mod.java.tpl"

templateFiles=(
  "./$foldername/build.gradle.tpl"
  "./$foldername/gradle.properties.tpl"
  "./$foldername/neoforge/build.gradle.tpl"
  "./$foldername/neoforge/src/main/resources/META-INF/neoforge.mods.toml.tpl"
  "./$foldername/neoforge/src/main/java/com/chimericdream/$modid/neoforge/${classname}NeoForge.java.tpl"
  "./$foldername/fabric/build.gradle.tpl"
  "./$foldername/fabric/src/main/resources/fabric.mod.json.tpl"
  "./$foldername/fabric/src/main/java/com/chimericdream/$modid/fabric/${classname}Fabric.java.tpl"
  "./$foldername/fabric/src/main/java/com/chimericdream/$modid/fabric/client/${classname}FabricClient.java.tpl"
  "./$foldername/common/build.gradle.tpl"
  "./$foldername/common/src/main/resources/architectury.common.json.tpl"
  "./$foldername/common/src/main/resources/$modid.mixins.json.tpl"
  "./$foldername/common/src/main/java/com/chimericdream/$modid/ModInfo.java.tpl"
  "./$foldername/common/src/main/java/com/chimericdream/$modid/${classname}Mod.java.tpl"
);

for tpl in ${templateFiles[@]}; do
  sed -Ei "s/\{\{MOD_NAME\}\}/$modname/g" $tpl
  sed -Ei "s/\{\{MOD_DESC\}\}/$moddesc/g" $tpl
  sed -Ei "s/\{\{MOD_ID\}\}/$modid/g" $tpl
  sed -Ei "s/\{\{FOLDER_NAME\}\}/$foldername/g" $tpl
  sed -Ei "s/\{\{CLASS_NAME\}\}/$classname/g" $tpl
done

mv "./$foldername/build.gradle.tpl" "./$foldername/build.gradle"
mv "./$foldername/gradle.properties.tpl" "./$foldername/gradle.properties"
mv "./$foldername/neoforge/build.gradle.tpl" "./$foldername/neoforge/build.gradle"
mv "./$foldername/neoforge/src/main/resources/META-INF/neoforge.mods.toml.tpl" "./$foldername/neoforge/src/main/resources/META-INF/neoforge.mods.toml"
mv "./$foldername/neoforge/src/main/java/com/chimericdream/$modid/neoforge/${classname}NeoForge.java.tpl" "./$foldername/neoforge/src/main/java/com/chimericdream/$modid/neoforge/${classname}NeoForge.java"
mv "./$foldername/fabric/build.gradle.tpl" "./$foldername/fabric/build.gradle"
mv "./$foldername/fabric/src/main/resources/fabric.mod.json.tpl" "./$foldername/fabric/src/main/resources/fabric.mod.json"
mv "./$foldername/fabric/src/main/java/com/chimericdream/$modid/fabric/${classname}Fabric.java.tpl" "./$foldername/fabric/src/main/java/com/chimericdream/$modid/fabric/${classname}Fabric.java"
mv "./$foldername/fabric/src/main/java/com/chimericdream/$modid/fabric/client/${classname}FabricClient.java.tpl" "./$foldername/fabric/src/main/java/com/chimericdream/$modid/fabric/client/${classname}FabricClient.java"
mv "./$foldername/common/build.gradle.tpl" "./$foldername/common/build.gradle"
mv "./$foldername/common/src/main/resources/architectury.common.json.tpl" "./$foldername/common/src/main/resources/architectury.common.json"
mv "./$foldername/common/src/main/resources/$modid.mixins.json.tpl" "./$foldername/common/src/main/resources/$modid.mixins.json"
mv "./$foldername/common/src/main/java/com/chimericdream/$modid/ModInfo.java.tpl" "./$foldername/common/src/main/java/com/chimericdream/$modid/ModInfo.java"
mv "./$foldername/common/src/main/java/com/chimericdream/$modid/${classname}Mod.java.tpl" "./$foldername/common/src/main/java/com/chimericdream/$modid/${classname}Mod.java"

rm "./$foldername/package.json"
