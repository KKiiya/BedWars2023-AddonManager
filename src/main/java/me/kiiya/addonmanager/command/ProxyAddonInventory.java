package me.kiiya.addonmanager.command;

import com.tomkeuper.bedwars.api.language.Language;
import me.kiiya.addonmanager.menu.proxy.AddonsMenu;
import me.kiiya.addonmanager.utils.Utility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ProxyAddonInventory implements CommandExecutor {
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

        new AddonsMenu(p, option);
        return false;
    }
}
