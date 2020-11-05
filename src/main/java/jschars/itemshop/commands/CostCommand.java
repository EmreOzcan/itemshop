package jschars.itemshop.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import jschars.itemshop.Itemshop;
import jschars.itemshop.compat.OffhandCompat;
import jschars.itemshop.config.ValueConfig;
import jschars.itemshop.itemdata.ItemValues;
import jschars.itemshop.multiplier.BuyMultiplier;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermission("itemshop.buy.cost")
@CommandAlias("cost")
public class CostCommand extends BaseCommand {
    final Itemshop plugin;

    public CostCommand(Itemshop plugin) {
        this.plugin = plugin;
    }

    @CommandPermission("itemshop.buy.cost")
    @Description("See the buy cost of a material")
    @CommandCompletion("@itemshop-buyables 1|32|64")
    @Default
    public void onCost(CommandSender sender, @Optional Material material, @Default("1") Integer amount) {
        if (material == null) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                material = OffhandCompat.getItemInMainHand(player).getType();
            } else {
                sender.sendMessage(ChatColor.RED+"Console must specify material");
                return;
            }
        }
        ItemValues itemValues = ItemValues.getFor(material, plugin.getValueConfig());
        BuyMultiplier multi = sender instanceof Player ? BuyMultiplier.best(((Player) sender), plugin.getMultiplierConfig()) : BuyMultiplier.unit;

        if (!itemValues.isBuyable()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                    "&c%s cannot be bought.",
                    material.name()
            )));
            return;
        }

        double cost = itemValues.getBuyCost();

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                "&c%d&a of &c%s&a costs &c$%.2f&a (&c$%.2f&a each)%s&a.",
                amount,
                material.name(),
                cost * amount * multi.getMultiplier(),
                cost * multi.getMultiplier(),
                multi.getMultiplier() != 1 ? String.format(" (&c%.2fx %s&a)", multi.getMultiplier(), !multi.getName().equals("") ? multi.getName() : "multiplier") : ""
        )));
    }

    @CommandPermission("itemshop.buy.cost.set")
    @Description("Set the bare buy cost of a material")
    @CommandCompletion("@itemshop-all-items 1.00|1.50|75")
    @Subcommand("set")
    @CommandAlias("setcost")
    public void onSetCost(CommandSender sender, Material material, double cost) {
        ValueConfig config = plugin.getValueConfig();
        config.getConfig().set(ItemValues.getCostPath(material), cost);
        config.saveConfig();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                "&aUpdated the buy cost of &a%s&a to &c$%.2f&a.",
                material.name(), cost)));
    }

    @HelpCommand
    public void doHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}