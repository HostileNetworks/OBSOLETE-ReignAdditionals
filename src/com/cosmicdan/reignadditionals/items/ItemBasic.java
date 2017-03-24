package com.cosmicdan.reignadditionals.items;

import com.cosmicdan.reignadditionals.Main;
import net.minecraft.item.Item;

public class ItemBasic extends Item {
    String unlocalizedName;

    protected ItemBasic(String unlocalizedName) {
        this.unlocalizedName = unlocalizedName;
        this.setUnlocalizedName(unlocalizedName);
        this.setTextureName(Main.MODID + ":" + unlocalizedName);
    }
}
