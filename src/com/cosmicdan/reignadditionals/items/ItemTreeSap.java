package com.cosmicdan.reignadditionals.items;

import com.cosmicdan.reignadditionals.Main;
import net.minecraft.item.Item;

public class ItemTreeSap extends Item {
    String unlocalizedName;

    protected ItemTreeSap(String unlocalizedName) {
        this.unlocalizedName = unlocalizedName;
        this.setUnlocalizedName(unlocalizedName);
        this.setTextureName(Main.MODID + ":" + unlocalizedName);
    }
}
