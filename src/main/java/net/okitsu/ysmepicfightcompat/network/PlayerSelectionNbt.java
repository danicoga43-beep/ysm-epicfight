package net.okitsu.ysmepicfightcompat.network;

import com.elfmcys.yesstevemodel.capability.ModelInfoCapability;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Method;
import java.util.Optional;

/// Membaca pilihan model pemain dari mod YSM yang terpasang:
///  - Fox Model Loader (mod id yes_steve_model, nama kelas terbaca): lewat ModelInfoCapability.
///  - YSM resmi 2.6.5 (nama kelas obfuscated): lewat refleksi ke kelas kapabilitas model dan getter-nya.
public final class PlayerSelectionNbt {
    public record Selection(String modelId, String textureName) {
    }

    private PlayerSelectionNbt() {
    }

    public static Selection read(Player player) {
        if (player == null) {
            return null;
        }

        Selection selection = readFoxModelLoader(player);
        return selection != null ? selection : readOfficial(player);
    }

    private static Selection build(String model, String texture) {
        return model == null || model.isEmpty() ? null : new Selection(model, texture == null ? "" : texture);
    }

    // ---- Fox Model Loader ----

    private static volatile boolean foxUnavailable;

    private static Selection readFoxModelLoader(Player player) {
        if (foxUnavailable) {
            return null;
        }
        try {
            return ModelInfoCapability.get(player)
                    .filter(info -> !info.isDisabled())
                    .map(info -> build(info.getModelId(), info.getSelectTexture()))
                    .orElse(null);
        } catch (LinkageError linkageError) {
            foxUnavailable = true; // kelas Fox Model Loader tidak ada (YSM resmi terpasang)
            return null;
        } catch (RuntimeException exception) {
            return null;
        }
    }

    // ---- YSM resmi 2.6.5 ----

    private static final String OFFICIAL_PACKAGE = "com.elfmcys.yesstevemodel.";

    private static final class Official {
        static final boolean AVAILABLE;
        static final Method GET_CAPABILITY;
        static final Object CAPABILITY_KEY;
        static final Method MODEL_ID;
        static final Method SELECT_TEXTURE;
        static final Method DISABLED;

        static {
            Method getCapability = null;
            Object key = null;
            Method modelId = null;
            Method selectTexture = null;
            Method disabled = null;
            boolean ok = false;
            try {
                Class<?> keyHolder = Class.forName(OFFICIAL_PACKAGE + "o0o0oO0o00o0ooOooO00O0OO");
                var keyField = keyHolder.getDeclaredField("OO0ooo0OooOoO0OoO0ooOO0o");
                keyField.setAccessible(true);
                key = keyField.get(null);

                Class<?> keyType = Class.forName(OFFICIAL_PACKAGE + "oOOoOOOoooOoo0000oooo0Oo");
                getCapability = Entity.class.getDeclaredMethod("OO0ooo0OooOoO0OoO0ooOO0o", keyType);
                getCapability.setAccessible(true);

                Class<?> capability = Class.forName(OFFICIAL_PACKAGE + "Oo0OO0o0Ooo0OO0o0oOo00oo");
                modelId = capability.getMethod("Ooo0OOOOO00O0Oo0oo0OOOo0");
                selectTexture = capability.getMethod("ooOoOOO00Oo0o0oO0Ooo0oO0");
                disabled = capability.getMethod("oOoO0OOO000OooO0OoooOOO0");
                ok = true;
            } catch (ReflectiveOperationException | LinkageError | RuntimeException exception) {
                ok = false;
            }
            AVAILABLE = ok;
            GET_CAPABILITY = getCapability;
            CAPABILITY_KEY = key;
            MODEL_ID = modelId;
            SELECT_TEXTURE = selectTexture;
            DISABLED = disabled;
        }
    }

    private static Selection readOfficial(Player player) {
        if (!Official.AVAILABLE) {
            return null;
        }
        try {
            Object result = Official.GET_CAPABILITY.invoke(player, Official.CAPABILITY_KEY);
            if (!(result instanceof Optional<?> optional) || optional.isEmpty()) {
                return null;
            }
            Object capability = optional.get();
            if ((Boolean) Official.DISABLED.invoke(capability)) {
                return null;
            }
            return build((String) Official.MODEL_ID.invoke(capability), (String) Official.SELECT_TEXTURE.invoke(capability));
        } catch (ReflectiveOperationException | RuntimeException exception) {
            return null;
        }
    }
}
