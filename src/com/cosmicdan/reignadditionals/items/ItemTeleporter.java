package com.cosmicdan.reignadditionals.items;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import com.cosmicdan.reignadditionals.Main;
import com.cosmicdan.reignadditionals.ModConfig;
import com.cosmicdan.reignadditionals.core.CorePlugin;
import com.cosmicdan.reignadditionals.gamedata.PlayerTeleporterTracker;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.utils.world.LMWorldServer;
import ftb.utils.world.claims.ClaimedChunk;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.shadowmage.ancientwarfare.core.gamedata.ChunkClaims;
import net.shadowmage.ancientwarfare.core.gamedata.ChunkClaims.TownHallEntry;
import net.shadowmage.ancientwarfare.core.interop.ModAccessors;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.npc.gamedata.HeadquartersTracker;

public class ItemTeleporter extends Item {

    private static final boolean DEBUG = false;
    
    
    protected ItemTeleporter(String unlocalizedName) {
        this.setUnlocalizedName(unlocalizedName);
        this.setTextureName(Main.MODID + ":" + unlocalizedName);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List tooltip, boolean bool) {
        ModItems.getTooltip(getUnlocalizedName(), tooltip);
    }
    
    @Override
    public ItemStack onItemRightClick(final ItemStack itemStack, final World world, final EntityPlayer entityPlayer) {
        if (world.isRemote)
            return itemStack;
        
        // get player's existing teleport props and return if they're already in the process of teleporting
        final PlayerTeleporterTracker teleporterProps = PlayerTeleporterTracker.get(entityPlayer);
        if (teleporterProps.isTeleporting())
            return itemStack;
        
        // play the teleport-away sound
        final float randomPitch = (float) (Math.random() * (1.1f - 0.9f) + 0.9f);
        world.playSoundAtEntity(entityPlayer, "ancientwarfare:teleport.out", 0.6F, randomPitch);
        
        // check if the player has a HQ, if so teleport them there and be done with it
        int[] hqPos = HeadquartersTracker.get(world).getHqPos(entityPlayer.getCommandSenderName(), world);
        if (hqPos != null) {
            EntityTools.teleportPlayerToBlock(entityPlayer, world, hqPos, false);
            world.playSoundAtEntity(entityPlayer, "ancientwarfare:teleport.in", 0.6F, randomPitch);
            itemStack.stackSize--;
            return itemStack;
        }
        
        Thread thread = new Thread(){
            public void run(){
                teleporterProps.setTeleporting(true);
                entityPlayer.getFoodStats().addStats(20, 10);
                entityPlayer.addPotionEffect(new PotionEffect(Potion.invisibility.getId(), Integer.MAX_VALUE, 0));
                entityPlayer.addChatMessage(new ChatComponentText(ModConfig.TELEPORT_OUT_MESSAGE));
                
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
                
                boolean foundBiome = false;
                
                int searchCooldown = ModConfig.TELEPORT_MIN_DISTANCE;
                int innerSegmentLengthMax = ModConfig.TELEPORT_SEARCH_WATERBIOME_RADIUS * 2 + 1;
                
                if (DEBUG) System.out.println("Starting teleport from " + posX + "x" + "posZ" + " with a segment length of " + segmentLength + ", cooldown of " + searchCooldown + " and waterbiome search distance of " + innerSegmentLengthMax);
                
                while (true) {
                    // NB: On my imaginary grid, I consider "right" as X+ and "down" as Z+
                    if (searchCooldown > 0)
                        searchCooldown--;
                    
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
                            segmentLength += ModConfig.TELEPORT_SEGMENT_INCREMENT;
                    }
                    //try {
                    //    Thread.sleep(1);
                    //} catch (InterruptedException e) {}
                    
                    if (searchCooldown == 0 && isBiomeTypeAtPos(world, posX, posZ, BiomeDictionary.Type.PLAINS)) {
                        if (DEBUG) System.out.println("Cooldown reached! Checking if we've passed the minimum distance since last teleport...");
                        searchCooldown = ModConfig.TELEPORT_MIN_DISTANCE;
                        boolean isTooClose = false;
                        
                        // Make sure this new position is further than searchMinDistance to the last teleport position
                        for (int i = 0; i < prevPosX.size(); i++) {
                            if (Math.hypot(posX-prevPosX.get(i), posZ-prevPosZ.get(i)) < ModConfig.TELEPORT_MIN_DISTANCE) {
                                if (DEBUG) System.out.println("    ...nope! Try again next cool-down...");
                                isTooClose = true;
                                break;
                            }
                        }
                        
                        if (isTooClose)
                            continue;
                        
                        if (DEBUG) System.out.println("    ...yep! Check if there is a non-allied claim nearby...");
                        
                        // Make sure that there are no non-allied player claims nearby
                        int chunkScanVectorX = 1;
                        int chunkScanVectorZ = 0;
                        int chunkX = posX / 16; // NB: divisions by powers of 2 are OK
                        int chunkZ = posZ / 16;
                        int chunkScanSegmentLength = 1;
                        int chunkScanSegmentPassed = 0;
                        int chunkScanSegmentLengthMax = ModConfig.TELEPORT_CLAIMEDCHUNK_BUFFER * 2 + 1;
                        
                        while (true) {
                            // check for existing claim
                            ClaimedChunk claim = LMWorldServer.inst.claimedChunks.getChunk(world.provider.dimensionId, chunkX, chunkZ);
                            if (claim != null) {
                                if (claim.ownerID == -1) {
                                    // commonwealth
                                    if (DEBUG) System.out.println("        ...yep! Found commonwealth at " + chunkX * 16 + " x " + chunkZ * 16 + ", trying again next cooldown...");
                                    isTooClose = true;
                                    break;
                                }
                                String claimPlayerName = claim.getOwnerS().getProfile().getName();
                                ScorePlayerTeam claimTeam = world.getScoreboard().getPlayersTeam(claimPlayerName);
                                if (claimTeam == null || entityPlayer.getTeam() == null || !claimTeam.isSameTeam(entityPlayer.getTeam())) {
                                    if (!ModAccessors.FTBU.areFriends(claimPlayerName, entityPlayer.getCommandSenderName())) {
                                        if (DEBUG) System.out.println("        ...yep! Found a claim at " + chunkX * 16 + " x " + chunkZ * 16 + " belonging to " + claimPlayerName + ", trying again next cooldown...");
                                        isTooClose = true;
                                        break;
                                    }
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
                                    break;
                                }
                            }
                            //try {
                            //    Thread.sleep(1);
                            //} catch (InterruptedException e) {}
                        }
                        
                        if (isTooClose)
                            continue;
                        
                        if (DEBUG) System.out.println("        ...nope! Now, is there a water biome nearby...?");
                        
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
                                    if (DEBUG) System.out.println("            ...yep!");
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
                        
                        if (foundBiome) {
                            if (DEBUG) System.out.println("                ...success! Teleporting to " + posX + "x" + posZ + "!");
                            entityPlayer.setPositionAndUpdate(posX + 0.5D, world.getTopSolidOrLiquidBlock(posX, posZ), posZ + 0.5D);
                            world.playSoundAtEntity(entityPlayer, "ancientwarfare:teleport.in", 0.6F, randomPitch);
                            
                            prevPosX.add(posX);
                            prevPosZ.add(posZ);
                            teleporterProps.setTeleportData(posX, posZ, vectorX, vectorZ, segmentLength, segmentPassed, prevPosX, prevPosZ);
                            teleporterProps.setTeleporting(false);
                            
                            entityPlayer.addChatMessage(new ChatComponentText(""));
                            entityPlayer.addChatMessage(new ChatComponentText(ModConfig.TELEPORT_IN_MESSAGE));
                            break;
                        }
                        if (DEBUG) System.out.println("            ...nope! Try again next cooldown.");
                    }
                }
            }
        };

        thread.start();
        
        return itemStack;
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
