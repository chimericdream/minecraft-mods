package com.chimericdream.minekea.client.render.block;

import com.chimericdream.minekea.block.furniture.armoires.ArmoireBlock;
import com.chimericdream.minekea.entity.block.furniture.ArmoireBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.armorstand.ArmorStandArmorModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.AtlasIds;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

public class ArmoireBlockEntityRenderer implements BlockEntityRenderer<ArmoireBlockEntity, ArmoireBlockEntityRenderState> {
    /*
     * Chestplates and leggings used to be displayed by equipping invisible, small, marker armor
     * stands placed inside the armoire. These offsets and yaws are the positions those stands
     * occupied, so armor hangs exactly where it did before.
     */
    private static final double STAND_Y_OFFSET = 0.78125;

    private final BlockEntityRendererProvider.Context context;
    private final ArmorModelSet<ArmorStandArmorModel> armorModels;
    private final EquipmentLayerRenderer equipmentRenderer;
    private final ArmorStandRenderState armorPose = new ArmorStandRenderState();

    public ArmoireBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
        this.armorModels = ArmorModelSet.bake(ModelLayers.ARMOR_STAND_SMALL_ARMOR, ctx.entityModelSet(), ArmorStandArmorModel::new);
        this.equipmentRenderer = new EquipmentLayerRenderer(
            ctx.entityRenderer().equipmentAssets,
            Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.ARMOR_TRIMS)
        );
    }

    @Override
    public @NotNull ArmoireBlockEntityRenderState createRenderState() {
        return new ArmoireBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(ArmoireBlockEntity entity, ArmoireBlockEntityRenderState state, float tickProgress, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(entity, state, tickProgress, cameraPos, crumblingOverlay);
        BlockState blockState = entity.getBlockState();

        List<ItemStack> items = entity.getItems();
        state.facing = blockState.getValue(ArmoireBlock.FACING);

        List<ArmoireBlockEntityRenderState.ArmorItemData> stateItems = new ArrayList<>();

        ItemStack stack;
        int lastIdx = 0;
        for (int i = 0; i < items.size(); i += 1) {
            // Chestplates and leggings are rendered as worn armor models; see below
            if (i % 4 < 2) {
                continue;
            }

            stack = items.get(i);

            boolean isHelmet = i % 4 == 2;
            int armorStand = Math.floorDiv(i, 4);
            double bootOffset = (isHelmet ? 0 : 1) * 0.1875;

            double xOffset = switch (state.facing) {
                case NORTH -> 0.171875 + (0.21875 * armorStand);
                case SOUTH -> 0.828125 - (0.21875 * armorStand);
                case EAST -> 0.59375 - bootOffset;
                case WEST -> 0.40625 + bootOffset;
                default -> 0.0;
            };

            double yOffset = isHelmet ? 0.5 : 0.078125;

            double zOffset = switch (state.facing) {
                case NORTH -> 0.40625 + bootOffset;
                case SOUTH -> 0.59375 - bootOffset;
                case EAST -> 0.171875 + (0.21875 * armorStand);
                case WEST -> 0.828125 - (0.21875 * armorStand);
                default -> 0.0;
            };

            stateItems.add(new ArmoireBlockEntityRenderState.ArmorItemData(xOffset, yOffset, zOffset, stack));
            this.context.itemModelResolver().appendItemLayers(state.displayItems.get(lastIdx++), stack, ItemDisplayContext.GROUND, null, null, 0);
        }

        state.items.clear();
        state.items.addAll(stateItems);

        state.standArmor.clear();
        for (int stand = 0; stand < 4; stand += 1) {
            ItemStack chestplate = items.get(stand * 4);
            ItemStack leggings = items.get(stand * 4 + 1);

            if (chestplate.isEmpty() && leggings.isEmpty()) {
                continue;
            }

            double xOffset = switch (state.facing) {
                case NORTH -> 0.1875 + (0.21875 * stand);
                case SOUTH -> 0.8125 - (0.21875 * stand);
                case EAST -> 0.59375;
                case WEST -> 0.40625;
                default -> 0.0;
            };

            double zOffset = switch (state.facing) {
                case NORTH -> 0.40625;
                case SOUTH -> 0.59375;
                case EAST -> 0.1875 + (0.21875 * stand);
                case WEST -> 0.8125 - (0.21875 * stand);
                default -> 0.0;
            };

            state.standArmor.add(new ArmoireBlockEntityRenderState.StandArmorData(xOffset, zOffset, chestplate, leggings));
        }
    }

    @Override
    public void submit(ArmoireBlockEntityRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        Direction facing = state.facing;

        Quaternionf rotation;
        switch (facing) {
            case NORTH -> rotation = Axis.YP.rotationDegrees(0);
            case EAST -> rotation = Axis.YP.rotationDegrees(90);
            case SOUTH -> rotation = Axis.YP.rotationDegrees(180);
            case WEST -> rotation = Axis.YP.rotationDegrees(270);
            default -> throw new IllegalStateException("Armoires cannot face ".concat(facing.toString()).concat("."));
        }

        ItemStackRenderState renderState;
        ArmoireBlockEntityRenderState.ArmorItemData item;
        for (int i = 0; i < state.items.size(); i += 1) {
            item = state.items.get(i);
            renderState = state.displayItems.get(i);

            double xOffset = item.xOffset();
            double yOffset = item.yOffset();
            double zOffset = item.zOffset();

            matrices.pushPose();

            matrices.translate(xOffset, yOffset, zOffset);
            matrices.mulPose(rotation);
            matrices.scale(0.5f, 0.5f, 0.5f);
            matrices.mulPose(Axis.YP.rotationDegrees(180));

            renderState.submit(matrices, queue, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

            matrices.popPose();
        }

        float standYaw = getStandYaw(facing);

        for (ArmoireBlockEntityRenderState.StandArmorData stand : state.standArmor) {
            matrices.pushPose();

            // The same transform LivingEntityRenderer applied to the invisible armor stands
            matrices.translate(stand.xOffset(), STAND_Y_OFFSET, stand.zOffset());
            matrices.mulPose(Axis.YP.rotationDegrees(180.0f - standYaw));
            matrices.scale(-1.0f, -1.0f, 1.0f);
            matrices.translate(0.0f, -1.501f, 0.0f);

            this.submitArmorPiece(stand.chestplate(), EquipmentSlot.CHEST, EquipmentClientInfo.LayerType.HUMANOID, matrices, queue, state.lightCoords);
            this.submitArmorPiece(stand.leggings(), EquipmentSlot.LEGS, EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS, matrices, queue, state.lightCoords);

            matrices.popPose();
        }
    }

    private void submitArmorPiece(ItemStack stack, EquipmentSlot slot, EquipmentClientInfo.LayerType layerType, PoseStack matrices, SubmitNodeCollector queue, int lightCoords) {
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);

        if (equippable == null || equippable.assetId().isEmpty() || equippable.slot() != slot) {
            return;
        }

        this.equipmentRenderer.renderLayers(
            layerType,
            equippable.assetId().get(),
            this.armorModels.get(slot),
            this.armorPose,
            stack,
            matrices,
            queue,
            lightCoords,
            0
        );
    }

    private static float getStandYaw(Direction facing) {
        return switch (facing) {
            case NORTH -> -90.0f;
            case SOUTH -> 90.0f;
            case WEST -> -180.0f;
            default -> 0.0f;
        };
    }
}
