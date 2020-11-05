package jschars.itemshop.multiplier;

import jschars.itemshop.Itemshop;
import jschars.itemshop.config.MultiplierConfig;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

class Multiplier {

    public static final Multiplier unit = new Multiplier(1, "");

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

    public static void listMultipliers(Player player, Itemshop plugin) {
        MultiplierConfig pluginMultiplierConfig = plugin.getMultiplierConfig();
        SellMultiplier sell = SellMultiplier.best(player, pluginMultiplierConfig);
        BuyMultiplier buy = BuyMultiplier.best(player, pluginMultiplierConfig);

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
