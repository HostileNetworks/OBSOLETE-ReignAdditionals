package com.cosmicdan.reignadditionals.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemCampfire extends ItemBlock {

    private String unlocalizedName;

    public ItemCampfire(Block parentBlock) {
        super(parentBlock);
        this.unlocalizedName = parentBlock.getUnlocalizedName().replace("tile.", "");
        this.setUnlocalizedName(unlocalizedName);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List tooltip, boolean bool) {
        ModItems.getTooltip(getUnlocalizedName(), tooltip);
    }
}
