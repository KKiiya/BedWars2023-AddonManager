package me.kiiya.addonmanager.listeners.bedwars2023;

import com.google.gson.JsonObject;
import com.tomkeuper.bedwars.api.addon.Addon;
import com.tomkeuper.bedwars.api.events.communication.RedisMessageEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import static me.kiiya.addonmanager.AddonManager.bedWars;

public class MessageReceive implements Listener {

    @EventHandler
    public void onMessageReceive(RedisMessageEvent e) {
        JsonObject message = e.getMessage();
        if (e.getAddonName().equalsIgnoreCase("addon-manager")) {
            JsonObject reply = new JsonObject();
            if (!message.has("action")) return;
            if (message.get("action").getAsString().equalsIgnoreCase("open-inventory" )) {
                reply.addProperty("action", "getAddons");
                JsonObject addonList = new JsonObject();
                switch (message.get("addons-action").getAsString()) {
                    case "get-loaded-addons":
                        reply.addProperty("addons-type", "loaded");
                        for (Addon addon : bedWars.getAddonsUtil().getLoadedAddons()) {
                            JsonObject addonData = getJsonObject(addon);
                            addonList.addProperty(addon.getName(), addonData.toString());
                            reply.addProperty("addons", addonList.toString());
                        }
                        bedWars.getRedisClient().sendMessage(reply, "addon-manager");
                        break;
                    case "get-unloaded-addons":
                        reply.addProperty("addons-type", "unloaded");
                        for (Addon addon : bedWars.getAddonsUtil().getUnloadedAddons()) {
                            JsonObject addonData = getJsonObject(addon);
                            addonList.addProperty(addon.getName(), addonData.toString());
                        }
                        reply.addProperty("addons", addonList.toString());
                        bedWars.getRedisClient().sendMessage(reply, "addon-manager");
                        break;
                    case "get-addons":
                        reply.addProperty("addons-type", "registered");
                        for (Addon addon : bedWars.getAddonsUtil().getAddons()) {
                            JsonObject addonData = getJsonObject(addon);
                            addonList.addProperty(addon.getName(), addonData.toString());
                        }
                        reply.addProperty("addons", addonList.toString());
                        bedWars.getRedisClient().sendMessage(reply, "addon-manager");
                        break;
                }
            } else if (message.get("action").getAsString().equalsIgnoreCase("update")) {
                switch (message.get("addon-action").getAsString()) {
                    case "enable":
                        bedWars.getAddonsUtil().getAddons().stream().filter(addon -> addon.getName().equalsIgnoreCase(message.get("name").getAsString())).forEach(addon -> {
                            if (!addon.getPlugin().isEnabled()) {
                                Bukkit.getPluginManager().enablePlugin(addon.getPlugin());
                            }
                            addon.load();
                            bedWars.getAddonsUtil().getUnloadedAddons().remove(addon);
                            bedWars.getAddonsUtil().getLoadedAddons().add(addon);
                        });
                        break;
                    case "disable":
                        bedWars.getAddonsUtil().getAddons().stream().filter(addon -> addon.getName().equalsIgnoreCase(message.get("name").getAsString())).forEach(addon -> {
                            addon.unload();
                            if (addon.getPlugin().isEnabled()) {
                                Bukkit.getPluginManager().disablePlugin(addon.getPlugin());
                            }
                            bedWars.getAddonsUtil().getLoadedAddons().remove(addon);
                            bedWars.getAddonsUtil().getUnloadedAddons().add(addon);
                        });
                        break;
                }
            }
        }
    }

    @NotNull
    private static JsonObject getJsonObject(Addon addon) {
        JsonObject addonData = new JsonObject();
        addonData.addProperty("name", addon.getName());
        addonData.addProperty("version", addon.getVersion());
        addonData.addProperty("author", addon.getAuthor());
        addonData.addProperty("description", addon.getDescription());
        if (addon.getPlugin().isEnabled())
            addonData.addProperty("status", "enabled");
        else
            addonData.addProperty("status", "disabled");
        addonData.addProperty("server", addon.getPlugin().getServer().getServerId());
        return addonData;
    }
}
