# YSM Epic Fight Compat — port Fabric 1.21.1

Port dari NeoForge 1.21.1 ke **Fabric 1.21.1** untuk:
- Epic Fight Fabric port `21.17.3-fabric.1` (`libs/epicfight-fabric-21_17_3-fabric_1.jar`)
- Yes Steve Model `2.6.5-fabric+mc1.21.1` (resmi)
- YSM Mapping API `0.1.8` untuk MC 1.21.1 (modul `fabric`)

## Cara build
1. JDK 21. `./gradlew build` (Gradle mengambil Mapping API dari tag `0.1.8-mc1.21.1` lewat git).
2. Jar hasil: `build/libs/ysm-epicfight-compat-fabric-*.jar`.
3. Di folder `mods` game harus ada: Fabric API, Forge Config API Port, Epic Fight Fabric port,
   YSM 2.6.5 Fabric, dan **YSM Mapping API (jar Fabric)**. Jar Mapping API di-build dari
   https://github.com/sakuraimikoto33/YSM-Mapping-API tag `0.1.8-mc1.21.1` (`./gradlew :fabric:build`).
   Jar YSM tidak ikut di zip ini (lisensi).

## Yang diganti
| NeoForge | Fabric |
|---|---|
| `@Mod`, `IEventBus` | `ModInitializer` + `ClientModInitializer` (`CompatMod`, `client/CompatClient`) |
| `RegisterPayloadHandlersEvent` / `PacketDistributor` | `PayloadTypeRegistry`, `ServerPlayNetworking`, `ClientPlayNetworking`, `PlayerLookup` (`CompatNetwork`, `client/CompatNetworkClient`) |
| `IPayloadContext` | `platform/PayloadContext` (nama method sama) |
| `@SubscribeEvent` server (login, tracking, tick, dll.) | Event Fabric API di `platform/CompatEvents`; handler lama tidak diubah |
| `EntityMountEvent` | `EntityMountMixin` |
| `CommonHooks.onDamageBlock` (shield) | `LivingEntityShieldMixin` + `PlayerShieldMixin` (`hurtCurrentlyUsingShield`) |
| `RenderPlayerEvent.Pre` HIGHEST | `PlayerRendererCombatMixin` (priority 50, sebelum mixin YSM) |
| `RenderFrameEvent.Pre` | `GameRendererFrameMixin` |
| Client tick / disconnect / tags / command / entity unload | `ClientTickEvents`, `ClientPlayConnectionEvents`, `CommonLifecycleEvents.TAGS_LOADED`, `ClientSendMessageEvents.COMMAND`, `ClientEntityEvents` |
| `ModConfigSpec`, `ModConfig` | tetap sama lewat Forge Config API Port (`NeoForgeConfigRegistry`) |
| `ModList`, `FMLPaths` | `FabricLoader` |
| NeoForge attachment NBT | `PlayerSelectionNbt` mencari compound `model_id`/`select_texture` secara struktural |
| Mixin ke Epic Fight | `remap=false` dihapus (deskriptor memuat tipe MC; refmap dibuat Loom). Semua target sudah dicek ada di jar EF Fabric port |

## Belum / berbeda (jujur)
- **Belum pernah dikompilasi atau dijalankan** (sandbox tanpa akses maven Fabric). Cek sintaks lewat javac lolos, tapi tipe Minecraft/Fabric belum terverifikasi. Kalau ada error build, kirim lognya.
- Mixin ke class YSM tetap lewat alias Mapping API. Berhasil atau tidaknya tergantung snapshot Mapping API untuk YSM 2.6.5 Fabric.
- Dibuang: integrasi GUI Configured (`integration/configured`, 3 mixin), filter peringatan loading NeoForge, dan `ScreenInputEvents` (klik yang menutup GUI bisa bocor jadi klik gameplay).
- `ysm.swim_speed` dan `ysm.nametag_distance` memakai nilai default tetap (atribut khusus NeoForge).
- Hook shield dipasang `require = 0`, jadi kalau gagal fitur animasi blok mati diam-diam tanpa crash.
- Folder `src/test` tidak ikut karena bergantung pada NeoForge.
