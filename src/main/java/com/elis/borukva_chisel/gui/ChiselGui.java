package com.elis.borukva_chisel.gui;

import com.elis.borukva_chisel.block.ModBlocks;
import com.elis.borukva_chisel.utils.PlaceableSlotActions;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.*;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.item.Item;
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

    // TODO save player items on disconnect (like -електрохарчування)
    public static final Logger logger = LoggerFactory.getLogger(ChiselGui.class);

    private static final int MAX_SLOTS = 32; // 8X4 grid
    private static final int startSlotIdx = 4;
    private static final int startPage = 1;

    private static final int inputIdx = 0;
    private static final int infoIdx = 1;
    private static final int prevPageIdx = 2;
    private static final int nextPageIdx = 3;
    private List<GuiElementBuilder> currentVariants = null;

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
                .setCallback(this::inputItemCallback)
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

    private void getItem(int index, ClickType type, SlotActionType action) {
        var inputSlot = Objects.requireNonNull(this.getSlot(inputIdx));
        var itemSlot = Objects.requireNonNull(this.getSlot(index));

        var handler = player.currentScreenHandler;

        // number of items given to player
        int itemsGet = 1;

        if (action == SlotActionType.QUICK_MOVE) {
            itemsGet = inputSlot.getItemStack().getCount();
            player.getInventory().offerOrDrop(
                    itemSlot.getItemStack().copyWithCount(itemsGet)
            );
        } else {
            // set cursor stack
            if (handler.getCursorStack().isEmpty()) {
                handler.setCursorStack(
                        itemSlot.getItemStack().copyWithCount(itemsGet));
            } else {
                Item item = itemSlot.getItemStack().getItem();
                // add to cursor stack
                if (handler.getCursorStack().isOf(item)) {
                    handler.setCursorStack(handler.getCursorStack()
                            .copyWithCount(handler.getCursorStack().getCount() + itemsGet));
                } else {
                    return;
                }
            }
        }

        inputSlot.getItemStack().decrement(itemsGet);

        if (inputSlot.getItemStack().isEmpty()) {
            clearSlots();
        }
    }

    private void inputItemCallback(int index, ClickType type, SlotActionType action) {
        GuiElement inputSlot = (GuiElement) Objects.requireNonNull(this.getSlot(index));

        PlaceableSlotActions
                .handlePlaceableSlotAction(this, index, type, action);

        if (inputSlot.getItemStack().isEmpty()) {
            clearSlots();
        } else{
            addVariants(inputSlot.getItemStack());
        }
    }

    private List<GuiElementBuilder> getVariants(ItemStack itemStack) {
        List<GuiElementBuilder> variantSlots = new ArrayList<>();

        // for each mod block if key is type of itemStack add slots
        ModBlocks.getAllBlocks().forEach((vanila_block, variants) -> {
            if (itemStack.isOf(vanila_block.asItem())) {
                variants.forEach((variant) -> {
                    variantSlots.add(new GuiElementBuilder(variant.asItem())
                            .setCallback(this::getItem));
                });
            }
        });
        return variantSlots;
    }

    private void addVariants(ItemStack itemStack) {
        // atomic integer is used because of .forEach() function
        AtomicInteger idx = new AtomicInteger(startSlotIdx);

        // for each mod block if key is type of itemStack add slots
        ModBlocks.getAllBlocks().forEach((vanila_block, variants) -> {
            if (itemStack.isOf(vanila_block.asItem())) {
                variants.forEach((variant) -> {
                    // setSlot is used instead of addSlot to prevent slots duplication
                    // for example if an item decrements to 0 and a new item
                    // of the same type is added to inputSlot
                    this.setSlot(idx.get(), new GuiElementBuilder(variant.asItem())
                            .setCallback(this::getItem));
                    idx.getAndIncrement();
                });
            }
        });
    }

    private void setPage(int page) {

    }

    private void prevPage() {

    }

    private void nextPage() {

    }

    @Override
    public boolean onClick(int index, ClickType type, SlotActionType action, GuiElementInterface element) {
        // for debug purpose
        logger.info("OnClick");
        logger.info("Index {}", index);
        logger.info("Click type {}", type.toString());
        logger.info("Action {}", action.toString());
        logger.info("Item stack {}", element.getItemStack());

        return false;
//        return super.onClick(index, type, action, element);
    }

    public void clearSlots() {
        for (int idx = startSlotIdx; idx < MAX_SLOTS; idx++) {
            this.clearSlot(idx);
        }
    }

    @Override
    public void onClose() {
        // offering items if player didn't get it manually
        player.getInventory().offerOrDrop(Objects.requireNonNull(getSlot(inputIdx)).getItemStack());
    }
}
