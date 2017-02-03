package com.cosmicdan.reignadditionals.items;

import java.util.List;

import com.cosmicdan.reignadditionals.Main;
import com.cosmicdan.reignadditionals.util.TextUtils;

import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemMilkBottle extends ItemBucketMilk {
    String unlocalizedName;

    protected ItemMilkBottle(String unlocalizedName) {
        this.unlocalizedName = unlocalizedName;
        this.setUnlocalizedName(unlocalizedName);
        this.setTextureName(Main.MODID + ":" + unlocalizedName);
    }
    
    @Override
    public ItemStack onEaten(ItemStack itemStack, World world, EntityPlayer entityPlayer)
    {
        if (!world.isRemote) {
            entityPlayer.curePotionEffects(new ItemStack(Items.milk_bucket));
        }
        
        if (!entityPlayer.capabilities.isCreativeMode) {
            --itemStack.stackSize;
            return new ItemStack(Items.glass_bottle);
        }
        
        return itemStack;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List tooltip, boolean bool) {
        ModItems.getTooltip(getUnlocalizedName(), tooltip);
    }
}
