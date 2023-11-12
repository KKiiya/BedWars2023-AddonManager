package me.kiiya.addonmanager.listeners.proxy;

import com.google.gson.JsonObject;
import com.tomkeuper.bedwars.proxy.api.event.RedisMessageEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MessageReceive implements Listener {

    @EventHandler
    public void onMessageReceive(RedisMessageEvent e) {
        JsonObject message = e.getMessage();
        System.out.println(message);
    }
}
