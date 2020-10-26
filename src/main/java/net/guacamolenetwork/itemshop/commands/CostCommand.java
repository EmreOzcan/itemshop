package net.guacamolenetwork.itemshop.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.guacamolenetwork.itemshop.Itemshop;
import net.guacamolenetwork.itemshop.classes.ItemValues;
import net.guacamolenetwork.itemshop.classes.Multiplier;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

@CommandPermission("itemshop.buy.cost")
@CommandAlias("cost")
public class CostCommand extends BaseCommand {
    Itemshop plugin;

    public CostCommand(Itemshop plugin) {
        this.plugin = plugin;
    }

    @CommandPermission("itemshop.buy.cost")
    @Description("See the buy cost of a material")
    @CommandCompletion("@itemMaterials 1|32|64")
    @Default
    public void onCost(CommandSender sender, @Optional Material material, @Default("1") Integer amount) {
        FileConfiguration config = plugin.getConfig();
        if (material == null && sender instanceof Player) {
            Player player = (Player) sender;
            material = player.getInventory().getItemInMainHand().getType();
        }
        ItemValues itemValues = ItemValues.getFor(material, config);
        Multiplier multiplier = sender instanceof Player ? Multiplier.buyBest(((Player) sender), config) : Multiplier.getDefault();

        if (!itemValues.isBuyable()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis item cannot be bought."));
            return;
        }

        double cost = itemValues.getBuyCost();

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                "&c%d&a of &c%s&a costs &c$%.2f&a (&c$%.2f&a each)%s&a.",
                amount,
                material.name(),
                cost * amount * multiplier.getMultiplier(),
                cost * multiplier.getMultiplier(),
                multiplier.getMultiplier() != 1 ? String.format(" (&c%.2fx %s&a)", multiplier.getMultiplier(), !multiplier.getName().equals("") ? multiplier.getName() : "multiplier") : ""
        )));
    }

    @CommandPermission("itemshop.buy.cost.set")
    @Description("Set the bare buy cost of a material")
    @CommandCompletion("@itemMaterials 1.00|1.50|75")
    @Subcommand("set")
    @CommandAlias("setcost")
    public void onSetCost(CommandSender sender, Material material, double worth) {
        FileConfiguration config = plugin.getConfig();
        config.set(ItemValues.getCostPath(material), worth);
        plugin.saveConfig();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                "&aUpdated the buy cost of &a%s&a to &c$%.2f&a.",
                material.name(), worth)));
    }

    @HelpCommand
    public void doHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    /*@CommandPermission("itemshop.sell.worth.missing")
    @Description("List all unbuyable items")
    @Subcommand("missing")
    public void onMissingCost(CommandSender sender) {
        FileConfiguration config = plugin.getConfig();
        List<String> missingMaterials = EnumSet.allOf(Material.class).stream()
                .filter(Material::isItem)
                .filter(mat -> !config.isDouble(ItemValues.getCostPath(mat)))
                .map(Material::name)
                .collect(Collectors.toList());
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                "&aFound &c%d&a materials with no buy cost:",
                missingMaterials.size())));
        for (String materialName : missingMaterials) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                    "&a- %s",
                    materialName)));
        }
    }*/
}