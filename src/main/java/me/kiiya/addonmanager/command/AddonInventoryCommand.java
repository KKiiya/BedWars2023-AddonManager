package me.kiiya.addonmanager.command;

import com.google.gson.JsonObject;
import com.tomkeuper.bedwars.api.language.Language;
import me.kiiya.addonmanager.utils.Utility;
import me.kiiya.addonmanager.menu.AddonsMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static me.kiiya.addonmanager.AddonManager.bedWars;

public class AddonInventoryCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof ConsoleCommandSender) {
            commandSender.sendMessage(Utility.c("&cYou must be in the server to use this command"));
            return false;
        }
        Player p = (Player) commandSender;

        if (!p.hasPermission("bw.addons") || !p.isOp() || !p.hasPermission("bw.*") || !p.hasPermission("*")) {
            p.sendMessage(Utility.c(Language.getMsg(p, "cmd-not-found").replace("%bw_lang_prefix%", Language.getMsg(p, "prefix"))));
            return false;
        }

        if (strings.length > 1) {
            p.sendMessage(Utility.c(Language.getMsg(p, "cmd-not-found").replace("%bw_lang_prefix%", Language.getMsg(p, "prefix"))));
            return false;
        }

        String option;
        if (strings.length == 0) {
            option = "--registered";
        } else {
            option = strings[0];
        }

        JsonObject json = new JsonObject();
        json.addProperty("player", p.getName());
        json.addProperty("server", p.getServer().getName());
        bedWars.getRedisClient().sendMessage(json, "addon-manager");
        new AddonsMenu(p, option);
        return false;
    }
}
