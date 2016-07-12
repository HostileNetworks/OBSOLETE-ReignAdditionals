package com.cosmicdan.reignadditionals.events;

import com.cosmicdan.reignadditionals.Main;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class PlayerEvents {
    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            Block tryingToHarvest = event.world.getBlock(event.x, event.y, event.z); 
            if (!tryHarvest(tryingToHarvest, event.entityPlayer, false)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onBreakSpeed(BreakSpeed event) {
        Block tryingToHarvest = event.entityPlayer.worldObj.getBlock(event.x, event.y, event.z); 
        if (!tryHarvest(tryingToHarvest, event.entityPlayer)) {
            event.setCanceled(true);
        }
    }
    
    private boolean tryHarvest(Block block, EntityPlayer entityPlayer) {
        return tryHarvest(block, entityPlayer, true);
    }

    private boolean tryHarvest(Block block, EntityPlayer entityPlayer, boolean silentCancel) {
        if (block.getMaterial() == Material.wood)
            return letPlayerUseTool(entityPlayer, "axe", silentCancel);
        if (block.getMaterial() == Material.rock)
            return letPlayerUseTool(entityPlayer, "pickaxe", silentCancel);
        // allow the player to punch-break/harvest anything that isn't explicitly prevented
        return true;
    }

    private boolean letPlayerUseTool(EntityPlayer entityPlayer, String toolClass, boolean silentCancel) {
        if (entityPlayer.getHeldItem() != null) {
            if (entityPlayer.getHeldItem().getItem() instanceof ItemTool)
                if (((ItemTool)entityPlayer.getHeldItem().getItem()).getToolClasses(entityPlayer.getHeldItem()).contains(toolClass))
                    return true;
        } else if (!silentCancel) {
            entityPlayer.attackEntityFrom(DamageSource.generic, 0.1f);
            entityPlayer.addChatMessage(new ChatComponentText("Ouch! It seems I need the right tool for the job..."));
        }
        return false;
    }

}
