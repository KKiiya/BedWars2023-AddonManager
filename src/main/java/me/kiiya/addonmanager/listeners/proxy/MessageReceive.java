package me.kiiya.addonmanager.listeners.proxy;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tomkeuper.bedwars.proxy.api.event.RedisMessageEvent;
import me.kiiya.addonmanager.utils.CachedAddon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import java.util.Map;

public class MessageReceive implements Listener {

    @EventHandler
    public void onMessageReceive(RedisMessageEvent e) {
        JsonObject message = e.getMessage();
        if (e.getAddonName().equalsIgnoreCase("addon-manager")) {
            if (!message.has("action")) return;
            switch (message.get("action").getAsString()) {
                case "getAddons":
                    CachedAddon.cachedAddons.clear();
                    CachedAddon.cachedLoadedAddons.clear();
                    CachedAddon.cachedUnloadedAddons.clear();
                    switch (message.get("addons-type").getAsString()) {
                        case "loaded":
                        case "unloaded":
                        case "registered":
                            for (Map.Entry<String, JsonElement> addon : (new JsonParser().parse(message.get("addons").getAsString())).getAsJsonObject().entrySet()) {
                                JsonObject addonData = new JsonParser().parse(addon.getValue().getAsString()).getAsJsonObject();
                                CachedAddon cachedAddon = new CachedAddon(addonData);
                                if (addonData.get("status").getAsString().equalsIgnoreCase("enabled")) {
                                    CachedAddon.cachedLoadedAddons.add(cachedAddon);
                                } else {
                                    CachedAddon.cachedUnloadedAddons.add(cachedAddon);
                                }
                                CachedAddon.cachedAddons.add(cachedAddon);
                            }
                            break;
                        default:
                            return;
                    }
                    break;
            }
        }
    }
}
