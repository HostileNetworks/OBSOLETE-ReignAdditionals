package com.cosmicdan.reignadditionals.items;

import java.util.List;

import com.cosmicdan.reignadditionals.util.TextUtils;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.util.EnumHelper;

public class ModItems {
    public static ToolMaterial FLINT = EnumHelper.addToolMaterial("FLINT", 0, 131, 1.0f, 0, 5);
    
    public static Item FLINT_HANDAXE;
    public static Item TREESAP;
    public static Item TELEPORTER;
    public static Item MATERIALIZER;
    public static Item MILK_BOTTLE;
    public static Item DEBUG_THINGY;
    public static Item RAWRUBBER;
    public static Item SALTCLUMP;
    
    public static final void init() {
        FLINT_HANDAXE = new ItemFlintAxe(FLINT, "flintHandaxe");
        GameRegistry.registerItem(FLINT_HANDAXE, "flintHandaxe");
        TREESAP = new ItemBasic("treeSap");
        GameRegistry.registerItem(TREESAP, "treeSap");
        TELEPORTER = new ItemTeleporter("teleporter");
        GameRegistry.registerItem(TELEPORTER, "teleporter");
        MATERIALIZER = new ItemMaterializer("materializer");
        GameRegistry.registerItem(MATERIALIZER, "materializer");
        MILK_BOTTLE = new ItemMilkBottle("milkBottle");
        GameRegistry.registerItem(MILK_BOTTLE, "milkBottle");
        DEBUG_THINGY = new ItemDebugThingy("debugThingy");
        GameRegistry.registerItem(DEBUG_THINGY, "debugThingy");
        RAWRUBBER = new ItemBasic("rawRubber");
        GameRegistry.registerItem(RAWRUBBER, "rawRubber");
        SALTCLUMP = new ItemBasic("saltClump");
        GameRegistry.registerItem(SALTCLUMP, "saltClump");
    }
    
    public static final void getTooltip(String unlocalizedName, List tooltip) {
        String tooltipText;
        tooltipText = LanguageRegistry.instance().getStringLocalization(unlocalizedName + ".tooltip.more");
        if (!tooltipText.isEmpty()) {
            if (!GuiScreen.isShiftKeyDown()) {
                tooltipText = LanguageRegistry.instance().getStringLocalization("tooltip.more");
            }
        } else {
            tooltipText = LanguageRegistry.instance().getStringLocalization(unlocalizedName + ".tooltip");
        }
        
        if (tooltipText != null) {
            for (String line : TextUtils.splitTextString(tooltipText, 32)) {
                tooltip.add(line);
            }
        }
        
    }
}
