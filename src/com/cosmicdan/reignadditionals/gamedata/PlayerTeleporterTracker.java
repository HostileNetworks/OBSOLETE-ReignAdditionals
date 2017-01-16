package com.cosmicdan.reignadditionals.gamedata;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class PlayerTeleporterTracker implements IExtendedEntityProperties {
    public final static String EXT_PROP_NAME = "PlayerTeleporterTracker";
    
    private final EntityPlayer entityPlayer;
    
    private boolean hasData = false;
    private int lastTeleportPosX;
    private int lastTeleportPosZ;
    private int lastTeleportVectorX;
    private int lastTeleportVectorZ;
    private int lastTeleportSegmentLength;
    private int lastTeleportSegmentPassed;
    private List<Integer> prevTeleportPosX = new ArrayList<Integer>();
    private List<Integer> prevTeleportPosZ = new ArrayList<Integer>();
    
    private boolean isDematerialized = false;
    
    // we don't need to save this value, it's just set temporarily to prevent teleport spamming
    private boolean isTeleporting = false;
    
    public PlayerTeleporterTracker(EntityPlayer entityPlayer) {
        this.entityPlayer = entityPlayer;
    }
    
    public static final void register(EntityPlayer entityPlayer) {
        entityPlayer.registerExtendedProperties(PlayerTeleporterTracker.EXT_PROP_NAME, new PlayerTeleporterTracker(entityPlayer));
    }
    
    public static final PlayerTeleporterTracker get(EntityPlayer entityPlayer) {
        return (PlayerTeleporterTracker) entityPlayer.getExtendedProperties(EXT_PROP_NAME);
    }
    

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        if (hasData) {
            NBTTagCompound props = new NBTTagCompound();
            props.setBoolean("hasData", hasData);
            props.setInteger("lastTeleportPosX", lastTeleportPosX);
            props.setInteger("lastTeleportPosZ", lastTeleportPosZ);
            props.setInteger("lastTeleportVectorX", lastTeleportVectorX);
            props.setInteger("lastTeleportVectorZ", lastTeleportVectorZ);
            props.setInteger("lastTeleportSegmentLength", lastTeleportSegmentLength);
            props.setInteger("lastTeleportSegmentPassed", lastTeleportSegmentPassed);
            props.setIntArray("prevTeleportPosX", ArrayUtils.toPrimitive(prevTeleportPosX.toArray(new Integer[0])));
            props.setIntArray("prevTeleportPosZ", ArrayUtils.toPrimitive(prevTeleportPosZ.toArray(new Integer[0])));
            props.setBoolean("isDematerialized", isDematerialized);
            nbt.setTag(EXT_PROP_NAME, props);
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        NBTTagCompound props = (NBTTagCompound) nbt.getTag(EXT_PROP_NAME);
        hasData = props.getBoolean("hasData");
        if (hasData) {
            lastTeleportPosX = props.getInteger("lastTeleportPosX");
            lastTeleportPosZ = props.getInteger("lastTeleportPosZ");
            lastTeleportVectorX = props.getInteger("lastTeleportVectorX");
            lastTeleportVectorZ = props.getInteger("lastTeleportVectorZ");
            lastTeleportSegmentLength = props.getInteger("lastTeleportSegmentLength");
            lastTeleportSegmentPassed = props.getInteger("lastTeleportSegmentPassed");
            prevTeleportPosX = intArrayToIntegerList(props.getIntArray("prevTeleportPosX"));
            prevTeleportPosZ = intArrayToIntegerList(props.getIntArray("prevTeleportPosZ"));
            isDematerialized = props.getBoolean("isDematerialized");
        }
    }

    @Override
    public void init(Entity entity, World world) {}
    
    public synchronized void setTeleporting(boolean isTeleporting) {
        this.isTeleporting = isTeleporting;
    }
    
    public synchronized boolean isTeleporting() {
        return isTeleporting;
    }
    
    public void setTeleportData(int posX, int posZ, int vectorX, int vectorZ, int segmentLength, int segmentPassed, List<Integer> prevPosX, List<Integer> prevPosZ) {
        lastTeleportPosX = posX;
        lastTeleportPosZ = posZ;
        lastTeleportVectorX = vectorX;
        lastTeleportVectorZ = vectorZ;
        lastTeleportSegmentLength = segmentLength;
        lastTeleportSegmentPassed = segmentPassed;
        prevTeleportPosX = prevPosX;
        prevTeleportPosZ = prevPosZ;
        hasData = true;
        isDematerialized = true;
    }
    
    public boolean hasTeleportData() {
        return hasData;
    }
    
    public int getLastTeleportPosX() {
        return lastTeleportPosX;
    }
    
    public int getLastTeleportPosZ() {
        return lastTeleportPosZ;
    }
    
    public int getLastTeleportVectorX() {
        return lastTeleportVectorX;
    }
    
    public int getLastTeleportVectorZ() {
        return lastTeleportVectorZ;
    }
    
    public int getLastTeleportSegmentLength() {
        return lastTeleportSegmentLength;
    }
    
    public int getLastTeleportSegmentPassed() {
        return lastTeleportSegmentPassed;
    }
    
    public List<Integer> getPrevPosX() {
        return prevTeleportPosX;
    }
    
    public List<Integer> getPrevPosZ() {
        return prevTeleportPosZ;
    }
    
    public boolean isDematerialized() {
        return isDematerialized;
    }
    
    public void rematerialize() {
        isDematerialized = false;
        hasData = false;
    }
    
    private List<Integer> intArrayToIntegerList (int[] intArray) {
        List<Integer> intList = new ArrayList<Integer>();
        for (int i = 0; i < intArray.length; i++) {
            intList.add(intArray[i]);
        }
        return intList;
    }
}

