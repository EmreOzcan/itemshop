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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@CommandAlias("itemshop|ishop")
public class MainCommand extends BaseCommand {

    Itemshop plugin;

    public MainCommand(Itemshop plugin) {
        this.plugin = plugin;
    }

    @CommandPermission("itemshop.sell")
    @Subcommand("sell|s")
    @CommandAlias("sell")
    public class SellCommand extends BaseCommand {

        // region Sell command helper methods
        private int removeAndCount(Inventory inventory, Material material, int maxAmount) {
            HashMap<Integer, ItemStack> unremovedItems = inventory.removeItem(new ItemStack(material, maxAmount));
            int totalItemsUnsold = 0;
            for (ItemStack stack : unremovedItems.values()) {
                totalItemsUnsold += stack.getAmount();
            }
            return maxAmount - totalItemsUnsold;
        }
        // endregion

        @Subcommand("all|a|inventory|inv")
        @CommandPermission("itemshop.sell.inventory")
        @Description("Sell all sellable items in your inventory")
        public void onInventory(Player player) {
            PlayerInventory inventory = player.getInventory();
            FileConfiguration config = plugin.getConfig();
            Multiplier sellMultiplier = Multiplier.sellBest(player, config);

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
                totalWorthOfItems += itemValues.getSellWorth() * sellMultiplier.getMultiplier() * stack.getAmount();
                totalItemsSold += stack.getAmount();
                stack.setAmount(0);
            }

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                    "&aSold &c%d&a items for a total of &c$%.2f&a.",
                    totalItemsSold, totalWorthOfItems)));

            Itemshop.getEconomy().depositPlayer(player, totalWorthOfItems);
        }

        @Subcommand("hand|h")
        @CommandPermission("itemshop.sell.hand")
        @Description("Sell items of the type you're currently holding")
        @CommandCompletion("1|32|64")
        public void onHand(Player player, @Default("2147483647") Integer maxAmount) {
            onMaterial(player, player.getInventory().getItemInMainHand().getType(), maxAmount);
        }

        @Subcommand("material|m|of|o")
        @CommandPermission("itemshop.sell.material")
        @Description("Sell items of the type you specified")
        @CommandCompletion("@itemMaterials 1|32|64")
        public void onMaterial(Player player, Material material, @Default("2147483647") Integer maxAmount) {
            PlayerInventory inventory = player.getInventory();
            FileConfiguration config = plugin.getConfig();
            Multiplier sellMultiplier = Multiplier.sellBest(player, config);
            ItemValues itemValues = ItemValues.getFor(material, config);

            if (!itemValues.isSellable()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis item cannot be sold."));
                return;
            }

            double sellPrice = itemValues.getSellWorth() * sellMultiplier.getMultiplier();

            int soldItems = removeAndCount(inventory, material, maxAmount);
            double total = sellPrice * soldItems;

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                    "&aSold &c%d&a of &c%s&a for &c$%.2f&a (&c$%.2f&a each).",
                    soldItems, material.name(), total, sellPrice)));

            Itemshop.getEconomy().depositPlayer(player, total);
        }


        // todo turn worth subcommands into a separate class.
        @Subcommand("worth|w")
        @CommandPermission("itemshop.sell.worth")
        @Description("See the sell worth of a material")
        @CommandCompletion("@itemMaterials 1|32|64")
        @CommandAlias("worth")
        public void onWorth(CommandSender sender, @Optional Material material, @Default("1") Integer amount) {
            FileConfiguration config = plugin.getConfig();
            if (material == null && sender instanceof Player) {
                Player player = (Player) sender;
                material = player.getInventory().getItemInMainHand().getType();
            }
            ItemValues itemValues = ItemValues.getFor(material, config);
            Multiplier multiplier = sender instanceof Player ? Multiplier.sellBest(((Player) sender), config) : Multiplier.getDefault();

            if (!itemValues.isSellable()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis item cannot be sold."));
                return;
            }

            double worth = itemValues.getSellWorth();

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                    "&c%d&a of &c%s&a is worth &c$%.2f&a (&c$%.2f&a each)%s&a.",
                    amount,
                    material.name(),
                    worth * amount * multiplier.getMultiplier(),
                    worth * multiplier.getMultiplier(),
                    multiplier.getMultiplier() != 1 ? String.format(" (&c%.2fx %s&a)", multiplier.getMultiplier(), !multiplier.getName().equals("") ? multiplier.getName() : "multiplier") : ""
            )));
        }

        @Subcommand("worth|w set|s")
        @CommandPermission("itemshop.sell.worth.set")
        @Description("Set the bare sell worth of a material")
        @CommandCompletion("@itemMaterials 1.00|1.50|75")
        @CommandAlias("setworth")
        public void onSetWorth(CommandSender sender, Material material, double worth) {
            FileConfiguration config = plugin.getConfig();
            config.set(ItemValues.getWorthPath(material), worth);
            plugin.saveConfig();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                    "&aUpdated the sell worth of &a%s&a to &c$%.2f&a.",
                    material.name(), worth)));
        }

        @Subcommand("worth|w missing|m")
        @CommandPermission("itemshop.sell.worth.missing")
        @Description("List all unsellable items")
        public void onMissingWorth(CommandSender sender) {
            FileConfiguration config = plugin.getConfig();
            List<String> missingMaterials = EnumSet.allOf(Material.class).stream()
                    .filter(Material::isItem)
                    .filter(mat -> !config.isDouble(ItemValues.getWorthPath(mat)))
                    .map(Material::name)
                    .collect(Collectors.toList());
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                    "&aFound &c%d&a materials with no sell worth:",
                    missingMaterials.size())));
            for (String materialName : missingMaterials) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                        "&a- %s",
                        materialName)));
            }
        }


        @HelpCommand
        @Description("Show this help menu")
        public void onHelp(CommandHelp help) {
            help.showHelp();
        }
    }

    @CommandPermission("itemshop.multipliers")
    @Subcommand("multipliers|multis|modifiers|mods|m")
    public class ModifierCommand extends BaseCommand {
        @Default
        public void sayModifiers(Player player) {
            FileConfiguration config = plugin.getConfig();
            Multiplier sell = Multiplier.sellBest(player, config);
            Multiplier buy = Multiplier.buyBest(player, config);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                    "&aYour multipliers:\n&aSell multiplier: &c%.2fx%s\n&aBuy multiplier: &c%.2fx",
                    sell.getMultiplier(),
                    !sell.getName().equals("") ? "&a (&c"+sell.getName()+"&a)" : "",
                    buy.getMultiplier(),
                    !buy.getName().equals("") ? "&a (&c"+buy.getName()+"&a)" : ""
            )));
        }
    }


    @CommandPermission("itemshop.buy")
    @Subcommand("buy|b")
    @CommandAlias("buy")
    public class BuyCommand extends BaseCommand {

        // region Buy command helper methods
        private int getAvaliableSlots(Inventory inv){
            int empty = 0;
            for (ItemStack item: inv.getContents()) {
                if(item == null) {
                    empty++;
                }
            }
            return empty;
        }
        // endregion

        @Subcommand("hand|h")
        @CommandPermission("itemshop.buy.hand")
        @Description("Buy items of the type you're currently holding")
        @CommandCompletion("1|32|64")
        public void onHand(Player player, @Default("1") Integer amount) {
            onMaterial(player, player.getInventory().getItemInMainHand().getType(), amount);
        }

        @Default
        @CommandPermission("itemshop.buy.material")
        @Description("Buy items of the type you specified")
        @CommandCompletion("@itemMaterials 1|32|64")
        public void onMaterial(Player player, Material material, @Default("1") Integer amount) {
            PlayerInventory inventory = player.getInventory();
            FileConfiguration config = plugin.getConfig();
            Multiplier buyMultiplier = Multiplier.buyBest(player, config);
            ItemValues itemValues = ItemValues.getFor(material, config);

            if (!itemValues.isBuyable()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis item cannot be bought."));
                return;
            }

            double buyPrice = itemValues.getBuyCost() * buyMultiplier.getMultiplier();
            double total = buyPrice * amount;
            if (Itemshop.getEconomy().has(player, total)) {
                int requiredSlots = (int) (Math.ceil((double) amount)/ ((double) material.getMaxStackSize()));
                int avaliableSlots = getAvaliableSlots(inventory);
                if (avaliableSlots >= requiredSlots) {
                    HashMap<Integer, ItemStack> failedItems = inventory.addItem(new ItemStack(material, amount));

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                            "&aYou have been charged &c$%.2f&a for &c%d&a of &c%s&a.",
                            total, amount, material.name())));
                    Itemshop.getEconomy().withdrawPlayer(player, total);

                    if (!failedItems.isEmpty()) {
                        int totalItemsFailed = 0;
                        for (ItemStack stack : failedItems.values()) {
                            totalItemsFailed += stack.getAmount();
                        }
                        double refundPrice = buyPrice * totalItemsFailed;
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                                "&cError: Failed to give %d items. You will be refunded $%.2f.",
                                totalItemsFailed, refundPrice)));
                        Itemshop.getEconomy().depositPlayer(player, refundPrice);
                    }

                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                            "&cYou need to have %d available inventory slots for this but you only have %d.",
                            requiredSlots, avaliableSlots)));
                }
            }
        }

        // todo turn cost subcommands into a separate class

        @Subcommand("cost|c")
        @CommandPermission("itemshop.buy.cost")
        @Description("See the buy cost of a material")
        @CommandCompletion("@itemMaterials 1|32|64")
        @CommandAlias("cost")
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

        @Subcommand("cost|c set|s")
        @CommandPermission("itemshop.buy.cost.set")
        @Description("Set the bare buy cost of a material")
        @CommandCompletion("@itemMaterials 1.00|1.50|75")
        @CommandAlias("setcost")
        public void onSetCost(CommandSender sender, Material material, double worth) {
            FileConfiguration config = plugin.getConfig();
            config.set(ItemValues.getCostPath(material), worth);
            plugin.saveConfig();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                    "&aUpdated the buy cost of &a%s&a to &c$%.2f&a.",
                    material.name(), worth)));
        }

        @Subcommand("cost|c missing|m")
        @CommandPermission("itemshop.sell.worth.missing")
        @Description("List all unbuyable items")
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
        }


        @HelpCommand
        @Description("Show this help menu")
        public void onHelp(CommandHelp help) {
            help.showHelp();
        }
    }

}
