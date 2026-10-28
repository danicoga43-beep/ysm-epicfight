package net.okitsu.ysmepicfightcompat.platform.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public final class EntityMountEvent {
    private final Entity mounting;
    private final Entity beingMounted;
    private final boolean mount;

    public EntityMountEvent(Entity mounting, Entity beingMounted, boolean mount) {
        this.mounting = mounting;
        this.beingMounted = beingMounted;
        this.mount = mount;
    }

    public Level getLevel() {
        return mounting.level();
    }

    public Entity getEntityMounting() {
        return mounting;
    }

    public Entity getEntityBeingMounted() {
        return beingMounted;
    }

    public boolean isMounting() {
        return mount;
    }
}
