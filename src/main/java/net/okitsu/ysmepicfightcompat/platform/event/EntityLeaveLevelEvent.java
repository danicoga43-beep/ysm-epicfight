package net.okitsu.ysmepicfightcompat.platform.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public final class EntityLeaveLevelEvent {
    private final Entity entity;
    private final Level level;

    public EntityLeaveLevelEvent(Entity entity, Level level) {
        this.entity = entity;
        this.level = level;
    }

    public Entity getEntity() {
        return entity;
    }

    public Level getLevel() {
        return level;
    }
}
