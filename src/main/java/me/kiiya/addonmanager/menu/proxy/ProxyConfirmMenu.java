package me.kiiya.addonmanager.menu.proxy;

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

public class ProxyConfirmMenu implements GUIHolder {
    private final Player p;
    private final String action;
    private final CachedAddon addon;
    private Inventory inv;

    public ProxyConfirmMenu(Player p, CachedAddon addon, String action) {
        this.p = p;
        this.addon = addon;
        this.action = action;
        createInventory();
        addContents();
        p.openInventory(inv);
    }
    private void createInventory() {
        inv = Bukkit.createInventory(this, 36, "Are you sure?");
    }

    private void addContents() {
        ItemStack yes = new ItemStack(Material.HARD_CLAY, 1, (byte) 5);
        ItemMeta yesMeta = yes.getItemMeta();
        yesMeta.setDisplayName(Utility.c("&a&lYes"));
        yesMeta.setLore(Utility.cList(Arrays.asList("&7I want to " + action + " &7this addon &7(" + addon.getName() + ")")));
        yes.setItemMeta(yesMeta);

        ItemStack no = new ItemStack(Material.HARD_CLAY, 1, (byte) 14);
        ItemMeta noMeta = no.getItemMeta();
        noMeta.setDisplayName(Utility.c("&c&lNo"));
        noMeta.setLore(Utility.cList(Arrays.asList("&7I don't want to " + action + " &7this addon &7(" + addon.getName() + ")")));
        no.setItemMeta(noMeta);

        inv.setItem(11, yes);
        inv.setItem(15, no);
    }
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals("Are you sure?")) return;

        switch (e.getSlot()) {
            case 11:
                switch (action) {
                    case "enable":
                        addon.enable();
                        break;
                    case "disable":
                        addon.disable();
                        break;
                }
                p.sendMessage(Utility.c("&aTask done!"));
                p.closeInventory();
                break;
            case 15:
                p.sendMessage(Utility.c("&cCancelled!"));
                p.closeInventory();
                break;
        }
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}
