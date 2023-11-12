package me.kiiya.addonmanager.menu.proxy;

import com.google.gson.JsonObject;
import com.tomkeuper.bedwars.proxy.BedWarsProxy;
import me.kiiya.addonmanager.AddonManager;
import me.kiiya.addonmanager.menu.GUIHolder;
import me.kiiya.addonmanager.utils.CachedAddon;
import me.kiiya.addonmanager.utils.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AddonsMenu implements GUIHolder {
    private Player p;
    private Inventory inv;
    private String option;
    private final HashMap<Integer, CachedAddon> addonMap = new HashMap<>();
    private boolean close = false;

    public AddonsMenu(Player p, String option) {
        this.p = p;
        this.option = option;
        createInventory();
        addContents();
        if (close) return;
        p.openInventory(inv);
    }

    public void createInventory() {
        inv = Bukkit.createInventory(this, 54, "Addons Available");
    }

    public void addContents() {
        AtomicReference<List<CachedAddon>> addons = new AtomicReference<>();
        JsonObject object = new JsonObject();
        object.addProperty("server", p.getServer().getServerId());
        object.addProperty("action", "open-inventory");

        if (option.startsWith("--")) {
            switch (option.replace("--", "").toLowerCase()) {
                case "loaded":
                    object.addProperty("addons-action", "get-loaded-addons");
                    BedWarsProxy.getRedisConnection().sendMessage(object, "addon-manager");
                    Bukkit.getScheduler().runTaskLater(AddonManager.getPlugins(), () -> {
                        addons.set(CachedAddon.getCachedLoadedAddons());
                    }, 30L);
                    break;
                case "unloaded":
                    object.addProperty("addons-action", "get-unloaded-addons");
                    BedWarsProxy.getRedisConnection().sendMessage(object, "addon-manager");
                    Bukkit.getScheduler().runTaskLater(AddonManager.getPlugins(), () -> {
                        addons.set(CachedAddon.getCachedUnloadedAddons());
                    }, 30L);
                    break;
                case "registered":
                    object.addProperty("addons-action", "get-addons");
                    BedWarsProxy.getRedisConnection().sendMessage(object, "addon-manager");
                    Bukkit.getScheduler().runTaskLater(AddonManager.getPlugins(), () -> {
                        addons.set(CachedAddon.getCachedAddons());
                    }, 30L);
                    break;
                default:
                    close = true;
                    p.sendMessage(Utility.c("&cThat's not a valid option (Available options: --loaded, --unloaded, --registered)"));
                    return;
            }
        } else {
            object.addProperty("addons-action", "get-addons");
            BedWarsProxy.getRedisConnection().sendMessage(object, "addon-manager");
            Bukkit.getScheduler().runTaskLater(AddonManager.getPlugins(), () -> {
                addons.set(CachedAddon.getAddonsByAuthor(option));
            }, 30L);
        }

        ItemStack addonLoader = new ItemStack(Material.EMERALD);
        ItemMeta loaderMeta = addonLoader.getItemMeta();
        loaderMeta.setDisplayName(Utility.c("&aLoad every unloaded addon!"));
        loaderMeta.setLore(Utility.cList(Arrays.asList(
                "&7Load every unloaded addon by",
                "&7left clicking this item",
                "",
                "&eLeft Click to Enable Addons!"
        )));
        addonLoader.setItemMeta(loaderMeta);

        inv.setItem(45, addonLoader);

        ItemStack addonUnloader = new ItemStack(Material.REDSTONE);
        ItemMeta unloaderMeta = addonUnloader.getItemMeta();
        unloaderMeta.setDisplayName(Utility.c("&cUnload every loaded addon!"));
        unloaderMeta.setLore(Utility.cList(Arrays.asList(
                "&7Unload every loaded addon by",
                "&7left clicking this item",
                "",
                "&eLeft Click to Disable Addons!"
        )));
        addonUnloader.setItemMeta(unloaderMeta);

        inv.setItem(53, addonUnloader);

        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(Utility.c("&cClose Addon Manager"));
        closeMeta.setLore(Utility.cList(Arrays.asList("&7Close this inventory")));
        close.setItemMeta(closeMeta);

        inv.setItem(49, close);

        ItemStack addonStack = new ItemStack(Material.PAPER);
        ItemMeta addonMeta = addonStack.getItemMeta();
        final int[] position = {0};

        Bukkit.getScheduler().runTaskLater(AddonManager.getPlugins(), () -> {
            for (CachedAddon addon : addons.get()) {
                addonMeta.setDisplayName(Utility.c("&a" + addon.getName()));
                String enabled;

                if (addon.getStatus().equalsIgnoreCase("enabled")) {
                    enabled = "&aEnabled";
                } else {
                    enabled = "&cDisabled";
                }

                addonMeta.setLore(Utility.cList(Arrays.asList(
                        "&7Server: " + addon.getServerID(),
                        "&7Author: " + addon.getAuthor(),
                        "&7Version: " + addon.getVersion(),
                        "&7Description: " + addon.getDescription(),
                        "",
                        "&eRight Click to enable/disable",
                        enabled
                )));
                addonStack.setItemMeta(addonMeta);
                inv.setItem(position[0], addonStack);
                addonMap.put(position[0], addon);
                position[0]++;
            }
        }, 30L);
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        CachedAddon addon = addonMap.get(e.getSlot());
        String action;
        if (addon != null) {
            if (e.isRightClick()) {
                if (addon.getStatus().equalsIgnoreCase("enabled")) {
                    action = "disable";
                } else {
                    action = "enable";
                }
                new ProxyConfirmMenu((Player) e.getWhoClicked(), addon, action);
            }
        } else {
            switch (e.getSlot()) {
                case 49:
                    p.closeInventory();
                    break;
                case 45:
                    for (CachedAddon add : CachedAddon.getCachedUnloadedAddons()) {
                        add.enable();
                    }
                    new AddonsMenu(p, option);
                    break;
                case 53:
                    for (CachedAddon add : CachedAddon.getCachedLoadedAddons()) {
                        add.disable();
                    }
                    new AddonsMenu(p, option);
                    break;
            }
        }
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}
