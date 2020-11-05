package jschars.itemshop.compat;

import org.bukkit.Material;

public class MaterialCompat {
    public static boolean isItem(Material material) {
        try {
            return material.isItem();
        } catch (NoSuchMethodError e) {
            return true;
        }
    }
}
