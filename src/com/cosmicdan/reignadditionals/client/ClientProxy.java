package com.cosmicdan.reignadditionals.client;

import com.cosmicdan.reignadditionals.CommonProxy;
import com.cosmicdan.reignadditionals.client.gui.GuiGameOverlay;
import com.cosmicdan.reignadditionals.client.particles.*;
import com.cosmicdan.reignadditionals.client.renderers.ModRenderers;
import com.cosmicdan.reignadditionals.client.renderers.tileentities.ModTileEntityRenderers;

import cpw.mods.fml.common.event.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
    
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        // register custom renderers
        ModRenderers.init();
    }
    
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        
    }
    
    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        ModTileEntityRenderers.init();
        MinecraftForge.EVENT_BUS.register(new GuiGameOverlay(Minecraft.getMinecraft()));
    }
    
    
    @Override
    public void generateParticleSteamFizz(World world, int posX, int posY, int posZ) {
        double genX = posX + world.rand.nextFloat();
        double genY = posY + world.rand.nextFloat() * 0.5F + 1;
        //double genY = posY + 0.5F;
        double genZ = posZ + world.rand.nextFloat();
        Minecraft.getMinecraft().effectRenderer.addEffect(new EntitySteamFizzFX(world, genX, genY, genZ));
    }
}