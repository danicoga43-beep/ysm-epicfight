package net.okitsu.ysmmapping.api;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * Semantic mappings verified against Fox Model Loader 1.3.6 for Minecraft 1.21.1.
 * Symbols without a Fox equivalent stay unavailable instead of being guessed.
 */
public final class YsmMappingApi {
    private static final Map<YsmSymbolKey<?>, Object> KNOWN = mappings();

    private YsmMappingApi() {}

    private static Map<YsmSymbolKey<?>, Object> mappings() {
        Map<YsmSymbolKey<?>, Object> symbols = new HashMap<>();
        add(symbols, YsmSymbols.CLIENT_AUDIO_STREAM_CACHE_ACQUIRE,
                "com/elfmcys/yesstevemodel/audio/AudioStreamCache", "getOrCreateProvider",
                "(Lcom/elfmcys/yesstevemodel/client/model/ModelAssembly;)Lcom/elfmcys/yesstevemodel/audio/IAudioStreamProvider;");
        add(symbols, YsmSymbols.CLIENT_AUDIO_STREAM_OPEN,
                "com/elfmcys/yesstevemodel/audio/IAudioStreamProvider", "createAudioStream",
                "(Lcom/elfmcys/yesstevemodel/audio/AudioTrackData;)Lcom/elfmcys/yesstevemodel/audio/IAudioStreamSupport;");
        add(symbols, YsmSymbols.CLIENT_MODEL_DATA_GETTER,
                "com/elfmcys/yesstevemodel/client/model/ModelAssembly", "getAnimationBundle",
                "()Lcom/elfmcys/yesstevemodel/client/model/PlayerModelBundle;");
        add(symbols, YsmSymbols.CLIENT_MODEL_LOOKUP,
                "com/elfmcys/yesstevemodel/client/ClientModelManager", "getModelContext",
                "(Ljava/lang/String;)Ljava/util/Optional;");
        add(symbols, YsmSymbols.CLIENT_MODEL_RESOURCES_GETTER,
                "com/elfmcys/yesstevemodel/client/model/ModelAssembly", "getExpressionCache",
                "()Lcom/elfmcys/yesstevemodel/client/model/ModelResourceBundle;");
        add(symbols, YsmSymbols.CLIENT_MODEL_SOUNDS_GETTER,
                "com/elfmcys/yesstevemodel/client/model/ModelResourceBundle", "getSoundEffects",
                "()Ljava/util/Map;");
        add(symbols, YsmSymbols.CLIENT_MODEL_TEXTURES_GETTER,
                "com/elfmcys/yesstevemodel/client/model/PlayerModelBundle", "getTextures",
                "()Lcom/elfmcys/yesstevemodel/util/data/OrderedStringMap;");
        add(symbols, YsmSymbols.CLIENT_TEXTURE_CACHE_ACQUIRE,
                "com/elfmcys/yesstevemodel/client/upload/UploadManager", "getOrCreateLocatable",
                "(Lnet/minecraft/class_1044;Z)Lcom/elfmcys/yesstevemodel/client/upload/IResourceLocatable;");
        add(symbols, YsmSymbols.CLIENT_TEXTURE_LOCATION_GETTER,
                "com/elfmcys/yesstevemodel/client/upload/IResourceLocatable", "getResourceLocation",
                "()Ljava/util/Optional;");
        add(symbols, YsmSymbols.MOLANG_GROUND_SPEED2_QUERY,
                "com/elfmcys/yesstevemodel/geckolib3/util/MovementQuery", "getPhysicalGroundSpeed",
                "(Lnet/minecraft/class_1297;Lcom/elfmcys/yesstevemodel/geckolib3/core/EntityFrameStateTracker;)F");
        add(symbols, YsmSymbols.MOLANG_QUERY_CONTEXT_GET,
                "com/elfmcys/yesstevemodel/geckolib3/core/AnimatableEntity", "getPositionTracker",
                "()Lcom/elfmcys/yesstevemodel/geckolib3/core/EntityFrameStateTracker;");
        add(symbols, YsmSymbols.PLAYER_STATE_MODEL_DISABLED_GETTER,
                "com/elfmcys/yesstevemodel/client/entity/CustomPlayerEntity", "isDisabledState",
                "()Z");
        add(symbols, YsmSymbols.PLAYER_STATE_MODEL_ID_GETTER,
                "com/elfmcys/yesstevemodel/client/entity/CustomPlayerEntity", "getSelectedModelId",
                "()Ljava/lang/String;");
        add(symbols, YsmSymbols.PLAYER_STATE_ROAMING_NAME_HASHER,
                "com/elfmcys/yesstevemodel/geckolib3/core/molang/util/StringPool", "getName",
                "(Ljava/lang/String;)I");
        add(symbols, YsmSymbols.PLAYER_STATE_ROAMING_PROVIDER_GETTER,
                "com/elfmcys/yesstevemodel/client/entity/CustomPlayerEntity", "getServerVarContainer",
                "()Lcom/elfmcys/yesstevemodel/molang/runtime/Struct;");
        add(symbols, YsmSymbols.PLAYER_STATE_ROAMING_VALUE_GETTER,
                "com/elfmcys/yesstevemodel/molang/runtime/Struct", "getProperty",
                "(I)Ljava/lang/Object;");
        add(symbols, YsmSymbols.PLAYER_STATE_ROAMING_VALUE_SETTER,
                "com/elfmcys/yesstevemodel/molang/runtime/Struct", "putProperty",
                "(ILjava/lang/Object;)V");
        return Map.copyOf(symbols);
    }

    private static void add(Map<YsmSymbolKey<?>, Object> target,
                            YsmSymbolKey<YsmMethodSymbol> key,
                            String owner, String name, String descriptor) {
        target.put(key, new YsmMethodSymbol(owner, name, descriptor));
    }

    public static MappingSnapshot resolve(String consumerId, Set<YsmSymbolKey<?>> required) {
        for (YsmSymbolKey<?> key : required) {
            if (!KNOWN.containsKey(key)) {
                throw new IllegalStateException("Fox Model Loader symbol is unavailable: " + key.id());
            }
        }
        return new MappingSnapshot(KNOWN);
    }
}
