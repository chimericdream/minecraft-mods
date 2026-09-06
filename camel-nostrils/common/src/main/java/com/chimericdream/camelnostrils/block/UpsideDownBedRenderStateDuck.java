package com.chimericdream.camelnostrils.block;

/**
 * Duck interface mixed onto vanilla's {@code BedRenderState} so {@code CN$BedRendererMixin} can stash
 * whether the bed entity being rendered belongs to one of camel-nostrils' upside-down bed blocks,
 * without a second {@code BlockEntityRenderer} - vanilla only ever registers one renderer per
 * {@code BlockEntityType}, and the upside-down beds intentionally reuse vanilla's
 * {@code BlockEntityType.BED} (see {@link ModBlockEntityValidBlocks}).
 * <p>
 * This lives outside the {@code mixin} package on purpose - Mixin reserves that whole package for
 * {@code @Mixin}-annotated classes and refuses to load a plain interface/class referenced from it.
 */
public interface UpsideDownBedRenderStateDuck {
    void camelnostrils$setUpsideDown(boolean upsideDown);

    boolean camelnostrils$isUpsideDown();
}
