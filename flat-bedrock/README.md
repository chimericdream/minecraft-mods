# Flat Bedrock (Fabric/NeoForge)

![Version: 4.0.0](https://img.shields.io/badge/version-4.0.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: not needed](https://img.shields.io/badge/client-not%20needed-9e9e9e?style=flat-square) ![Server: required](https://img.shields.io/badge/server-required-4caf50?style=flat-square)

_Flattens bedrock._

## Introduction

Vanilla generates bedrock as a jagged, randomized layer several blocks thick at the bottom of the world (and the
roof of the Nether). Flat Bedrock replaces that noisy gradient with a single, clean layer, giving you a flat and
predictable floor to build on and mine down to.

This is a world-generation tweak, so it only needs to be installed on the server (or in your single-player world).

### Minecraft Versions

* 26.2: supported

### Current Features

* Collapses the randomized bedrock **floor** into a flat layer at the bottom of the world, with a configurable
  thickness (1 by default, same as vanilla's thinnest spot).
* Collapses the randomized bedrock **roof** in the Nether into a flat layer, also with a configurable thickness,
  or removes it entirely with the "No roof" option.
* Lets you swap bedrock for a different block, separately for the Overworld and the Nether.
* All of the above can be set independently for the Overworld and the Nether via an in-game config screen
  (requires Mod Menu).
* Works by adjusting the surface rules that place bedrock, so it stays compatible with normal terrain generation.

### Notes

* This affects **newly generated** chunks. Terrain that has already generated keeps its original bedrock.
* The End doesn't generate bedrock in vanilla, so there's nothing for this mod to configure there.

## Notes for Documentation

This mod changes world generation only and adds no items or blocks, so in-game documentation is not applicable.

## Issues & Suggestions

Please use the GitHub issue tracker to report any bugs you find.

## Credits

Thanks go to the developers of the Fabric and NeoForge mod loaders and the Architectury API.

## License

This mod is released under the MIT license. [The full text of the license can be found here.](./LICENSE)
