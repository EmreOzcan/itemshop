package net.guacamolenetwork.itemshop.classes;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Locale;

public class ItemValues {
    private Material material;
    private boolean isSellable;
    private double sellWorth;
    private boolean isBuyable;
    private double buyCost;

    public Material getMaterial() {
        return material;
    }

    public boolean isSellable() {
        return isSellable;
    }

    public double getSellWorth() {
        return sellWorth;
    }

    public boolean isBuyable() {
        return isBuyable;
    }

    public double getBuyCost() {
        return buyCost;
    }

    public ItemValues(Material material, boolean isSellable, double sellWorth, boolean isBuyable, double buyCost) {
        this.material = material;
        this.isSellable = isSellable;
        this.sellWorth = sellWorth;
        this.isBuyable = isBuyable;
        this.buyCost = buyCost;
    }

    public static ItemValues getFor(Material item, FileConfiguration config) {
        String worthPath = getWorthPath(item);
        boolean sellable = config.isDouble(worthPath);
        double worth = sellable ? config.getDouble(worthPath) : 0;

        String costPath = getCostPath(item);
        boolean buyable = config.isDouble(costPath);
        double cost = buyable ? config.getDouble(costPath) : 0;

        return new ItemValues(item, sellable, worth, buyable, cost);
    }

    public static String getCostPath(Material material) {
        return String.format("item-worths.%s.buy", material.toString().toLowerCase(Locale.ENGLISH));
    }

    public static String getWorthPath(Material material) {
        return String.format("item-worths.%s.sell", material.toString().toLowerCase(Locale.ENGLISH));
    }
}
