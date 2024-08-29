package com.elis.borukva_chisel.block;

import com.elis.borukva_chisel.BorukvaChisel;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;

public class CommonBlock extends Block implements PolymerTexturedBlock {
    private final BlockState block;

    public CommonBlock(Settings settings, String modelPath) {
        super(settings);
        PolymerBlockModel blockModel = PolymerBlockModel.of(Identifier.of(BorukvaChisel.MOD_ID, modelPath));
        this.block = PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK, blockModel);
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return block;
    }
}
