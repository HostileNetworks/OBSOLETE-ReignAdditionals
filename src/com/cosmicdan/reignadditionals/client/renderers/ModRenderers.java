package com.cosmicdan.reignadditionals.client.renderers;

import cpw.mods.fml.client.registry.RenderingRegistry;

public class ModRenderers {
    public static int CAMPFIRE;
    
    public static void init() {
        RenderCampfire renderCampfire = new RenderCampfire();
        CAMPFIRE = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(CAMPFIRE, renderCampfire);
    }
}
