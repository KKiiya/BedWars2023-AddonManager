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

import java.util.Arrays;

import static me.kiiya.addonmanager.AddonManager.bedWars;

public class ConfirmMenu implements GUIHolder {
    private final Player p;
    private final String action;
    private final Addon addon;
    private Inventory inv;

    public ConfirmMenu(Player p, Addon addon, String action) {
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
                        Bukkit.getPluginManager().enablePlugin(addon.getPlugin());
                        addon.load();
                        bedWars.getAddonsUtil().getLoadedAddons().add(addon);
                        bedWars.getAddonsUtil().getUnloadedAddons().remove(addon);
                        break;
                    case "disable":
                        addon.unload();
                        bedWars.getAddonsUtil().getLoadedAddons().remove(addon);
                        bedWars.getAddonsUtil().getUnloadedAddons().add(addon);
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
        return null;
    }
}
