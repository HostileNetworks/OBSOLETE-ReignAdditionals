package com.cosmicdan.reignadditionals.events;

import com.cosmicdan.reignadditionals.Main;
import com.cosmicdan.reignadditionals.ModConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class PlayerEvents {
    
    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (ModConfig.ALLOW_BREAKING_WITHOUT_TOOL) return;
        if (event.entityPlayer.capabilities.isCreativeMode) return;
        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            Block tryingToHarvest = event.world.getBlock(event.x, event.y, event.z); 
            int blockMeta = event.world.getBlockMetadata(event.x, event.y, event.z);
            if (!tryHarvest(tryingToHarvest, blockMeta, event.entityPlayer)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onBreakSpeed(BreakSpeed event) {
        if (ModConfig.ALLOW_BREAKING_WITHOUT_TOOL) return;
        if (event.entityPlayer.capabilities.isCreativeMode) return;
        Block tryingToHarvest = event.entityPlayer.worldObj.getBlock(event.x, event.y, event.z);
        int blockMeta = event.entityPlayer.worldObj.getBlockMetadata(event.x, event.y, event.z);
        if (!tryHarvest(tryingToHarvest, blockMeta, event.entityPlayer)) {
            event.setCanceled(true);
        }
    }
    
    
    /*
     * Helper methods for block harvest/break prevention stuff
     * 
     */

    private boolean tryHarvest(Block block, int blockMeta, EntityPlayer entityPlayer) {
        if (block.getMaterial() == Material.wood) {
            for (int oreId : OreDictionary.getOreIDs(new ItemStack(block))) {
                if (OreDictionary.getOreName(oreId).equals("logWood"))
                    return letPlayerUseTool(block, blockMeta, entityPlayer, "axe");
            }
        }
        if (block.getMaterial() == Material.rock) {
            for (int oreId : OreDictionary.getOreIDs(new ItemStack(block))) {
                if (OreDictionary.getOreName(oreId).equals("stone"))
                    return letPlayerUseTool(block, blockMeta, entityPlayer, "pickaxe");
            }
        }
        // allow the player to punch-break/harvest anything that isn't explicitly prevented
        return true;
    }

    private boolean letPlayerUseTool(Block block, int blockMeta, EntityPlayer entityPlayer, String toolClass) {
        if (entityPlayer.getHeldItem() != null) {
            if ((block.canHarvestBlock(entityPlayer, blockMeta)) && (entityPlayer.getHeldItem().getItem().getHarvestLevel(entityPlayer.getHeldItem(), toolClass) != -1))
                    return true;
        }
        return false;
    }
}
