package com.chimericdream.rulesforthee.compat;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Applies the mixins in {@code rulesforthee.compat.mixins.json} only when the third-party class each one
 * patches exists, so modpacks without that mod are unaffected. To support another mod, add a mixin class
 * under {@code mixin.compat}, list it in that config, and add one {@link #TARGETS} row.
 *
 * <p>The check looks for the class file without loading it: loading a class from a plugin would run it
 * before Mixin got the chance to transform it.
 */
public final class CompatMixinPlugin implements IMixinConfigPlugin {
    private static final Logger LOGGER = Logger.getLogger("rulesforthee");

    /** Mixin class (simple name, under {@code mixin.compat}) to the class it patches, in dotted form. */
    private static final Map<String, String> TARGETS = Map.of(
        "RFT$YourItemsAreSafeMixin", "com.serilum.youritemsaresafe.events.DeathEvent"
    );

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String simpleName = mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1);
        String target = TARGETS.get(simpleName);
        if (target == null) {
            return false;
        }

        boolean present = classExists(target);
        if (present) {
            LOGGER.info("Rules for Thee: enabling compatibility with " + target);
        }

        return present;
    }

    private static boolean classExists(String className) {
        String path = className.replace('.', '/') + ".class";
        try (InputStream stream = MixinService.getService().getResourceAsStream(path)) {
            return stream != null;
        } catch (IOException e) {
            return false;
        }
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
