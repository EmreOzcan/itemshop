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

@CommandPermission("itemshop.sell.worth")
@CommandAlias("worth")
public class WorthCommand extends BaseCommand {
    Itemshop plugin;

    public WorthCommand(Itemshop plugin) {
        this.plugin = plugin;
    }

    @CommandPermission("itemshop.sell.worth")
    @Description("See the sell worth of a material")
    @CommandCompletion("@itemMaterials 1|32|64")
    @Default
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

    @CommandPermission("itemshop.sell.worth.set")
    @Description("Set the bare sell worth of a material")
    @CommandCompletion("@itemMaterials 1.00|1.50|75")
    @Subcommand("set")
    @CommandAlias("setworth")
    public void onSetWorth(CommandSender sender, Material material, double worth) {
        FileConfiguration config = plugin.getConfig();
        config.set(ItemValues.getWorthPath(material), worth);
        plugin.saveConfig();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                "&aUpdated the sell worth of &a%s&a to &c$%.2f&a.",
                material.name(), worth)));
    }

    @HelpCommand
    public void doHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    /*@CommandPermission("itemshop.sell.worth.missing")
    @Description("List all unsellable items")
    @Subcommand("missing")
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
    }*/
}