package com.chimericdream.allhallowssteve.fabric.test;

import com.chimericdream.allhallowssteve.block.ModBlocks;
import com.chimericdream.allhallowssteve.component.type.AllHallowsSteveComponentTypes;
import com.chimericdream.allhallowssteve.component.type.PumpkinStencilsComponent;
import com.chimericdream.allhallowssteve.wearable.PumpkinFaceData;
import com.chimericdream.allhallowssteve.wearable.PumpkinFaces;
import com.mojang.serialization.DataResult;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.GameType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Coverage for wearable decorated pumpkins: the head-slot item data, the enderman/creaking disguise tag,
 * the face-cycling rules and the saved per-player choice. How the pumpkin and the vision overlay actually
 * look is covered by a visual check and the manual steps in {@code TEST_PLAN.md}.
 */
@SuppressWarnings("unused")
public class WearablePumpkinGameTest {
    private static Item pumpkin() {
        return ModBlocks.DECORATED_PUMPKIN.get().asItem();
    }

    @GameTest
    public void faceCycleVisitsNorthEastSouthWestInOrder(GameTestHelper context) {
        Direction face = Direction.NORTH;
        List<Direction> visited = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            face = PumpkinFaces.next(face);
            visited.add(face);
        }

        context.assertValueEqual(visited, List.of(Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST), "faces after each press");
        context.succeed();
    }

    @GameTest
    public void bringingAFaceToTheFrontTurnsThePumpkinTheOtherWay(GameTestHelper context) {
        context.assertValueEqual(PumpkinFaces.renderFacing(Direction.NORTH), Direction.NORTH, "render facing for north");
        context.assertValueEqual(PumpkinFaces.renderFacing(Direction.EAST), Direction.WEST, "render facing for east");
        context.assertValueEqual(PumpkinFaces.renderFacing(Direction.SOUTH), Direction.SOUTH, "render facing for south");
        context.assertValueEqual(PumpkinFaces.renderFacing(Direction.WEST), Direction.EAST, "render facing for west");
        context.succeed();
    }

    @GameTest
    public void unlitPumpkinIsAnUnswappableHeadItemWithACameraOverlay(GameTestHelper context) {
        Equippable equippable = new ItemStack(pumpkin()).get(DataComponents.EQUIPPABLE);

        context.assertTrue(equippable != null, Component.literal("the decorated pumpkin should be equippable"));
        context.assertValueEqual(equippable.slot(), EquipmentSlot.HEAD, "equipment slot");
        context.assertValueEqual(equippable.swappable(), false, "swappable by right-click");
        context.assertValueEqual(equippable.cameraOverlay().orElse(null), PumpkinFaces.CAMERA_OVERLAY, "camera overlay");
        context.succeed();
    }

    @GameTest
    public void litPumpkinsCannotBeWorn(GameTestHelper context) {
        for (var lit : ModBlocks.LIT_DECORATED_PUMPKINS) {
            ItemStack stack = new ItemStack(lit.get().asItem());

            context.assertTrue(stack.get(DataComponents.EQUIPPABLE) == null, Component.literal(stack + " should not be equippable"));
            context.assertTrue(!PumpkinFaces.isWearable(stack), Component.literal(stack + " should not count as wearable"));
        }

        context.succeed();
    }

    @GameTest
    public void onlyTheUnlitPumpkinDisguisesItsWearer(GameTestHelper context) {
        context.assertTrue(new ItemStack(pumpkin()).is(ItemTags.GAZE_DISGUISE_EQUIPMENT), Component.literal("the decorated pumpkin should be in the gaze disguise tag"));

        for (var lit : ModBlocks.LIT_DECORATED_PUMPKINS) {
            context.assertTrue(!new ItemStack(lit.get().asItem()).is(ItemTags.GAZE_DISGUISE_EQUIPMENT), Component.literal("a lit pumpkin should not be in the gaze disguise tag"));
        }

        context.succeed();
    }

    @GameTest
    public void wearingThePumpkinCountsAsWearingADisguise(GameTestHelper context) {
        Player player = context.makeMockServerPlayer(GameType.SURVIVAL);
        context.assertValueEqual(LivingEntity.PLAYER_NOT_WEARING_DISGUISE_ITEM.test(player), true, "bare-headed player counts as not disguised");

        player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(pumpkin()));

        context.assertValueEqual(LivingEntity.PLAYER_NOT_WEARING_DISGUISE_ITEM.test(player), false, "pumpkin-headed player counts as not wearing a disguise");
        context.succeed();
    }

    @GameTest
    public void choosingAFaceCyclesBackToNothingStored(GameTestHelper context) {
        PumpkinFaceData data = new PumpkinFaceData();
        UUID player = UUID.randomUUID();

        context.assertValueEqual(data.get(player), Direction.NORTH, "face before any press");
        context.assertValueEqual(data.cycle(player), Direction.EAST, "face after one press");
        context.assertValueEqual(data.get(player), Direction.EAST, "stored face");
        data.cycle(player);
        data.cycle(player);
        context.assertValueEqual(data.cycle(player), Direction.NORTH, "face after four presses");
        context.assertValueEqual(data.all().isEmpty(), true, "stored choices once back on north");
        context.succeed();
    }

    @GameTest
    public void playersChooseTheirFacesIndependently(GameTestHelper context) {
        PumpkinFaceData data = new PumpkinFaceData();
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();

        data.cycle(first);
        data.cycle(second);
        data.cycle(second);

        context.assertValueEqual(data.get(first), Direction.EAST, "first player's face");
        context.assertValueEqual(data.get(second), Direction.SOUTH, "second player's face");
        context.succeed();
    }

    @GameTest
    public void chosenFacesSurviveSavingAndLoading(GameTestHelper context) {
        PumpkinFaceData data = new PumpkinFaceData();
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        data.cycle(first);
        data.cycle(second);
        data.cycle(second);

        DataResult<Tag> encoded = PumpkinFaceData.CODEC.encodeStart(NbtOps.INSTANCE, data);
        PumpkinFaceData loaded = PumpkinFaceData.CODEC.parse(NbtOps.INSTANCE, encoded.getOrThrow()).getOrThrow();

        context.assertValueEqual(loaded.all(), data.all(), "choices after a save/load round trip");
        context.succeed();
    }

    @GameTest
    public void turningTheFaceNeverChangesThePumpkinItem(GameTestHelper context) {
        ItemStack worn = new ItemStack(pumpkin());
        worn.set(AllHallowsSteveComponentTypes.STENCILS_COMPONENT.get(), PumpkinStencilsComponent.EMPTY.with(Direction.NORTH, "creeper"));
        ItemStack spare = worn.copy();

        PumpkinFaceData data = new PumpkinFaceData();
        UUID player = UUID.randomUUID();
        data.cycle(player);
        data.cycle(player);

        context.assertValueEqual(ItemStack.isSameItemSameComponents(worn, spare), true, "worn pumpkin stacks with an identical one after the face was turned");
        context.assertTrue(worn.get(AllHallowsSteveComponentTypes.WORN_FACE_COMPONENT.get()) == null, Component.literal("the real item must never carry the render-only face"));
        context.succeed();
    }
}
