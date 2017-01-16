package com.cosmicdan.reignadditionals.events;

import com.cosmicdan.reignadditionals.ModConfig;
import com.cosmicdan.reignadditionals.gamedata.PlayerTeleporterTracker;
import com.cosmicdan.reignadditionals.items.ModItems;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.oredict.OreDictionary;
import net.shadowmage.ancientwarfare.core.gamedata.Timekeeper;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;

public class PlayerEvents {
    
    @SubscribeEvent
    public void onPlayerEntityInteract(EntityInteractEvent event) {
        if (PlayerTeleporterTracker.get(event.entityPlayer).isDematerialized()) {
            event.setCanceled(true);
            return;
        }
    }
    
    @SubscribeEvent
    public void onPlayerAttackEntity(AttackEntityEvent event) {
        if (PlayerTeleporterTracker.get(event.entityPlayer).isDematerialized()) {
            event.setCanceled(true);
            return;
        }
    }
    
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.entity instanceof EntityPlayer) {
            if (PlayerTeleporterTracker.get((EntityPlayer)event.entity).isDematerialized()) {
                event.setCanceled(true);
                return;
            }
        }
    }
    
    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (PlayerTeleporterTracker.get(event.entityPlayer).isDematerialized()) {
            event.setCanceled(true);
            if (event.entityPlayer.getHeldItem() != null) {
                Item heldItem = event.entityPlayer.getHeldItem().getItem();
                if (heldItem == ModItems.TELEPORTER || heldItem == ModItems.MATERIALIZER) {
                    if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
                        event.setCanceled(false);
                    }
                }
            }
            return;
        }
        if (ModConfig.ALLOW_BREAKING_WITHOUT_TOOL)
            return;
        if (event.entityPlayer.capabilities.isCreativeMode)
            return;
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
        if (PlayerTeleporterTracker.get(event.entityPlayer).isDematerialized()) {
            event.setCanceled(true);
            return;
        }
        if (ModConfig.ALLOW_BREAKING_WITHOUT_TOOL)
            return;
        if (event.entityPlayer.capabilities.isCreativeMode)
            return;
        Block tryingToHarvest = event.entityPlayer.worldObj.getBlock(event.x, event.y, event.z);
        int blockMeta = event.entityPlayer.worldObj.getBlockMetadata(event.x, event.y, event.z);
        if (!tryHarvest(tryingToHarvest, blockMeta, event.entityPlayer)) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void playerSleepInBed(PlayerSleepInBedEvent event) {
        if (Timekeeper.isNighttime()) {
            
            // check if today is a siege day
            int currentDay = (int) ((event.entityPlayer.worldObj.getWorldTime() / 24000L));
            int currentYear = currentDay / ModConfig.daysPerYear + ModConfig.STARTING_YEAR;
            if (currentYear > ModConfig.STARTING_YEAR) {
                currentDay = currentDay - ((currentYear - ModConfig.STARTING_YEAR) * ModConfig.daysPerYear);
            }
            int daysUntilFullMoon = ModConfig.daysPerMonth - (currentDay % ModConfig.daysPerMonth);
            if ((daysUntilFullMoon == ModConfig.daysPerMonth) && (currentDay > 1)) {
                // is a siege day, don't let the player sleep
                event.result = EntityPlayer.EnumStatus.OTHER_PROBLEM;
                event.entityPlayer.addChatComponentMessage(new ChatComponentTranslation("siegeevent.cantsleep.msg"));
            }
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
