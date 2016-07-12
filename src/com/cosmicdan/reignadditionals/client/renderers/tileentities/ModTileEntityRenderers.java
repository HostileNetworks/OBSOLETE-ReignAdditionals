package com.cosmicdan.reignadditionals.client.renderers.tileentities;

import com.cosmicdan.reignadditionals.blocks.tileentities.TileEntityCampfire;

import cpw.mods.fml.client.registry.ClientRegistry;

public class ModTileEntityRenderers {
    public static void init() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCampfire.class, new TileEntityCampfireRenderer());
    }
}
