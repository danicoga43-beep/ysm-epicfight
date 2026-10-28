package net.okitsu.ysmepicfightcompat.event;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EntityType;
import net.okitsu.ysmepicfightcompat.CompatMod;
import net.okitsu.ysmepicfightcompat.mesh.CombatMeshCache;
import net.okitsu.ysmepicfightcompat.network.ClientSubEntityModelPreferences;
import net.okitsu.ysmepicfightcompat.network.geometry.ClientModelTransfers;
import net.okitsu.ysmepicfightcompat.render.CombatPlayerRenderer;
import net.okitsu.ysmepicfightcompat.render.PlayerSelectionResolver;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;
import yesman.epicfight.api.client.event.types.registry.RegisterPatchedRenderersEvent;

/** Registers the compatibility renderer and its client lifecycle hooks. */
public final class ClientLifecycleEvents {
    private ClientLifecycleEvents() {
    }

    public static void register() {
        // Runs during client initialization, before the initial renderer resource reload.
        // Epic Fight keys listeners by priority, so reserve a distinct final slot.
        EpicFightClientEventHooks.Registry.ADD_PATCHED_ENTITY.registerEvent(
                ClientLifecycleEvents::installPlayerRenderer,
                CompatMod.MOD_ID + ":combat_player_renderer", Integer.MIN_VALUE);
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(
                new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public ResourceLocation getFabricId() {
                        return ResourceLocation.fromNamespaceAndPath(CompatMod.MOD_ID, "client_caches");
                    }

                    @Override
                    public void onResourceManagerReload(ResourceManager resources) {
                        ClientSubEntityModelPreferences.modelDefinitionsUpdated();
                        PlayerSelectionResolver.clear();
                        ClientModelTransfers.clear();
                        CombatMeshCache.clear();
                    }
                });
    }

    public static void installPlayerRenderer(RegisterPatchedRenderersEvent.AddEntity event) {
        event.addPatchedEntityRenderer(EntityType.PLAYER, type ->
                new CombatPlayerRenderer(event.getContext(), type)
                        .initLayerLast(event.getContext(), type));
        CompatMod.LOG.info("YSM-EF Compat: installed combat player renderer");
    }
}
