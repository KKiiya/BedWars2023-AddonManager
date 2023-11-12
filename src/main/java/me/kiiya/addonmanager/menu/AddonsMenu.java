package me.kiiya.addonmanager.menu;

import com.tomkeuper.bedwars.api.addon.Addon;
import me.kiiya.addonmanager.utils.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import static me.kiiya.addonmanager.AddonManager.bedWars;

public class AddonsMenu implements GUIHolder {
    private final Player p;
    private Inventory inv;
    private final String option;
    private final HashMap<Integer, Addon> addonMap = new HashMap<>();
    private boolean close = false;

    public AddonsMenu(Player p, String option) {
        this.p = p;
        this.option = option;
        createInventory();
        addContents();
        if (close) return;
        p.openInventory(inv);
    }
    private void createInventory() {
        inv = Bukkit.createInventory(this, 54, "Addons Available");
    }

    private void addContents() {
        List<Addon> addons;
        if (option.startsWith("--")) {
            switch (option.replace("--", "").toLowerCase()) {
                case "loaded":
                    addons = bedWars.getAddonsUtil().getLoadedAddons();
                    break;
                case "unloaded":
                    addons = bedWars.getAddonsUtil().getUnloadedAddons();
                    break;
                case "registered":
                    addons = bedWars.getAddonsUtil().getAddons();
                    break;
                default:
                    close = true;
                    p.sendMessage(Utility.c("&cThat's not a valid option (Available options: --loaded, --unloaded, --registered)"));
                    return;
            }
        } else {
            addons = bedWars.getAddonsUtil().getAddonsByAuthor(option);
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
        int position = 0;

        for (Addon addon : addons) {
            addonMeta.setDisplayName(Utility.c("&a" + addon.getName()));
            String enabled;

            if (Bukkit.getPluginManager().isPluginEnabled(addon.getPlugin())) {
                enabled = "&aEnabled";
            } else {
                enabled = "&cDisabled";
            }

            addonMeta.setLore(Utility.cList(Arrays.asList(
                    "&7Author: " + addon.getAuthor(),
                    "&7Version: " + addon.getVersion(),
                    "&7Description: " + addon.getDescription(),
                    "",
                    "&eRight Click to enable/disable",
                    enabled
            )));
            addonStack.setItemMeta(addonMeta);
            inv.setItem(position, addonStack);
            addonMap.put(position, addon);
            position++;
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        Addon addon = addonMap.get(e.getSlot());
        String action;
        if (addon != null) {
            if (e.isRightClick()) {
                if (Bukkit.getPluginManager().isPluginEnabled(addon.getPlugin())) {
                    action = "disable";
                } else {
                    action = "enable";
                }
                new ConfirmMenu((Player) e.getWhoClicked(), addon, action);
            }
        } else {
            List<Addon> addonsToRemoveFromUnloaded = new ArrayList<>();
            List<Addon> addonsToRemoveFromLoaded = new ArrayList<>();
            switch (e.getSlot()) {
                case 49:
                    p.closeInventory();
                    break;
                case 45:
                    for (Addon add : bedWars.getAddonsUtil().getUnloadedAddons()) {
                        Bukkit.getPluginManager().enablePlugin(add.getPlugin());
                        add.load();
                        addonsToRemoveFromUnloaded.add(add);
                        bedWars.getAddonsUtil().getLoadedAddons().add(add);
                    }

                    for (Addon add : addonsToRemoveFromUnloaded) {
                        bedWars.getAddonsUtil().getUnloadedAddons().remove(add);
                     }
                    new AddonsMenu(p, option);
                    break;
                case 53:
                    for (Addon add : bedWars.getAddonsUtil().getLoadedAddons()) {
                        add.unload();
                        addonsToRemoveFromLoaded.add(add);
                        bedWars.getAddonsUtil().getUnloadedAddons().add(add);
                    }
                    for (Addon add : addonsToRemoveFromLoaded) {
                        bedWars.getAddonsUtil().getLoadedAddons().remove(add);
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
