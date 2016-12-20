package com.cosmicdan.reignadditionals.events;

import com.cosmicdan.reignadditionals.client.gui.GuiTextOverlay;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class EntityEvents {
    @SubscribeEvent
    public void onPlayerLoggedIn(EntityJoinWorldEvent event) {
        if ((event.world.isRemote) && (event.entity instanceof EntityPlayer)) {
            if (Minecraft.getMinecraft().thePlayer.getUniqueID().equals(((EntityPlayer)event.entity).getUniqueID()))
                GuiTextOverlay.triggerPlayerJoin();
        }
    }
}
