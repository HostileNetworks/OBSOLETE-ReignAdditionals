package com.cosmicdan.reignadditionals.blocks.tileentities;

import cpw.mods.fml.common.registry.GameRegistry;

public final class ModTileEntities {
    public static void init() {
        GameRegistry.registerTileEntity(TileEntityCampfire.class, "campfire");
    }
}
