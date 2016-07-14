package com.cosmicdan.reignadditionals;

import com.cosmicdan.reignadditionals.blocks.ModBlocks;
import com.cosmicdan.reignadditionals.events.BlockEvents;
import com.cosmicdan.reignadditionals.events.PlayerEvents;
import com.cosmicdan.reignadditionals.items.ModItems;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(
    modid = Main.MODID, 
    name = Main.MODNAME,
    version = "${version}",
    dependencies = "required-after:Forge@[10.13,);required-after:Waila"
)

public class Main {
    public static final String MODID = "reignadditionals";
    public static final String MODNAME = "ReignAdditionals";
    private static final String PROXY_CLIENT = "com.cosmicdan.reignadditionals.client.ClientProxy";
    private static final String PROXY_COMMON = "com.cosmicdan.reignadditionals.CommonProxy";
    
    @Instance(MODNAME)
    public static Main INSTANCE;
    
    @SidedProxy(clientSide=PROXY_CLIENT, serverSide=PROXY_COMMON)
    public static CommonProxy PROXY;
    
    public static Logger LOGGER = LogManager.getLogger(Main.MODNAME);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Main.LOGGER = event.getModLog();
        ModConfig.doConfig(event.getModConfigurationDirectory());
        PROXY.preInit(event);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        PROXY.init(event);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        PROXY.postInit(event);
    }
}
