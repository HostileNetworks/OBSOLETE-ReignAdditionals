package com.cosmicdan.reignadditionals;

import java.io.File;
import java.io.IOException;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ModConfig {
    public static int STICK_DROP_CHANCE = 2;
    public static boolean MIXING_ENABLED_COBBLE = false;
    public static boolean MIXING_ENABLED_SMOOTHSTONE = false;
    public static boolean ALLOW_BREAKING_WITHOUT_TOOL = false;
    
    public static String CONFIG_PATH;
    
    private static Configuration CONFIG;
    
    public static void doConfig(File configPath) {
        // ensure the config directory exists
        try {
            CONFIG_PATH = configPath.getCanonicalPath() + "/" + Main.MODNAME;
            configPath = new File(CONFIG_PATH);
            if (!configPath.exists())
                configPath.mkdirs();
        } catch (IOException e) {
            Main.LOGGER.error("An error occured while getting/setting configuration: " + e.getMessage());
        }
        
        // ensure the config file exists, load it if so
        CONFIG = new Configuration(new File(configPath, (Main.MODNAME + ".cfg")));
        CONFIG.load();
        
        // game additions
        Property STICK_DROP_CHANCE_PROP = CONFIG.get("additions", "stickDropChance", STICK_DROP_CHANCE);
        STICK_DROP_CHANCE_PROP.comment = "Set the 1 in X (this many) chance that a stick is added to \n"
                                       + "the drop list of hand-harvested leaves.";

        // game removals
        Property MIXING_ENABLED_COBBLE_PROP = CONFIG.get("removals", "enabledMixingForCobble", MIXING_ENABLED_COBBLE);
        MIXING_ENABLED_COBBLE_PROP.comment = "Allow cobblestone generating when water spread makes contact with lava?";
        
        Property MIXING_ENABLED_SMOOTHSTONE_PROP = CONFIG.get("removals", "enabledMixingForSmoothstone", MIXING_ENABLED_SMOOTHSTONE);
        MIXING_ENABLED_SMOOTHSTONE_PROP.comment = "Allow smoothstone generating when lava spread makes contact with water?";
        
        Property ALLOW_BREAKING_WITHOUT_TOOL_PROP = CONFIG.get("removals", "enabledBreakingBlocksWithoutTool", ALLOW_BREAKING_WITHOUT_TOOL);
        ALLOW_BREAKING_WITHOUT_TOOL_PROP.comment = "Allow breaking some blocks (stone, wood) without their right tool class (pickaxe, axe)?";
        
        // save config if it differs to the default values
        if(CONFIG.hasChanged())
            CONFIG.save();
        
        // Need to actually populate the values *after* saving config, otherwise first-run defaults are not persisted
        STICK_DROP_CHANCE = STICK_DROP_CHANCE_PROP.getInt(STICK_DROP_CHANCE);
        MIXING_ENABLED_COBBLE = MIXING_ENABLED_COBBLE_PROP.getBoolean(MIXING_ENABLED_COBBLE);
        MIXING_ENABLED_SMOOTHSTONE = MIXING_ENABLED_SMOOTHSTONE_PROP.getBoolean(MIXING_ENABLED_SMOOTHSTONE);
        ALLOW_BREAKING_WITHOUT_TOOL = ALLOW_BREAKING_WITHOUT_TOOL_PROP.getBoolean(ALLOW_BREAKING_WITHOUT_TOOL);
        
        // all done
        Main.LOGGER.info("Config loaded");
    }
}
