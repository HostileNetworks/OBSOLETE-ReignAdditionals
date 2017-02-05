package com.cosmicdan.reignadditionals.items;

import java.util.List;

import com.cosmicdan.reignadditionals.Main;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemDebugThingy extends Item {

    public ItemDebugThingy(String unlocalizedName) {
        setFull3D();
        setTextureName(Main.MODID + ":" + unlocalizedName);
        setUnlocalizedName(unlocalizedName);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List tooltip, boolean bool) {
        ModItems.getTooltip(getUnlocalizedName(), tooltip);
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        if (world.isRemote) {
            int distance = 100;
            MovingObjectPosition rayTraceEntity = getMouseOverExtended(distance);
            
            if (rayTraceEntity.entityHit != null) {
                Main.LOGGER.info("~~~~~~~ Hit an entity! Details:");
                Main.LOGGER.info("Entity name: " + rayTraceEntity.entityHit.getCommandSenderName());
                Main.LOGGER.info("Entity class: " + rayTraceEntity.entityHit.getClass().getCanonicalName());
                boolean isEntityLiving = rayTraceEntity.entityHit instanceof EntityLiving;
                Main.LOGGER.info("Instance of EntityLiving? " + isEntityLiving);
                if (isEntityLiving) {
                    EntityLiving entity = (EntityLiving) rayTraceEntity.entityHit;
                    Main.LOGGER.info("    EntityLiving navigator class: " + entity.getNavigator().getClass().getCanonicalName());
                    int[] spawnPos = null;
                    if (entity.getEntityData().hasKey("ORIGINAL_SPAWN_POS")) {
                        spawnPos = entity.getEntityData().getIntArray("ORIGINAL_SPAWN_POS");
                    }
                    Main.LOGGER.info("    EntityLiving original spawn pos: " + ((spawnPos != null && spawnPos.length == 3) ? spawnPos[0] + "x" + spawnPos[1] + "x" + spawnPos[2] : "UNKNOWN [Spawned before ReignAdditionals could catch the event]"));
                    boolean hasAiTasks = entity.tasks != null;
                    Main.LOGGER.info("    EntityLiving has AI tasks? " + hasAiTasks);
                    if (hasAiTasks) {
                        Main.LOGGER.info("        EntityLiving list of AI tasks [priority // class]: ");
                        for (Object task : entity.tasks.taskEntries) {
                            if (task instanceof EntityAITasks.EntityAITaskEntry) {
                                Main.LOGGER.info("            " + ((EntityAITasks.EntityAITaskEntry)task).priority + " // " + ((EntityAITasks.EntityAITaskEntry)task).action.getClass().getCanonicalName()); 
                            }
                        }
                    }
                    boolean hasTargetTasks = entity.targetTasks != null;
                    Main.LOGGER.info("    EntityLiving has target tasks? " + hasTargetTasks);
                    if (hasTargetTasks) {
                        Main.LOGGER.info("        EntityLiving list of AI tasks [priority // class]: ");
                        for (Object task : entity.targetTasks.taskEntries) {
                            if (task instanceof EntityAITasks.EntityAITaskEntry) {
                                Main.LOGGER.info("            " + ((EntityAITasks.EntityAITaskEntry)task).priority + " // " + ((EntityAITasks.EntityAITaskEntry)task).action.getClass().getCanonicalName()); 
                            }
                        }
                        EntityLivingBase currentTarget = entity.getAITarget();
                        Main.LOGGER.info("        Current target: " + currentTarget);
                        if (currentTarget != null) {
                            Main.LOGGER.info("        Current target class: " + currentTarget.getClass().getCanonicalName());
                            Main.LOGGER.info("            Current target alive? " + currentTarget.isEntityAlive());
                        }
                        EntityLivingBase currentAttackTarget = entity.getAttackTarget();
                        Main.LOGGER.info("        Current attack target: " + currentAttackTarget);
                        if (currentAttackTarget != null) {
                            Main.LOGGER.info("        Current attack target class: " + currentAttackTarget.getClass().getCanonicalName());
                            Main.LOGGER.info("            Current attack target alive? " + currentAttackTarget.isEntityAlive());
                        }
                    }
                }
                Main.LOGGER.info("~~~~~~~ End details");
            } else {
                Main.LOGGER.info("~~~~~~~ DebugThingy didn't find anything of interest within a distance of " + distance);
            }
        }
        return itemStack;
    }
    
    // This is mostly copied from the EntityRenderer#getMouseOver() method
    // Credits to Jabelar for the guide
    public static MovingObjectPosition getMouseOverExtended(float dist) {
        Minecraft mc = FMLClientHandler.instance().getClient();
        EntityLivingBase theRenderViewEntity = mc.renderViewEntity;
        AxisAlignedBB theViewBoundingBox = AxisAlignedBB.getBoundingBox(
                theRenderViewEntity.posX-0.5D,
                theRenderViewEntity.posY-0.0D,
                theRenderViewEntity.posZ-0.5D,
                theRenderViewEntity.posX+0.5D,
                theRenderViewEntity.posY+1.5D,
                theRenderViewEntity.posZ+0.5D
                );
        MovingObjectPosition returnMOP = null;
        if (mc.theWorld != null)
        {
            double var2 = dist;
            returnMOP = theRenderViewEntity.rayTrace(var2, 0);
            double calcdist = var2;
            Vec3 pos = Vec3.createVectorHelper(theRenderViewEntity.posX, theRenderViewEntity.posY + theRenderViewEntity.getEyeHeight(), theRenderViewEntity.posZ);
            var2 = calcdist;
            if (returnMOP != null)
            {
                calcdist = returnMOP.hitVec.distanceTo(pos);
            }
             
            Vec3 lookvec = theRenderViewEntity.getLook(0);
            Vec3 var8 = pos.addVector(lookvec.xCoord * var2, 

                  lookvec.yCoord * var2, 

                  lookvec.zCoord * var2);
            Entity pointedEntity = null;
            float var9 = 1.0F;
            @SuppressWarnings("unchecked")
            List<Entity> list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(

                  theRenderViewEntity, 

                  theViewBoundingBox.addCoord(

                        lookvec.xCoord * var2, 

                        lookvec.yCoord * var2, 

                        lookvec.zCoord * var2).expand(var9, var9, var9));
            double d = calcdist;
                
            for (Entity entity : list)
            {
                if (entity.canBeCollidedWith())
                {
                    float bordersize = entity.getCollisionBorderSize();
                    AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(

                          entity.posX-entity.width/2, 

                          entity.posY, 

                          entity.posZ-entity.width/2, 

                          entity.posX+entity.width/2, 

                          entity.posY+entity.height, 

                          entity.posZ+entity.width/2);
                    aabb.expand(bordersize, bordersize, bordersize);
                    MovingObjectPosition mop0 = aabb.calculateIntercept(pos, var8);
                        
                    if (aabb.isVecInside(pos))
                    {
                        if (0.0D < d || d == 0.0D)
                        {
                            pointedEntity = entity;
                            d = 0.0D;
                        }
                    } else if (mop0 != null)
                    {
                        double d1 = pos.distanceTo(mop0.hitVec);
                            
                        if (d1 < d || d == 0.0D)
                        {
                            pointedEntity = entity;
                            d = d1;
                        }
                    }
                }
            }
               
            if (pointedEntity != null && (d < calcdist || returnMOP == null))
            {
                 returnMOP = new MovingObjectPosition(pointedEntity);
            }

        }
        return returnMOP;
    }


}
