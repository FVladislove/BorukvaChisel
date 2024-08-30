package com.elis.borukva_chisel.block;

import com.elis.borukva_chisel.BorukvaChisel;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Stream;

public class WoodBlocks {

    private static final Logger logger = LoggerFactory.getLogger(WoodBlocks.class);

    // because using Blocks.CHERRY_LOG isn't possible for settings
    // TODO check why it causes a problem
    public static final Map<Block, List<Block>> WOOD_BLOCKS = registerBlocks(
            Blocks.CHERRY_LOG, Blocks.CHERRY_WOOD,
            new HashSet<>() {{
                add("test_wood");
                add("test_wood2");
            }});


    private static Map<Block, List<Block>> registerBlocks(Block parentBLock, Set<String> blocksNames) {
        return registerBlocks(parentBLock, parentBLock, blocksNames);
    }

    private static Map<Block, List<Block>> registerBlocks(Block vanillaBock, Block settingsBlock, Set<String> blockNames) {
        List<Block> blocks = new ArrayList<>();
        for (var name : blockNames) {
            blocks.add(registerBlock(
                    name,
                    new CommonBlock(AbstractBlock.Settings.copy(settingsBlock), "block/" + name),
                    new Item.Settings()));
        }

        return Map.of(
                vanillaBock, blocks
        );
    }

    public static Map<Block, List<Block>> getWoodBlocks(){
        logger.debug("Collecting all mod blocks");

        // TODO find better way?
        var map = new HashMap<Block, List<Block>>();
        Stream.of(WoodBlocks.WOOD_BLOCKS).forEach(map::putAll);

        return map;
    }
    // TODO make function to use less repeatable data
    private static Block registerBlock(String name, Block
            block, Item.Settings settings) {
        logger.info("Registering wood block: {}", name);

        registerBlockItem(name, block, settings);
        return Registry.register(Registries.BLOCK, Identifier.of(BorukvaChisel.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block
            block, Item.Settings settings) {
        return Registry.register(Registries.ITEM,
                Identifier.of(BorukvaChisel.MOD_ID, name), new BlockItem(block, settings));
    }

    private static void addBlocksToBuildingBlocksTabItemGroup
            (FabricItemGroupEntries entries) {
//        CHERRY_VARIANTS.forEach((name, block) -> entries.add(block));
        WOOD_BLOCKS.forEach((block, list) -> list.forEach(entries::add));
//                    CHERRY_VARIANTS.forEach(entries::add);
    }

    public static void registerModBlocks() {
        logger.info("Registering mod blocks");
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(WoodBlocks::addBlocksToBuildingBlocksTabItemGroup);
    }
}
