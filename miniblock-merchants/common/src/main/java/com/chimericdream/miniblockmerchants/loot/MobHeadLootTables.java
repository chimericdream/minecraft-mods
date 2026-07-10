package com.chimericdream.miniblockmerchants.loot;

import com.chimericdream.miniblockmerchants.util.DataUtil;
import com.mojang.authlib.GameProfile;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.NbtPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import static com.chimericdream.miniblockmerchants.data.SkullTextureData.ALCHEMIST_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.ALCHEMIST_ZOMBIE_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.ARBORICULTURIST_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.ARBORICULTURIST_ZOMBIE_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.ASTRONOMER_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.ASTRONOMER_ZOMBIE_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.BAKER_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.BAKER_ZOMBIE_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.BARTENDER_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.BARTENDER_ZOMBIE_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.BEEKEEPER_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.BEEKEEPER_ZOMBIE_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.BLACKSMITH_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.BLACKSMITH_ZOMBIE_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.CHEF_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.CHEF_ZOMBIE_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.ENGINEER_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.ENGINEER_ZOMBIE_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.EREMOLOGIST_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.EREMOLOGIST_ZOMBIE_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.FURNISHER_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.FURNISHER_ZOMBIE_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.GAMEMASTER_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.GAMEMASTER_ZOMBIE_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.GRIEFER_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.GRIEFER_ZOMBIE_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.HORTICULTURIST_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.HORTICULTURIST_ZOMBIE_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.MINERALOGIST_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.MINERALOGIST_ZOMBIE_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.NETHEROGRAPHER_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.NETHEROGRAPHER_ZOMBIE_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.OCEANOGRAPHER_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.OCEANOGRAPHER_ZOMBIE_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.OLERICULTURIST_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.OLERICULTURIST_ZOMBIE_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.PETROLOGIST_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.PETROLOGIST_ZOMBIE_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.PLUSHIE_MANIAC_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.PLUSHIE_MANIAC_ZOMBIE_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.POMOLOGIST_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.POMOLOGIST_ZOMBIE_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.RECYCLER_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.RECYCLER_ZOMBIE_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.RITUALIST_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.RITUALIST_ZOMBIE_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.SCULPTOR_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.SCULPTOR_ZOMBIE_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.STEAMPUNKER_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.STEAMPUNKER_ZOMBIE_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.TAILOR_VILLAGER;
import static com.chimericdream.miniblockmerchants.data.SkullTextureData.TAILOR_ZOMBIE_VILLAGER;

public class MobHeadLootTables {
    private static NbtPredicate makeProfessionPredicate(String profession) {
        CompoundTag villagerData = new CompoundTag();
        villagerData.putString("profession", profession);

        CompoundTag nbt = new CompoundTag();
        nbt.put("VillagerData", villagerData);

        return new NbtPredicate(nbt);

    }

    private static LootPoolEntryContainer.Builder<?> getZombieHeadLootTable(String name, String profession, Tuple<String, int[]> data) {
        return getHeadLootTable(name, profession, data, true);
    }

    private static LootPoolEntryContainer.Builder<?> getHeadLootTable(String name, String profession, Tuple<String, int[]> data) {
        return getHeadLootTable(name, profession, data, false);
    }

    private static LootPoolEntryContainer.Builder<?> getHeadLootTable(String name, String profession, Tuple<String, int[]> data, boolean isZombieVillager) {
        Item headItem = Items.PLAYER_HEAD;

        String nbSound;
        if (isZombieVillager) {
            nbSound = "minecraft:entity.zombie_villager.ambient";
        } else {
            nbSound = "minecraft:entity.villager.ambient";
        }

        GameProfile gameProfile = DataUtil.makeGameProfile("mmmobhead", data);

        Component formattedName = MutableComponent.create(PlainTextContents.EMPTY)
            .append(name)
            .setStyle(Style.EMPTY.withItalic(false));

        LootItemFunction.Builder nameBuilder = () -> SetNameFunction.setName(formattedName, SetNameFunction.Target.ITEM_NAME).build();
        LootItemFunction.Builder textureBuilder = () -> SetComponentsFunction.setComponent(DataComponents.PROFILE, ResolvableProfile.createResolved(gameProfile)).build();
        LootItemFunction.Builder nbSoundBuilder = () -> SetComponentsFunction.setComponent(DataComponents.NOTE_BLOCK_SOUND, Identifier.parse(nbSound)).build();

        NbtPredicate professionPredicate = makeProfessionPredicate(profession);

        return LootItem.lootTableItem(headItem)
            .apply(nameBuilder)
            .apply(textureBuilder)
            .apply(nbSoundBuilder)
            .when(() -> LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().nbt(professionPredicate)).build());
    }

    public static LootPool.Builder getVillagerHeadLootTable() {
        LootPool.Builder builder = LootPool.lootPool();

        return builder
            .add(AlternativesEntry.alternatives(
                getAlchemistHeadLootTable(),
                getArboriculturistHeadLootTable(),
                getAstronomerHeadLootTable(),
                getBakerHeadLootTable(),
                getBartenderHeadLootTable(),
                getBeekeeperHeadLootTable(),
                getBlacksmithHeadLootTable(),
                getChefHeadLootTable(),
                getEngineerHeadLootTable(),
                getEremologistHeadLootTable(),
                getFurnisherHeadLootTable(),
                getGamemasterHeadLootTable(),
                getGrieferHeadLootTable(),
                getHorticulturistHeadLootTable(),
                getMineralogistHeadLootTable(),
                getNetherographerHeadLootTable(),
                getOceanographerHeadLootTable(),
                getOlericulturistHeadLootTable(),
                getPetrologistHeadLootTable(),
                getPlushieManiacHeadLootTable(),
                getPomologistHeadLootTable(),
                getRecyclerHeadLootTable(),
                getRitualistHeadLootTable(),
                getSculptorHeadLootTable(),
                getSteampunkerHeadLootTable(),
                getTailorHeadLootTable()
            ))
            .when(() -> LootItemKilledByPlayerCondition.killedByPlayer().build())
            .setRolls(ConstantValue.exactly(1));
    }

    public static LootPool.Builder getZombieVillagerHeadLootTable(HolderLookup.Provider wrapperLookup) {
        LootPool.Builder builder = LootPool.lootPool();

        return builder
            .add(AlternativesEntry.alternatives(
                getZombieAlchemistHeadLootTable(),
                getZombieArboriculturistHeadLootTable(),
                getZombieAstronomerHeadLootTable(),
                getZombieBakerHeadLootTable(),
                getZombieBartenderHeadLootTable(),
                getZombieBeekeeperHeadLootTable(),
                getZombieBlacksmithHeadLootTable(),
                getZombieChefHeadLootTable(),
                getZombieEngineerHeadLootTable(),
                getZombieEremologistHeadLootTable(),
                getZombieFurnisherHeadLootTable(),
                getZombieGamemasterHeadLootTable(),
                getZombieGrieferHeadLootTable(),
                getZombieHorticulturistHeadLootTable(),
                getZombieMineralogistHeadLootTable(),
                getZombieNetherographerHeadLootTable(),
                getZombieOceanographerHeadLootTable(),
                getZombieOlericulturistHeadLootTable(),
                getZombiePetrologistHeadLootTable(),
                getZombiePlushieManiacHeadLootTable(),
                getZombiePomologistHeadLootTable(),
                getZombieRecyclerHeadLootTable(),
                getZombieRitualistHeadLootTable(),
                getZombieSculptorHeadLootTable(),
                getZombieSteampunkerHeadLootTable(),
                getZombieTailorHeadLootTable()
            ))
            .when(() -> LootItemKilledByPlayerCondition.killedByPlayer().build())
            .when(() -> LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(wrapperLookup, 0.5f, 0.02f).build())
            .setRolls(ConstantValue.exactly(1));
    }

    public static LootPoolEntryContainer.Builder<?> getAlchemistHeadLootTable() {
        return getHeadLootTable("Alchemist", "miniblockmerchants:mm_alchemist", ALCHEMIST_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getZombieAlchemistHeadLootTable() {
        return getZombieHeadLootTable("Zombie Alchemist", "miniblockmerchants:mm_alchemist", ALCHEMIST_ZOMBIE_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getArboriculturistHeadLootTable() {
        return getHeadLootTable("Arboriculturist", "miniblockmerchants:mm_arboriculturist", ARBORICULTURIST_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getZombieArboriculturistHeadLootTable() {
        return getZombieHeadLootTable("Zombie Arboriculturist", "miniblockmerchants:mm_arboriculturist", ARBORICULTURIST_ZOMBIE_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getAstronomerHeadLootTable() {
        return getHeadLootTable("Astronomer", "miniblockmerchants:mm_astronomer", ASTRONOMER_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getZombieAstronomerHeadLootTable() {
        return getZombieHeadLootTable("Zombie Astronomer", "miniblockmerchants:mm_astronomer", ASTRONOMER_ZOMBIE_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getBakerHeadLootTable() {
        return getHeadLootTable("Baker", "miniblockmerchants:mm_baker", BAKER_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getZombieBakerHeadLootTable() {
        return getZombieHeadLootTable("Zombie Baker", "miniblockmerchants:mm_baker", BAKER_ZOMBIE_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getBartenderHeadLootTable() {
        return getHeadLootTable("Bartender", "miniblockmerchants:mm_bartender", BARTENDER_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getZombieBartenderHeadLootTable() {
        return getZombieHeadLootTable("Zombie Bartender", "miniblockmerchants:mm_bartender", BARTENDER_ZOMBIE_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getBeekeeperHeadLootTable() {
        return getHeadLootTable("Beekeeper", "miniblockmerchants:mm_beekeeper", BEEKEEPER_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getZombieBeekeeperHeadLootTable() {
        return getZombieHeadLootTable("Zombie Beekeeper", "miniblockmerchants:mm_beekeeper", BEEKEEPER_ZOMBIE_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getBlacksmithHeadLootTable() {
        return getHeadLootTable("Blacksmith", "miniblockmerchants:mm_blacksmith", BLACKSMITH_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getZombieBlacksmithHeadLootTable() {
        return getZombieHeadLootTable("Zombie Blacksmith", "miniblockmerchants:mm_blacksmith", BLACKSMITH_ZOMBIE_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getChefHeadLootTable() {
        return getHeadLootTable("Chef", "miniblockmerchants:mm_chef", CHEF_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getZombieChefHeadLootTable() {
        return getZombieHeadLootTable("Zombie Chef", "miniblockmerchants:mm_chef", CHEF_ZOMBIE_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getEngineerHeadLootTable() {
        return getHeadLootTable("Engineer", "miniblockmerchants:mm_engineer", ENGINEER_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getZombieEngineerHeadLootTable() {
        return getZombieHeadLootTable("Zombie Engineer", "miniblockmerchants:mm_engineer", ENGINEER_ZOMBIE_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getEremologistHeadLootTable() {
        return getHeadLootTable("Eremologist", "miniblockmerchants:mm_eremologist", EREMOLOGIST_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getZombieEremologistHeadLootTable() {
        return getZombieHeadLootTable("Zombie Eremologist", "miniblockmerchants:mm_eremologist", EREMOLOGIST_ZOMBIE_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getFurnisherHeadLootTable() {
        return getHeadLootTable("Furnisher", "miniblockmerchants:mm_furnisher", FURNISHER_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getZombieFurnisherHeadLootTable() {
        return getZombieHeadLootTable("Zombie Furnisher", "miniblockmerchants:mm_furnisher", FURNISHER_ZOMBIE_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getGamemasterHeadLootTable() {
        return getHeadLootTable("Gamemaster", "miniblockmerchants:mm_gamemaster", GAMEMASTER_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getZombieGamemasterHeadLootTable() {
        return getZombieHeadLootTable("Zombie Gamemaster", "miniblockmerchants:mm_gamemaster", GAMEMASTER_ZOMBIE_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getGrieferHeadLootTable() {
        return getHeadLootTable("Griefer", "miniblockmerchants:mm_griefer", GRIEFER_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getZombieGrieferHeadLootTable() {
        return getZombieHeadLootTable("Zombie Griefer", "miniblockmerchants:mm_griefer", GRIEFER_ZOMBIE_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getHorticulturistHeadLootTable() {
        return getHeadLootTable("Horticulturist", "miniblockmerchants:mm_horticulturist", HORTICULTURIST_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getZombieHorticulturistHeadLootTable() {
        return getZombieHeadLootTable("Zombie Gorticulturist", "miniblockmerchants:mm_horticulturist", HORTICULTURIST_ZOMBIE_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getMineralogistHeadLootTable() {
        return getHeadLootTable("Mineralogist", "miniblockmerchants:mm_mineralogist", MINERALOGIST_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getZombieMineralogistHeadLootTable() {
        return getZombieHeadLootTable("Zombie Mineralogist", "miniblockmerchants:mm_mineralogist", MINERALOGIST_ZOMBIE_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getNetherographerHeadLootTable() {
        return getHeadLootTable("Netherographer", "miniblockmerchants:mm_netherographer", NETHEROGRAPHER_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getZombieNetherographerHeadLootTable() {
        return getZombieHeadLootTable("Zombie Netherographer", "miniblockmerchants:mm_netherographer", NETHEROGRAPHER_ZOMBIE_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getOceanographerHeadLootTable() {
        return getHeadLootTable("Oceanographer", "miniblockmerchants:mm_oceanographer", OCEANOGRAPHER_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getZombieOceanographerHeadLootTable() {
        return getZombieHeadLootTable("Zombie Oceanographer", "miniblockmerchants:mm_oceanographer", OCEANOGRAPHER_ZOMBIE_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getOlericulturistHeadLootTable() {
        return getHeadLootTable("Olericulturist", "miniblockmerchants:mm_olericulturist", OLERICULTURIST_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getZombieOlericulturistHeadLootTable() {
        return getZombieHeadLootTable("Zombie Olericulturist", "miniblockmerchants:mm_olericulturist", OLERICULTURIST_ZOMBIE_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getPetrologistHeadLootTable() {
        return getHeadLootTable("Petrologist", "miniblockmerchants:mm_petrologist", PETROLOGIST_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getZombiePetrologistHeadLootTable() {
        return getZombieHeadLootTable("Zombie Petrologist", "miniblockmerchants:mm_petrologist", PETROLOGIST_ZOMBIE_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getPlushieManiacHeadLootTable() {
        return getHeadLootTable("Plushie Maniac", "miniblockmerchants:mm_plushie_maniac", PLUSHIE_MANIAC_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getZombiePlushieManiacHeadLootTable() {
        return getZombieHeadLootTable("Zombie Plushie Maniac", "miniblockmerchants:mm_plushie_maniac", PLUSHIE_MANIAC_ZOMBIE_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getPomologistHeadLootTable() {
        return getHeadLootTable("Pomologist", "miniblockmerchants:mm_pomologist", POMOLOGIST_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getZombiePomologistHeadLootTable() {
        return getZombieHeadLootTable("Zombie Pomologist", "miniblockmerchants:mm_pomologist", POMOLOGIST_ZOMBIE_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getRecyclerHeadLootTable() {
        return getHeadLootTable("Recycler", "miniblockmerchants:mm_recycler", RECYCLER_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getZombieRecyclerHeadLootTable() {
        return getZombieHeadLootTable("Zombie Recycler", "miniblockmerchants:mm_recycler", RECYCLER_ZOMBIE_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getRitualistHeadLootTable() {
        return getHeadLootTable("Ritualist", "miniblockmerchants:mm_ritualist", RITUALIST_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getZombieRitualistHeadLootTable() {
        return getZombieHeadLootTable("Zombie Ritualist", "miniblockmerchants:mm_ritualist", RITUALIST_ZOMBIE_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getSculptorHeadLootTable() {
        return getHeadLootTable("Sculptor", "miniblockmerchants:mm_sculptor", SCULPTOR_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getZombieSculptorHeadLootTable() {
        return getZombieHeadLootTable("Zombie Sculptor", "miniblockmerchants:mm_sculptor", SCULPTOR_ZOMBIE_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getSteampunkerHeadLootTable() {
        return getHeadLootTable("Steampunker", "miniblockmerchants:mm_steampunker", STEAMPUNKER_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getZombieSteampunkerHeadLootTable() {
        return getZombieHeadLootTable("Zombie Steampunker", "miniblockmerchants:mm_steampunker", STEAMPUNKER_ZOMBIE_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getTailorHeadLootTable() {
        return getHeadLootTable("Tailor", "miniblockmerchants:mm_tailor", TAILOR_VILLAGER);
    }

    public static LootPoolEntryContainer.Builder<?> getZombieTailorHeadLootTable() {
        return getZombieHeadLootTable("Zombie Tailor", "miniblockmerchants:mm_tailor", TAILOR_ZOMBIE_VILLAGER);
    }
}
