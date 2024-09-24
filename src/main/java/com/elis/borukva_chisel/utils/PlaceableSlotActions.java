package com.elis.borukva_chisel.utils;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class PlaceableSlotActions {
    private static final Logger logger = LoggerFactory.getLogger(PlaceableSlotActions.class);

    public static void handlePlaceableSlotAction(
            @NotNull SimpleGui gui,
            int slotIndex,
            ClickType clickType,
            SlotActionType slotAction) {
        var slot = (GuiElement) Objects.requireNonNull(gui.getSlot(slotIndex));


        if (slotAction == SlotActionType.QUICK_MOVE) {
            gui.getPlayer().getInventory().offerOrDrop(slot.getItemStack());
        }

        if (clickType.isRight) {
            handleRightClick(gui, slotIndex);
            // right click also trigger PICKUP action
            return;
        }

        // I think QUICK_CRAFT and MOUSE_DRAG_ON option are the same
        // TODO think about QUICK_CRAFT interactions

        if (slotAction == SlotActionType.PICKUP ||
                slotAction == SlotActionType.QUICK_CRAFT) {
            handleLeftClick(gui, slotIndex);
        }
    }

    public static void handleLeftClick(
            @NotNull SimpleGui gui,
            int slotIndex) {
        var placeableSlot = (GuiElement) Objects.requireNonNull(gui.getSlot(slotIndex));
        var playerHand = gui.getPlayer().currentScreenHandler;

        if (playerHand.getCursorStack().isEmpty()) {
            // Adding item from slot to hand
            playerHand.setCursorStack(placeableSlot.getItemStack().copyAndEmpty());
        } else {
            if (placeableSlot.getItemStack().isEmpty()) {
                // Add item to slot
                placeableSlot.setItemStack(playerHand.getCursorStack().copyAndEmpty());
            } else {
                // player add item to slot which contains item already
                placeableSlot.getItemStack().increment(playerHand.getCursorStack().getCount());
                playerHand.getCursorStack().decrement(playerHand.getCursorStack().getCount());
            }
        }
    }

    public static void handleRightClick(
            @NotNull SimpleGui gui,
            int slotIndex) {
        var placeableSlot = (GuiElement) Objects.requireNonNull(gui.getSlot(slotIndex));
        var playerHand = gui.getPlayer().currentScreenHandler;

        // adding 1 item to empty slot
        if (placeableSlot.getItemStack().isEmpty()) {
            placeableSlot.setItemStack(playerHand.getCursorStack().copyWithCount(1));
            playerHand.getCursorStack().decrement(1);
        } else {
            // adding half of stack to hand
            if (playerHand.getCursorStack().isEmpty()) {
                int slotHalfStack = placeableSlot.getItemStack().getCount() / 2;
                playerHand.setCursorStack(placeableSlot.getItemStack().copyWithCount(slotHalfStack));
                placeableSlot.getItemStack().decrement(slotHalfStack);
            } else {
                // incrementing if item is the same
                if (playerHand.getCursorStack().isOf(placeableSlot.getItemStack().getItem())) {
                    placeableSlot.getItemStack().increment(1);
                    playerHand.getCursorStack().decrement(1);
                } else {
                    // swap items
                    var temp = placeableSlot.getItemStack();
                    placeableSlot.setItemStack(playerHand.getCursorStack());
                    playerHand.setCursorStack(temp);
                }
            }
        }
    }
}
