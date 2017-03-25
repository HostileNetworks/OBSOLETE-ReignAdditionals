package com.cosmicdan.reignadditionals.events;

import com.cosmicdan.reignadditionals.ModConfig;
import com.cosmicdan.reignadditionals.items.ModItems;
import com.cosmicdan.reignadditionals.util.BlockAndMeta;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.BlockLog;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
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
        if (event.block instanceof BlockLog) { // TODO: actually make this spruce only
            if (event.blockMetadata == 1) { // spruce log
                if (event.world.rand.nextInt(ModConfig.TREESAP_DROP_CHANCE) == 0)
                    event.drops.add(new ItemStack(ModItems.TREESAP));
            }
        }
    }
    
    public static boolean shouldStopFire(World world, int posX, int posY, int posZ) {
        if (ModConfig.getNonflammableBlocks() != null) {
            Block thisBlock = world.getBlock(posX, posY - 1, posZ);
            int thisMeta = world.getBlockMetadata(posX, posY - 1, posZ);
            for ( BlockAndMeta blockAndMeta : ModConfig.getNonflammableBlocks() ) {
                if ( Block.isEqualTo(blockAndMeta.block, thisBlock) ) {
                    if ( blockAndMeta.meta == -1 || blockAndMeta.meta == thisMeta ) {
                        world.setBlockToAir(posX, posY, posZ);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
