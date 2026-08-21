package com.chimericdream.effectivegear.neoforge.data;

import com.chimericdream.effectivegear.ModInfo;
import com.chimericdream.effectivegear.item.armor.Trims;
import com.chimericdream.lib.trims.ArmorTrimAtlasProvider;
import com.chimericdream.lib.trims.TrimMaterialConfig;
import com.chimericdream.lib.trims.TrimMaterialRegistryHelper;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID)
public class ModDataGenerator {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Server event) {
        RegistrySetBuilder registryBuilder = new RegistrySetBuilder()
            .add(Registries.TRIM_MATERIAL, context -> TrimMaterialRegistryHelper.bootstrap(context, Trims.MATERIALS));

        event.createDatapackRegistryObjects(registryBuilder);

        event.addProvider(new ArmorTrimAtlasProvider(event.getGenerator().getPackOutput(), Trims.MATERIALS));
        event.addProvider(new EffectiveGearLangProvider(event.getGenerator().getPackOutput()));
    }

    private static class EffectiveGearLangProvider extends LanguageProvider {
        EffectiveGearLangProvider(PackOutput output) {
            super(output, ModInfo.MOD_ID, "en_us");
        }

        @Override
        protected void addTranslations() {
            for (TrimMaterialConfig material : Trims.MATERIALS) {
                add(material.translationKey(), material.displayName());
            }
        }
    }
}
