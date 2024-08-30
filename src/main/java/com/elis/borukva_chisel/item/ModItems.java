package com.elis.borukva_chisel.item;

import com.elis.borukva_chisel.BorukvaChisel;
import com.elis.borukva_chisel.block.ModBlocks;
import com.elis.borukva_chisel.block.WoodBlocks;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item POLYMER_CHISEL = registerItem("chisel",
            new ChiselItem(new Item.Settings()));

    private static Item registerItem(String name, Item item){
        BorukvaChisel.logger.info("Registering item: {}", name);
        return Registry.register(Registries.ITEM, Identifier.of(BorukvaChisel.MOD_ID, name), item);
    }

    public static void register(){
        BorukvaChisel.logger.info("Registering Mod items for " + BorukvaChisel.MOD_ID);

        ItemGroup.Builder builder = PolymerItemGroupUtils.builder();
        builder.icon(() -> new ItemStack(ModItems.POLYMER_CHISEL, 1));
        builder.displayName(Text.of("Borukva Chisel Polymer"));

        builder.entries(((displayContext, entries) -> {
            entries.add(ModItems.POLYMER_CHISEL);

            ModBlocks.getAllBlocks().forEach((vanillaBlock, variants) -> variants.forEach(entries::add));
        }));

        ItemGroup polymerGroup = builder.build();
        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(BorukvaChisel.MOD_ID), polymerGroup);
    }
}
