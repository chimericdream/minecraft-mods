package com.chimericdream.allhallowssteve.block.entity;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.component.type.AllHallowsSteveComponentTypes;
import com.chimericdream.allhallowssteve.component.type.DyedColorComponent;
import com.chimericdream.allhallowssteve.component.type.PumpkinStencilsComponent;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.List;

import static com.chimericdream.allhallowssteve.block.ModBlocks.DECORATED_PUMPKIN_BLOCK_ENTITY;

public class DecoratedPumpkinBlockEntity extends BlockEntity {
    public static final Identifier ENTITY_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/entity/decorated_pumpkin");

    private static final Codec<List<Item>> CANDLES_CODEC = BuiltInRegistries.ITEM.byNameCodec().listOf();

    private int color =DyedColorComponent.DEFAULT_COLOR;
    private PumpkinStencilsComponent stencils = PumpkinStencilsComponent.EMPTY;
    private List<Item> candles = new ArrayList<>();

    public DecoratedPumpkinBlockEntity(BlockPos pos, BlockState state) {
        super(DECORATED_PUMPKIN_BLOCK_ENTITY.get(), pos, state);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        setChanged();
    }

    /** The candles held by a {@code CandleLitDecoratedPumpkinBlock}, in the order they were added; always empty for every other pumpkin block. */
    public List<Item> getCandles() {
        return List.copyOf(candles);
    }

    public void setCandles(List<Item> candles) {
        this.candles = new ArrayList<>(candles);
        setChanged();
    }

    public PumpkinStencilsComponent getStencils() {
        return stencils;
    }

    public void setStencils(PumpkinStencilsComponent stencils) {
        this.stencils = stencils;
        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("Color", color);
        output.store("Stencils", PumpkinStencilsComponent.CODEC, stencils);

        if (!candles.isEmpty()) {
            output.store("Candles", CANDLES_CODEC, candles);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        color = input.getIntOr("Color", DyedColorComponent.DEFAULT_COLOR);
        stencils = input.read("Stencils", PumpkinStencilsComponent.CODEC).orElse(PumpkinStencilsComponent.EMPTY);
        candles = new ArrayList<>(input.read("Candles", CANDLES_CODEC).orElse(List.of()));
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
        stencils = components.getOrDefault(AllHallowsSteveComponentTypes.STENCILS_COMPONENT.get(), PumpkinStencilsComponent.EMPTY);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(AllHallowsSteveComponentTypes.DYED_COLOR_COMPONENT.get(), new DyedColorComponent(color));
        components.set(AllHallowsSteveComponentTypes.STENCILS_COMPONENT.get(), stencils);
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        output.discard("Color");
        output.discard("Stencils");
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
