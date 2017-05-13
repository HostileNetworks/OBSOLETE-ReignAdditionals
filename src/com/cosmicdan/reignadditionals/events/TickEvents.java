package com.cosmicdan.reignadditionals.events;

import com.cosmicdan.reignadditionals.items.ModItems;
import com.cosmicdan.reignadditionals.server.GuiInfoPacket;
import com.cosmicdan.reignadditionals.server.PacketHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.npc.gamedata.HeadquartersTracker;

public class TickEvents {
    private static int TICKER_MAX = 100;
    private static int TICKER = 0;
    private static int FLUX_MAX_STACK_SIZE = 0;
    
    @SubscribeEvent
    public void onServerPlayerTick(PlayerTickEvent event) {
        if (event.side == Side.CLIENT)
            return;

        TICKER++;
        if (TICKER == TICKER_MAX) {
            TICKER = 0;
            
            // assign set-once vars
            if (FLUX_MAX_STACK_SIZE == 0)
                FLUX_MAX_STACK_SIZE = new ItemStack(ModItems.CRYSTALIZED_FLUX).getMaxStackSize();
            
            int fluxTotal = -1;
            int fluxSpace = -1;
            int[] hqPos = HeadquartersTracker.get(event.player.worldObj).getHqPos(event.player.getCommandSenderName(), event.player.worldObj);
            if (hqPos != null) {
                IInventory hq = (IInventory) event.player.worldObj.getTileEntity(hqPos[0], hqPos[1], hqPos[2]);
                fluxTotal = 0;
                fluxSpace = hq.getSizeInventory() * FLUX_MAX_STACK_SIZE;
                for (int slot = hq.getSizeInventory() - 1; slot >= 0; slot--) {
                    ItemStack stackThisSlot = hq.getStackInSlot(slot);
                    if (stackThisSlot == null)
                        continue;
                    if (stackThisSlot.getItem() == ModItems.CRYSTALIZED_FLUX) {
                        fluxTotal += stackThisSlot.stackSize;
                        fluxSpace -= stackThisSlot.stackSize;
                    } else {
                        fluxSpace -= FLUX_MAX_STACK_SIZE;
                    }
                }
            }
            boolean isNearlyFull = fluxSpace != -1 && fluxSpace < 50;
            PacketHandler.packetReq.sendTo(new GuiInfoPacket(fluxTotal, isNearlyFull), (EntityPlayerMP) event.player);
        }
    }
}
