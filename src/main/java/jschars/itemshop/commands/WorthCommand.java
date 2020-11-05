package jschars.itemshop.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import jschars.itemshop.Itemshop;
import jschars.itemshop.compat.OffhandCompat;
import jschars.itemshop.config.MultiplierConfig;
import jschars.itemshop.config.ValueConfig;
import jschars.itemshop.itemdata.ItemValues;
import jschars.itemshop.multiplier.SellMultiplier;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermission("itemshop.sell.worth")
@CommandAlias("worth")
public class WorthCommand extends BaseCommand {
    final Itemshop plugin;

    public WorthCommand(Itemshop plugin) {
        this.plugin = plugin;
    }

    @CommandPermission("itemshop.sell.worth")
    @Description("See the sell worth of a material")
    @CommandCompletion("@itemshop-sellables 1|32|64")
    @Default
    public void onWorth(CommandSender sender, @Optional Material material, @Default("1") Integer amount) {
        ValueConfig valueConfig = plugin.getValueConfig();
        MultiplierConfig multiplierConfig = plugin.getMultiplierConfig();
        if (material == null && sender instanceof Player) {
            Player player = (Player) sender;
            material = OffhandCompat.getItemInMainHand(player).getType();
        }
        ItemValues itemValues = ItemValues.getFor(material, valueConfig);
        SellMultiplier multi = sender instanceof Player ? SellMultiplier.best(((Player) sender), multiplierConfig) : SellMultiplier.unit;

        if (!itemValues.isSellable()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis item cannot be sold."));
            return;
        }

        double worth = itemValues.getSellWorth();

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                "&c%d&a of &c%s&a is worth &c$%.2f&a (&c$%.2f&a each)%s&a.",
                amount,
                material.name(),
                worth * amount * multi.getMultiplier(),
                worth * multi.getMultiplier(),
                multi.getMultiplier() != 1 ? String.format(" (&c%.2fx %s&a)", multi.getMultiplier(), !multi.getName().equals("") ? multi.getName() : "multiplier") : ""
        )));
    }

    @CommandPermission("itemshop.sell.worth.set")
    @Description("Set the bare sell worth of a material")
    @CommandCompletion("@itemshop-all-items 1.00|1.50|75")
    @Subcommand("set")
    @CommandAlias("setworth")
    public void onSetWorth(CommandSender sender, Material material, double worth) {
        ValueConfig config = plugin.getValueConfig();
        config.getConfig().set(ItemValues.getWorthPath(material), worth);
        config.saveConfig();
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