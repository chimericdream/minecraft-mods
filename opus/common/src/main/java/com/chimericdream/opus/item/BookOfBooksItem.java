package com.chimericdream.opus.item;

import com.chimericdream.opus.ModInfo;
import com.chimericdream.opus.client.OpusClient;
import com.chimericdream.opus.client.book.BookRepository;
import com.chimericdream.opus.component.OpusComponentTypes;
import com.chimericdream.opus.component.TomeContentsComponent;
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

import java.util.List;
import java.util.function.Consumer;

import static com.chimericdream.opus.OpusMod.REGISTRY_HELPER;

/**
 * Holds any number of books in one slot: Opus guides, plus any item in the {@code opus:book_of_books_storable}
 * tag. They are added in a crafting grid ({@code TomeCombineRecipe}). Right-click opens a menu to pick one, which
 * turns this item into that book; punching the air turns it back, and sneak-dropping the book takes it out of the set (see {@code TomeEvents}).
 *
 * <p>Inspired by the Akashic Tome mod's controls.
 */
public class BookOfBooksItem extends Item {
    public static final Identifier ITEM_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "book_of_books");

    /** How many book names the tooltip lists before it says "and N more". */
    private static final int TOOLTIP_LIMIT = 8;

    @SuppressWarnings("UnstableApiUsage")
    public BookOfBooksItem() {
        super(
            new Properties()
                .stacksTo(1)
                .arch$tab(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .useItemDescriptionPrefix()
                .setId(REGISTRY_HELPER.makeItemRegistryKey(ITEM_ID))
        );
    }

    @Override
    public @NotNull InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (world.isClientSide()) {
            TomeContentsComponent contents = stack.getOrDefault(OpusComponentTypes.TOME_CONTENTS.get(), TomeContentsComponent.EMPTY);

            if (contents.isEmpty()) {
                player.sendOverlayMessage(Component.translatable("opus.tome.empty"));
            } else {
                OpusClient.openTomePicker(hand, contents);
            }
        }

        return InteractionResult.SUCCESS.heldItemTransformedTo(stack);
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
        TomeContentsComponent contents = itemStack.getOrDefault(OpusComponentTypes.TOME_CONTENTS.get(), TomeContentsComponent.EMPTY);

        if (contents.isEmpty()) {
            builder.accept(Component.translatable("opus.tome.tooltip.empty").withStyle(ChatFormatting.GRAY));
            return;
        }

        List<ItemStack> books = contents.books();
        builder.accept(Component.translatable("opus.tome.tooltip.count", books.size()).withStyle(ChatFormatting.GRAY));

        for (int i = 0; i < Math.min(books.size(), TOOLTIP_LIMIT); i++) {
            builder.accept(Component.literal(" " + titleOf(books.get(i))).withStyle(ChatFormatting.DARK_GRAY));
        }
        if (books.size() > TOOLTIP_LIMIT) {
            builder.accept(Component.translatable("opus.tome.tooltip.more", books.size() - TOOLTIP_LIMIT).withStyle(ChatFormatting.DARK_GRAY));
        }

        builder.accept(Component.translatable("opus.tome.tooltip.use").withStyle(ChatFormatting.DARK_GRAY));
    }

    /**
     * The stored book's own title. For an Opus guide that is the guide's title, or its id when it is not installed:
     * book data lives in client resource packs, so on a dedicated server (and before the first reload) it is the id.
     */
    public static String titleOf(ItemStack stack) {
        if (!stack.is(OpusItems.BOOK.get())) {
            return stack.getHoverName().getString();
        }

        String bookId = TomeStacks.bookId(stack);
        try {
            Book book = BookRepository.get(bookId);
            if (book != null && book.meta().title() != null && !book.meta().title().isBlank()) {
                return book.meta().title();
            }
        } catch (Exception | LinkageError e) {
            // fall through to the id
        }

        return bookId;
    }
}
