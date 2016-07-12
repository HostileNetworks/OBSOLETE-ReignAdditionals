package com.cosmicdan.reignadditionals.items;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.util.EnumHelper;

public class ModItems {
    public static ToolMaterial FLINT = EnumHelper.addToolMaterial("FLINT", 0, 131, 1.0f, 0, 5);
    
    public static Item FLINT_HANDAXE;
    
    public static final void init() {
        FLINT_HANDAXE = new ItemFlintAxe(FLINT, "flintHandaxe");
        GameRegistry.registerItem(FLINT_HANDAXE, "flintHandaxe");
    }
}
