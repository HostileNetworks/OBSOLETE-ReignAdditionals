package com.cosmicdan.reignadditionals.events;

import com.cosmicdan.reignadditionals.Main;
import com.cosmicdan.reignadditionals.ModConfig;

import net.minecraft.world.World;

public class LiquidMixingEvents {
    public static boolean cobbleMix() {
        return ModConfig.MIXING_ENABLED_COBBLE;
    }
    
    public static boolean smoothstoneMix() {
        return ModConfig.MIXING_ENABLED_SMOOTHSTONE;
    }
    
    public static void hotAndColdLiquidContact(World world, int posX, int posY, int posZ) {
        if (!world.isAirBlock(posX, posY + 1, posZ))
            return;
        for (int i = 0; i < 16; i++) {
            Main.PROXY.generateParticleSteamFizz(world, posX, posY, posZ);
        }
    }
}
