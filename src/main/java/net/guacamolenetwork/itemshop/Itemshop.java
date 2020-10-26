package net.guacamolenetwork.itemshop;

import co.aikar.commands.PaperCommandManager;
import net.guacamolenetwork.itemshop.commands.MainCommand;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class Itemshop extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;

    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();

        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.getCommandCompletions().registerCompletion("itemMaterials", c ->
                EnumSet.allOf(Material.class).stream()
                        .filter(Material::isItem)
                        .map(Material::name)
                        .collect(Collectors.toList())
        );
        commandManager.enableUnstableAPI("help");
        commandManager.registerCommand(new MainCommand(this));
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    public static Economy getEconomy() {
        return econ;
    }
}
