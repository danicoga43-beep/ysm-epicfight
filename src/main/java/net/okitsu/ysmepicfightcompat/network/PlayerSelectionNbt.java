package net.okitsu.ysmepicfightcompat.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;

/**
 * Reads the serialized selection of official YSM from a player's saved NBT.
 *
 * <p>NeoForge stored the value below {@code neoforge:attachments}. YSM on Fabric writes the same
 * compound ({@code model_id}, {@code select_texture}, {@code disabled}) through its own entity save
 * hook, so the compound is located structurally instead of by loader-specific key.</p>
 */
public final class PlayerSelectionNbt {
    private static final String MODEL_ID = "model_id";
    private static final String SELECT_TEXTURE = "select_texture";
    private static final String DISABLED = "disabled";
    private static final int MAX_DEPTH = 4;

    public record Selection(String modelId, String textureName) {
    }

    private PlayerSelectionNbt() {
    }

    public static Selection read(Player player) {
        if (player == null) {
            return null;
        }
        try {
            return parse(player.saveWithoutId(new CompoundTag()));
        } catch (RuntimeException exception) {
            return null;
        }
    }

    static Selection parse(CompoundTag root) {
        if (root == null) {
            return null;
        }
        CompoundTag selected = find(root, 0);
        if (selected == null || selected.getBoolean(DISABLED)) {
            return null;
        }
        String model = selected.getString(MODEL_ID);
        return model.isEmpty() ? null : new Selection(model, selected.getString(SELECT_TEXTURE));
    }

    private static CompoundTag find(CompoundTag tag, int depth) {
        if (tag.contains(MODEL_ID, Tag.TAG_STRING) && tag.contains(SELECT_TEXTURE, Tag.TAG_STRING)) {
            return tag;
        }
        if (depth >= MAX_DEPTH) {
            return null;
        }
        for (String key : tag.getAllKeys()) {
            if (tag.contains(key, Tag.TAG_COMPOUND)) {
                CompoundTag found = find(tag.getCompound(key), depth + 1);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
