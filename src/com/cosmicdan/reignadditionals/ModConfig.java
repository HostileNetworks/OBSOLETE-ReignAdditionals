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
    public static int REIGN_IDLE_TARGET_DESPAWN_SECS = 30;
    
    public static int DAYS_PER_MOON_PHASE = 6;
    public static int STARTING_YEAR = 1000;
    public static int FADE_MAINTEXT_AT = 50;
    public static int FADE_INFOS_AT = 240;
    public static int FADE_OUT_TIME = 80;
    public static String YEAR_SUFFIX = "AGC";
    
    public static int TELEPORT_MIN_DISTANCE = 300;
    public static int TELEPORT_SEARCH_WATERBIOME_RADIUS = 64;
    public static int TELEPORT_SEARCH_WATERBIOME_RETRYCOOLDOWN = 300;
    public static int TELEPORT_CLAIMEDCHUNK_BUFFER = 100;
    public static String TELEPORT_MESSAGE = "Teleporting...";
    public static int TELEPORT_SEGMENT_INCREMENT = 4;
    
    
    public static String CONFIG_PATH;
    
    private static Configuration CONFIG;
    
    // not set by config but re-calculated after config loads
    public static int daysPerMonth = DAYS_PER_MOON_PHASE * 8; // 8 moon phases per month (in Harder Wildlife) 
    public static int daysPerSeason = daysPerMonth * 2; // 2 months per season (in Harder Wildlife)
    public static int daysPerYear = daysPerSeason * 4; // 4 seasons per year
    
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
        
        Property REIGN_IDLE_TARGET_DESPAWN_TICKS_PROP = CONFIG.get("changes", "idleTargetDespawnSeconds", REIGN_IDLE_TARGET_DESPAWN_SECS);
        REIGN_IDLE_TARGET_DESPAWN_TICKS_PROP.comment = "If one of the Entities listed has had no target for this many seconds, it will instantly despawn. \n"
                                        + "Designed to work with ESM's xray mode. Note that this is VERY lazy. Applies to the following entities:\n"
                                        + "EntityZombie (only if they're holding an ItemBlock, their spawn item, or no item at all).";
        
        
        // gui stuff
        CONFIG.addCustomCategoryComment("gui", "GUI settings are all client-side. Some should match the pack/server for lore or gameplay reasons, but they don't technically have to.");
        Property DAYS_PER_MOON_PHASE_PROP = CONFIG.get("gui", "daysPerMoonPhase", DAYS_PER_MOON_PHASE);
        DAYS_PER_MOON_PHASE_PROP.comment = "How many days per moon-phase. Vanilla is 1. This MUST match the Harder Wildlife moonPhaseTime.";
        
        Property STARTING_YEAR_PROP = CONFIG.get("gui", "startingYear", STARTING_YEAR);
        STARTING_YEAR_PROP.comment = "Starting year. For display only. 1000 is default for the Reign modpack.";
        
        Property FADE_MAINTEXT_AT_PROP = CONFIG.get("gui", "fadeMaintextAt", FADE_MAINTEXT_AT);
        FADE_MAINTEXT_AT_PROP.comment = "How many render ticks (40 render ticks in a second?) until the big 'Day' and 'Year' text that appears on new days should start fading out. You can change this to whatever you prefer.";
        
        Property FADE_INFOS_AT_PROP = CONFIG.get("gui", "fadeInfosAt", FADE_INFOS_AT);
        FADE_INFOS_AT_PROP.comment = "How many render ticks (40 render ticks in a second?) until the next full moon/season info that appears on new days should start fading out. You can change this to whatever you prefer.";
        
        Property FADE_OUT_TIME_PROP = CONFIG.get("gui", "fadeOutTime", FADE_OUT_TIME);
        FADE_OUT_TIME_PROP.comment = "How many render ticks (40 render ticks in a second?)  the new day elements should fade out over.";
        
        Property YEAR_SUFFIX_PROP = CONFIG.get("gui", "yearSuffix", YEAR_SUFFIX);
        YEAR_SUFFIX_PROP.comment = "String to append at the end of the Year display. Purely cosmetic, default of AGC is a Reign Modpack lore thing (means 'After Great Cleansing').";
        
        
        // teleporter stuff
        CONFIG.addCustomCategoryComment("teleporter", "Settings related to the Teleporter Stone / Materializer starter items");
        
        Property TELEPORT_MIN_DISTANCE_PROP = CONFIG.get("teleporter", "teleportMinimumDistance", TELEPORT_MIN_DISTANCE);
        TELEPORT_MIN_DISTANCE_PROP.comment = "Minimum block distance between each teleportation attempt.";
        
        Property TELEPORT_SEARCH_WATERBIOME_RADIUS_PROP = CONFIG.get("teleporter", "teleportSearchWaterBiomeRadius", TELEPORT_SEARCH_WATERBIOME_RADIUS);
        TELEPORT_SEARCH_WATERBIOME_RADIUS_PROP.comment = "When a Plains-type biome is found, only teleport if there is a beach/ocean/river biome within this many blocks (radius) of that location.";
        
        Property TELEPORT_SEARCH_WATERBIOME_RETRYCOOLDOWN_PROP = CONFIG.get("teleporter", "teleportSearchWaterBiomeRetryCooldown", TELEPORT_SEARCH_WATERBIOME_RETRYCOOLDOWN);
        TELEPORT_SEARCH_WATERBIOME_RETRYCOOLDOWN_PROP.comment = "When the beach/ocean/river radius search fails, wait this many blocks before scanning again. Remember that the search pattern is an outwards spiral - so keep this number low to make best use of potential starting locations.";
        
        Property TELEPORT_MESSAGE_PROP = CONFIG.get("teleporter", "teleportMessage", TELEPORT_MESSAGE);
        TELEPORT_MESSAGE_PROP.comment = "Chat message to display when the teleport stone starts searching for a location.";
        
        Property TELEPORT_CLAIMEDCHUNK_BUFFER_PROP = CONFIG.get("teleporter", "teleportClaimedChunkBuffer", TELEPORT_CLAIMEDCHUNK_BUFFER);
        TELEPORT_CLAIMEDCHUNK_BUFFER_PROP.comment = "Minimim space/distance in chunks (i.e. a radius) to teleport the player from chunks that are already claimed by non-team players, OR that have a Headquarters";
        
        Property TELEPORT_SEGMENT_INCREMENT_PROP = CONFIG.get("teleporter", "teleportSegmentIncrement", TELEPORT_SEGMENT_INCREMENT);
        TELEPORT_SEGMENT_INCREMENT_PROP.comment = "The teleport searches in an outward spiral from origin, increasing each segement length (edge) every 2 'corners'. This value determines how much to increment the segment length every second corner. A value of 1 will be the tightest spiral, checking every block (which is not necessary and just harms performance). Don't set too high if you want to make use of all available space though.";
        
        
        // save config if it differs to the default values
        if(CONFIG.hasChanged())
            CONFIG.save();
        
        // Need to actually populate the values *after* saving config, otherwise first-run defaults are not persisted
        STICK_DROP_CHANCE = STICK_DROP_CHANCE_PROP.getInt(STICK_DROP_CHANCE);
        TREESAP_DROP_CHANCE = TREESAP_DROP_CHANCE_PROP.getInt(TREESAP_DROP_CHANCE);
        MIXING_ENABLED_COBBLE = MIXING_ENABLED_COBBLE_PROP.getBoolean(MIXING_ENABLED_COBBLE);
        MIXING_ENABLED_SMOOTHSTONE = MIXING_ENABLED_SMOOTHSTONE_PROP.getBoolean(MIXING_ENABLED_SMOOTHSTONE);
        ALLOW_BREAKING_WITHOUT_TOOL = ALLOW_BREAKING_WITHOUT_TOOL_PROP.getBoolean(ALLOW_BREAKING_WITHOUT_TOOL);
        REIGN_IDLE_TARGET_DESPAWN_SECS = REIGN_IDLE_TARGET_DESPAWN_TICKS_PROP.getInt(REIGN_IDLE_TARGET_DESPAWN_SECS);
        
        DAYS_PER_MOON_PHASE = DAYS_PER_MOON_PHASE_PROP.getInt(DAYS_PER_MOON_PHASE);
        STARTING_YEAR = STARTING_YEAR_PROP.getInt(STARTING_YEAR);
        FADE_MAINTEXT_AT = FADE_MAINTEXT_AT_PROP.getInt(FADE_MAINTEXT_AT);
        FADE_INFOS_AT = FADE_INFOS_AT_PROP.getInt(FADE_INFOS_AT);
        FADE_OUT_TIME = FADE_OUT_TIME_PROP.getInt(FADE_OUT_TIME);
        YEAR_SUFFIX = YEAR_SUFFIX_PROP.getString();
        
        TELEPORT_MIN_DISTANCE = TELEPORT_MIN_DISTANCE_PROP.getInt(TELEPORT_MIN_DISTANCE);
        TELEPORT_SEARCH_WATERBIOME_RADIUS = TELEPORT_SEARCH_WATERBIOME_RADIUS_PROP.getInt(TELEPORT_SEARCH_WATERBIOME_RADIUS);
        TELEPORT_SEARCH_WATERBIOME_RETRYCOOLDOWN = TELEPORT_SEARCH_WATERBIOME_RETRYCOOLDOWN_PROP.getInt(TELEPORT_SEARCH_WATERBIOME_RETRYCOOLDOWN);
        TELEPORT_MESSAGE = TELEPORT_MESSAGE_PROP.getString();
        TELEPORT_CLAIMEDCHUNK_BUFFER = TELEPORT_CLAIMEDCHUNK_BUFFER_PROP.getInt(TELEPORT_CLAIMEDCHUNK_BUFFER);
        TELEPORT_SEGMENT_INCREMENT = TELEPORT_SEGMENT_INCREMENT_PROP.getInt(TELEPORT_SEGMENT_INCREMENT);
        
        // reset some initial config-based values
        daysPerMonth = ModConfig.DAYS_PER_MOON_PHASE * 8;
        daysPerSeason = daysPerMonth * 2;
        daysPerYear = daysPerSeason * 4;
        
        // all done
        Main.LOGGER.info("Config loaded");
    }
}
