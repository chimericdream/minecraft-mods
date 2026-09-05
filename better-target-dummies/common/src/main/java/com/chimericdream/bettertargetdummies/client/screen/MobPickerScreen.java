package com.chimericdream.bettertargetdummies.client.screen;

import com.chimericdream.bettertargetdummies.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Searchable list of every summonable mob, opened by right-clicking with a Dummy Spawn Egg in the
 * air. This is a plain {@link Screen} (like the language/controls screens), not an
 * {@code AbstractContainerScreen} -- that class is built around item slots and quick-crafting, none
 * of which apply here; the only thing we actually need from the menu system is {@link MenuAccess} so
 * {@code MenuScreens.register} can open us, and {@code containerId} so a pick can be reported back to
 * the server via the vanilla container-button-click protocol ({@link MobPickerMenu#clickMenuButton}).
 * Everything else (the entity type list itself) is identical, static registry data on both sides, so
 * no custom networking is needed either.
 */
public class MobPickerScreen extends Screen implements MenuAccess<MobPickerMenu> {
    private static final Component SEARCH_HINT = Component.translatable(ModInfo.MOD_ID + ".mob_picker.search").withStyle(EditBox.SEARCH_HINT_STYLE);
    private static final int TOP_MARGIN = 44;
    private static final int BOTTOM_MARGIN = 12;

    private final MobPickerMenu menu;
    private final List<EntityType<?>> summonableTypes;

    private EditBox search;
    private MobList mobList;

    public MobPickerScreen(MobPickerMenu menu, Inventory inventory, Component title) {
        super(title);
        this.menu = menu;
        this.summonableTypes = BuiltInRegistries.ENTITY_TYPE.stream()
            .filter(type -> type.canSummon() && type.getCategory() != MobCategory.MISC)
            .sorted(Comparator.comparing(type -> type.getDescription().getString()))
            .toList();
    }

    @Override
    public @NotNull MobPickerMenu getMenu() {
        return this.menu;
    }

    @Override
    protected void init() {
        int titleWidth = this.font.width(this.title);
        this.addRenderableWidget(new StringWidget((this.width - titleWidth) / 2, 8, titleWidth, this.font.lineHeight, this.title, this.font));

        this.search = this.addRenderableWidget(new EditBox(this.font, this.width / 2 - 100, 22, 200, 16, Component.empty()));
        this.search.setHint(SEARCH_HINT);
        this.search.setResponder(this::onSearchChanged);

        int listHeight = this.height - BOTTOM_MARGIN - TOP_MARGIN;
        this.mobList = this.addRenderableWidget(new MobList(this.minecraft, this.width, listHeight, TOP_MARGIN, 16));
        this.mobList.filter("");

        this.setInitialFocus(this.search);
    }

    // AbstractContainerScreen normally does this for us; a plain Screen doesn't know it's backed by a
    // server-side menu, so we have to tell the server it's closed ourselves.
    @Override
    public void onClose() {
        this.minecraft.player.closeContainer();
        super.onClose();
    }

    private void onSearchChanged(String filter) {
        this.mobList.filter(filter);
    }

    private void onMobChosen(EntityType<?> type) {
        int buttonId = BuiltInRegistries.ENTITY_TYPE.getId(type);
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
        this.onClose();
    }

    private class MobList extends ObjectSelectionList<MobList.Entry> {
        public MobList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
            super(minecraft, width, height, y, itemHeight);
        }

        public void filter(String rawFilter) {
            String filter = rawFilter.trim().toLowerCase(Locale.ROOT);

            this.replaceEntries(
                MobPickerScreen.this.summonableTypes.stream()
                    .filter(type -> matches(type, filter))
                    .map(Entry::new)
                    .toList()
            );
        }

        private boolean matches(EntityType<?> type, String filter) {
            if (filter.isEmpty()) {
                return true;
            }

            if (type.getDescription().getString().toLowerCase(Locale.ROOT).contains(filter)) {
                return true;
            }

            return BuiltInRegistries.ENTITY_TYPE.getKey(type).toString().contains(filter);
        }

        private class Entry extends ObjectSelectionList.Entry<Entry> {
            private final EntityType<?> type;
            private final Component name;

            public Entry(EntityType<?> type) {
                this.type = type;
                this.name = type.getDescription();
            }

            @Override
            public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
                Font font = MobPickerScreen.this.font;
                graphics.text(font, this.name, this.getContentX() + 4, this.getContentYMiddle() - font.lineHeight / 2, 0xFFFFFFFF);
            }

            @Override
            public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
                this.select();
                MobPickerScreen.this.onMobChosen(this.type);
                return super.mouseClicked(event, doubleClick);
            }

            @Override
            public boolean keyPressed(KeyEvent event) {
                if (event.isSelection()) {
                    this.select();
                    MobPickerScreen.this.onMobChosen(this.type);
                    return true;
                }
                return super.keyPressed(event);
            }

            private void select() {
                MobList.this.setSelected(this);
            }

            @Override
            public @NotNull Component getNarration() {
                return Component.translatable("narrator.select", this.name);
            }
        }
    }
}
