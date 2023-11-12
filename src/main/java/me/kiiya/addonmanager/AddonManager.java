package me.kiiya.addonmanager;

import com.tomkeuper.bedwars.api.BedWars;
import me.kiiya.addonmanager.utils.Utility;
import me.kiiya.addonmanager.command.AddonInventoryCommand;
import me.kiiya.addonmanager.listeners.InventoryListener;
import me.kiiya.addonmanager.listeners.bedwars2023.MessageReceive;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AddonManager extends JavaPlugin {

    public static BedWars bedWars;
    @Override
    public void onEnable() {
       if (Bukkit.getPluginManager().isPluginEnabled("BedWars2023")) {
            getLogger().info(Utility.c("&aBedWars2023 found! Hooking..."));
            bedWars = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();

            getLogger().info(Utility.c("&eLoading listeners..."));
            Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
            Bukkit.getPluginManager().registerEvents(new MessageReceive(), this);
            getLogger().info(Utility.c("&aListeners loaded successfully!"));

            getLogger().info(Utility.c("&eLoading commands..."));
            getServer().getPluginCommand("addons").setExecutor(new AddonInventoryCommand());
            getLogger().info(Utility.c("&aCommands loaded successfully!"));

            getLogger().info(Utility.c("&aAddon Manager for BedWars2023 has been enabled successfully!"));
        } else if (Bukkit.getPluginManager().isPluginEnabled("BWProxy2023")) {
            getLogger().info(Utility.c("&aBWProxy2023 found! Hooking..."));

            getLogger().info(Utility.c("&eLoading listeners..."));
            Bukkit.getPluginManager().registerEvents(new InventoryListener(), getPlugins());
            Bukkit.getPluginManager().registerEvents(new me.kiiya.addonmanager.listeners.proxy.MessageReceive(), getPlugins());
            getLogger().info(Utility.c("&aListeners loaded successfully!"));

            getLogger().info(Utility.c("&eLoading commands..."));
            getLogger().info(Utility.c("&aCommands loaded successfully!"));

            getLogger().info(Utility.c("&aAddon Manager for BWProxy2023 has been enabled successfully!"));
        } else {
           getLogger().info(Utility.c("&cI can't run because BedWars2023 or BWProxy2023 wasn't found! Disabling..."));
           Bukkit.getPluginManager().disablePlugin(this);
       }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static AddonManager getPlugins() {
        return AddonManager.getPlugin(AddonManager.class);
    }
}
