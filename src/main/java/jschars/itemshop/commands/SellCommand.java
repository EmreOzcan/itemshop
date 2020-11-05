package jschars.itemshop.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import jschars.itemshop.Itemshop;
import jschars.itemshop.classes.ItemValues;
import jschars.itemshop.classes.SellMultiplier;
import jschars.itemshop.compat.OffhandCompat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;

@CommandPermission("itemshop.sell")
@CommandAlias("sell")
public class SellCommand extends BaseCommand {
    final Itemshop plugin;

    public SellCommand(Itemshop plugin) {
        this.plugin = plugin;
    }

    private int removeAndCount(Inventory inventory, Material material, int maxAmount) {
        HashMap<Integer, ItemStack> unremovedItems = inventory.removeItem(new ItemStack(material, maxAmount));
        int totalItemsUnsold = 0;
        for (ItemStack stack : unremovedItems.values()) {
            totalItemsUnsold += stack.getAmount();
        }
        return maxAmount - totalItemsUnsold;
    }

    @Subcommand("all|inventory")
    @CommandPermission("itemshop.sell.inventory")
    @Description("Sell all sellable items in your inventory")
    public void onInventory(Player player) {
        PlayerInventory inventory = player.getInventory();
        FileConfiguration config = plugin.getConfig();
        SellMultiplier multi = SellMultiplier.best(player, config);

        int totalItemsSold = 0;
        double totalWorthOfItems = 0.0;
        for (ItemStack stack : inventory.getContents()) {
            if (stack == null) {
                continue;
            }
            ItemValues itemValues = ItemValues.getFor(stack.getType(), config);
            if (!itemValues.isSellable()) {
                continue;
            }
            totalWorthOfItems += itemValues.getSellWorth() * multi.getMultiplier() * stack.getAmount();
            totalItemsSold += stack.getAmount();
            stack.setAmount(0);
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                "&aSold &c%d&a items for a total of &c$%.2f&a.",
                totalItemsSold, totalWorthOfItems)));

        Itemshop.getEconomy().depositPlayer(player, totalWorthOfItems);
    }

    @Subcommand("hand")
    @CommandPermission("itemshop.sell.hand")
    @Description("Sell items of the type you're currently holding")
    @CommandCompletion("1|32|64")
    public void onHand(Player player, @Default("2147483647") Integer maxAmount) {
        onMaterial(player, OffhandCompat.getItemInMainHand(player).getType(), maxAmount);
    }

    @Subcommand("material")
    @CommandPermission("itemshop.sell.material")
    @Description("Sell items of the type you specified")
    @CommandCompletion("@itemshop-sellables 1|32|64")
    public void onMaterial(Player player, Material material, @Default("2147483647") Integer maxAmount) {
        PlayerInventory inventory = player.getInventory();
        FileConfiguration config = plugin.getConfig();
        SellMultiplier multi = SellMultiplier.best(player, config);
        ItemValues itemValues = ItemValues.getFor(material, config);

        if (!itemValues.isSellable()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis item cannot be sold."));
            return;
        }

        double sellPrice = itemValues.getSellWorth() * multi.getMultiplier();

        int soldItems = removeAndCount(inventory, material, maxAmount);
        double total = sellPrice * soldItems;

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                "&aSold &c%d&a of &c%s&a for &c$%.2f&a (&c$%.2f&a each).",
                soldItems, material.name(), total, sellPrice)));

        Itemshop.getEconomy().depositPlayer(player, total);
    }

    @Subcommand("multipliers")
    @CommandPermission("itemshop.multipliers")
    @Description("See active multipliers")
    public void onMultipliers(Player player) {
        SellMultiplier.listMultipliers(player, plugin);
    }

    @HelpCommand
    public void doHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}