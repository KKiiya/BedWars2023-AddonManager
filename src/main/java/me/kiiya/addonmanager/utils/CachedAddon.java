package me.kiiya.addonmanager.utils;

import com.google.gson.JsonObject;
import com.tomkeuper.bedwars.proxy.BedWarsProxy;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class CachedAddon {
    private JsonObject addonData;
    public static LinkedList<CachedAddon> cachedAddons = new LinkedList<>();
    public static LinkedList<CachedAddon> cachedLoadedAddons = new LinkedList<>();
    public static LinkedList<CachedAddon> cachedUnloadedAddons = new LinkedList<>();
    private CachedAddon() {}

    public CachedAddon(JsonObject addonData) {
        this.addonData = addonData;
    }

    public String getName() {
        return addonData.get("name").getAsString();
    }

    public String getVersion() {
        return addonData.get("version").getAsString();
    }

    public String getAuthor() {
        return addonData.get("author").getAsString();
    }

    public String getDescription() {
        return addonData.get("description").getAsString();
    }

    public String getStatus() {
        return addonData.get("status").getAsString();
    }

    public void disable() {
        JsonObject addonUpdate = new JsonObject();
        addonUpdate.addProperty("action", "update");
        addonUpdate.addProperty("name", getName());
        addonUpdate.addProperty("version", getVersion());
        addonUpdate.addProperty("author", getAuthor());
        addonUpdate.addProperty("description", getDescription());
        addonUpdate.addProperty("server", getServerID());
        addonUpdate.addProperty("addon-action", "disable");
        BedWarsProxy.getRedisConnection().sendMessage(addonUpdate, "addon-manager");
    }

    public void enable() {
        JsonObject addonUpdate = new JsonObject();
        addonUpdate.addProperty("action", "update");
        addonUpdate.addProperty("name", getName());
        addonUpdate.addProperty("version", getVersion());
        addonUpdate.addProperty("author", getAuthor());
        addonUpdate.addProperty("description", getDescription());
        addonUpdate.addProperty("server", getServerID());
        addonUpdate.addProperty("addon-action", "enable");
        BedWarsProxy.getRedisConnection().sendMessage(addonUpdate, "addon-manager");
    }

    public String getServerID() {
        return addonData.get("server").getAsString();
    }

    public static LinkedList<CachedAddon> getCachedAddons() {
        return cachedAddons;
    }

    public static LinkedList<CachedAddon> getCachedLoadedAddons() {
        return cachedLoadedAddons;
    }

    public static LinkedList<CachedAddon> getCachedUnloadedAddons() {
        return cachedUnloadedAddons;
    }

    public static LinkedList<CachedAddon> getAddonsByAuthor(String author) {
        return cachedAddons.stream().filter(addon -> addon.getAuthor().equalsIgnoreCase(author)).collect(Collectors.toCollection(LinkedList::new));
    }
}
