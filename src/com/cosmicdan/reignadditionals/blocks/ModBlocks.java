package com.cosmicdan.reignadditionals.blocks;

import com.cosmicdan.reignadditionals.items.ItemCampfire;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

public class ModBlocks {
    public static Block CAMPFIRE;
    public static Block CAMPFIRE_LIT;
    
    public static final void init() {
        CAMPFIRE = new BlockCampfire();
        GameRegistry.registerBlock(CAMPFIRE, ItemCampfire.class, "campfire");
        CAMPFIRE_LIT = new BlockCampfireLit();
        GameRegistry.registerBlock(CAMPFIRE_LIT, ItemCampfire.class, "campfireLit");
    }
}
