package com.cosmicdan.reignadditionals.client.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.shadowmage.ancientwarfare.core.gamedata.Timekeeper;

public class GuiTextOverlay {
    private final Minecraft mc;
    private final TextureManager re;
    private static EntityPlayer player;
    private static int screenWidth;
    private static int screenHeight;
    private static int screenScale;
    
    private static final ResourceLocation[] iconsMoonphases = new ResourceLocation[8];
    private static final ResourceLocation[] iconsSeasons = new ResourceLocation[4];
    
    // TODO: Config for these
    private static final int daysPerMoonPhase = 6;
    private static final int startingYear = 1000;
    private static final int mainTextStartFadeAt = 50;
    private static final int InfosStartFadeAt = 240;
    private static final int fadeTime = 80;
    // debug note to self: new year = 9216000 ticks
    
    private static int TICKER = 0;
    
    private static int lastDay = -1;
    private static int currentDay = 0;
    private static int currentYear = 0;
    private static int currentMoonphase = 0;
    private static int currentSeason = 0;
    
    private static int daysUntilFullMoon = 0;
    private static int daysUntilNextSeason = 0;
    
    private static final int daysPerMonth = daysPerMoonPhase * 8; // 8 moon phases per month
    private static final int daysPerSeason = daysPerMonth * 2; // 2 months per season (in Harder Wildlife)
    private static final int daysPerYear = daysPerSeason * 4; // 4 seasons per year
    
    private static String line1 = "";
    private static String line2 = "";
    
    private static boolean doNewDayText = false;
    private static int newDayDrawTime = 0;
    
    public GuiTextOverlay(Minecraft mc) {
        this.mc = mc;
        this.re = mc.renderEngine;
        for (int i = 0; i < 8; i++) {
            iconsMoonphases[i] = new ResourceLocation("reignadditionals:textures/gui/moon_" + i + ".png");
        }
        for (int i = 0; i < 4; i++) {
            iconsSeasons[i] = new ResourceLocation("reignadditionals:textures/gui/season_" + i + ".png");
        }
        
        
    }
    
    @SubscribeEvent
    public void drawOverlay(RenderGameOverlayEvent event) {
        if (event.isCancelable() || event.type != ElementType.EXPERIENCE) {      
            return;
        }
        
        player = mc.thePlayer;
        screenWidth = event.resolution.getScaledWidth();
        screenHeight = event.resolution.getScaledHeight();
        screenScale = event.resolution.getScaleFactor();
        
        // update the current day/year values
        TICKER++;
        if (TICKER == 10) {
            TICKER = 0;
            currentDay = (int) ((player.worldObj.getWorldTime() / 24000L));
            currentYear = currentDay / daysPerYear + startingYear;
            if (currentYear > startingYear) {
                currentDay = currentDay - ((currentYear - startingYear) * daysPerYear);
            }
        }
        
        if (lastDay != currentDay) {
            // new day, update moonphase and season
            currentMoonphase = player.worldObj.getMoonPhase();
            currentSeason = currentDay / daysPerSeason;
            doNewDayText = true;
            newDayDrawTime = 0;
            daysUntilFullMoon = daysPerMonth - (currentDay % daysPerMonth);
            daysUntilNextSeason = daysPerSeason - (currentDay % daysPerSeason);
        }
        lastDay = currentDay;
        
        
        line1 = Timekeeper.getTimeOfDayHour() +
                ":" +
                (Timekeeper.getTimeOfDayMinute() < 10 ? "0" : "") + Timekeeper.getTimeOfDayMinute() +
                ", " +
                player.worldObj.getBiomeGenForCoords(player.chunkCoordX, player.chunkCoordZ).biomeName;
        line2 = "Day " + (currentDay + 1) + ", " + currentYear + " AGC";
        

        GL11.glPushMatrix();
        GL11.glScaled(0.75, 0.75, 1.0);
        mc.fontRenderer.drawString(line1, 40, 1, 0x00FFFFFF, true);
        mc.fontRenderer.drawString(line2, 40, mc.fontRenderer.FONT_HEIGHT + 2, 0x00FFFFFF, true);
        re.bindTexture(iconsMoonphases[currentMoonphase]);
        drawTexturedRect(1, 2, 0, 0, 16, 16, 16, 16);
        re.bindTexture(iconsSeasons[currentSeason]);
        drawTexturedRect(20, 2, 0, 0, 16, 16, 16, 16);
        
        
        
        if (doNewDayText) {
            newDayDrawTime++;
            // calculate alphas
            int alphaMainText = 255;
            int alphaRemainingInfos = 255;
            if (newDayDrawTime > mainTextStartFadeAt) {
                if (newDayDrawTime - mainTextStartFadeAt <= fadeTime) {
                    alphaMainText -= (int) (alphaMainText * (newDayDrawTime - mainTextStartFadeAt) * 1f/(float)fadeTime);
                } else
                    alphaMainText = 4; // why does anything less than 4 mean opaque to MC?
                if (alphaMainText < 10)
                    alphaMainText = 4;
            }
            
            if (newDayDrawTime > InfosStartFadeAt) {
                if (newDayDrawTime - InfosStartFadeAt <= fadeTime) {
                    alphaRemainingInfos -= (int) (alphaRemainingInfos * (newDayDrawTime - InfosStartFadeAt) * 1f/(float)fadeTime);
                } else
                    alphaRemainingInfos = 4;
                if (alphaRemainingInfos < 10)
                    alphaRemainingInfos = 4;
            }
            
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            int scaleFactor = 3;
            GL11.glScaled(scaleFactor, scaleFactor, 1);
            String text = "Day " + (currentDay + 1);
            int argb = ( alphaMainText << 24 ) | ( 255 << 16 ) | ( 255 << 8) | 255;
            mc.fontRenderer.drawString(text, screenWidth / (scaleFactor * 2) - mc.fontRenderer.getStringWidth(text) / 2, screenHeight / (scaleFactor * 2) - 74 / scaleFactor, argb, true);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            text = "of";
            scaleFactor = 1;
            mc.fontRenderer.drawString(text, screenWidth / (scaleFactor * 2) - mc.fontRenderer.getStringWidth(text) / 2, screenHeight / (scaleFactor * 2) - 42 / scaleFactor, argb, true);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            text = "Year " + currentYear + " AGC";
            scaleFactor = 1;
            mc.fontRenderer.drawString(text, screenWidth / (scaleFactor * 2) - mc.fontRenderer.getStringWidth(text) / 2, screenHeight / (scaleFactor * 2) - 28 / scaleFactor, argb, true);
            
            // draw full moon/season first
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            scaleFactor = 1;
            
            GL11.glColor4f(255, 255, 255, ((float)alphaRemainingInfos / 255f));
            re.bindTexture(iconsMoonphases[0]);
            drawTexturedRect(screenWidth / 2 - 60, screenHeight / 2 - 10, 0, 0, 24, 24, 24, 24);
            re.bindTexture(iconsSeasons[(currentSeason < 3 ? currentSeason + 1 : 0)]);
            drawTexturedRect(screenWidth / 2 + 60 - 24, screenHeight / 2 - 10, 0, 0, 24, 24, 24, 24);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            
            if (daysUntilFullMoon == daysPerMonth)
                text = "TODAY";
            else
                text = "in " + daysUntilFullMoon + (daysUntilFullMoon == 1 ? " day" : " days");
            
            // colorize the full moon countdown if < 10 days to go
            int colorChannelBlueAndGreen = 255; 
            if ((daysUntilFullMoon < 12) || (daysUntilFullMoon == daysPerMonth)) {
                if (daysUntilFullMoon == daysPerMonth)
                    colorChannelBlueAndGreen = 0;
                else
                    colorChannelBlueAndGreen =- 255 + (daysUntilFullMoon * 20) ;
            }
            argb = ( alphaRemainingInfos << 24 ) | ( 255 << 16 ) | ( colorChannelBlueAndGreen << 8) | colorChannelBlueAndGreen;
            mc.fontRenderer.drawString(text, screenWidth / 2 - 60 - (mc.fontRenderer.getStringWidth(text) / 2) + (24 / 2), screenHeight / (scaleFactor * 2) - 10 + 26, argb, true);
            
            if (daysUntilNextSeason == daysPerSeason)
                text = "TODAY";
            else
                text = "in " + daysUntilNextSeason + (daysUntilNextSeason == 1 ? " day" : " days");
            argb = ( alphaRemainingInfos << 24 ) | ( 255 << 16 ) | ( 255 << 8) | 255;
            mc.fontRenderer.drawString(text, screenWidth / 2 + 60 - (mc.fontRenderer.getStringWidth(text) / 2) + (24 / 2) - 24, screenHeight / (scaleFactor * 2) - 10 + 26, argb, true);
        }
        
        GL11.glPopMatrix();
    }
    
    public void drawTexturedRect(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
        float f = 1F / (float)textureWidth;
        float f1 = 1F / (float)textureHeight;
        Tessellator.instance.startDrawingQuads();
        Tessellator.instance.addVertexWithUV((double)(x), (double)(y + height), 0, (double)((float)(u) * f), (double)((float)(v + height) * f1));
        Tessellator.instance.addVertexWithUV((double)(x + width), (double)(y + height), 0, (double)((float)(u + width) * f), (double)((float)(v + height) * f1));
        Tessellator.instance.addVertexWithUV((double)(x + width), (double)(y), 0, (double)((float)(u + width) * f), (double)((float)(v) * f1));
        Tessellator.instance.addVertexWithUV((double)(x), (double)(y), 0, (double)((float)(u) * f), (double)((float)(v) * f1));
        Tessellator.instance.draw();
    }
    
}
