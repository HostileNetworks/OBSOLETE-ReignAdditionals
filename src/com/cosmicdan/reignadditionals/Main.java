package com.cosmicdan.reignadditionals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(
    modid = Main.MODID, 
    name = Main.MODNAME,
    version = "${version}",
    dependencies = "required-after:Forge@[10.13,);required-after:Waila;required-after:AncientWarfare;after:ImmersiveEngineering;after:TConstruct"
)

public class Main {
    public static final String MODID = "reignadditionals";
    public static final String MODNAME = "ReignAdditionals";
    private static final String PROXY_CLIENT = "com.cosmicdan.reignadditionals.client.ClientProxy";
    private static final String PROXY_COMMON = "com.cosmicdan.reignadditionals.CommonProxy";
    
    public static boolean IS_CLIENT = true;
    
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
    
    @EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        if (Main.IS_CLIENT) {
            Display.setTitle("Minecraft: Reign Modpack " + ModConfig.TITLE_SUFFIX);
        }
    }
}
