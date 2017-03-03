package com.cosmicdan.reignadditionals.events;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;

import com.cosmicdan.reignadditionals.ModConfig;
import com.cosmicdan.reignadditionals.client.gui.GuiTextOverlay;
import com.cosmicdan.reignadditionals.gamedata.PlayerTeleporterTracker;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.shadowmage.ancientwarfare.core.interop.ModAccessors;
import openblocks.api.GraveSpawnEvent;

public class EntityEvents {
    
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityPlayer) {
            if (event.world.isRemote) {
                if (Minecraft.getMinecraft().thePlayer.getUniqueID().equals(((EntityPlayer)event.entity).getUniqueID()))
                    GuiTextOverlay.triggerPlayerJoin();
            }
            PlayerTeleporterTracker teleporterProps = PlayerTeleporterTracker.get((EntityPlayer)event.entity);
            if (teleporterProps.isDematerialized()) {
                ((EntityPlayer)event.entity).addPotionEffect(new PotionEffect(Potion.invisibility.getId(), Integer.MAX_VALUE, 0));
            }
            return;
        }
        if (event.entity instanceof EntityLiving) {
            if (!event.entity.getEntityData().hasKey("ORIGINAL_SPAWN_POS")) {
                event.entity.getEntityData().setIntArray("ORIGINAL_SPAWN_POS", new int[] {(int) event.entity.posX, (int) event.entity.posY, (int) event.entity.posZ});
            }
        }
    }
    
    @SubscribeEvent
    public void onGraveSpawn(GraveSpawnEvent event) {
        if (event.hasLocation()) {
            // show notification
            String notificationTitle = "notification.graveplaced.title";
            ChatComponentTranslation notificationMsg = new ChatComponentTranslation("notification.graveplaced.msg", event.getX(), event.getY(), event.getZ());
            List<ChatComponentTranslation> notificationTooltip = new ArrayList<ChatComponentTranslation>();
            notificationTooltip.add(new ChatComponentTranslation("notification.graveplaced.tooltip1"));
            notificationTooltip.add(new ChatComponentTranslation("notification.graveplaced.tooltip2"));
            notificationTooltip.add(new ChatComponentTranslation("notification.tooltip.close"));
            ModAccessors.FTBU.notifyPlayer(EnumChatFormatting.AQUA, event.entityPlayer.getCommandSenderName(), notificationTitle, notificationMsg, notificationTooltip);
            
            String chatAndLogMessage = "Created grave for '" + event.entityPlayer.getCommandSenderName() + "' at " + event.getX() + "x" + event.getY() + "x" + event.getZ();
            
            // log to the base game logger
            LogManager.getLogger().info(chatAndLogMessage);
            
            // and send to player chat
            event.entityPlayer.addChatMessage(new ChatComponentText("§e" + chatAndLogMessage + "§r"));
        }
    }
    
    @SubscribeEvent
    public void onEntityConstructing(EntityConstructing event) {
        if (event.entity instanceof EntityPlayer && PlayerTeleporterTracker.get((EntityPlayer) event.entity) == null)
            PlayerTeleporterTracker.register((EntityPlayer) event.entity);
        
        if (event.entity instanceof EntityPlayer && event.entity.getExtendedProperties(PlayerTeleporterTracker.EXT_PROP_NAME) == null)
            event.entity.registerExtendedProperties(PlayerTeleporterTracker.EXT_PROP_NAME, new PlayerTeleporterTracker((EntityPlayer) event.entity));
    }
    
    static int LIVING_UPDATE_COUNTER = 0;
    static int LIVING_UPDATE_COUNTER_MAX = 20;
    
    @SubscribeEvent
    public void onEntityLivingUpdate(LivingUpdateEvent event) {
        if (event.entity.worldObj.isRemote)
            return;
        if (++LIVING_UPDATE_COUNTER < LIVING_UPDATE_COUNTER_MAX)
            return;
        LIVING_UPDATE_COUNTER = 0;
        if (event.entity instanceof EntityZombie) {
            if (((EntityZombie)event.entity).getAttackTarget() != null) {
                event.entity.getEntityData().setInteger("REIGN_IDLE_TARGET_COUNTER", 0);
                return;
            }
            String startingHeldItem = event.entity.getEntityData().getString("REIGN_STARTING_HELD_ITEM");
            ItemStack heldItem = ((EntityZombie)event.entity).getHeldItem();
            if (startingHeldItem.isEmpty()) {
                event.entity.getEntityData().setString("REIGN_STARTING_HELD_ITEM", heldItem == null ? "NULL" : heldItem.getUnlocalizedName());
            }
            if (heldItem == null || heldItem.getItem() instanceof ItemBlock || heldItem.getItem().getUnlocalizedName().equals(startingHeldItem)) {
                int entityIdleTargetTimer = event.entity.getEntityData().getInteger("REIGN_IDLE_TARGET_COUNTER");
                if (entityIdleTargetTimer >= ModConfig.REIGN_IDLE_TARGET_DESPAWN_SECS) {
                    event.entity.worldObj.removeEntity(event.entity);
                    //Main.LOGGER.warn("ReignAdditionals is removing mob " + event.entity.getCommandSenderName() + " at " + event.entity.posX + "x" + event.entity.posY + "x" + event.entity.posZ + " after idle target timeout ");
                } else {
                    //Main.LOGGER.warn("Setting timer to " + (entityIdleTargetTimer + 1) + " because it's not yet " + ModConfig.REIGN_IDLE_TARGET_DESPAWN_SECS);
                    event.entity.getEntityData().setInteger("REIGN_IDLE_TARGET_COUNTER", ++entityIdleTargetTimer);
                }
            }
        }
    }
}
