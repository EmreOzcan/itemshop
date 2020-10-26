package net.guacamolenetwork.itemshop.classes;

import net.guacamolenetwork.itemshop.Itemshop;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Multiplier {
    private final double multiplier;

    private final String name;

    public double getMultiplier() {
        return multiplier;
    }

    public String getName() {
        return name;
    }

    public Multiplier(double multiplier, String name) {
        this.multiplier = multiplier;
        this.name = name;
    }

    public static Multiplier getDefault() {
        return new Multiplier(1, "");
    }

    public static Multiplier buyFromName(String multiplier, FileConfiguration config) {
        String valuePath = "modifiers."+multiplier+".buy";
        String namePath = "modifiers."+multiplier+".name";
        return new Multiplier(
                config.isDouble(valuePath) ? config.getDouble(valuePath) : 1,
                config.isString(namePath) ? config.getString(namePath) : ""
        );
    }

    public static Multiplier buyBest(Player player, FileConfiguration config) {
        Multiplier selectedMultiplier = Multiplier.getDefault();
        ConfigurationSection modifiers = config.getConfigurationSection("modifiers");
        assert modifiers != null;
        for (String multiplier : modifiers.getKeys(false)) {
            if (player.hasPermission("itemshop.m."+multiplier)) {
                Multiplier tempMultiplier = buyFromName(multiplier, config);
                if (tempMultiplier.getMultiplier() < selectedMultiplier.getMultiplier()) {
                    // A lower buy multiplier is better than a higher one, so we select it only if it's lower.
                    selectedMultiplier = tempMultiplier;
                }
            }
        }
        return selectedMultiplier;
    }

    public static Multiplier sellFromName(String multiplier, FileConfiguration config) {
        String valuePath = "modifiers."+multiplier+".sell";
        String namePath = "modifiers."+multiplier+".name";
        return new Multiplier(
                config.isDouble(valuePath) ? config.getDouble(valuePath) : 1,
                config.isString(namePath) ? config.getString(namePath) : ""
        );
    }

    public static Multiplier sellBest(Player player, FileConfiguration config) {
        Multiplier selectedMultiplier = Multiplier.getDefault();
        ConfigurationSection modifiers = config.getConfigurationSection("modifiers");
        assert modifiers != null;
        for (String multiplier : modifiers.getKeys(false)) {
            if (player.hasPermission("itemshop.m."+multiplier)) {
                Multiplier tempMultiplier = sellFromName(multiplier, config);
                if (tempMultiplier.getMultiplier() > selectedMultiplier.getMultiplier()) {
                    // A higher sell multiplier is better than a lower one, so we select it only if it's higher.
                    selectedMultiplier = tempMultiplier;
                }
            }
        }
        return selectedMultiplier;
    }

    public static void listMultipliers(Player player, Itemshop plugin) {
        FileConfiguration config = plugin.getConfig();
        Multiplier sell = Multiplier.sellBest(player, config);
        Multiplier buy = Multiplier.buyBest(player, config);

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYour multipliers:"));
        if (player.hasPermission("itemshop.sell")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                    "&aSell multiplier: &c%.2fx%s",
                    sell.getMultiplier(),
                    !sell.getName().equals("") ? "&a (&c"+sell.getName()+"&a)": ""
            )));
        }
        if (player.hasPermission("itemshop.buy")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                    "&aBuy multiplier: &c%.2fx%s",
                    buy.getMultiplier(),
                    !buy.getName().equals("") ? "&a (&c"+sell.getName()+"&a)": ""
            )));
        }
        if (!player.hasPermission("itemshop.buy") && !player.hasPermission("itemshop.sell")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have any multipliers."));
        }
    }
}
