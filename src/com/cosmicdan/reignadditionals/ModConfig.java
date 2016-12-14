package com.cosmicdan.reignadditionals;

import java.io.File;
import java.io.IOException;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ModConfig {
    public static int STICK_DROP_CHANCE = 2;
    public static int TREESAP_DROP_CHANCE = 7;
    public static boolean MIXING_ENABLED_COBBLE = false;
    public static boolean MIXING_ENABLED_SMOOTHSTONE = false;
    public static boolean ALLOW_BREAKING_WITHOUT_TOOL = false;
    
    public static int DAYS_PER_MOON_PHASE = 6;
    public static int STARTING_YEAR = 1000;
    public static int FADE_MAINTEXT_AT = 50;
    public static int FADE_INFOS_AT = 240;
    public static int FADE_OUT_TIME = 80;
    public static String YEAR_SUFFIX = "AGC";
    
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
        
        Property TREESAP_DROP_CHANCE_PROP = CONFIG.get("additions", "treeSapDropChance", TREESAP_DROP_CHANCE);
        TREESAP_DROP_CHANCE_PROP.comment = "Set the 1 in X (this many) chance that treesap is added to \n"
                                       + "the drop list of spruce log harvests.";

        // game removals
        Property MIXING_ENABLED_COBBLE_PROP = CONFIG.get("removals", "enabledMixingForCobble", MIXING_ENABLED_COBBLE);
        MIXING_ENABLED_COBBLE_PROP.comment = "Allow cobblestone generating when water spread makes contact with lava?";
        
        Property MIXING_ENABLED_SMOOTHSTONE_PROP = CONFIG.get("removals", "enabledMixingForSmoothstone", MIXING_ENABLED_SMOOTHSTONE);
        MIXING_ENABLED_SMOOTHSTONE_PROP.comment = "Allow smoothstone generating when lava spread makes contact with water?";
        
        Property ALLOW_BREAKING_WITHOUT_TOOL_PROP = CONFIG.get("removals", "enabledBreakingBlocksWithoutTool", ALLOW_BREAKING_WITHOUT_TOOL);
        ALLOW_BREAKING_WITHOUT_TOOL_PROP.comment = "Allow breaking some blocks (stone, wood) without their right tool class (pickaxe, axe)?";
        
        
        // gui stuff
        CONFIG.addCustomCategoryComment("gui", "GUI settings are all client-side. Some should match the pack/server for lore or gameplay reasons, but they don't technically have to.");
        Property DAYS_PER_MOON_PHASE_PROP = CONFIG.get("gui", "daysPerMoonPhase", DAYS_PER_MOON_PHASE);
        DAYS_PER_MOON_PHASE_PROP.comment = "How many days per moon-phase. Default is vanilla. This MUST match the Harder Wildlife moonPhaseTime.";
        
        Property STARTING_YEAR_PROP = CONFIG.get("gui", "startingYear", STARTING_YEAR);
        STARTING_YEAR_PROP.comment = "Starting year. For display only. 1000 is default for the Reign modpack.";
        
        Property FADE_MAINTEXT_AT_PROP = CONFIG.get("gui", "fadeMaintextAt", FADE_MAINTEXT_AT);
        FADE_MAINTEXT_AT_PROP.comment = "How many ticks (20 ticks in a second) until the big 'Day' and 'Year' text that appears on new days should start fading out. You can change this to whatever you prefer.";
        
        Property FADE_INFOS_AT_PROP = CONFIG.get("gui", "fadeInfosAt", FADE_INFOS_AT);
        FADE_INFOS_AT_PROP.comment = "How many ticks (20 ticks in a second) until the next full moon/season info that appears on new days should start fading out. You can change this to whatever you prefer.";
        
        Property FADE_OUT_TIME_PROP = CONFIG.get("gui", "fadeOutTime", FADE_OUT_TIME);
        FADE_OUT_TIME_PROP.comment = "How many ticks (20 ticks in a second) the new day elements should fade out over.";
        
        Property YEAR_SUFFIX_PROP = CONFIG.get("gui", "yearSuffix", YEAR_SUFFIX);
        YEAR_SUFFIX_PROP.comment = "String to append at the end of the Year display. Purely cosmetic, default of AGC is a Reign Modpack lore thing (means 'After Great Cleansing').";        
        
        // save config if it differs to the default values
        if(CONFIG.hasChanged())
            CONFIG.save();
        
        // Need to actually populate the values *after* saving config, otherwise first-run defaults are not persisted
        STICK_DROP_CHANCE = STICK_DROP_CHANCE_PROP.getInt(STICK_DROP_CHANCE);
        TREESAP_DROP_CHANCE = TREESAP_DROP_CHANCE_PROP.getInt(TREESAP_DROP_CHANCE);
        MIXING_ENABLED_COBBLE = MIXING_ENABLED_COBBLE_PROP.getBoolean(MIXING_ENABLED_COBBLE);
        MIXING_ENABLED_SMOOTHSTONE = MIXING_ENABLED_SMOOTHSTONE_PROP.getBoolean(MIXING_ENABLED_SMOOTHSTONE);
        ALLOW_BREAKING_WITHOUT_TOOL = ALLOW_BREAKING_WITHOUT_TOOL_PROP.getBoolean(ALLOW_BREAKING_WITHOUT_TOOL);
        
        // all done
        Main.LOGGER.info("Config loaded");
    }
}
