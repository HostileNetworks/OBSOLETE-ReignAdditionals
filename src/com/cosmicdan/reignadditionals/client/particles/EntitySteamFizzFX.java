/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package com.cosmicdan.reignadditionals.client.particles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntitySteamFizzFX extends EntitySimpleParticleFX {

    public EntitySteamFizzFX(World par1World, double x, double y, double z) {
        this(par1World, x, y, z, 0, 0, 0, 1f);
    }

    public EntitySteamFizzFX(World par1World, double x, double y, double z, double velX, double velY, double velZ, float scale) {
        super(par1World, x, y, z, velX, velY, velZ, scale);
        this.particleRed = this.particleGreen = this.particleBlue = (float) (Math.random() * 0.3);
        this.particleMaxAge = (int) (12.0D / (Math.random() * 0.5D + 0.2D));
        this.particleMaxAge = (int) (this.particleMaxAge * scale);
        this.noClip = true; // TODO: for some reason this causes crashes when false 
    }
}
