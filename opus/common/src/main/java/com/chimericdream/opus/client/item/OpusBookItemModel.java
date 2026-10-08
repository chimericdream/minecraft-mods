package com.chimericdream.opus.client.item;

import com.chimericdream.opus.ModInfo;
import com.chimericdream.opus.component.BookIdComponent;
import com.chimericdream.opus.component.OpusComponentTypes;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Item model type {@code opus:book}: picks a model per stack from its {@code opus:book_id}, using the
 * {@code texture}/{@code model} keys of that book's {@code book.yml}. Stacks of books without either key, or whose
 * files are missing, get the default {@code opus:item/book} look.
 */
public final class OpusBookItemModel implements ItemModel {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "book");

    private static final Identifier DEFAULT_MODEL = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "item/book");
    private static final Identifier GENERATED_MODEL = Identifier.withDefaultNamespace("item/generated");

    private final ItemModel fallback;
    private final Map<String, ItemModel> perBook;

    private OpusBookItemModel(ItemModel fallback, Map<String, ItemModel> perBook) {
        this.fallback = fallback;
        this.perBook = perBook;
    }

    @Override
    public void update(ItemStackRenderState output, ItemStack item, ItemModelResolver resolver, ItemDisplayContext displayContext,
                       @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        BookIdComponent book = item.get(OpusComponentTypes.BOOK_ID.get());
        ItemModel model = book == null ? fallback : perBook.getOrDefault(book.bookId(), fallback);

        output.appendModelIdentityElement(this);
        model.update(output, item, resolver, displayContext, level, owner, seed);
    }

    public static final class Unbaked implements ItemModel.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        private volatile Map<Identifier, BookLook> looks;

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        private Map<Identifier, BookLook> looks() {
            Map<Identifier, BookLook> current = looks;
            if (current == null) {
                // The manager being reloaded is the one Minecraft holds while the reload runs.
                current = BookLookScanner.scan(Minecraft.getInstance().getResourceManager());
                looks = current;
            }

            return current;
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver resolver) {
            resolver.markDependency(DEFAULT_MODEL);
            resolver.markDependency(GENERATED_MODEL);

            for (BookLook look : looks().values()) {
                if (look.model() != null) {
                    resolver.markDependency(look.model());
                }
            }
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext context, Matrix4fc transformation) {
            ItemModel fallback = bakeModel(DEFAULT_MODEL, context, transformation);

            Map<String, ItemModel> perBook = new Object2ObjectOpenHashMap<>();
            looks().forEach((bookId, look) -> perBook.put(bookId.toString(), look.model() != null
                ? bakeModel(look.model(), context, transformation)
                : bakeTexture(look.texture(), context, transformation)));

            return new OpusBookItemModel(fallback, perBook);
        }

        private static ItemModel bakeModel(Identifier model, ItemModel.BakingContext context, Matrix4fc transformation) {
            return new CuboidItemModelWrapper.Unbaked(model, Optional.empty(), List.of()).bake(context, transformation);
        }

        /** A flat item: the vanilla {@code item/generated} geometry and display transforms, with this sprite in layer0. */
        private static ItemModel bakeTexture(Identifier texture, ItemModel.BakingContext context, Matrix4fc transformation) {
            ModelBaker baker = context.blockModelBaker();
            ResolvedModel generated = baker.getModel(GENERATED_MODEL);
            TextureSlots slots = new TextureSlots.Resolver()
                .addLast(new TextureSlots.Data.Builder().addTexture("layer0", new Material(texture)).addReference("particle", "layer0").build())
                .resolve(texture::toString);

            // ResolvedModel.bakeTopGeometry (IDENTITY) and resolveParticleMaterial memoize on the shared item/generated
            // model and ignore the slots passed later, so every book after the first would reuse the first book's sprite.
            QuadCollection quads = generated.getTopGeometry().bake(slots, baker, BlockModelRotation.IDENTITY, generated);
            ModelRenderProperties properties = new ModelRenderProperties(
                generated.getTopGuiLight().lightLikeBlock(),
                ResolvedModel.resolveParticleMaterial(slots, baker, generated),
                generated.getTopTransforms());

            return new FlatItemModel(quads, properties, transformation);
        }
    }
}
