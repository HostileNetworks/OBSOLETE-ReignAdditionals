package com.cosmicdan.reignadditionals.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.CRC32;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;

import com.cosmicdan.reignadditionals.Main;
import com.cosmicdan.reignadditionals.core.transformers.*;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.minecraft.launchwrapper.Launch;

@IFMLLoadingPlugin.Name(value = "ReignAdditionalsCore")
@IFMLLoadingPlugin.MCVersion(value = "1.7.10")
@IFMLLoadingPlugin.TransformerExclusions(value = "com.cosmicdan.reignadditionals.")
@IFMLLoadingPlugin.SortingIndex(value = 1001) // How early your core mod is called - Use > 1000 to work with srg names
public class CorePlugin implements IFMLLoadingPlugin {
    public static boolean isDevEnv() {
        return (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
    }
    
    @Override
    public String[] getASMTransformerClass() {
        ArrayList<String> transformers = new ArrayList<String>();
        transformers.add(TransformBlockLiquid.class.getName());
        transformers.add(TransformBlockLiquid2.class.getName());
        transformers.add(TransformBlockDynamicLiquid.class.getName());
        transformers.add(TransformBlockFire.class.getName());
        
        /********************************************************************************
         * THIS VERSION OF REIGN ADDITIONALS IS ONLY ALLOWED FOR USE IN THE REIGN MODPACK.
         * 
         * IF YOU WANT TO USE THIS MOD FOR YOUR OWN WORK, REMOVE THIS SAFETY CHECK, AND
         * YOU *MUST* ALSO REMOVE THE REIKA MODS TRANSFORMER(S). UNLESS, OBVIOUSLY, IF
         * REIKA ALSO GIVES YOU PERMISSION TO USE THESE CHANGES.
         ********************************************************************************/
        
        boolean isReignModpack = true;
        
        // verify that the splash file is set to the reign logo path
        File splashCfg = new File("config/splash.properties");
        try {
            Scanner scanner = new Scanner(splashCfg);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains("forgeTexture=")) {
                    if (!line.equals("forgeTexture=ReignModpack\\:splash.png")) {
                        isReignModpack = false;
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            isReignModpack = false;
        }
        
        // verify that the reign logo is found and matches our CRC32
        File splashPng = new File("resources/assets/ReignModpack/splash.png");
        try {
            FileInputStream splashPngIs = new FileInputStream(splashPng);
            CRC32 crcMaker = new CRC32();
            byte[] buffer = new byte[5353];
            int bytesRead;
            while((bytesRead = splashPngIs.read(buffer)) != -1) {
                crcMaker.update(buffer, 0, bytesRead);
            }
            String crc = Long.toHexString(crcMaker.getValue());
            splashPngIs.close();
            if (!crc.equals("24f09c5b"))
                isReignModpack = false;
        } catch (FileNotFoundException e) {
            isReignModpack = false;
        } catch (IOException e) {
            isReignModpack = false;
        }
        
        if (!isReignModpack)
            throw new IllegalStateException("Reign Additionals may not be taken as-is for use in another Modpack. You need to fork it and modify it for your own purposes, removing the Reika transformers.");
        
        // also verify that the known-version Reika mods exist (but only in non-dev environment)
        if (!isDevEnv()) {
            File jarCheck = new File("mods/RotaryCraft 1.7.10 V16d.jar");
            if (!jarCheck.exists() || !jarCheck.isFile())
                throw new IllegalStateException("A specific version of RotaryCraft was not found. You need to update ReignAdditionals, Dan!");
            jarCheck = new File("mods/ReactorCraft 1.7.10 V16d.jar");
            if (!jarCheck.exists() || !jarCheck.isFile())
                throw new IllegalStateException("A specific version of ReactorCraft was not found. You need to update ReignAdditionals, Dan!");
        }
        
        transformers.add(ReactorCraftOreGeneratorTransformer.class.getName());
        
        /**
         * END SPECIAL TRANSFORMERS/SAFETY CHECKS
         */
        
        return transformers.toArray(new String[transformers.size()]);
    }

    @Override
    public String getModContainerClass() {
        try {
            // don't try and set window title on linux/dedicated server 
            Class.forName("org.lwjgl.Sys");
            try {
                // don't try and set window title on windows/bundled server 
                Sys.initialize();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            if (!Display.getTitle().startsWith("Minecraft"))
                                continue;
                                
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            
                            Display.setTitle("Minecraft: Reign Modpack");
                            if (Display.getTitle().contains("Reign"))
                                break;
                        }
                    }
                }).start();
            } catch (UnsatisfiedLinkError e) {
                Main.IS_CLIENT = false;
            }
        } catch( ClassNotFoundException e ) {
            Main.IS_CLIENT = false;
        }
        
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getSetupClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getAccessTransformerClass() {
        // TODO Auto-generated method stub
        return null;
    }
}
