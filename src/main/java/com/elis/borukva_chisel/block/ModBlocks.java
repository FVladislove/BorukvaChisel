package com.elis.borukva_chisel.block;

import net.minecraft.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ModBlocks {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModBlocks.class);

    public static Map<Block, List<Block>> getAllBlocks(){
        LOGGER.info("Collecting all mod blocks");

        // TODO find better way?
        var map = new HashMap<Block, List<Block>>();
        Stream.of(WoodBlocks.getWoodBlocks()).forEach(map::putAll);

        return map;
    }

    public static void register(){
        WoodBlocks.registerModBlocks();
    }
}
