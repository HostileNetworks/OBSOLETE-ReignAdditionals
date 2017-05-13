package com.cosmicdan.reignadditionals.client.gui;

import org.lwjgl.opengl.GL11;

import com.cosmicdan.reignadditionals.ModConfig;
import com.cosmicdan.reignadditionals.items.ModItems;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.shadowmage.ancientwarfare.core.gamedata.Timekeeper;
import net.shadowmage.ancientwarfare.npc.gamedata.HeadquartersTracker;

public class GuiGameOverlay {
    private final Minecraft mc;
    private final TextureManager re;
    private static EntityPlayer player;
    private static int playerX;
    private static int playerY;
    private static int playerZ;
    private static int playerChunkOffsetX;
    private static int playerChunkOffsetZ;
    private static int playerChunkPosX;
    private static int playerChunkPosZ;
    private static int screenWidth;
    private static int screenHeight;
    protected static final RenderItem itemRenderer = new RenderItem();
    
    private static final ResourceLocation[] iconsMoonphases = new ResourceLocation[8];
    private static final ResourceLocation[] iconsSeasons = new ResourceLocation[4];
    private static final ResourceLocation compassTexture = new ResourceLocation("reignadditionals:textures/gui/compass.png");
    private static final int compassTexWidth = 360;
    private static final int compassTexHeight = 64;
    private static final ResourceLocation chunkMap = new ResourceLocation("reignadditionals:textures/gui/chunk_map.png");
    private static final ResourceLocation chunkMapIndicator = new ResourceLocation("reignadditionals:textures/gui/chunk_map_indicator.png");
    
    private static int TICKER_MAX = 8;
    private static int TICKER = 0;
    private static boolean TOCKER = false;
    
    private static int lastDay = -1;
    private static int currentDay = 0;
    private static int currentYear = 0;
    private static int currentMoonphase = 0;
    private static int currentSeason = 0;
    
    private static int daysUntilFullMoon = 0;
    private static int daysUntilNextSeason = 0;
    
    private static String line1 = "";
    private static String line2 = "";
    private static String biomeName = "";
    
    private static boolean doNewDayText = false;
    private static int newDayDrawTime = 0;
    
    // updated via server packets
    public static int cachedFluxStore = -2;
    public static boolean isNearlyFull = false; 
    
    public GuiGameOverlay(Minecraft mc) {
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
    public void drawOverlay(RenderGameOverlayEvent.Post event) {
        if (event.isCancelable() || event.type != ElementType.EXPERIENCE) {      
            return;
        }
        
        if (mc.gameSettings.showDebugInfo)
            return;
        
        player = mc.thePlayer;
        screenWidth = event.resolution.getScaledWidth();
        screenHeight = event.resolution.getScaledHeight();
        
        
        TICKER++;
        if (TICKER == TICKER_MAX) {
            // update some relatively-expensive info only so-often
            TICKER = 0;
            TOCKER = !TOCKER;
            
            playerX = MathHelper.floor_double(player.posX);
            playerY = MathHelper.floor_double(player.posY);
            playerZ = MathHelper.floor_double(player.posZ);
            playerChunkOffsetX = playerX & 15;
            playerChunkOffsetZ = playerZ & 15;
            
            if (TOCKER) {
                currentDay = (int) ((player.worldObj.getWorldTime() / 24000L));
                currentYear = currentDay / ModConfig.daysPerYear + ModConfig.STARTING_YEAR;
                if (currentYear > ModConfig.STARTING_YEAR) {
                    currentDay = currentDay - ((currentYear - ModConfig.STARTING_YEAR) * ModConfig.daysPerYear);
                }
                
                playerChunkPosX = playerX >> 4;
                playerChunkPosZ = playerZ >> 4;
                biomeName = mc.theWorld.getChunkFromChunkCoords(playerChunkPosX, playerChunkPosZ).getBiomeGenForWorldCoords(playerChunkOffsetX, playerChunkOffsetZ, this.mc.theWorld.getWorldChunkManager()).biomeName;
                
                if (lastDay != currentDay) {
                    // new day, refresh moonphase and season
                    currentMoonphase = player.worldObj.getMoonPhase();
                    currentSeason = currentDay / ModConfig.daysPerSeason;
                    doNewDayText = true;
                    newDayDrawTime = 0;
                    daysUntilFullMoon = ModConfig.daysPerMonth - (currentDay % ModConfig.daysPerMonth);
                    daysUntilNextSeason = ModConfig.daysPerSeason - (currentDay % ModConfig.daysPerSeason);
                }
                lastDay = currentDay;
                
                
                line1 = Timekeeper.getTimeOfDayHour() +
                        ":" +
                        (Timekeeper.getTimeOfDayMinute() < 10 ? "0" : "") + Timekeeper.getTimeOfDayMinute() +
                        ", " +
                        biomeName;
                        
                line2 = "Day " + (currentDay + 1 + (Timekeeper.getTimeOfDayHour() < 6 ? 1 : 0)) + ", " + currentYear + " " + ModConfig.YEAR_SUFFIX;
            }
        }
        
        
        // compass
        /*
        int directionBearing = MathHelper.floor_double((double)MathHelper.wrapAngleTo180_float(player.rotationYaw));
        if (directionBearing < 0)
            directionBearing += 360;
        // scale multiplier (corresponds to glScaled later) 
        int scaleMulti = 2;
        // viewport limiter is what factor the display is "narrowed" by
        double viewportLimiter = 2.2D;
        int compassXStart = (int) ((screenWidth * scaleMulti - (compassTexWidth / viewportLimiter)) / 2);
        int compassYStart = 8;
        int compassUStart = (int) (directionBearing + 45 + (45 * viewportLimiter));
        GL11.glPushMatrix();
        GL11.glScaled(0.5D, 0.5D, 1.0D);
        re.bindTexture(compassTexture);
        drawTexturedRect(compassXStart, compassYStart, compassUStart, 0, (int) (compassTexWidth / viewportLimiter), compassTexHeight, compassTexWidth, compassTexHeight);
        GL11.glPopMatrix();
        */
        
        // first row - CF indicator
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_LIGHTING);
        itemRenderer.renderItemIntoGUI(this.mc.fontRenderer, re, new ItemStack(ModItems.CRYSTALIZED_FLUX, 100), -1, -1);
        GL11.glDisable(GL11.GL_LIGHTING);
        String fluxStoreText = cachedFluxStore < 0 ? (cachedFluxStore == -1 ? "NO HQ" : "") : Integer.toString(cachedFluxStore);
        int fluxStoreTextColor = isNearlyFull ? 0x00FF5555 : 0x00FFFFFF;
        GL11.glScaled(0.5, 0.5, 1.0);
        mc.fontRenderer.drawString(fluxStoreText, 15 - (int)(mc.fontRenderer.getStringWidth(fluxStoreText) * 0.5), 20, fluxStoreTextColor, true);
        GL11.glPopMatrix();
        // first row - moonphase
        GL11.glPushMatrix();
        GL11.glScaled(0.75, 0.75, 1.0);
        re.bindTexture(iconsMoonphases[currentMoonphase]);
        drawTexturedRect(20, 2, 0, 0, 16, 16, 16, 16);
        // first row - season
        re.bindTexture(iconsSeasons[currentSeason]);
        drawTexturedRect(40, 2, 0, 0, 16, 16, 16, 16);
        GL11.glPopMatrix();
        // first row - time, date and biome text
        GL11.glPushMatrix();
        GL11.glScaled(0.65, 0.65, 1.0);
        mc.fontRenderer.drawString(line1, 70, 2, 0x00FFFFFF, true);
        mc.fontRenderer.drawString(line2, 70, mc.fontRenderer.FONT_HEIGHT + 4, 0x00FFFFFF, true);
        GL11.glPopMatrix();
        
        // second row icons and text (co-ord info)
        if (player.isSneaking()) {
            GL11.glPushMatrix();
            GL11.glScaled(0.6, 0.6, 1.0);
            re.bindTexture(chunkMap);
            drawTexturedRect(3, 28, 0, 0, 18, 18, 18, 18);
            re.bindTexture(chunkMapIndicator);
            drawTexturedRect(4 + playerChunkOffsetX, 29 + playerChunkOffsetZ, 0, 0, 1, 1, 1, 1);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScaled(0.55, 0.55, 1.0);
            mc.fontRenderer.drawString("Chunk: " + playerChunkPosX + "x" + playerChunkPosZ, 30, 30, 0x00FFFFFF, true);
            mc.fontRenderer.drawString("Height: " + playerY, 30, mc.fontRenderer.FONT_HEIGHT + 32, 0x00FFFFFF, true);
            GL11.glPopMatrix();
        }
        
        if (doNewDayText) {
            GL11.glPushMatrix();
            newDayDrawTime++;
            
            // calculate alphas for fade-outs
            int alphaMainText = 255;
            int alphaRemainingInfos = 255;
            if (newDayDrawTime > ModConfig.FADE_MAINTEXT_AT) {
                if (newDayDrawTime - ModConfig.FADE_MAINTEXT_AT <= ModConfig.FADE_OUT_TIME) {
                    alphaMainText -= (int) (alphaMainText * (newDayDrawTime - ModConfig.FADE_MAINTEXT_AT) * 1f/(float)ModConfig.FADE_OUT_TIME);
                } else
                    alphaMainText = 4; // why does anything less than 4 mean opaque to MC? Surely I've overlooked something 
                if (alphaMainText < 4)
                    alphaMainText = 4;
            }
            
            if (newDayDrawTime > ModConfig.FADE_INFOS_AT) {
                if (newDayDrawTime - ModConfig.FADE_INFOS_AT <= ModConfig.FADE_OUT_TIME) {
                    alphaRemainingInfos -= (int) (alphaRemainingInfos * (newDayDrawTime - ModConfig.FADE_INFOS_AT) * 1f/(float)ModConfig.FADE_OUT_TIME);
                } else
                    alphaRemainingInfos = 4;
                if (alphaRemainingInfos < 4)
                    alphaRemainingInfos = 4;
            }
            
            // disable and skip the newday GUI if everything has faded out
            if ((alphaMainText == 4) && (alphaRemainingInfos == 4))
                doNewDayText = false;
            else {
                // draw the big day/year text first
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
                text = "Year " + currentYear + " " + ModConfig.YEAR_SUFFIX;
                scaleFactor = 1;
                mc.fontRenderer.drawString(text, screenWidth / (scaleFactor * 2) - mc.fontRenderer.getStringWidth(text) / 2, screenHeight / (scaleFactor * 2) - 28 / scaleFactor, argb, true);
                
                // draw full moon/season icons
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                scaleFactor = 1;
                GL11.glColor4f(255, 255, 255, ((float)alphaRemainingInfos / 255f));
                re.bindTexture(iconsMoonphases[0]);
                drawTexturedRect(screenWidth / 2 - 60, screenHeight / 2 - 10, 0, 0, 24, 24, 24, 24);
                int nextSeason = currentSeason < 3 ? currentSeason + 1 : 0;
                re.bindTexture(iconsSeasons[daysUntilNextSeason == ModConfig.daysPerSeason ? currentSeason : nextSeason]);
                drawTexturedRect(screenWidth / 2 + 60 - 24, screenHeight / 2 - 10, 0, 0, 24, 24, 24, 24);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                
                // build the moon string
                if ((daysUntilFullMoon == ModConfig.daysPerMonth) && (currentDay > 1) && (currentYear > ModConfig.STARTING_YEAR))
                    // only show "today" if it isn't the very first day (first fullmoon) - we're assuming that ESM is set to have a cooldown
                    text = "TODAY";
                else
                    text = "in " + daysUntilFullMoon + (daysUntilFullMoon == 1 ? " day" : " days");
                
                // colorize the full moon countdown if < 12 days to go (fades to red)
                int colorChannelBlueAndGreen = 255; 
                if (((daysUntilFullMoon < 12) || (daysUntilFullMoon == ModConfig.daysPerMonth)) && (currentDay > 1)) {
                    if (daysUntilFullMoon == ModConfig.daysPerMonth)
                        colorChannelBlueAndGreen = 0;
                    else
                        colorChannelBlueAndGreen =- 255 + (daysUntilFullMoon * 20) ;
                }
                
                // draw moon string
                argb = ( alphaRemainingInfos << 24 ) | ( 255 << 16 ) | ( colorChannelBlueAndGreen << 8) | colorChannelBlueAndGreen;
                mc.fontRenderer.drawString(text, screenWidth / 2 - 60 - (mc.fontRenderer.getStringWidth(text) / 2) + (24 / 2), screenHeight / (scaleFactor * 2) - 10 + 26, argb, true);
                
                // build and draw the season string
                if (daysUntilNextSeason == ModConfig.daysPerSeason)
                    text = "TODAY";
                else
                    text = "in " + daysUntilNextSeason + (daysUntilNextSeason == 1 ? " day" : " days");
                
                argb = ( alphaRemainingInfos << 24 ) | ( 255 << 16 ) | ( 255 << 8) | 255;
                mc.fontRenderer.drawString(text, screenWidth / 2 + 60 - (mc.fontRenderer.getStringWidth(text) / 2) + (24 / 2) - 24, screenHeight / (scaleFactor * 2) - 10 + 26, argb, true);
                
                // reset alpha
                GL11.glColor4f(255, 255, 255, 255f);
            }
            GL11.glPopMatrix();
        }
    }
    
    private static boolean fireWelcomeGraphic = false;
    private static int welcomeGraphicProgress = 0;
    
    @SubscribeEvent
    public void renderChatEventPre(RenderGameOverlayEvent.Chat event) {
        if (event.type != ElementType.CHAT)
            return;
        if (fireWelcomeGraphic) {
            welcomeGraphicProgress++;
            
            int fadeInAt = 6 * 20; // start from 3 seconds
            int fadeOutAt = fadeInAt + 20 * 10; // ..end 5 seconds later 
            
            int alpha = 200; // keep the branding a little translucent
            int fadeTime = 60; // fade over 1.5 seconds
            
            if (welcomeGraphicProgress > fadeOutAt) { 
                if (welcomeGraphicProgress < fadeOutAt + fadeTime) {
                    alpha -= (int) (alpha * (welcomeGraphicProgress - fadeOutAt) * 1f/(float)fadeTime);
                } else {
                    fireWelcomeGraphic = false;
                    welcomeGraphicProgress = 0;
                    return;
                }
            }
            
            if (welcomeGraphicProgress < fadeInAt) {
                return;
            } else {
                if (welcomeGraphicProgress < fadeInAt + fadeTime) {
                    alpha = (int) (alpha * (welcomeGraphicProgress - fadeInAt) * 1f/(float)fadeTime);
                    if (alpha < 4)
                        alpha = 4; // again, anything less than 4 is opaque for some reason
                }
            }
            
            event.setCanceled(true);
            
            GL11.glColor4f(255, 255, 255, ((float)alpha / 255f));

            GL11.glPushMatrix();
            
            int startX = event.posX + screenWidth / 40;
            int startY = event.posY - 50;
            
            re.bindTexture(new ResourceLocation("reignadditionals:textures/gui/reign_white.png"));
            drawTexturedRect(startX, startY, 0, 0, 100, 61, 100, 61);
            
            int argb = ( alpha << 24 ) | ( 255 << 16 ) | ( 255 << 8) | 255;
            String text = "HostileNetworks.com";
            mc.fontRenderer.drawString(text, startX + 50 - mc.fontRenderer.getStringWidth(text) / 2, startY + 61, argb, true);
            
            
            GL11.glPopMatrix();
            GL11.glColor4f(255, 255, 255, 255f);
        }
    }
    
    public static void triggerPlayerJoin() {
        lastDay = -1;
        fireWelcomeGraphic = true;
        welcomeGraphicProgress = 0;
    }
    
    private static void drawTexturedRect(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
        float f = 1F / (float)textureWidth;
        float f1 = 1F / (float)textureHeight;
        Tessellator.instance.startDrawingQuads();
        Tessellator.instance.addVertexWithUV((double)(x), (double)(y + height), 0, (double)((float)(u) * f), (double)((float)(v + height) * f1));
        Tessellator.instance.addVertexWithUV((double)(x + width), (double)(y + height), 0, (double)((float)(u + width) * f), (double)((float)(v + height) * f1));
        Tessellator.instance.addVertexWithUV((double)(x + width), (double)(y), 0, (double)((float)(u + width) * f), (double)((float)(v) * f1));
        Tessellator.instance.addVertexWithUV((double)(x), (double)(y), 0, (double)((float)(u) * f), (double)((float)(v) * f1));
        Tessellator.instance.draw();
    }
    
    // based on method with same name from GuiIngame
    private void renderInventorySlot(ItemStack itemstack, int x, int y) {

        if (itemstack != null) {
            
            
            /*
            GL11.glPushMatrix();
            
            float f2 = 1.0F + f1 / 5.0F;
            GL11.glTranslatef((float)(x + 8), (float)(y + 12), 0.0F);
            GL11.glScalef(1.0F / f2, (f2 + 1.0F) / 2.0F, 1.0F);
            GL11.glTranslatef((float)(-(x + 8)), (float)(-(y + 12)), 0.0F);
            */

            //itemRenderer.renderItemAndEffectIntoGUI(this.mc.fontRenderer, this.mc.getTextureManager(), itemstack, x, y);

            //if (f1 > 0.0F)
            //{
            //    GL11.glPopMatrix();
            //}

            //itemRenderer.renderItemOverlayIntoGUI(this.mc.fontRenderer, this.mc.getTextureManager(), itemstack, x, y);
        }
    }
}
