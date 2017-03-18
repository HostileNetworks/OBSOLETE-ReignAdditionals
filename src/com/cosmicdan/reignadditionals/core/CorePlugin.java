package com.cosmicdan.reignadditionals.core;

import java.util.Map;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;

import com.cosmicdan.reignadditionals.Main;
import com.cosmicdan.reignadditionals.core.transformers.*;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name(value = "ReignAdditionalsCore")
@IFMLLoadingPlugin.MCVersion(value = "1.7.10")
@IFMLLoadingPlugin.TransformerExclusions(value = "com.cosmicdan.reignadditionals.")
@IFMLLoadingPlugin.SortingIndex(value = 1001) // How early your core mod is called - Use > 1000 to work with srg names
public class CorePlugin implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{
          TransformBlockLiquid.class.getName(),
          TransformBlockLiquid2.class.getName(),
          TransformBlockDynamicLiquid.class.getName()
        };
    }

    @Override
    public String getModContainerClass() {
        try {
            // don't try and set window title on windows/bundled server 
            Sys.initialize();
        } catch (UnsatisfiedLinkError e) {
            Main.IS_CLIENT = false;
        }
        
        try {
         // don't try and set window title on linux/dedicated server 
            Class.forName("org.lwjgl.Sys");
        } catch( ClassNotFoundException e ) {
            Main.IS_CLIENT = false;
        }
        
        if (Main.IS_CLIENT) {
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
