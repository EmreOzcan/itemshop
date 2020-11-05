package jschars.itemshop;

import co.aikar.commands.PaperCommandManager;
import jschars.itemshop.classes.ItemValues;
import jschars.itemshop.commands.*;
import jschars.itemshop.compat.MaterialCompat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Itemshop extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;

    private static final Set<String> allItems = EnumSet.allOf(Material.class).stream()
            .filter(MaterialCompat::isItem)
            .map(Material::name)
            .collect(Collectors.toSet());

    private final Set<Permission> multiPermissions = new HashSet<>();

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

        commandManager.getCommandCompletions().registerCompletion("itemshop-all-items", c -> allItems);

        Stream<ItemValues> itemValuesStream = getConfig().getConfigurationSection("item-worths").getKeys(false).stream()
                .map(mat -> mat.toUpperCase(Locale.ENGLISH))
                .map(Material::getMaterial)
                .filter(Objects::nonNull)
                .map(mat -> ItemValues.getFor(mat, getConfig()));

        commandManager.getCommandCompletions().registerCompletion("itemshop-sellables", c ->
                itemValuesStream
                        .filter(ItemValues::isSellable)
                        .map(values -> values.getMaterial().name().toLowerCase(Locale.ENGLISH))
                        .collect(Collectors.toSet())
        );

        commandManager.getCommandCompletions().registerCompletion("itemshop-buyables", c ->
                itemValuesStream
                        .filter(ItemValues::isBuyable)
                        .map(values -> values.getMaterial().name().toLowerCase(Locale.ENGLISH))
                        .collect(Collectors.toSet())
        );

        commandManager.enableUnstableAPI("help");
        commandManager.registerCommand(new BuyCommand(this));
        commandManager.registerCommand(new CostCommand(this));
        commandManager.registerCommand(new SellCommand(this));
        commandManager.registerCommand(new WorthCommand(this));
        commandManager.registerCommand(new ReloadCommand(this));

        registerMultiplierPermissions();
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

    public void registerMultiplierPermissions() {
        PluginManager pluginManager = getServer().getPluginManager();
        multiPermissions.forEach(pluginManager::removePermission);
        multiPermissions.clear();
        for (String multi : getConfig().getConfigurationSection("modifiers").getKeys(false)) {
            Permission permission = new Permission("itemshop.m." + multi, "", PermissionDefault.FALSE);
            multiPermissions.add(permission);
            pluginManager.addPermission(permission);
        }
    }
}
