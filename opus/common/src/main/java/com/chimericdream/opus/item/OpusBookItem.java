package com.chimericdream.opus.item;

import com.chimericdream.opus.ModInfo;
import com.chimericdream.opus.client.OpusClient;
import com.chimericdream.opus.client.book.BookRepository;
import com.chimericdream.opus.component.BookIdComponent;
import com.chimericdream.opus.component.OpusComponentTypes;
import com.chimericdream.opus.core.book.Book;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

import static com.chimericdream.opus.OpusMod.REGISTRY_HELPER;

/**
 * A generic book: right-click opens whichever book its {@link BookIdComponent} names (the bundled guide when
 * unset). The screen itself is client-only; the server never needs to know a book was opened.
 *
 * <p>Compiles against 26.2; not yet run in game. Properties/use() follow hopper-xtreme's {@code HopperItemFilterItem}.
 */
public class OpusBookItem extends Item {
    public static final Identifier ITEM_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "book");
    public static final String DEFAULT_BOOK = ModInfo.MOD_ID + ":guide";

    @SuppressWarnings("UnstableApiUsage")
    public OpusBookItem() {
        super(
            new Properties()
                .stacksTo(1)
                .arch$tab(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .useItemDescriptionPrefix()
                .setId(REGISTRY_HELPER.makeItemRegistryKey(ITEM_ID))
        );
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack stack = new ItemStack(this);
        stack.set(OpusComponentTypes.BOOK_ID.get(), new BookIdComponent(DEFAULT_BOOK));
        return stack;
    }

    @Override
    public @NotNull InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (world.isClientSide()) {
            BookIdComponent book = stack.getOrDefault(OpusComponentTypes.BOOK_ID.get(), new BookIdComponent(DEFAULT_BOOK));
            OpusClient.openBook(book.bookId());
        }

        return InteractionResult.SUCCESS.heldItemTransformedTo(stack);
    }

    @Override
    public @NonNull Component getName(final @NonNull ItemStack itemStack) {
        Book book = lookup(itemStack);
        if (book == null || book.meta().title() == null || book.meta().title().isBlank()) {
            return super.getName(itemStack);
        }

        return Component.literal(book.meta().title());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(
        final @NonNull ItemStack itemStack,
        final @NonNull TooltipContext context,
        final @NonNull TooltipDisplay display,
        final @NonNull Consumer<Component> builder,
        final @NonNull TooltipFlag flag
    ) {
        Book book = lookup(itemStack);
        if (book != null && book.meta().description() != null && !book.meta().description().isBlank()) {
            builder.accept(Component.literal(book.meta().description()).withStyle(ChatFormatting.GRAY));
        }
    }

    /**
     * Book data lives in client resource packs, so this is null on a dedicated server (and before the first reload).
     */
    private static Book lookup(ItemStack stack) {
        try {
            BookIdComponent id = stack.getOrDefault(OpusComponentTypes.BOOK_ID.get(), new BookIdComponent(DEFAULT_BOOK));
            return BookRepository.get(id.bookId());
        } catch (Exception | LinkageError e) {
            return null;
        }
    }
}
