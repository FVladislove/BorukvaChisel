package com.elis.borukva_chisel.gui;

import com.elis.borukva_chisel.block.ModBlocks;
import com.elis.borukva_chisel.utils.PlaceableSlotActions;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.*;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.block.Block;
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

    private static final int MAX_SLOTS = 18; // 6X3 grid
    private static final int startSlotIdx = 4;
    private static final int endSlotIdx = MAX_SLOTS + startSlotIdx;
    private int currentPage = 0;

    private static final int inputIdx = 0;
    private static final int infoIdx = 1;
    private static final int prevPageIdx = 2;
    private static final int nextPageIdx = 3;
    private final List<GuiElementBuilder> currentVariantSlots = new ArrayList<>();

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
        // TODO use localizations
        this.setSlot(inputIdx, new GuiElementBuilder(ItemStack.EMPTY)
                .setName(Text.literal("Converting item"))
                .setCallback(this::inputItemCallback)
                .build());

        // TODO add to Polydex?
        this.setSlot(infoIdx, new GuiElementBuilder(Items.BOOK)
                .setName(Text.literal("Info"))
                .setCallback((index, type, action) -> {
                    // infoMenu();
                })
                .build());

        this.setSlot(prevPageIdx, new GuiElementBuilder(Items.GREEN_CARPET)
                .setCallback(this::prevPage)
                .setName(Text.literal("Prev Page"))
        );

        this.setSlot(nextPageIdx, new GuiElementBuilder(Items.RED_CARPET)
                .setCallback(this::nextPage)
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
            // reset variables to default values
            clearSlots();
            currentVariantSlots.clear();
            currentPage = 0;
        } else {
            //  is it triggered after each interaction with the input slot,
            // so maybe it is a way for updating it using the `isVariantsAdded`
            // boolean variable, but I don't know how to handle item swapping correctly
            addVariants(inputSlot.getItemStack());
//            addTestVariants(inputSlot.getItemStack());
        }
    }

    private void addVariants(ItemStack itemStack) {
        // for each vanilla block variant add it as a slot to currentVariantSlots
        ModBlocks.getAllBlocks().forEach((vanila_block, vanilla_variants) -> {
            if (itemStack.isOf(vanila_block.asItem())) {
                vanilla_variants.forEach((variant) -> {
                    currentVariantSlots.add(new GuiElementBuilder(variant.asItem())
                            .setCallback(this::getItem)
                            .setName(variant.getName()));
                });
            }
        });

        this.setPage(currentPage);
    }

    private void addTestVariants(ItemStack itemStack) {
        // for each mod block if key is type of itemStack add slots
        List<Block> variants = new ArrayList<>();
        ModBlocks.getAllBlocks().forEach((vanila_block, vanilla_variants) -> {
            if (itemStack.isOf(vanila_block.asItem())) {
                variants.addAll(vanilla_variants);
            }
        });

        int currentSlotIdx = startSlotIdx;
        for (var block : variants) {
            for (int i = 0; i < 30; i++) {
                currentVariantSlots.add(new GuiElementBuilder(block.asItem())
                        .setCallback(this::getItem)
                        .setName(Text.of(String.valueOf(currentSlotIdx - startSlotIdx))));

                if (currentSlotIdx < endSlotIdx) {
                    this.setSlot(currentSlotIdx, currentVariantSlots.getLast());
                }
                currentSlotIdx++;
            }
        }
    }

    private void setPage(int page) {
        clearSlots();

        var maxPage = Math.ceil((double) currentVariantSlots.size()
                / (double) MAX_SLOTS) - 1;

        if (page > maxPage) {
            currentPage = 0;
        } else if (page < 0) {
            currentPage = (int) maxPage;
        }

        int leftBorder = MAX_SLOTS * currentPage;
        if (leftBorder > currentVariantSlots.size()) {
            leftBorder = MAX_SLOTS * (currentPage - 1);
        }

        int rightBorder = Math.min(leftBorder + MAX_SLOTS, currentVariantSlots.size());
        var slots = currentVariantSlots.subList(leftBorder, rightBorder);

        for (int i = 0; i < slots.size(); i++) {
            this.setSlot(startSlotIdx + i, slots.get(i));
        }
    }

    private void prevPage() {
        currentPage -= 1;
        setPage(currentPage);
    }

    private void nextPage() {
        currentPage += 1;
        setPage(currentPage);
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
    }

    public void clearSlots() {
        for (int idx = startSlotIdx; idx < endSlotIdx; idx++) {
            this.clearSlot(idx);
        }
    }

    @Override
    public void onClose() {
        // offering placed items if player didn't get them manually
        player.getInventory().offerOrDrop(Objects.requireNonNull(getSlot(inputIdx)).getItemStack());
    }
}
