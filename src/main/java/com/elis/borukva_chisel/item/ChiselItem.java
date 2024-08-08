package com.elis.borukva_chisel.item;

import com.elis.borukva_chisel.BorukvaChisel;
import com.elis.borukva_chisel.gui.ChiselGui;
import com.mojang.authlib.GameProfile;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.*;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ChiselItem extends Item implements PolymerItem {



    public ChiselItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        if (world.isClient) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }

        ServerPlayerEntity playerEntity = Objects.requireNonNull(world.getServer()).getPlayerManager().getPlayer(user.getName().getString());

        if (playerEntity == null){
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
        // TODO: TEST
//        if (user instanceof ServerPlayerEntity){
//            SimpleGui gui1 = getSimpleGui((ServerPlayerEntity) user);
//        }

        var guiOpened = new ChiselGui(playerEntity).open();
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public void appendTooltip(ItemStack itemStack, @Nullable Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.of("Chisel"));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Items.BOOK;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipType tooltipType, RegistryWrapper.WrapperLookup lookup, @Nullable ServerPlayerEntity player) {
        return PolymerItem.super.getPolymerItemStack(itemStack, tooltipType, lookup, player);
    }

    @Override
    public void modifyClientTooltip(List<Text> tooltip, ItemStack stack, @Nullable ServerPlayerEntity player) {
        PolymerItem.super.modifyClientTooltip(tooltip, stack, player);
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return PolymerResourcePackUtils.requestModel(Items.BOOK,
                        Identifier.of(BorukvaChisel.MOD_ID, "item/chisel"))
                .value();
    }

    @Override
    public boolean isEnabled(FeatureSet enabledFeatures) {
        return super.isEnabled(enabledFeatures);
    }
}
