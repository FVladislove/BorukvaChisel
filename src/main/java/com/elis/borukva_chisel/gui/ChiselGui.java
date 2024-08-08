package com.elis.borukva_chisel.gui;

import com.elis.borukva_chisel.BorukvaChisel;
import com.mojang.authlib.GameProfile;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.GuiHelpers;
import eu.pb4.sgui.api.elements.*;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ChiselGui extends SimpleGui {

    public static final Logger LOGGER = LoggerFactory.getLogger(BorukvaChisel.MOD_ID);
    private static final int MAX_SLOTS = 32; // 8X4 grid

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
        this.setSlot(0, new GuiElementBuilder(ItemStack.EMPTY)
                .setName(Text.literal("Converting item"))
                .setCallback((index, type, action) -> {
                    ChoseMenu();
                })
                .build());

        this.setSlot(1, new GuiElementBuilder(Items.PAPER)
                .setName(Text.literal("Possible variants"))
                .setCallback((index, type, action) -> {
                    // TODO: Implement interface
                }));
    }

    private void ChoseMenu() {
        // TODO: make item move to inventory on CTRL+Click
        GuiElement currentSlot = (GuiElement) Objects.requireNonNull(this.getSlot(0));

        var currentStack = currentSlot.getItemStack();
        var handler = player.currentScreenHandler;

        LOGGER.info("Stack in slot -> {}", currentStack.toString());
        LOGGER.info("Stack in hand -> {}", handler.getCursorStack());

        if (handler.getCursorStack().isEmpty()) {
            handler.setCursorStack(currentStack);
            // Used because the ItemStack icon remained after
            // adding it to the player's hand
            currentSlot.setItemStack(ItemStack.EMPTY);
        } else {
            if (currentStack.isEmpty()) {
                currentSlot.setItemStack(handler.getCursorStack());
                handler.setCursorStack(ItemStack.EMPTY);
            } else {
                if (player.isCreative()) {
                    if (player.getInventory().contains(currentStack)) {
                        player.getInventory().insertStack(currentStack);
                    }
                } else {
                    player.getInventory().offerOrDrop(currentStack);
                }
            }
        }

        currentSlot.getItemStack().streamTags().forEach((tag) -> {
                    LOGGER.info(tag.toString());
                }
        );

        if (currentSlot.getItemStack().isOf(Items.DIAMOND)) {
            int idx = 3;

            for (var woodBlock : Registries.BLOCK) {
                if (woodBlock.getDefaultState().isIn(BlockTags.LOGS) ||
                        woodBlock.getDefaultState().isIn(BlockTags.PLANKS)) {

                    this.setSlot(idx, new GuiElementBuilder(woodBlock.asItem())
                            .setName(Text.literal("Possible variants"))
                            .setCallback((index, type, action) -> {
                                // TODO: Implement interface
                            }));
                    idx++;
                }
            }
        }
    }


    // TODO replace with function for items pick
    @Override
    public boolean onClick(int index, ClickType type, SlotActionType action, GuiElementInterface element) {
        LOGGER.info("Click type {}", type.toString());
        LOGGER.info("Item stack {}", element.getItemStack());

        if (index == 0 || index == 1) {
            return false;
        }

        if (type.equals(ClickType.MOUSE_LEFT_SHIFT)) {
            LOGGER.info("Adding full stack of {}", element.getItemStack().toString());
            this.player.currentScreenHandler.setCursorStack(
                    element.getItemStack().copyWithCount(64)
            );
        }

        if (type.equals(ClickType.MOUSE_LEFT)) {
            LOGGER.info("Adding {}", element.getItemStack().toString());
            this.player.currentScreenHandler.setCursorStack(
                    element.getItemStack()
            );
        }

        return super.onClick(index, type, action, element);
    }
}
