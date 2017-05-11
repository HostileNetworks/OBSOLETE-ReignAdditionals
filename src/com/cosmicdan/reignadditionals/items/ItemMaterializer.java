package com.cosmicdan.reignadditionals.items;

import java.util.List;

import com.cosmicdan.reignadditionals.Main;
import com.cosmicdan.reignadditionals.gamedata.PlayerTeleporterTracker;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemMaterializer extends Item {
    String unlocalizedName;

    protected ItemMaterializer(String unlocalizedName) {
        this.unlocalizedName = unlocalizedName;
        this.setUnlocalizedName(unlocalizedName);
        this.setTextureName(Main.MODID + ":" + unlocalizedName);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List tooltip, boolean bool) {
        ModItems.getTooltip(getUnlocalizedName(), tooltip);
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        if (world.isRemote)
            return itemStack;
        
        PlayerTeleporterTracker teleporterProps = PlayerTeleporterTracker.get(entityPlayer);
        if (teleporterProps.isDematerialized()) {
            // make sure the player actually has the teleporter in inventory before doing anything
            int itemTeleporterSlot = -1;
            for (int i = 0; i < entityPlayer.inventory.mainInventory.length; i++) {
                ItemStack item = entityPlayer.inventory.mainInventory[i];
                if (item != null)
                    if (item.getItem() == ModItems.TELEPORTER)
                        itemTeleporterSlot = i;
            }
            
            if (itemTeleporterSlot >= 0) {
                teleporterProps.rematerialize();
                itemStack.stackSize--;
                entityPlayer.clearActivePotions();
                entityPlayer.inventory.setInventorySlotContents(itemTeleporterSlot, null);
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
                entityPlayer.getFoodStats().addStats(20, 10);
            }
        }
        
        return itemStack;
    }
}
