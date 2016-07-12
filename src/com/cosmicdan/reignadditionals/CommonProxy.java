package com.cosmicdan.reignadditionals;

import com.cosmicdan.reignadditionals.blocks.ModBlocks;
import com.cosmicdan.reignadditionals.blocks.tileentities.ModTileEntities;
import com.cosmicdan.reignadditionals.events.BlockEvents;
import com.cosmicdan.reignadditionals.events.PlayerEvents;
import com.cosmicdan.reignadditionals.items.ModItems;

import cpw.mods.fml.common.event.*;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {
    
    
    public void preInit(FMLPreInitializationEvent event) {
        ModBlocks.init();
        ModItems.init();
        ModTileEntities.init();
    }
    
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new BlockEvents());
        MinecraftForge.EVENT_BUS.register(new PlayerEvents());
    }
    
    public void postInit(FMLPostInitializationEvent event) {
        
    }
    
    public void generateParticleSteamFizz(World world, int posX, int posY, int posZ) {}
}