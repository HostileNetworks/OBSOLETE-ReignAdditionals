package com.cosmicdan.reignadditionals.items;

import java.util.List;

import com.cosmicdan.reignadditionals.Main;
import com.cosmicdan.reignadditionals.util.TextUtils;

import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;

public class ItemFlintAxe extends ItemAxe {
    
    String unlocalizedName;

    protected ItemFlintAxe(ToolMaterial material, String unlocalizedName) {
        super(material);
        this.unlocalizedName = unlocalizedName;
        this.setUnlocalizedName(unlocalizedName);
        this.setTextureName(Main.MODID + ":" + unlocalizedName);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List tooltip, boolean bool) {
        String tooltipText = LanguageRegistry.instance().getStringLocalization("item." + unlocalizedName + ".tooltip");
        for (String line : TextUtils.splitTextString(tooltipText, 32)) {
            tooltip.add(line);
        }
        
        
                
                
        
        
        
    }
}
