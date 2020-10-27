package net.guacamolenetwork.itemshop.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import net.guacamolenetwork.itemshop.Itemshop;
import org.bukkit.command.CommandSender;

@CommandPermission("itemshop.reload")
@Description("Reloads the config file")
@CommandAlias("itemshop")
public class ReloadCommand extends BaseCommand {
    final Itemshop plugin;

    public ReloadCommand(Itemshop plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onCall(CommandSender sender) {
        plugin.reloadConfig();
        sender.sendMessage("Reloaded.");
    }
}
