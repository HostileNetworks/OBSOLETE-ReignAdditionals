package com.cosmicdan.reignadditionals.items;

import java.util.List;

import com.cosmicdan.reignadditionals.Main;
import com.cosmicdan.reignadditionals.util.TextUtils;

import cpw.mods.fml.common.registry.LanguageRegistry;
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
        String tooltipText = LanguageRegistry.instance().getStringLocalization("tile." + unlocalizedName + ".tooltip");
        for (String line : TextUtils.splitTextString(tooltipText, 32)) {
            tooltip.add(line);
        }
    }
}