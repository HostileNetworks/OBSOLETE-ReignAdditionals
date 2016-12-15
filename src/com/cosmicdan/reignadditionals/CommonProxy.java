package com.cosmicdan.reignadditionals;

import com.cosmicdan.reignadditionals.blocks.ModBlocks;
import com.cosmicdan.reignadditionals.blocks.tileentities.ModTileEntities;
import com.cosmicdan.reignadditionals.events.BlockEvents;
import com.cosmicdan.reignadditionals.events.EntityEvents;
import com.cosmicdan.reignadditionals.events.PlayerEvents;
import com.cosmicdan.reignadditionals.interop.ImmersiveEngineeringAddons;
import com.cosmicdan.reignadditionals.items.ModItems;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.*;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {
    
    
    public void preInit(FMLPreInitializationEvent event) {
        ModBlocks.init();
        ModItems.init();
        ModTileEntities.init();
        FMLInterModComms.sendMessage("Waila", "register", "com.cosmicdan.reignadditionals.waila.WailaDataProvider.init");
    }
    
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new BlockEvents());
        MinecraftForge.EVENT_BUS.register(new PlayerEvents());
        MinecraftForge.EVENT_BUS.register(new EntityEvents());
        if ((Loader.isModLoaded("ImmersiveEngineering")) && Loader.isModLoaded("TConstruct")) {
            Main.LOGGER.info("Immersive Engineering and TConstruct detected. Adding Metal Presser Recipes for all Tinker's Construct things...");
            ImmersiveEngineeringAddons.load();
        } else {
            Main.LOGGER.info("Immersive Engineering and/or TConstruct not detected, skipping Metal Presser Recipes.");
        }
    }
    
    public void postInit(FMLPostInitializationEvent event) {
        
    }
    
    public void generateParticleSteamFizz(World world, int posX, int posY, int posZ) {}
}