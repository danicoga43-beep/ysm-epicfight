package net.okitsu.ysmepicfightcompat.mixin.plugin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/// Mod YSM yang terpasang bisa Fox Model Loader (nama kelas terbaca) atau YSM resmi (nama obfuscated).
/// Mixin untuk dua varian itu didaftarkan bersama; yang kelas targetnya tidak ada di mod yang terpasang dilewati
/// tanpa peringatan. Plugin ini sengaja tidak menyentuh kelas game apa pun.
public final class YsmTargetPlugin implements IMixinConfigPlugin {
    private static final String YSM_PACKAGE = "com.elfmcys.yesstevemodel.";

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!targetClassName.startsWith(YSM_PACKAGE)) {
            return true;
        }
        String resource = targetClassName.replace('.', '/') + ".class";
        ClassLoader loader = YsmTargetPlugin.class.getClassLoader();
        return loader.getResource(resource) != null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
