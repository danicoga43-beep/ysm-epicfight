# Fabric port status

## Target

- Minecraft 1.21.1, Java 21, Fabric Loader 0.16.14+
- Fox Model Loader 1.3.6 (`yes_steve_model`)
- Epic Fight Fabric 21.17.3

## Implemented in this source

- Fabric entrypoints, event bridge, payload adapters, and Epic Fight/Fox mixins.
- Runtime mappings for Fox player model selection, roaming-variable storage, model lookup, textures, and sound resources.
- Ground speed sampling through Fox Model Loader's `MovementQuery.getPhysicalGroundSpeed` and the player's live position tracker.
- Model audio routed through Fox's `YSMSoundInstance` and `IAudioStreamFactory`, so Fox's existing Fabric sound hook receives streams.
- GitHub Actions compiles on pushes/manual runs, uploads the remapped jar on success, and retains the build log on failure.

## Verification limits

- The Fox API symbols above were checked against the included Fox Model Loader 1.3.6 jar.
- Gradle was intentionally not run in this workspace. The first compile is performed by the included GitHub Actions workflow after the source is pushed.
- No Minecraft client or multiplayer session was run here. Runtime behavior still needs a client test with the exact target versions.
- This Fabric source targets Fox Model Loader; it does not promise support for the separate official YSM build that shares the same mod id.
- Optional upstream integrations (Configured, ParCool, SWEM, Iris, and Touhou Little Maid) are not verified as part of this Fabric port.

## Build output

The GitHub Actions artifact named `ysm-epicfight-compat-jar` contains the remapped mod jar. On a failed build, download `build-log` to inspect the compiler output.
