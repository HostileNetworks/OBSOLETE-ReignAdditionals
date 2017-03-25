package com.cosmicdan.reignadditionals.util;

import java.lang.reflect.Array;
import java.util.HashSet;

import com.cosmicdan.reignadditionals.Main;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

/**
 * Wrapper class used for the break/interact whitelists.
 * It's better to use block instances instead of parsing the String
 * names to blocks every time (this is better for performance).
 * @author CosmicDan
 *
 */
public class BlockAndMeta {
    public final Block block;
    public final int meta;
    
    public BlockAndMeta(Block block, int meta) {
        this.block = block;
        this.meta = meta;
    }
    
    public static BlockAndMeta[] buildList(String listName, String[] blockListRaw) {
        HashSet<BlockAndMeta> blockList = new HashSet<BlockAndMeta>();

        Main.LOGGER.info("Building " + listName + "...");
        
        for (String blockName : blockListRaw) {
            blockName = blockName.trim();
            if (!blockName.equals("")) {
                String[] blockId = blockName.split(":");
                if (Array.getLength(blockId) != 2 && Array.getLength(blockId) != 3 ) {
                    Main.LOGGER.warn(" - Invalid block (bad length of " + Array.getLength(blockId) + "): " + blockName);
                    continue;
                }
                if (blockId[0] == null || blockId[1] == null) {
                    Main.LOGGER.warn(" - Invalid block (parse/format error): " + blockName);
                    continue;
                }
                Block block = GameRegistry.findBlock(blockId[0], blockId[1]);
                if (block == null) {
                    Main.LOGGER.warn(" - Skipping missing block: " + blockName);
                    continue;
                }
                int meta = -1;
                if (Array.getLength(blockId) == 3) {
                    try {
                        meta = Integer.parseInt(blockId[2]);
                        if (meta < 0 || meta > 15)
                            throw new NumberFormatException();
                    } catch (NumberFormatException e) {
                        Main.LOGGER.warn(" - Meta value invalid : '" + blockId[2] + "', must be a number between 0 and 15");
                        continue;
                    }
                }
                blockList.add(new BlockAndMeta(block, meta));
            }
        }

        Main.LOGGER.info("...added " + blockList.size() + " blocks to " + listName);
        
        
        return blockList.toArray(new BlockAndMeta[blockList.size()]);
    }
}
