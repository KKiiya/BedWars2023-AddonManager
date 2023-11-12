package me.kiiya.addonmanager.listeners.bedwars2023;

import com.google.gson.JsonObject;
import com.tomkeuper.bedwars.api.events.communication.RedisMessageEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MessageReceive implements Listener {

    @EventHandler
    public void onMessageReceive(RedisMessageEvent e) {
        JsonObject message = e.getMessage();
        System.out.println(message);
    }
}
