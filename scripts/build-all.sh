#!/bin/sh

projectList=(
    "archaeology-tweaks"
    "athenaeum"
    "banner-tweaks"
    "beacon-conduit-tweaks"
    "blacklight"
    "chimeric-lib"
    "cobblicious"
    "enchantment-numbers-fix"
    "flat-bedrock"
    "hopper-xtreme"
    "houdini-block"
    "jdcrafte"
    "minekea"
    "miniblock-merchants"
    "pannotia-companion"
    "sponj"
    "villager-tweaks"
);

./gradlew clean

for project in ${projectList[@]}; do
  ./gradlew :$project:common:build
  ./gradlew :$project:fabric:build
  ./gradlew :$project:neoforge:build
done
