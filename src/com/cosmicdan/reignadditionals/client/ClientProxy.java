package com.cosmicdan.reignadditionals.client;

import com.cosmicdan.reignadditionals.CommonProxy;
import com.cosmicdan.reignadditionals.Main;
import com.cosmicdan.reignadditionals.client.particles.*;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
    @Override
    public void init() {
        super.init();
    }
    
    @Override
    public void generateParticleSteamFizz(World world, int posX, int posY, int posZ) {
        double genX = posX + world.rand.nextFloat();
        double genY = posY + world.rand.nextFloat() * 0.5F + 1;
        //double genY = posY + 0.5F;
        double genZ = posZ + world.rand.nextFloat();
        Minecraft.getMinecraft().effectRenderer.addEffect(new EntitySteamFizzFX(world, genX, genY, genZ));
    }
}