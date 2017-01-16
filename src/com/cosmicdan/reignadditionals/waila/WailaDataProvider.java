package com.cosmicdan.reignadditionals.waila;

import java.util.List;

import com.cosmicdan.reignadditionals.blocks.BlockCampfireLit;
import com.cosmicdan.reignadditionals.util.TextUtils;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class WailaDataProvider implements IWailaDataProvider {

    public static void init(IWailaRegistrar registrar) {
        registrar.registerBodyProvider(new WailaDataProvider(), BlockCampfireLit.class);
        
    }
    
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int posX, int posY, int posZ) {
        return tag;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getBlock() instanceof BlockCampfireLit) {
            int meta = accessor.getMetadata();
            if (meta > 6)
                meta = meta - 7;
            currenttip.add(meta + " " + TextUtils.translate("waila.campfirelit.body"));
            
        }
        return currenttip;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return new ItemStack(accessor.getBlock());
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

}
