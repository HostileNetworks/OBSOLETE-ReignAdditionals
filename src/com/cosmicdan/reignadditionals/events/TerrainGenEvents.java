package com.cosmicdan.reignadditionals.events;

import java.util.HashSet;
import java.util.Iterator;

import com.cosmicdan.reignadditionals.Main;
import com.cosmicdan.reignadditionals.ModConfig;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;

public class TerrainGenEvents {
    
    HashSet<Block> saplingsGrowthNerf = new HashSet<Block>();
    
    @SubscribeEvent
    public void onSaplingGrowTreeEvent(SaplingGrowTreeEvent event) {
        
        if (saplingsGrowthNerf.size() == 0) {
            buildSaplingsGrowthNerfSet();
        }
        
        if (saplingsGrowthNerf.contains(event.world.getBlock(event.x, event.y, event.z))) {
            if (ModConfig.RARE_SAPLING_GROWTH_RATE_NERF > 1) {
                if (event.rand.nextInt(ModConfig.RARE_SAPLING_GROWTH_RATE_NERF) != 0)
                    event.setResult(Result.DENY);
            }
        }
    }
    
    private void buildSaplingsGrowthNerfSet() {
        Main.LOGGER.info("Building saplingsGrowthNerf collection...");
        @SuppressWarnings("unchecked")
        Iterator<Block> iterator = Block.blockRegistry.iterator();
        while(iterator.hasNext()) {
            Block block = iterator.next();
            if (block instanceof BlockSapling) {
                String regName = Block.blockRegistry.getNameForObject(block);
                String modId = regName.split(":")[0]; 
                if (modId.equals("Natura")) {
                    saplingsGrowthNerf.add(block);
                    Main.LOGGER.info("    ...added block '" + regName + "' to saplingsGrowthNerf collection");
                }
            }
        }
    }
}
