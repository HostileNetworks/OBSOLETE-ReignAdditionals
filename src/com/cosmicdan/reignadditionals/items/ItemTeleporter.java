package com.cosmicdan.reignadditionals.items;

import java.util.ArrayList;
import java.util.List;

import com.cosmicdan.reignadditionals.Main;
import com.cosmicdan.reignadditionals.ModConfig;
import com.cosmicdan.reignadditionals.gamedata.PlayerTeleporterTracker;
import com.cosmicdan.reignadditionals.util.TextUtils;

import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.shadowmage.ancientwarfare.core.interop.InteropFtbuChunkData;
import net.shadowmage.ancientwarfare.core.interop.InteropFtbuChunkData.TownHallOwner;

public class ItemTeleporter extends Item {
    private String unlocalizedName;

    protected ItemTeleporter(String unlocalizedName) {
        this.unlocalizedName = unlocalizedName;
        this.setUnlocalizedName(unlocalizedName);
        this.setTextureName(Main.MODID + ":" + unlocalizedName);
    }
    
    @Override
    public ItemStack onItemRightClick(final ItemStack itemStack, final World world, final EntityPlayer entityPlayer) {
        if (world.isRemote)
            return itemStack;
        
        final PlayerTeleporterTracker teleporterProps = PlayerTeleporterTracker.get(entityPlayer);
        
        if (teleporterProps.isTeleporting())
            return itemStack;
        else
            teleporterProps.setTeleporting(true);
        
        Thread thread = new Thread(){
            public void run(){
                //System.out.println("Teleporting...");
                entityPlayer.getFoodStats().addStats(20, 10);
                
                entityPlayer.addChatMessage(new ChatComponentText(ModConfig.TELEPORT_MESSAGE));
                
                // set some default/initial values in case this is a fresh teleport
                int posX = (int) entityPlayer.posX;
                int posZ = (int) entityPlayer.posZ;
                int vectorX = 1;
                int vectorZ = 0;
                int segmentLength = 1;
                int segmentPassed = 0;
                List<Integer> prevPosX = new ArrayList<Integer>();
                List<Integer> prevPosZ = new ArrayList<Integer>();
                
                // get existing teleport data if it exists
                if (teleporterProps.hasTeleportData()) {
                    posX = teleporterProps.getLastTeleportPosX();
                    posZ = teleporterProps.getLastTeleportPosZ();
                    vectorX = teleporterProps.getLastTeleportVectorX();
                    vectorZ = teleporterProps.getLastTeleportVectorZ();
                    segmentLength = teleporterProps.getLastTeleportSegmentLength();
                    segmentPassed = teleporterProps.getLastTeleportSegmentPassed();
                    prevPosX = teleporterProps.getPrevPosX();
                    prevPosZ = teleporterProps.getPrevPosZ();
                }
                
                entityPlayer.addPotionEffect(new PotionEffect(Potion.invisibility.getId(), Integer.MAX_VALUE, 0));
                boolean foundBiome = false;
                
                int searchCooldown = ModConfig.TELEPORT_MIN_DISTANCE;
                int innerSegmentLengthMax = ModConfig.TELEPORT_SEARCH_WATERBIOME_RADIUS * 2 + 1;
                
                //System.out.println("Starting search at " + posX + "x" + posZ);
                
                
                while (true) {
                    // NB: On my imaginary grid, I consider "right" as X+ and "down" as Z+
                    if (searchCooldown > 0)
                        searchCooldown--;
                    
                    if (searchCooldown == 0 && isBiomeTypeAtPos(world, posX, posZ, BiomeDictionary.Type.PLAINS)) {
                        
                        searchCooldown = ModConfig.TELEPORT_MIN_DISTANCE;
                        boolean isTooClose = false;
                        
                        // Make sure this new position is further than searchMinDistance to the last teleport position
                        for (int i = 0; i < prevPosX.size(); i++) {
                            if (Math.hypot(posX-prevPosX.get(i), posZ-prevPosZ.get(i)) < ModConfig.TELEPORT_MIN_DISTANCE)
                                isTooClose = true;
                        }
                        
                        // Make sure that there are no non-allied player claims nearby
                        int chunkScanVectorX = 1;
                        int chunkScanVectorZ = 0;
                        int chunkX = posX / 16; // NB: divisions by powers of 2 are OK
                        int chunkZ = posZ / 16;
                        int chunkScanSegmentLength = 1;
                        int chunkScanSegmentPassed = 0;
                        int chunkScanSegmentLengthMax = ModConfig.TELEPORT_CLAIMEDCHUNK_BUFFER * 2 + 1; // 30 is the "radius", offload to config
                        
                        while (true) {
                            List<TownHallOwner> claimStakes = InteropFtbuChunkData.INSTANCE.chunkClaims.get(new InteropFtbuChunkData.ChunkLocation(chunkX, chunkZ, world.provider.dimensionId));
                            if (claimStakes != null) {
                                ScorePlayerTeam claimTeam = world.getScoreboard().getPlayersTeam(claimStakes.get(0).getOwnerName());
                                if (claimTeam == null || entityPlayer.getTeam() == null || !claimTeam.isSameTeam(entityPlayer.getTeam())) {
                                    //System.out.println("Found a non-allied claim at chunk " + chunkX + "x" + chunkZ);
                                    isTooClose = true;
                                    break;
                                }
                            }
                            // step forward
                            chunkX += chunkScanVectorX;
                            chunkZ += chunkScanVectorZ;
                            chunkScanSegmentPassed++;
                            if (chunkScanSegmentPassed == chunkScanSegmentLength) {
                                // segment done
                                chunkScanSegmentPassed = 0;
                                
                                // rotate the vector
                                int tmp = chunkScanVectorX;
                                chunkScanVectorX = -chunkScanVectorZ;
                                chunkScanVectorZ = tmp;
                                
                                // bump the segment length if necessary (every second turn)
                                if (chunkScanVectorZ == 0)
                                    chunkScanSegmentLength++;
                                
                                if (chunkScanSegmentLength == chunkScanSegmentLengthMax + 1) {
                                    // search limit reached - no nearby claims found!
                                    //System.out.println("No non-allied claims nearby, teleport point is OK!");
                                    break;
                                }
                            }
                            //try {
                            //    Thread.sleep(1);
                            //} catch (InterruptedException e) {}
                        }
                        
                        
                        if (!isTooClose) {
                            // do another spiral search from here for a beach/ocean/river biome 
                            int innerVectorX = 1;
                            int innerVectorZ = 0;
                            int innerPosX = posX;
                            int innerPosZ = posZ;
                            int innerSegmentLength = 1;
                            int innerSegmentPassed = 0;
                            
                            while (true) {
                                if (isBiomeTypeAtPos(world, innerPosX, innerPosZ, BiomeDictionary.Type.BEACH) || isBiomeTypeAtPos(world, innerPosX, innerPosZ, BiomeDictionary.Type.OCEAN) || isBiomeTypeAtPos(world, innerPosX, innerPosZ, BiomeDictionary.Type.RIVER)) {
                                    // verify that the candidate block has solid footing before confirming
                                    if (!world.getBlock(posX, world.getTopSolidOrLiquidBlock(posX, posZ), posZ).getMaterial().isLiquid()) {
                                        foundBiome = true;
                                        break;
                                    }
                                }
                                
                                // take a step "forward"
                                innerPosX += innerVectorX;
                                innerPosZ += innerVectorZ;
                                innerSegmentPassed++;
                                if (innerSegmentPassed == innerSegmentLength) {
                                    // segment done
                                    innerSegmentPassed = 0;
                                    
                                    // rotate the vector
                                    int tmp = innerVectorX;
                                    innerVectorX = -innerVectorZ;
                                    innerVectorZ = tmp;
                                    
                                    // bump the segment length if necessary (every second turn)
                                    if (innerVectorZ == 0)
                                        innerSegmentLength++;
                                    
                                    if (innerSegmentLength == innerSegmentLengthMax + 1) {
                                        // no beach/ocean/river biome found within desired range
                                        // set cooldown
                                        searchCooldown = ModConfig.TELEPORT_SEARCH_WATERBIOME_RETRYCOOLDOWN;
                                        break;
                                    }
                                }
                                //try {
                                //    Thread.sleep(1);
                                //} catch (InterruptedException e) {}
                            }
                        }
                    }
                    
                    if (foundBiome) {
                        //System.out.println("...success! Found plains biome at " + posX + "x" + posZ + " which has a beach/ocean/river biome within a " + waterBiomeSearchRadius + " block radius.");
                        entityPlayer.setPositionAndUpdate(posX + 0.5D, world.getTopSolidOrLiquidBlock(posX, posZ), posZ + 0.5D);
                        
                        prevPosX.add(posX);
                        prevPosZ.add(posZ);
                        teleporterProps.setTeleportData(posX, posZ, vectorX, vectorZ, segmentLength, segmentPassed, prevPosX, prevPosZ);
                        teleporterProps.setTeleporting(false);
                        break;
                    }
                    
                    // take a step "forward"
                    posX += vectorX;
                    posZ += vectorZ;
                    segmentPassed++;
                    if (segmentPassed == segmentLength) {
                        // segment done
                        segmentPassed = 0;
                        
                        // rotate the vector
                        int tmp = vectorX;
                        vectorX = -vectorZ;
                        vectorZ = tmp;
                        
                        // bump the segment length if necessary (every second turn)
                        if (vectorZ == 0)
                            segmentLength++;
                    }
                    //try {
                    //    Thread.sleep(1);
                    //} catch (InterruptedException e) {}
                }
                
                
                
            }
        };

        thread.start();
        
        return itemStack;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List tooltip, boolean bool) {
        String tooltipText;
        if (GuiScreen.isShiftKeyDown())
            tooltipText = LanguageRegistry.instance().getStringLocalization("item." + unlocalizedName + ".tooltip.more");
        else
            tooltipText = LanguageRegistry.instance().getStringLocalization("item." + unlocalizedName + ".tooltip");
        for (String line : TextUtils.splitTextString(tooltipText, 32)) {
            tooltip.add(line);
        }
    }
    
    private synchronized boolean isBiomeTypeAtPos(final World world, final int posX, final int posZ, final BiomeDictionary.Type biomeTypeSearch) {
        final BiomeDictionary.Type[] biomeTypes = BiomeDictionary.getTypesForBiome(world.getBiomeGenForCoords(posX, posZ));
        for (final BiomeDictionary.Type biomeType : biomeTypes) {
            if (biomeType == biomeTypeSearch)
                return true;
        }
        return false;
    }
}
