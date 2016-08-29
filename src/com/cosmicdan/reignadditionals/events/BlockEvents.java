package com.cosmicdan.reignadditionals.events;

import com.cosmicdan.reignadditionals.Main;
import com.cosmicdan.reignadditionals.ModConfig;
import com.cosmicdan.reignadditionals.items.ModItems;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.BlockLog;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;

public class BlockEvents {

    @SubscribeEvent
    public void harvestDropsEvent(HarvestDropsEvent event) {
        if (event.block instanceof BlockLeavesBase) {
            // leaves broken
            if (event.harvester != null) {
                // player broke leaves
                if (event.harvester.getHeldItem() == null)
                    // player is bare handed
                    if (event.world.rand.nextInt(ModConfig.STICK_DROP_CHANCE) == 0)
                        event.drops.add(new ItemStack(Items.stick));
            }
        } else
        if (event.block instanceof BlockLog) {
            if (event.blockMetadata == 1) { // spruce log
                if (event.world.rand.nextInt(ModConfig.TREESAP_DROP_CHANCE) == 0)
                    event.drops.add(new ItemStack(ModItems.TREESAP));
            }
        }
    }
}
