package me.kiiya.addonmanager.listeners;

import me.kiiya.addonmanager.menu.GUIHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e == null) return;

        if (e.getInventory() == null) return;
        if (e.getClickedInventory() == null) return;
        if (e.getInventory().getHolder() == null) return;
        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getItemMeta() == null) return;

        if (e.getInventory().getHolder() instanceof GUIHolder) {
            e.setCancelled(true);
            ((GUIHolder) e.getInventory().getHolder()).onInventoryClick(e);
        }
    }
}
