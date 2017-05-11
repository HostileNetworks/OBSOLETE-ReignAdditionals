package com.cosmicdan.reignadditionals;

import java.util.ArrayList;

import com.cosmicdan.reignadditionals.blocks.ModBlocks;
import com.cosmicdan.reignadditionals.blocks.tileentities.ModTileEntities;
import com.cosmicdan.reignadditionals.core.CorePlugin;
import com.cosmicdan.reignadditionals.events.BlockEvents;
import com.cosmicdan.reignadditionals.events.EntityEvents;
import com.cosmicdan.reignadditionals.events.PlayerEvents;
import com.cosmicdan.reignadditionals.events.TerrainGenEvents;
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
        MinecraftForge.TERRAIN_GEN_BUS.register(new TerrainGenEvents());
        if ((Loader.isModLoaded("ImmersiveEngineering")) && Loader.isModLoaded("TConstruct")) {
            Main.LOGGER.info("Immersive Engineering and TConstruct detected. Adding Metal Presser Recipes for all Tinker's Construct things...");
            ImmersiveEngineeringAddons.load();
        } else {
            Main.LOGGER.info("Immersive Engineering and/or TConstruct not detected, skipping Metal Presser Recipes.");
        }
    }
    
    public void postInit(FMLPostInitializationEvent event) {
        if (!CorePlugin.isDevEnv()) {
            // Disable ElectriCraft and ReactorCraft native oregen (so another mod can implement it)
            try {
                Object electriOregenInstance = Class.forName("Reika.ElectriCraft.World.ElectriOreGenerator").getDeclaredField("instance").get(null);
                ArrayList<? extends Object> electriGenerator = (ArrayList<? extends Object>) electriOregenInstance.getClass().getField("generators").get(electriOregenInstance);
                electriGenerator.clear();
                Main.LOGGER.warn("ElectriCraft native oregen disabled");
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalStateException("ElectriCraft NOT detected or has changed internally. Cannot guarantee it's native oregen has been disabled so I've crashed Minecraft intentionally to avoid world corruption.");
            }
            try {
                Object reactorOregenInstance = Class.forName("Reika.ReactorCraft.World.ReactorOreGenerator").getDeclaredField("instance").get(null);
                ArrayList<? extends Object> reactorGenerator = (ArrayList<? extends Object>) reactorOregenInstance.getClass().getField("generators").get(reactorOregenInstance);
                reactorGenerator.clear();
                Main.LOGGER.warn("ReactorCraft native oregen disabled");
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalStateException("ReactorCraft NOT detected or has changed internally. Cannot guarantee it's native oregen has been disabled so I've crashed Minecraft intentionally to avoid world corruption.");
            }
        }
    }
    
    public void generateParticleSteamFizz(World world, int posX, int posY, int posZ) {}
}