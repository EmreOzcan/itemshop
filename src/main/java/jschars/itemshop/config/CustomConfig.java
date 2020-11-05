package jschars.itemshop.config;

import jschars.itemshop.Itemshop;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class CustomConfig {

    private final YamlConfiguration config;
    private final File file;

    public CustomConfig(Itemshop plugin, String name) {

        file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(name, false);
        }

        config = new YamlConfiguration();
        reloadConfig();
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
