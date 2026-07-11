package com.chimericdream.lib.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jspecify.annotations.NonNull;

public class DoubleWideInventoryScreen<Handler extends AbstractContainerMenu> extends AbstractContainerScreen<Handler> {
    private static final Identifier BG = Identifier.withDefaultNamespace("textures/gui/demo_background.png");
    private static final int BG_CORNER = 4;
    private static final int BG_X = 0;
    private static final int BG_Y = 0;
    private static final int BG_W = 350;
    private static final int BG_H = 344;

    private static final Identifier CONTAINER = Identifier.withDefaultNamespace("textures/gui/container/generic_54.png");
    private static final int CINV_X = 7;
    private static final int CINV_Y = 17;
    private static final int CINV_COLS = 9;
    private static final int CINV_ROWS = 6;
    private static final int PINV_X = 7;
    private static final int PINV_Y = 139;
    private static final int PINV_W = 162;
    private static final int PINV_H = 76;

    private static final int SLOT_SIZE = 18;
    private static final int TEXT_LH = 11;
    private static final int PT = 17;
    private static final int PB = 13;
    private static final int PL = 13;
    private static final int PR = 13;
    private static final int CINV_MB = 14;
    private static final int PINV_MB = 4;

    private final int numCols;
    private final int numRows;

    public DoubleWideInventoryScreen(Handler handler, int numRows, Inventory inventory, Component title) {
        this(handler, 18, numRows, inventory, title);
    }

    public DoubleWideInventoryScreen(Handler handler, int numCols, int numRows, Inventory inventory, Component title) {
        super(
            handler,
            inventory,
            title,
            PL + (numCols * SLOT_SIZE) + PR,
            PT + (numRows * SLOT_SIZE) + CINV_MB + (3 * SLOT_SIZE) + PINV_MB + SLOT_SIZE + PB
        );
        this.numCols = numCols;
        this.numRows = numRows;

        this.titleLabelX = PL + 1 - CINV_X;
        this.titleLabelY = PT - TEXT_LH;
        this.inventoryLabelX = PL + ((this.numCols - CINV_COLS) * (SLOT_SIZE / 2)) + 1 - PINV_X;
        this.inventoryLabelY = this.imageHeight - (TEXT_LH + (3 * SLOT_SIZE) + PINV_MB + SLOT_SIZE + PB);
    }

//    @Override
//    public void extractRenderState(@NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
//        this.renderBackground(context, mouseX, mouseY, delta);
//        super.render(context, mouseX, mouseY, delta);
//        this.renderTooltip(context, mouseX, mouseY);
//    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        this.drawBackgroundTexture(context);
        this.drawSlotTexture(context);
    }

    private void drawBackgroundTexture(GuiGraphicsExtractor context) {
        int hnum = (this.imageWidth - (BG_CORNER * 2)) / (BG_W - (BG_CORNER * 2));
        int hrem = (this.imageWidth - (BG_CORNER * 2)) % (BG_W - (BG_CORNER * 2));

        int vnum = (this.imageHeight - (BG_CORNER * 2)) / (BG_H - (BG_CORNER * 2));
        int vrem = (this.imageHeight - (BG_CORNER * 2)) % (BG_H - (BG_CORNER * 2));

        //
        // corner
        //

        // left-top
        context.blit(
            RenderPipelines.GUI_TEXTURED,
            BG,
            this.leftPos,
            this.topPos,
            BG_X,
            BG_Y,
            BG_CORNER,
            BG_CORNER,
            BG_W,
            BG_H
        );

        // left-bottom
        context.blit(
            RenderPipelines.GUI_TEXTURED,
            BG,
            this.leftPos,
            this.topPos + this.imageHeight - BG_CORNER,
            BG_X,
            BG_H - BG_CORNER,
            BG_CORNER,
            BG_CORNER,
            BG_W,
            BG_H
        );

        // right-top
        context.blit(
            RenderPipelines.GUI_TEXTURED,
            BG,
            this.leftPos + this.imageWidth - BG_CORNER,
            this.topPos,
            BG_W - BG_CORNER,
            BG_Y,
            BG_CORNER,
            BG_CORNER,
            BG_W,
            BG_H
        );

        // right-bottom
        context.blit(
            RenderPipelines.GUI_TEXTURED,
            BG,
            this.leftPos + this.imageWidth - BG_CORNER,
            this.topPos + this.imageHeight - BG_CORNER,
            BG_W - BG_CORNER,
            BG_H - BG_CORNER,
            BG_CORNER,
            BG_CORNER,
            BG_W,
            BG_H
        );

        //
        // edge
        //

        for (int hcnt = 0; hcnt < hnum; ++hcnt) {
            // top
            context.blit(
                RenderPipelines.GUI_TEXTURED,
                BG,
                this.leftPos + BG_CORNER + hcnt * (BG_W - BG_CORNER * 2),
                this.topPos,
                BG_CORNER,
                BG_Y,
                BG_W - BG_CORNER * 2,
                BG_CORNER,
                BG_W,
                BG_H
            );

            // bottom
            context.blit(
                RenderPipelines.GUI_TEXTURED,
                BG,
                this.leftPos + BG_CORNER + hcnt * (BG_W - BG_CORNER * 2),
                this.topPos + this.imageHeight - BG_CORNER,
                BG_CORNER,
                BG_H - BG_CORNER,
                BG_W - BG_CORNER * 2,
                BG_CORNER,
                BG_W,
                BG_H
            );
        }

        for (int vcnt = 0; vcnt < vnum; ++vcnt) {
            // left
            context.blit(
                RenderPipelines.GUI_TEXTURED,
                BG,
                this.leftPos,
                this.topPos + BG_CORNER + vcnt * (BG_H - BG_CORNER * 2),
                BG_X,
                BG_CORNER,
                BG_CORNER,
                BG_H - BG_CORNER * 2,
                BG_W,
                BG_H
            );

            // right
            context.blit(
                RenderPipelines.GUI_TEXTURED,
                BG,
                this.leftPos + this.imageWidth - BG_CORNER + 50,
                this.topPos + BG_CORNER + vcnt * (BG_H - BG_CORNER * 2),
                BG_W - BG_CORNER,
                BG_CORNER,
                BG_CORNER,
                BG_H - BG_CORNER * 2,
                BG_W,
                BG_H
            );
        }

        // top
        context.blit(
            RenderPipelines.GUI_TEXTURED,
            BG,
            this.leftPos + BG_CORNER + hnum * (BG_W - BG_CORNER * 2),
            this.topPos,
            BG_CORNER,
            BG_Y,
            hrem,
            BG_CORNER,
            BG_W,
            BG_H
        );

        // bottom
        context.blit(
            RenderPipelines.GUI_TEXTURED,
            BG,
            this.leftPos + BG_CORNER + hnum * (BG_W - BG_CORNER * 2),
            this.topPos + this.imageHeight - BG_CORNER,
            BG_CORNER,
            BG_H - BG_CORNER,
            hrem,
            BG_CORNER,
            BG_W,
            BG_H
        );

        // left
        context.blit(
            RenderPipelines.GUI_TEXTURED,
            BG,
            this.leftPos,
            this.topPos + BG_CORNER + vnum * (BG_H - BG_CORNER * 2),
            BG_X,
            BG_CORNER,
            BG_CORNER,
            vrem,
            BG_W,
            BG_H
        );

        // right
        context.blit(
            RenderPipelines.GUI_TEXTURED,
            BG,
            this.leftPos + this.imageWidth - BG_CORNER,
            this.topPos + BG_CORNER + vnum * (BG_H - BG_CORNER * 2),
            BG_W - BG_CORNER,
            BG_CORNER,
            BG_CORNER,
            vrem,
            BG_W,
            BG_H
        );

        //
        // area
        //

        for (int vcnt = 0; vcnt < vnum; ++vcnt) {
            for (int hcnt = 0; hcnt < hnum; ++hcnt) {
                context.blit(
                    RenderPipelines.GUI_TEXTURED,
                    BG,
                    this.leftPos + BG_CORNER + hcnt * (BG_W - BG_CORNER * 2),
                    this.topPos + BG_CORNER + vcnt * (BG_H - BG_CORNER * 2),
                    BG_CORNER,
                    BG_CORNER,
                    BG_W - BG_CORNER * 2,
                    BG_H - BG_CORNER * 2,
                    BG_W,
                    BG_H
                );
            }

            context.blit(
                RenderPipelines.GUI_TEXTURED,
                BG,
                this.leftPos + BG_CORNER + hnum * (BG_W - BG_CORNER * 2),
                this.topPos + BG_CORNER + vcnt * (BG_H - BG_CORNER * 2),
                BG_CORNER,
                BG_CORNER,
                hrem,
                BG_H - BG_CORNER * 2,
                BG_W,
                BG_H
            );
        }

        for (int hcnt = 0; hcnt < hnum; ++hcnt) {
            context.blit(
                RenderPipelines.GUI_TEXTURED,
                BG,
                this.leftPos + BG_CORNER + hcnt * (BG_W - BG_CORNER * 2),
                this.topPos + BG_CORNER + vnum * (BG_H - BG_CORNER * 2),
                BG_CORNER,
                BG_CORNER,
                BG_W - BG_CORNER * 2,
                vrem,
                BG_W,
                BG_H
            );
        }

        context.blit(
            RenderPipelines.GUI_TEXTURED,
            BG,
            this.leftPos + BG_CORNER + hnum * (BG_W - BG_CORNER * 2),
            this.topPos + BG_CORNER + vnum * (BG_H - BG_CORNER * 2),
            BG_CORNER,
            BG_CORNER,
            hrem,
            vrem,
            BG_W,
            BG_H
        );
    }

    private void drawSlotTexture(GuiGraphicsExtractor context) {
        //
        // container inventory
        //

        int hnum = this.numCols / CINV_COLS;
        int hrem = (this.numCols % CINV_COLS) * SLOT_SIZE;

        int vnum = this.numRows / CINV_ROWS;
        int vrem = (this.numRows % CINV_ROWS) * SLOT_SIZE;

        for (int vcnt = 0; vcnt < vnum; ++vcnt) {
            for (int hcnt = 0; hcnt < hnum; ++hcnt) {
                context.blit(
                    RenderPipelines.GUI_TEXTURED,
                    CONTAINER,
                    this.leftPos + CINV_X + hcnt * CINV_COLS * SLOT_SIZE,
                    this.topPos + CINV_Y + vcnt * CINV_ROWS * SLOT_SIZE,
                    CINV_X,
                    CINV_Y,
                    CINV_COLS * SLOT_SIZE,
                    CINV_ROWS * SLOT_SIZE,
                    256,
                    256
                );
            }

            context.blit(
                RenderPipelines.GUI_TEXTURED,
                CONTAINER,
                this.leftPos + CINV_X + hnum * CINV_COLS * SLOT_SIZE,
                this.topPos + CINV_Y + vcnt * CINV_ROWS * SLOT_SIZE,
                CINV_X,
                CINV_Y,
                hrem,
                CINV_ROWS * SLOT_SIZE,
                256,
                256
            );
        }

        for (int hcnt = 0; hcnt < hnum; ++hcnt) {
            context.blit(
                RenderPipelines.GUI_TEXTURED,
                CONTAINER,
                this.leftPos + CINV_X + hcnt * CINV_COLS * SLOT_SIZE,
                this.topPos + CINV_Y + vnum * CINV_ROWS * SLOT_SIZE,
                CINV_X,
                CINV_Y,
                CINV_COLS * SLOT_SIZE,
                vrem,
                256,
                256
            );
        }

        context.blit(
            RenderPipelines.GUI_TEXTURED,
            CONTAINER,
            this.leftPos + CINV_X + hnum * CINV_COLS * SLOT_SIZE,
            this.topPos + CINV_Y + vnum * CINV_ROWS * SLOT_SIZE,
            CINV_X,
            CINV_Y,
            hrem,
            vrem,
            256,
            256
        );

        //
        // player inventory
        //

        context.blit(
            RenderPipelines.GUI_TEXTURED,
            CONTAINER,
            this.leftPos + CINV_X + (this.numCols - CINV_COLS) * (SLOT_SIZE / 2),
            this.topPos + CINV_Y + this.numRows * SLOT_SIZE + CINV_MB,
            PINV_X,
            PINV_Y,
            PINV_W,
            PINV_H,
            256,
            256
        );
    }
}
