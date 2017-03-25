package com.cosmicdan.reignadditionals.core;

import java.util.ArrayList;
import java.util.Map;

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
        
        // TODO: Add the ReactorCraft transformer IF Reign Modpack is detected
        transformers.add(ReactorCraftOreGeneratorTransformer.class.getName());
        
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
