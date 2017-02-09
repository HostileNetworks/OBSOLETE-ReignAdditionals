package com.cosmicdan.reignadditionals.events;

import com.cosmicdan.reignadditionals.ModConfig;
import com.cosmicdan.reignadditionals.client.gui.GuiTextOverlay;
import com.cosmicdan.reignadditionals.gamedata.PlayerTeleporterTracker;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;

public class EntityEvents {
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityPlayer) {
            if (event.world.isRemote) {
                if (Minecraft.getMinecraft().thePlayer.getUniqueID().equals(((EntityPlayer)event.entity).getUniqueID()))
                    GuiTextOverlay.triggerPlayerJoin();
            }
            PlayerTeleporterTracker teleporterProps = PlayerTeleporterTracker.get((EntityPlayer)event.entity);
            if (teleporterProps.isDematerialized()) {
                ((EntityPlayer)event.entity).addPotionEffect(new PotionEffect(Potion.invisibility.getId(), Integer.MAX_VALUE, 0));
            }
            return;
        }
        if (event.entity instanceof EntityLiving) {
            if (!event.entity.getEntityData().hasKey("ORIGINAL_SPAWN_POS")) {
                event.entity.getEntityData().setIntArray("ORIGINAL_SPAWN_POS", new int[] {(int) event.entity.posX, (int) event.entity.posY, (int) event.entity.posZ});
            }
        }
    }
    
    
    @SubscribeEvent
    public void onCheckSpawn(CheckSpawn event) {
        if (event.entityLiving instanceof EntityAmbientCreature)
            return;
        
        if (getSavedLightValue(event.world, (int)event.x, (int)event.y, (int)event.z) > 7) {
            event.setResult(Result.DENY);
        }
    }
    
    
    @SubscribeEvent
    public void onEntityConstructing(EntityConstructing event) {
        if (event.entity instanceof EntityPlayer && PlayerTeleporterTracker.get((EntityPlayer) event.entity) == null)
            PlayerTeleporterTracker.register((EntityPlayer) event.entity);
        
        if (event.entity instanceof EntityPlayer && event.entity.getExtendedProperties(PlayerTeleporterTracker.EXT_PROP_NAME) == null)
            event.entity.registerExtendedProperties(PlayerTeleporterTracker.EXT_PROP_NAME, new PlayerTeleporterTracker((EntityPlayer) event.entity));
    }
    
    static int LIVING_UPDATE_COUNTER = 0;
    static int LIVING_UPDATE_COUNTER_MAX = 20;
    
    @SubscribeEvent
    public void onEntityLivingUpdate(LivingUpdateEvent event) {
        if (event.entity.worldObj.isRemote)
            return;
        if (++LIVING_UPDATE_COUNTER < LIVING_UPDATE_COUNTER_MAX)
            return;
        LIVING_UPDATE_COUNTER = 0;
        if (event.entity instanceof EntityZombie) {
            if (((EntityZombie)event.entity).getAttackTarget() != null) {
                event.entity.getEntityData().setInteger("REIGN_IDLE_TARGET_COUNTER", 0);
                return;
            }
            String startingHeldItem = event.entity.getEntityData().getString("REIGN_STARTING_HELD_ITEM");
            ItemStack heldItem = ((EntityZombie)event.entity).getHeldItem();
            if (startingHeldItem.isEmpty()) {
                event.entity.getEntityData().setString("REIGN_STARTING_HELD_ITEM", heldItem == null ? "NULL" : heldItem.getUnlocalizedName());
            }
            if (heldItem == null || heldItem.getItem() instanceof ItemBlock || heldItem.getItem().getUnlocalizedName().equals(startingHeldItem)) {
                int entityIdleTargetTimer = event.entity.getEntityData().getInteger("REIGN_IDLE_TARGET_COUNTER");
                if (entityIdleTargetTimer >= ModConfig.REIGN_IDLE_TARGET_DESPAWN_SECS) {
                    event.entity.worldObj.removeEntity(event.entity);
                    //Main.LOGGER.warn("ReignAdditionals is removing mob " + event.entity.getCommandSenderName() + " at " + event.entity.posX + "x" + event.entity.posY + "x" + event.entity.posZ + " after idle target timeout ");
                } else {
                    //Main.LOGGER.warn("Setting timer to " + (entityIdleTargetTimer + 1) + " because it's not yet " + ModConfig.REIGN_IDLE_TARGET_DESPAWN_SECS);
                    event.entity.getEntityData().setInteger("REIGN_IDLE_TARGET_COUNTER", ++entityIdleTargetTimer);
                }
            }
        }
    }
    
    // cut-down version of the same world method, but returns 15 instead of 0 in the event that non-skylight light level could not be retrieved
    private int getSavedLightValue(World world, int posX, int posY, int posZ) {
        if (posY < 0)
            posY = 0;

        if (posY >= 256)
            posY = 255;

        if (posX >= -30000000 && posZ >= -30000000 && posX < 30000000 && posZ < 30000000) {
            int chunkX = posX >> 4;
            int chunkZ = posZ >> 4;

            if (world.getChunkProvider().chunkExists(chunkX, chunkZ)) {
                Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
                //return chunk.getSavedLightValue(enumSkyBlock, p_72972_2_ & 15, p_72972_3_, p_72972_4_ & 15);
                ExtendedBlockStorage extendedblockstorage = chunk.getBlockStorageArray()[posY >> 4];
                return extendedblockstorage == null ? 15 : extendedblockstorage.getExtBlocklightValue(posX & 15, posY & 15, posZ & 15);
            }
        }
        return 15;
    }
}
