package com.cosmicdan.reignadditionals.events;

import com.cosmicdan.reignadditionals.client.gui.GuiTextOverlay;
import com.cosmicdan.reignadditionals.gamedata.PlayerTeleporterTracker;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class EntityEvents {
    @SubscribeEvent
    public void onPlayerLoggedIn(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityPlayer) {
            if (event.world.isRemote) {
                if (Minecraft.getMinecraft().thePlayer.getUniqueID().equals(((EntityPlayer)event.entity).getUniqueID()))
                    GuiTextOverlay.triggerPlayerJoin();
            }
            PlayerTeleporterTracker teleporterProps = PlayerTeleporterTracker.get((EntityPlayer)event.entity);
            if (teleporterProps.isDematerialized()) {
                ((EntityPlayer)event.entity).addPotionEffect(new PotionEffect(Potion.invisibility.getId(), Integer.MAX_VALUE, 0));
            }
        }
    }
    
    @SubscribeEvent
    public void onEntityConstructing(EntityConstructing event) {
        if (event.entity instanceof EntityPlayer && PlayerTeleporterTracker.get((EntityPlayer) event.entity) == null)
            PlayerTeleporterTracker.register((EntityPlayer) event.entity);
        
        if (event.entity instanceof EntityPlayer && event.entity.getExtendedProperties(PlayerTeleporterTracker.EXT_PROP_NAME) == null)
            event.entity.registerExtendedProperties(PlayerTeleporterTracker.EXT_PROP_NAME, new PlayerTeleporterTracker((EntityPlayer) event.entity));
    }
}
