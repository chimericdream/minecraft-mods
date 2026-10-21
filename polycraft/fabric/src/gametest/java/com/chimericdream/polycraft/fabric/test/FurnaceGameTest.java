package com.chimericdream.polycraft.fabric.test;

import com.chimericdream.polycraft.data.PolyCraftChoices;
import com.chimericdream.polycraft.network.FurnaceOffers;
import com.chimericdream.polycraft.network.PolyCraftNetworking;
import com.chimericdream.polycraft.network.SelectRecipePayload;
import com.chimericdream.polycraft.network.SetPinPayload;
import com.chimericdream.polycraft.recipe.ConflictService.Offer;
import com.chimericdream.polycraft.recipe.FurnaceResolver;
import com.chimericdream.polycraft.recipe.StationBlock;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * M4: furnaces. The fixture has two smelting recipes for gunpowder: {@code gunpowder_fast} (coal, 100 ticks, vanilla's
 * pick because its id sorts first) and {@code gunpowder_slow} (charcoal, 200 ticks). Furnaces are ticked by calling the
 * real server tick directly, so a whole cook takes no game time.
 */
@SuppressWarnings("unused")
public class FurnaceGameTest {
    private static final BlockPos POS = new BlockPos(1, 1, 1);
    private static final String FAST = "polycraft_test:gunpowder_fast";
    private static final String SLOW = "polycraft_test:gunpowder_slow";

    private static int nextContainerId = 200;

    private static AbstractFurnaceBlockEntity placeFurnace(GameTestHelper context) {
        context.setBlock(POS, Blocks.FURNACE);
        AbstractFurnaceBlockEntity furnace = context.getBlockEntity(POS, AbstractFurnaceBlockEntity.class);
        furnace.setItem(1, new ItemStack(Items.COAL, 8));
        return furnace;
    }

    private static void tick(GameTestHelper context, AbstractFurnaceBlockEntity furnace, int times) {
        BlockPos absolute = context.absolutePos(POS);
        ServerLevel level = context.getLevel();
        for (int i = 0; i < times; i++) {
            AbstractFurnaceBlockEntity.serverTick(level, absolute, level.getBlockState(absolute), furnace);
        }
    }

    private static ServerPlayer viewer(GameTestHelper context, AbstractFurnaceBlockEntity furnace) {
        ServerPlayer player = (ServerPlayer) context.makeMockServerPlayerInLevel();
        player.containerMenu = new FurnaceMenu(nextContainerId++, player.getInventory(), furnace, new SimpleContainerData(4));
        return player;
    }

    private static String idOf(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }

    private static boolean is(ItemStack stack, String id, int count) {
        return idOf(stack).equals(id) && stack.getCount() == count;
    }

    @SuppressWarnings("unchecked")
    private static String groupKey(GameTestHelper context, AbstractFurnaceBlockEntity furnace) {
        ServerLevel level = context.getLevel();
        SingleRecipeInput input = new SingleRecipeInput(new ItemStack(Items.GUNPOWDER));
        RecipeHolder<?> vanilla = level.recipeAccess().getRecipeFor(RecipeType.SMELTING, input, level).orElseThrow();
        return FurnaceResolver.compute(level, furnace, input, vanilla).orElseThrow().offer().groupKey();
    }

    private static StationBlock station(AbstractFurnaceBlockEntity furnace) {
        return (StationBlock) furnace;
    }

    @GameTest
    public void aFurnaceNobodyIsWatchingUsesVanillasPick(GameTestHelper context) {
        AbstractFurnaceBlockEntity furnace = placeFurnace(context);
        furnace.setItem(0, new ItemStack(Items.GUNPOWDER));

        tick(context, furnace, 101);

        context.assertTrue(is(furnace.getItem(2), "minecraft:coal", 1), "the 100-tick recipe is vanilla's pick, so coal should come out");
        context.succeed();
    }

    @GameTest
    public void aPinnedRecipeIsUsedFromTheStart(GameTestHelper context) {
        AbstractFurnaceBlockEntity furnace = placeFurnace(context);
        station(furnace).polycraft$state().pins().pin(groupKey(context, furnace), SLOW);
        furnace.setItem(0, new ItemStack(Items.GUNPOWDER));

        context.assertTrue(station(furnace).polycraft$cookingTotalTime() == 200, "the pinned recipe takes 200 ticks");
        tick(context, furnace, 199);
        context.assertTrue(furnace.getItem(2).isEmpty(), "nothing should be done after 199 ticks");
        tick(context, furnace, 2);
        context.assertTrue(is(furnace.getItem(2), "minecraft:charcoal", 1), "the pinned recipe makes charcoal");
        context.succeed();
    }

    @GameTest
    public void aViewersChoiceAppliesAndAPinBeatsIt(GameTestHelper context) {
        AbstractFurnaceBlockEntity furnace = placeFurnace(context);
        ServerPlayer player = viewer(context, furnace);
        PolyCraftChoices.get(context.getLevel().getServer()).reset(player.getUUID());
        furnace.setItem(0, new ItemStack(Items.GUNPOWDER, 2));
        FurnaceOffers.refresh(player);
        Offer offer = PolyCraftNetworking.offered(player.containerMenu).orElseThrow();
        context.assertTrue(offer.options().size() == 2 && offer.pinnable(), "the viewer should be offered both recipes and the pin");

        PolyCraftNetworking.applySelection(player, new SelectRecipePayload(player.containerMenu.containerId, offer.groupKey(), SLOW));
        tick(context, furnace, 201);
        context.assertTrue(is(furnace.getItem(2), "minecraft:charcoal", 1), "the viewer's choice should be cooked");

        // Now pin the other recipe on the block: it wins over what this viewer prefers. Empty the output slot first,
        // because a furnace won't mix charcoal and coal in it.
        furnace.setItem(2, ItemStack.EMPTY);
        station(furnace).polycraft$state().pins().pin(offer.groupKey(), FAST);
        station(furnace).polycraft$state().invalidate();
        tick(context, furnace, 101);
        context.assertTrue(is(furnace.getItem(2), "minecraft:coal", 1), "the second item should have been cooked with the pinned recipe, not the viewer's");
        context.assertTrue(furnace.getItem(0).isEmpty(), "the second item should be used up");
        context.succeed();
    }

    @GameTest
    public void switchingMidCookKeepsTheTimeAndWastesNothing(GameTestHelper context) {
        AbstractFurnaceBlockEntity furnace = placeFurnace(context);
        ServerPlayer player = viewer(context, furnace);
        PolyCraftChoices.get(context.getLevel().getServer()).reset(player.getUUID());
        furnace.setItem(0, new ItemStack(Items.GUNPOWDER));
        FurnaceOffers.refresh(player);
        String key = PolyCraftNetworking.offered(player.containerMenu).orElseThrow().groupKey();

        tick(context, furnace, 40);
        int fuelBefore = furnace.getItem(1).getCount();
        context.assertTrue(station(furnace).polycraft$cookingTotalTime() == 100 && station(furnace).polycraft$cookingTimer() == 40, "vanilla's recipe should be 40 ticks in");

        PolyCraftNetworking.applySelection(player, new SelectRecipePayload(player.containerMenu.containerId, key, SLOW));
        tick(context, furnace, 1);

        context.assertTrue(station(furnace).polycraft$cookingTotalTime() == 200, "the total should become the new recipe's 200");
        int timer = station(furnace).polycraft$cookingTimer();
        context.assertTrue(timer >= 40 && timer <= 42, "the time already spent should be kept, got " + timer);
        context.assertTrue(furnace.getItem(1).getCount() == fuelBefore, "no extra fuel should be used");
        context.assertTrue(furnace.getItem(0).getCount() == 1, "the input should not be consumed by the switch");

        tick(context, furnace, 165);
        context.assertTrue(is(furnace.getItem(2), "minecraft:charcoal", 1), "the cook should finish with the new recipe's result");
        context.assertTrue(furnace.getItem(0).isEmpty(), "the input is used up when the cook finishes");
        context.succeed();
    }

    @GameTest
    public void switchingPastTheNewTotalFinishesAlmostAtOnce(GameTestHelper context) {
        AbstractFurnaceBlockEntity furnace = placeFurnace(context);
        String key = groupKey(context, furnace);
        station(furnace).polycraft$state().pins().pin(key, SLOW);
        furnace.setItem(0, new ItemStack(Items.GUNPOWDER));
        tick(context, furnace, 150);
        context.assertTrue(furnace.getItem(2).isEmpty(), "the slow recipe isn't done after 150 ticks");

        station(furnace).polycraft$state().pins().unpin(key);
        station(furnace).polycraft$state().invalidate();
        tick(context, furnace, 2);

        context.assertTrue(is(furnace.getItem(2), "minecraft:coal", 1), "150 ticks is already more than the fast recipe needs, so it should finish at once");
        context.succeed();
    }

    @GameTest
    public void closingTheScreenDoesNotChangeTheRecipe(GameTestHelper context) {
        AbstractFurnaceBlockEntity furnace = placeFurnace(context);
        ServerPlayer player = viewer(context, furnace);
        PolyCraftChoices.get(context.getLevel().getServer()).reset(player.getUUID());
        furnace.setItem(0, new ItemStack(Items.GUNPOWDER));
        FurnaceOffers.refresh(player);
        String key = PolyCraftNetworking.offered(player.containerMenu).orElseThrow().groupKey();
        PolyCraftNetworking.applySelection(player, new SelectRecipePayload(player.containerMenu.containerId, key, SLOW));
        tick(context, furnace, 10);

        player.containerMenu = player.inventoryMenu;
        tick(context, furnace, 200);

        context.assertTrue(is(furnace.getItem(2), "minecraft:charcoal", 1), "the cook should still finish with the recipe the viewer chose");
        context.succeed();
    }

    @GameTest
    public void optionsTheOutputSlotCantTakeAreDisabled(GameTestHelper context) {
        AbstractFurnaceBlockEntity furnace = placeFurnace(context);
        furnace.setItem(2, new ItemStack(Items.COAL));
        ServerPlayer player = viewer(context, furnace);
        PolyCraftChoices choices = PolyCraftChoices.get(context.getLevel().getServer());
        choices.reset(player.getUUID());
        furnace.setItem(0, new ItemStack(Items.GUNPOWDER));
        FurnaceOffers.refresh(player);
        Offer offer = PolyCraftNetworking.offered(player.containerMenu).orElseThrow();

        context.assertTrue(offer.byId(FAST).orElseThrow().enabled(), "coal can join the coal already in the output");
        context.assertTrue(!offer.byId(SLOW).orElseThrow().enabled(), "charcoal can't join coal");

        PolyCraftNetworking.applySelection(player, new SelectRecipePayload(player.containerMenu.containerId, offer.groupKey(), SLOW));
        context.assertTrue(choices.choiceFor(player.getUUID(), offer.groupKey()).isEmpty(), "a disabled option can't be chosen");
        context.succeed();
    }

    @GameTest
    public void pinningFollowsWhatTheViewerSees(GameTestHelper context) {
        AbstractFurnaceBlockEntity furnace = placeFurnace(context);
        ServerPlayer player = viewer(context, furnace);
        furnace.setItem(0, new ItemStack(Items.GUNPOWDER));
        FurnaceOffers.refresh(player);
        Offer offer = PolyCraftNetworking.offered(player.containerMenu).orElseThrow();
        int id = player.containerMenu.containerId;
        var pins = station(furnace).polycraft$state().pins();

        PolyCraftNetworking.applyPin(player, new SetPinPayload(id, "not|a|group", true));
        context.assertTrue(pins.isEmpty(), "a pin for a group that wasn't offered should be ignored");

        PolyCraftNetworking.applyPin(player, new SetPinPayload(id + 1, offer.groupKey(), true));
        context.assertTrue(pins.isEmpty(), "a pin for another menu should be ignored");

        PolyCraftNetworking.applyPin(player, new SetPinPayload(id, offer.groupKey(), true));
        context.assertTrue(pins.get(offer.groupKey()).equals(java.util.Optional.of(offer.selected().id())), "the pin should hold the recipe in use");

        FurnaceOffers.refresh(player);
        context.assertTrue(PolyCraftNetworking.offered(player.containerMenu).orElseThrow().pinned(), "the viewer should now see the pin");

        PolyCraftNetworking.applyPin(player, new SetPinPayload(id, offer.groupKey(), false));
        context.assertTrue(pins.isEmpty(), "unpinning should remove it");
        context.succeed();
    }

    @GameTest
    public void pinsAndTheRecipeInUseSurviveSavingAndLoading(GameTestHelper context) {
        AbstractFurnaceBlockEntity furnace = placeFurnace(context);
        station(furnace).polycraft$state().pins().pin("some|group", "a:recipe");
        station(furnace).polycraft$state().setActive("input-key", "a:active");

        CompoundTag saved = furnace.saveWithFullMetadata(context.getLevel().registryAccess());
        BlockEntity loaded = BlockEntity.loadStatic(context.absolutePos(POS), context.getLevel().getBlockState(context.absolutePos(POS)), saved, context.getLevel().registryAccess());

        context.assertTrue(loaded instanceof StationBlock, "the loaded block entity should be a furnace");
        var state = ((StationBlock) loaded).polycraft$state();
        context.assertTrue(state.pins().get("some|group").equals(java.util.Optional.of("a:recipe")), "the pin should survive");
        context.assertTrue("a:active".equals(state.activeRecipe()) && "input-key".equals(state.activeInput()), "the recipe in use should survive");
        context.succeed();
    }

    @GameTest
    public void aBlastFurnaceAndASmokerGetTheSameTreatment(GameTestHelper context) {
        context.setBlock(POS, Blocks.BLAST_FURNACE);
        AbstractFurnaceBlockEntity blast = context.getBlockEntity(POS, AbstractFurnaceBlockEntity.class);
        context.assertTrue(station(blast).polycraft$recipeType() == RecipeType.BLASTING, "a blast furnace should use blasting recipes");

        context.setBlock(POS, Blocks.SMOKER);
        AbstractFurnaceBlockEntity smoker = context.getBlockEntity(POS, AbstractFurnaceBlockEntity.class);
        context.assertTrue(station(smoker).polycraft$recipeType() == RecipeType.SMOKING, "a smoker should use smoking recipes");
        context.succeed();
    }
}
