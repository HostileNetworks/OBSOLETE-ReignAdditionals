package com.cosmicdan.reignadditionals.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.cosmicdan.reignadditionals.Main;
import com.cosmicdan.reignadditionals.util.TextUtils;

import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

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
        ModItems.getTooltip(getUnlocalizedName(), tooltip);
    }
    
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int posX, int posY, int posZ, int side, float hitX, float hitY, float hitZ) {
        Block targetBlock = world.getBlock(posX, posY, posZ);
        if (targetBlock instanceof IShearable) {
            if (((IShearable)targetBlock).isShearable(itemStack, world, posX, posY, posZ)) {
                if (world.isRemote) {
                    world.playSound(posX, posY, posZ, "mob.sheep.shear", 1.0f, 1.0f, true);
                } else {
                    ArrayList<ItemStack> drops = ((IShearable)targetBlock).onSheared(itemStack, entityPlayer.worldObj, posX, posY, posZ, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemStack));
                    Random rand = new Random();
    
                    if (rand.nextBoolean()) {
                        for(ItemStack stack : drops) {
                            float f = 0.7F;
                            double d  = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                            double d1 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                            double d2 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                            EntityItem entityitem = new EntityItem(world, (double)posX + d, (double)posY + d1, (double)posZ + d2, stack);
                            entityitem.delayBeforeCanPickup = 10;
                            world.spawnEntityInWorld(entityitem);
                        }
                    }
    
                    world.setBlockToAir(posX, posY, posZ);
                    itemStack.damageItem(4, entityPlayer);
                    return true;
                }
            }
        }
        return false;
    }
}
