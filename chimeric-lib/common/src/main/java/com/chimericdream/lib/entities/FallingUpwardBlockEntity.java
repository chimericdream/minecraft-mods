package com.chimericdream.lib.entities;

import com.chimericdream.lib.blocks.Risable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.util.function.Predicate;
import net.minecraft.CrashReportCategory;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.FallingBlockRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter.ScopedCollector;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/**
 * The inverse of vanilla's {@code FallingBlockEntity}: rises instead of falls, and despawns once
 * it passes the top of the world instead of the bottom. See {@link com.chimericdream.lib.blocks.FallingUpwardBlock}
 * for how this gets spawned.
 */
public class FallingUpwardBlockEntity extends Entity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final BlockState DEFAULT_BLOCK_STATE = Blocks.SAND.defaultBlockState();
    private BlockState blockState = DEFAULT_BLOCK_STATE;
    public int time = 0;
    public boolean dropItem = true;
    private boolean cancelDrop = false;
    private boolean hurtEntities;
    private int fallDamageMax = 40;
    private float fallDamagePerDistance = 0.0F;
    @Nullable
    public CompoundTag blockData;
    public boolean forceTickAfterTeleportToDuplicate;
    protected static final EntityDataAccessor<BlockPos> DATA_START_POS = SynchedEntityData.defineId(FallingUpwardBlockEntity.class, EntityDataSerializers.BLOCK_POS);

    public FallingUpwardBlockEntity(EntityType<? extends FallingUpwardBlockEntity> type, Level level) {
        super(type, level);
    }

    private FallingUpwardBlockEntity(EntityType<? extends FallingUpwardBlockEntity> type, Level level, double x, double y, double z, BlockState blockState) {
        this(type, level);
        this.blockState = blockState;
        this.blocksBuilding = true;
        this.setPos(x, y, z);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.setStartPos(this.blockPosition());
    }

    public static FallingUpwardBlockEntity rise(EntityType<? extends FallingUpwardBlockEntity> entityType, Level level, BlockPos pos, BlockState state) {
        FallingUpwardBlockEntity entity = new FallingUpwardBlockEntity(
            entityType,
            level,
            (double) pos.getX() + 0.5,
            (double) pos.getY(),
            (double) pos.getZ() + 0.5,
            state.hasProperty(BlockStateProperties.WATERLOGGED) ? (BlockState) state.setValue(BlockStateProperties.WATERLOGGED, false) : state
        );
        level.setBlock(pos, state.getFluidState().createLegacyBlock(), 3);
        level.addFreshEntity(entity);
        return entity;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public final boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        if (!this.isInvulnerableToBase(source)) {
            this.markHurt();
        }

        return false;
    }

    public void setStartPos(BlockPos pos) {
        this.entityData.set(DATA_START_POS, pos);
    }

    public BlockPos getStartPos() {
        return this.entityData.get(DATA_START_POS);
    }

    @Override
    protected MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        entityData.define(DATA_START_POS, BlockPos.ZERO);
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    /** Negative gravity: {@code Entity#applyGravity} adds {@code -getGravity()} to the Y velocity every tick, so a negative value here accelerates upward instead of down. */
    @Override
    protected double getDefaultGravity() {
        return -0.04;
    }

    @Override
    public void tick() {
        if (this.blockState.isAir()) {
            this.discard();
        } else {
            Block block = this.blockState.getBlock();
            this.time++;
            this.applyGravity();
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.applyEffectsFromBlocks();
            this.handlePortal();
            if (this.level() instanceof ServerLevel serverLevel && (this.isAlive() || this.forceTickAfterTeleportToDuplicate)) {
                BlockPos pos = this.blockPosition();
                boolean hitCeiling = this.verticalCollision && !this.verticalCollisionBelow;

                if (!hitCeiling) {
                    if (pos.getY() > this.level().getMaxY()) {
                        // Mirrors a falling block hitting the void: gone without a trace, no grace period.
                        this.discard();
                    } else if (this.time > 600) {
                        if (this.dropItem && serverLevel.getGameRules().get(GameRules.ENTITY_DROPS)) {
                            this.spawnAtLocation(serverLevel, block);
                        }

                        this.discard();
                    }
                } else {
                    BlockState currentState = this.level().getBlockState(pos);
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
                    if (!currentState.is(Blocks.MOVING_PISTON)) {
                        if (!this.cancelDrop) {
                            boolean mayReplace = currentState.canBeReplaced(
                                new DirectionalPlaceContext(this.level(), pos, Direction.UP, ItemStack.EMPTY, Direction.DOWN)
                            );
                            boolean wouldContinueRising = FallingBlock.isFree(this.level().getBlockState(pos.above()));
                            boolean wouldSurvive = this.blockState.canSurvive(this.level(), pos) && !wouldContinueRising;

                            if (mayReplace && wouldSurvive) {
                                this.hurtEntitiesAtImpact();

                                if (this.blockState.hasProperty(BlockStateProperties.WATERLOGGED) && this.level().getFluidState(pos).is(Fluids.WATER)) {
                                    this.blockState = (BlockState) this.blockState.setValue(BlockStateProperties.WATERLOGGED, true);
                                }

                                if (this.level().setBlock(pos, this.blockState, 3)) {
                                    serverLevel.getChunkSource()
                                        .chunkMap
                                        .sendToTrackingPlayers(this, new ClientboundBlockUpdatePacket(pos, this.level().getBlockState(pos)));
                                    this.discard();
                                    if (block instanceof Risable risable) {
                                        risable.onLand(this.level(), pos, this.blockState, currentState, this);
                                    }

                                    if (this.blockData != null && this.blockState.hasBlockEntity()) {
                                        BlockEntity blockEntity = this.level().getBlockEntity(pos);
                                        if (blockEntity != null) {
                                            try {
                                                ScopedCollector reporter = new ScopedCollector(blockEntity.problemPath(), LOGGER);

                                                try {
                                                    RegistryAccess registryAccess = this.level().registryAccess();
                                                    TagValueOutput output = TagValueOutput.createWithContext(reporter, registryAccess);
                                                    blockEntity.saveWithoutMetadata(output);
                                                    CompoundTag merged = output.buildResult();
                                                    this.blockData.forEach((name, tag) -> merged.put(name, tag.copy()));
                                                    blockEntity.loadWithComponents(TagValueInput.create(reporter, registryAccess, merged));
                                                } catch (Throwable var18) {
                                                    try {
                                                        reporter.close();
                                                    } catch (Throwable var17) {
                                                        var18.addSuppressed(var17);
                                                    }

                                                    throw var18;
                                                }

                                                reporter.close();
                                            } catch (Exception var19) {
                                                LOGGER.error("Failed to load block entity from rising block", var19);
                                            }

                                            blockEntity.setChanged();
                                        }
                                    }
                                } else if (this.dropItem && serverLevel.getGameRules().get(GameRules.ENTITY_DROPS)) {
                                    this.discard();
                                    this.callOnBrokenAfterRise(block, pos);
                                    this.spawnAtLocation(serverLevel, block);
                                }
                            } else {
                                this.discard();
                                if (this.dropItem && serverLevel.getGameRules().get(GameRules.ENTITY_DROPS)) {
                                    this.callOnBrokenAfterRise(block, pos);
                                    this.spawnAtLocation(serverLevel, block);
                                }
                            }
                        } else {
                            this.discard();
                            this.callOnBrokenAfterRise(block, pos);
                        }
                    }
                }
            }

            this.setDeltaMovement(this.getDeltaMovement().scale(this.getAirDrag()));
        }
    }

    public void callOnBrokenAfterRise(Block block, BlockPos pos) {
        if (block instanceof Risable risable) {
            risable.onBrokenAfterRise(this.level(), pos, this);
        }
    }

    private void hurtEntitiesAtImpact() {
        if (!this.hurtEntities) {
            return;
        }

        double riseDistance = this.getY() - (double) this.getStartPos().getY();
        int riseDistanceInt = Mth.ceil(riseDistance - 1.0);
        if (riseDistanceInt < 0) {
            return;
        }

        Predicate<Entity> entitySelector = EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(EntitySelector.LIVING_ENTITY_STILL_ALIVE);
        DamageSource damageSource = this.blockState.getBlock() instanceof Risable risable
            ? risable.getRiseDamageSource(this)
            : this.damageSources().fallingBlock(this);
        float damage = (float) Math.min(Mth.floor((float) riseDistanceInt * this.fallDamagePerDistance), this.fallDamageMax);
        if (damage > 0.0F) {
            this.level().getEntities(this, this.getBoundingBox(), entitySelector).forEach(entity -> entity.hurt(damageSource, damage));
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.store("BlockState", BlockState.CODEC, this.blockState);
        output.putInt("Time", this.time);
        output.putBoolean("DropItem", this.dropItem);
        output.putBoolean("HurtEntities", this.hurtEntities);
        output.putFloat("FallHurtAmount", this.fallDamagePerDistance);
        output.putInt("FallHurtMax", this.fallDamageMax);
        if (this.blockData != null) {
            output.store("TileEntityData", CompoundTag.CODEC, this.blockData);
        }

        output.putBoolean("CancelDrop", this.cancelDrop);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        this.blockState = input.read("BlockState", BlockState.CODEC).orElse(DEFAULT_BLOCK_STATE);
        this.time = input.getIntOr("Time", 0);
        this.hurtEntities = input.getBooleanOr("HurtEntities", false);
        this.fallDamagePerDistance = input.getFloatOr("FallHurtAmount", 0.0F);
        this.fallDamageMax = input.getIntOr("FallHurtMax", 40);
        this.dropItem = input.getBooleanOr("DropItem", true);
        this.blockData = input.read("TileEntityData", CompoundTag.CODEC).orElse(null);
        this.cancelDrop = input.getBooleanOr("CancelDrop", false);
    }

    public void setHurtsEntities(float damagePerDistance, int damageMax) {
        this.hurtEntities = true;
        this.fallDamagePerDistance = damagePerDistance;
        this.fallDamageMax = damageMax;
    }

    public void disableDrop() {
        this.cancelDrop = true;
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public void fillCrashReportCategory(CrashReportCategory category) {
        super.fillCrashReportCategory(category);
        category.setDetail("Immitating BlockState", this.blockState.toString());
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    @Override
    protected Component getTypeName() {
        return Component.translatable("entity.chimericlib.falling_upward_block_type", this.blockState.getBlock().getName());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
        return new ClientboundAddEntityPacket(this, serverEntity, Block.getId(this.getBlockState()));
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.blockState = Block.stateById(packet.getData());
        this.blocksBuilding = true;
        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();
        this.setPos(x, y, z);
        this.setStartPos(this.blockPosition());
    }

    @Nullable
    @Override
    public Entity teleport(TeleportTransition transition) {
        ResourceKey<Level> newDimension = transition.newLevel().dimension();
        ResourceKey<Level> oldDimension = this.level().dimension();
        boolean fromOrToEnd = (oldDimension == Level.END || newDimension == Level.END) && oldDimension != newDimension;
        Entity newEntity = super.teleport(transition);
        this.forceTickAfterTeleportToDuplicate = newEntity != null && fromOrToEnd;
        return newEntity;
    }

    public static class Renderer extends EntityRenderer<FallingUpwardBlockEntity, FallingBlockRenderState> {
        public Renderer(EntityRendererProvider.Context context) {
            super(context);
            this.shadowRadius = 0.5F;
        }

        @Override
        public boolean shouldRender(FallingUpwardBlockEntity entity, Frustum culler, double camX, double camY, double camZ) {
            return super.shouldRender(entity, culler, camX, camY, camZ)
                && entity.getBlockState() != entity.level().getBlockState(entity.blockPosition());
        }

        @Override
        public void submit(FallingBlockRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
            BlockState blockState = state.movingBlockRenderState.blockState;
            if (blockState.getRenderShape() == RenderShape.MODEL) {
                poseStack.pushPose();
                poseStack.translate(-0.5, 0.0, -0.5);
                submitNodeCollector.submitMovingBlock(poseStack, state.movingBlockRenderState, state.outlineColor);
                poseStack.popPose();
                super.submit(state, poseStack, submitNodeCollector, camera);
            }
        }

        @Override
        public FallingBlockRenderState createRenderState() {
            return new FallingBlockRenderState();
        }

        @Override
        public void extractRenderState(FallingUpwardBlockEntity entity, FallingBlockRenderState state, float partialTicks) {
            super.extractRenderState(entity, state, partialTicks);
            BlockPos pos = BlockPos.containing(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
            state.movingBlockRenderState.randomSeedPos = entity.getStartPos();
            state.movingBlockRenderState.blockPos = pos;
            state.movingBlockRenderState.blockState = entity.getBlockState();
            if (entity.level() instanceof ClientLevel clientLevel) {
                state.movingBlockRenderState.biome = clientLevel.getBiome(pos);
                state.movingBlockRenderState.cardinalLighting = clientLevel.cardinalLighting();
                state.movingBlockRenderState.lightEngine = clientLevel.getLightEngine();
            }
        }
    }
}
