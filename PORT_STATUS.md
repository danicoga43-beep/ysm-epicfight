# Port YSM-Epic-Fight-Compat -> Fabric 1.21.1 (target: Fox Model Loader = mod id `yes_steve_model` 1.3.6)

Sumber: YSM-EpicFight-Compat (MIT, sakuraimikoto33), NeoForge 1.21.1, menargetkan YSM resmi lewat YSM Mapping API.
Ukuran: 231 file Java di main (sekitar 44 ribu baris) + 29 mixin.

## Tahap 1 (zip ini)
- Proyek Fabric (Loom, Mojang mappings) + workflow GitHub Actions. Jar Epic Fight Fabric dan Fox Model Loader ada di `libs/`.
- Import NeoForge dialihkan ke shim Epic Fight Fabric (IPayloadContext, PacketDistributor, PayloadRegistrar, bus, ClientTick/RenderFrame)
  dan shim kecil lokal (`fabric/shim`: Dist, OnlyIn, ModList, FMLPaths).
- File yang menyentuh NeoForge turun dari 54 menjadi 23 (daftar: SISA_FILE_NEOFORGE.txt). 6 di antaranya hanya memakai
  `ModConfigSpec`/`ModConfig` yang disediakan Forge Config API Port, jadi boleh tetap.
- Entrypoint sementara `CompatModFabric`: hanya mencatat log. Mod ini BELUM berfungsi dan belum dikompilasi.

## Sisa pekerjaan
1. Event NeoForge -> callback Fabric (belum ada di shim): ServerStopped, ServerTick, PlayerEvent (login/logout/clone),
   EntityLeaveLevel, EntityJoinLevel, EntityMount, LivingShieldBlock, TagsUpdated, Command, RenderPlayer, Screen,
   ClientPlayerNetwork, RegisterClientReloadListeners, FMLLoadComplete/ClientSetup, DamageContainer, CommonHooks.
2. `CompatMod` (@Mod) -> entrypoint Fabric penuh, registrasi payload (24 handler network memakai IPayloadContext shim).
3. Lapisan YSM: 5 file memakai YSM Mapping API (khusus YSM resmi yang diobfuscate) + 7 alias di `ysmref`.
   Fox Model Loader memakai nama kelas terbaca (`com.elfmcys.yesstevemodel`), jadi lapisan ini diganti dengan rujukan langsung.
   Perlu dicek tiap simbol yang dipakai terhadap jar Fox Model Loader.
4. 29 mixin: sebagian menarget Epic Fight (nama kelas sama di port Fabric, tetapi perlu dicek tiap target), sebagian menarget YSM
   lewat alias/mapping (harus diarahkan ulang ke kelas Fox Model Loader), sebagian menarget Configured/TouhouLittleMaid (dibuang dulu).
5. Integrasi opsional (Configured, ParCool, SWEM, TLM, Oculus): dibuang dulu, ditambah belakangan.
6. Menghidupkan kembali `mixins.json` (sengaja belum ada supaya jar tahap 1 tidak bisa crash saat dimuat).


## Tahap 2 (zip ini)
- Shim sendiri `fabric/shim`: Dist, OnlyIn, ModList, FMLPaths, ServerLifecycleHooks, event bus (`bus/`) dan kelas event bergaya NeoForge (`event/`).
  (Catatan: pengalihan tahap 1 ke shim bus Epic Fight salah karena API-nya berbeda; sudah dikembalikan ke shim sendiri.)
- `FabricEventBridge`: callback Fabric -> event: login/logout, clone, respawn, ganti dimensi, start/stop tracking, server tick, server stopped,
  entity join/leave (server dan klien), tags updated, client tick, disconnect klien, perintah klien, reload listener klien.
- Entrypoint `CompatModFabric` (umum) dan `CompatModFabricClient`; config lewat Forge Config API Port; paket jaringan lewat `PayloadRegistrar` Epic Fight.
- Stub Mapping API (`net.okitsu.ysmmapping.api`): simbol YSM resmi belum dipetakan ke Fox Model Loader, jadi fitur yang memakainya
  (roaming variables, ground speed query, texture/audio YSM, sinkron animasi) akan mati dengan aman dan mencatat peringatan di log.
- Pilihan model pemain dibaca lewat `ModelInfoCapability` milik Fox Model Loader (`getModelId`, `getSelectTexture`, `isDisabled`).

## Belum (tahap 3)
- `mixins.json` belum diaktifkan. Mixin yang tidak bergantung pada YSM/Configured masih ada di `mixin/`; yang bergantung disimpan di `_pending/`.
  Mixin yang menarget kelas YSM resmi harus diarahkan ke kelas Fox Model Loader.
- Belum ada padanan Fabric untuk: EntityMountEvent, LivingShieldBlockEvent (perlu mixin), ScreenEvent klik, RenderPlayerEvent.Pre (CombatRenderInterceptor tidak aktif).
- Memetakan simbol YSM satu per satu di `YsmMappingApi` (lihat daftar di `YsmSymbols`).
- Belum pernah dikompilasi: dari hasil build pertama, jumlah dan jenis error baru bisa diketahui.


## Putaran 3 (dari build-log pertama dengan semua error)
Log GitHub Actions menunjukkan 174 error dari ~10 penyebab, semuanya diperbaiki:
- `IPayloadContext` Epic Fight hanya punya `player()`; compat butuh `flow()`, `enqueueWork`, `connection()` -> shim `fabric/shim/network` sendiri (+ `PayloadRegistrar`, `IPayloadHandler`).
- `ServerGamePacketListenerImpl#getConnection` (patch NeoForge) -> `isAcceptingMessages()`.
- `ClientPacketListener#hasChannel` -> `ClientPlayNetworking.canSend`.
- `ModList#getModContainerById/getModFileById` -> FabricLoader (`getModContainer`, `findPath`).
- `ElytraLayer#shouldRender` (patch NeoForge) -> `is(Items.ELYTRA)`.
- `SoundInstance#getStream` (patch NeoForge) tidak di-override di Fabric: suara model YSM kustom belum aktif sampai ada mixin SoundEngine.


## Putaran 4 (build sukses -> mixin)
- Build kompilasi SUKSES (jar jadi). `ysm_epicfight_compat.mixins.json` diaktifkan dengan `required:false` dan `defaultRequire:0`
  (mixin yang gagal hanya memberi peringatan di log, tidak membuat crash).
- Aktif: 1 mixin umum + 13 mixin klien yang menarget Epic Fight/vanilla (sudah divalidasi processor mixin saat kompilasi,
  kecuali `FirstPersonRenderScopeMixin` yang descriptor `epicfight$renderHand` disesuaikan ke `RenderHandEvent` shim Fabric).
- Belum aktif (menarget kelas YSM lewat alias `ysmref/*` yang dulu diterjemahkan Mapping API): CombatOverlayMixin, PlayerStateCapabilityMixin,
  YsmRoamingProviderBindingMixin, YsmFishingHookRendererMixin, YsmProjectileRendererMixin, YsmVehicleRendererMixin, YsmVehiclePreviewMixin,
  YsmConfigurationExpressionMixin. Perlu diarahkan ke kelas Fox Model Loader (nama terbaca) satu per satu.
- Belum aktif karena target di mod lain: ConfiguredAnimationRateSlider/ForgeConfig/ForgeFolderEntry (Configured), TouhouMaid* (TLM).


## Putaran 5
- 7 mixin yang menarget YSM diarahkan langsung ke kelas Fox Model Loader (descriptor dalam nama intermediary karena jar Fox Model Loader
  adalah build produksi): CustomFishingHookRenderer.tryRenderCustomHook, CustomProjectileRenderer.renderProjectile, CustomVehicleRenderer.renderVehicle,
  ModelPreviewRenderer.renderVehicleModel dan renderPlayerOverlay (x,y bertipe double), AnimationRouletteScreen.executeExpression, PlayerCapability.<init>(Player).
- Tidak ada padanan: `YsmRoamingProviderBindingMixin` (AnimationContext Fox Model Loader tidak punya bindRoamingProvider) -> tidak diaktifkan.
- Pilihan model pemain tetap lewat `ModelInfoCapability` (PlayerSelectionNbt); jalur "Official*" via Mapping API tetap nonaktif.


## Putaran 6: dua jalur YSM (Fox Model Loader dan YSM resmi 2.6.5)
- Kedua mod memakai id mod `yes_steve_model`, jadi tidak bisa dipasang bersamaan; compat sekarang mendukung yang mana pun terpasang.
- 7 mixin varian resmi (`Official*Mixin`, nama kelas dan method obfuscated 2.6.5; kedua jar resmi yang diupload identik). `YsmTargetPlugin` melewati
  mixin yang kelas targetnya tidak ada di mod yang terpasang.
- Pilihan model YSM resmi dibaca lewat refleksi: kunci kapabilitas `o0o0oO0o00o0ooOooO00O0OO.OO0ooo0OooOoO0OoO0ooOO0o`, pembaca `Entity#OO0ooo0OooOoO0OoO0ooOO0o(key)`,
  getter model id `Ooo0OOOOO00O0Oo0oo0OOOo0`, tekstur `ooOoOOO00Oo0o0oO0Ooo0oO0`, status mati `oOoO0OOO000OooO0OoooOOO0` (urutan dipastikan dari bytecode serialisasi NBT).
- Hanya berlaku untuk YSM resmi 2.6.5-fabric+mc1.21.1; versi lain butuh tabel baru.


## Putaran 7: perbaikan crash startup + hook Fabric yang kurang
Crash (`Mixin transformation of yesman.epicfight.api.animation.types.AttackAnimation failed`, `Scanned 0 target(s)`):
- Penyebab: mixin ke kelas Epic Fight memakai `remap = false` sementara selector/target-nya memuat tipe Minecraft bernama Mojang
  (mis. `Lnet/minecraft/sounds/SoundEvent;`). Di game produksi tipe itu bernama `class_3414`, jadi target tidak ketemu; `require = 1`
  membuat kegagalan itu menjatuhkan seluruh inisialisasi Epic Fight.
- Perbaikan: semua selector dicocokkan ke bytecode jar Epic Fight (javap). Selector yang unik dipakai berdasarkan nama saja
  (`attackTick`, `getCorrectionMatrix`, `renderLayer`, `epicfight$renderHand`, target `...;playSound` dst.), tanpa tipe Minecraft.
  Hanya dua method overload yang butuh descriptor (WearableItemLayer.renderLayer dan PatchedLivingEntityRenderer.render); keduanya
  ditulis dengan nama intermediary hasil pembacaan jar. Semua `require` dibuat 0 sehingga target yang tidak cocok hanya
  menonaktifkan satu fitur, tidak pernah menjatuhkan startup.
- Mixin ke Fox Model Loader (Putaran 5) diperiksa ulang terhadap descriptor di jar: semuanya cocok.

Hook yang di Tahap 6 belum ada padanannya, sekarang ada (mixin memposting event ke bus lokal):
- `PlayerRendererCombatMixin` (priority 100, berjalan sebelum mixin render pemain Fox Model Loader): RenderPlayerEvent.Pre -> `CombatRenderInterceptor`.
  Tanpa ini Epic Fight tidak pernah merender model YSM.
- `LivingEntityShieldBlockMixin`: shield block berhasil -> `ServerShieldBlockObserver`.
- `EntityMountMixin`: EntityMountEvent (naik kendaraan) untuk `SubEntityPreferenceBroadcaster`.
- `MouseHandlerClosingClickMixin`: klik yang menutup GUI tidak jadi serangan/aksi gameplay.

NeoForge: tidak ada lagi API/komponen NeoForge. Impor `net.neoforged.neoforge.common.ModConfigSpec` dan `net.neoforged.fml.config.ModConfig`
di config/ berasal dari Forge Config API Port (mod Fabric yang sudah wajib bagi Epic Fight dan Fox Model Loader), bukan dari NeoForge.
Sisanya (`fabric/shim/*`) adalah bus event lokal berbasis callback Fabric API.
