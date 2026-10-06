package com.chimericdream.opus.item;

import com.chimericdream.opus.ModInfo;
import com.chimericdream.opus.client.OpusClient;
import com.chimericdream.opus.component.BookIdComponent;
import com.chimericdream.opus.component.OpusComponentTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import static com.chimericdream.opus.OpusMod.REGISTRY_HELPER;

/**
 * A generic book: right-click opens whichever book its {@link BookIdComponent} names (the bundled guide when
 * unset). The screen itself is client-only; the server never needs to know a book was opened.
 *
 * <p>UNVERIFIED: never compiled. Properties/use() follow hopper-xtreme's {@code HopperItemFilterItem}.
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
}
