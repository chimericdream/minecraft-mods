# Flat Bedrock (Fabric/NeoForge)

![Version: 4.0.0-alpha.0](https://img.shields.io/badge/version-4.0.0--alpha.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: not needed](https://img.shields.io/badge/client-not%20needed-9e9e9e?style=flat-square) ![Server: required](https://img.shields.io/badge/server-required-4caf50?style=flat-square)

_Flattens bedrock._

## Introduction

Vanilla generates bedrock as a jagged, randomized layer several blocks thick at the bottom of the world (and the
roof of the Nether). Flat Bedrock replaces that noisy gradient with a single, clean layer, giving you a flat and
predictable floor to build on and mine down to.

This is a world-generation tweak, so it only needs to be installed on the server (or in your single-player world).

### Minecraft Versions

* 26.2: supported

### Current Features

* Collapses the randomized bedrock **floor** into a single flat layer at the very bottom of the world.
* Collapses the randomized bedrock **roof** (e.g. the top of the Nether) into a single flat layer.
* Works by adjusting the surface rules that place bedrock, so it stays compatible with normal terrain generation.

### Notes

* This affects **newly generated** chunks. Terrain that has already generated keeps its original bedrock.

## Notes for Documentation

This mod changes world generation only and adds no items or blocks, so in-game documentation is not applicable.

## Issues & Suggestions

Please use the GitHub issue tracker to report any bugs you find.

## Credits

Thanks go to the developers of the Fabric and NeoForge mod loaders and the Architectury API.

## License

This mod is released under the MIT license. [The full text of the license can be found here.](./LICENSE)
