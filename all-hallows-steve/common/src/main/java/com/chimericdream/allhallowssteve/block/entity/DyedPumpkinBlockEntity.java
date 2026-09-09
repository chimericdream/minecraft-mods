package com.chimericdream.allhallowssteve.block.entity;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.component.type.AllHallowsSteveComponentTypes;
import com.chimericdream.allhallowssteve.component.type.DyedColorComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import static com.chimericdream.allhallowssteve.block.ModBlocks.DYED_PUMPKIN_BLOCK_ENTITY;

public class DyedPumpkinBlockEntity extends BlockEntity {
    public static final Identifier ENTITY_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/entity/dyed_pumpkin");

    private int color = DyedColorComponent.DEFAULT_COLOR;

    public DyedPumpkinBlockEntity(BlockPos pos, BlockState state) {
        super(DYED_PUMPKIN_BLOCK_ENTITY.get(), pos, state);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("Color", color);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        color = input.getIntOr("Color", DyedColorComponent.DEFAULT_COLOR);
    }

    /**
     * Placing the item runs {@code BlockItem#updateBlockEntityComponents}, which calls this with the
     * placed stack's components — this is how the dyed color the station stored on the item makes it
     * onto the placed block, the same mechanism vanilla uses for banner patterns.
     */
    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        color = components.getOrDefault(AllHallowsSteveComponentTypes.DYED_COLOR_COMPONENT.get(), DyedColorComponent.DEFAULT).color();
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(AllHallowsSteveComponentTypes.DYED_COLOR_COMPONENT.get(), new DyedColorComponent(color));
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        output.discard("Color");
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}
