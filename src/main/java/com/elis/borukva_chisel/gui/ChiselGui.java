package com.elis.borukva_chisel.gui;

import com.elis.borukva_chisel.block.ModBlocks;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.*;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ChiselGui extends SimpleGui {

    // TODO fix
    public static final Logger logger = LoggerFactory.getLogger(ChiselGui.class);

    private static final int MAX_SLOTS = 32; // 8X4 grid
    private static final int startSlotIdx = 5;

    private static final int inputIdx = 0;
    private static final int infoIdx = 1;
    private static final int prevPageIdx = 2;
    private static final int nextPageIdx = 3;

    /**
     * Constructs a new simple container gui for the supplied player.
     *
     * @param player the player to server this gui to
     */
    public ChiselGui(ServerPlayerEntity player) {
        super(ScreenHandlerType.GENERIC_9X6, player, false);


        this.setTitle(Text.literal("Chisel"));
        addButtons();
    }

    private void addButtons() {
        this.setSlot(inputIdx, new GuiElementBuilder(ItemStack.EMPTY)
                .setName(Text.literal("Converting item"))
                .setCallback((index, type, action) -> mainMenu())
                .build());

        this.setSlot(infoIdx, new GuiElementBuilder(Items.BOOK)
                .setName(Text.literal("Info"))
                .setCallback((index, type, action) -> {
                    // infoMenu();
                })
                .build());

        this.setSlot(prevPageIdx, new GuiElementBuilder(Items.GREEN_CARPET)
                .setName(Text.literal("Prev Page"))
        );

        this.setSlot(nextPageIdx, new GuiElementBuilder(Items.RED_CARPET)
                .setName(Text.literal("Next Page")));
    }

    private void getItem(int idx) {
        Objects.requireNonNull(this.getSlot(inputIdx))
                .getItemStack()
                .decrement(1);
        if (player.currentScreenHandler.getCursorStack().isEmpty()) {
            player.currentScreenHandler.setCursorStack(
                    Objects.requireNonNull(this.getSlot(idx))
                            .getItemStack()
                            .copy());
        }
    }

    private void mainMenu() {
        // TODO: make item move to inventory on SHIFT+Click
        GuiElement currentSlot = (GuiElement) Objects.requireNonNull(this.getSlot(inputIdx));

        var currentStack = currentSlot.getItemStack();
        var handler = player.currentScreenHandler;

        logger.debug("Stack in slot -> {}", currentStack.toString());
        logger.debug("Stack in hand -> {}", handler.getCursorStack());

        if (handler.getCursorStack().isEmpty()) {
            // Adding item to hand
            handler.setCursorStack(currentStack);
            // Used because the ItemStack icon remained after
            // adding it to the player's hand
            currentSlot.setItemStack(ItemStack.EMPTY);
            clearSlots();
        } else {
            if (currentStack.isEmpty()) {
                // Add item to slot
                currentSlot.setItemStack(handler.getCursorStack());
                handler.setCursorStack(ItemStack.EMPTY);
            } else {
                if (player.isCreative()) {
                    if (player.getInventory().contains(currentStack)) {
                        player.getInventory().insertStack(currentStack);
                    }
                } else {
                    // TODO check
                    player.getInventory().offerOrDrop(currentStack);
                }
            }
        }

        currentStack = currentSlot.getItemStack();

        AtomicInteger idx = new AtomicInteger(startSlotIdx);
        ItemStack finalCurrentStack = currentStack;

        ModBlocks.getAllBlocks().forEach((vanila_block, variants) -> {
            if (finalCurrentStack.isOf(vanila_block.asItem())) {
                variants.forEach((variant) ->
                        this.addSlot(new GuiElementBuilder(variant.asItem())
                                .setCallback((index, type, action) -> getItem(index))));
                idx.getAndIncrement();
            }
        });
        // TODO fix duplication bug: if picked item placed in slot, all items duplicated

        logger.info("\n");
    }

    // TODO replace with function for items pick
    @Override
    public boolean onClick(int index, ClickType type, SlotActionType action, GuiElementInterface element) {
        // TODO REMOVE

        logger.debug("OnClick\n");
        logger.debug("Index {}", index);
        logger.debug("Click type {}", type.toString());
        logger.debug("Action {}", action.toString());
        logger.debug("Item stack {}", element.getItemStack());

        return false;
//        return super.onClick(index, type, action, element);
    }

    public void clearSlots() {
        for (int idx = startSlotIdx; idx < MAX_SLOTS; idx++) {
            this.clearSlot(idx);
        }
    }
}
