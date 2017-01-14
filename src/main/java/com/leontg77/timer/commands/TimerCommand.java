package com.leontg77.timer.commands;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.leontg77.timer.Main;
import com.leontg77.timer.managers.TimerRunnable;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Timer command class.
 *
 * @author LeonTG77
 */
public class TimerCommand implements CommandExecutor, TabCompleter {
    private final TimerRunnable timer;

    public TimerCommand(TimerRunnable timer) {
        this.timer = timer;
    }

    private static final String PERMISSION = "uhc.timer";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(ChatColor.RED + "You can't use that command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Main.PREFIX + "Usage: /timer <seconds|-1> <message> | /timer cancel");
            return true;
        }

        // check for cancelling
        if (args[0].equalsIgnoreCase("cancel")) {
            if (!timer.isRunning()) {
                sender.sendMessage(ChatColor.RED + "Timers is not running.");
                return true;
            }

            timer.cancel();
            sender.sendMessage(Main.PREFIX + "The timer has been cancelled.");
            return true;
        }

        // check enough args for setting a timer
        if (args.length < 2) {
            sender.sendMessage(Main.PREFIX + "Usage: /timer <seconds> <message> | /timer -1 <message>");
            return true;
        }

        if (timer.isRunning()) {
            sender.sendMessage(ChatColor.RED + "The action bar timer is already running, cancel with /timer cancel.");
            return true;
        }

        int seconds;

        try {
            seconds = Integer.parseInt(args[0]);
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + "'" + args[0] + "' is not a valid time.");
            return true;
        }

        String message = Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length));
        message = ChatColor.translateAlternateColorCodes('&', message);

        timer.startSendingMessage(message, seconds);
        sender.sendMessage(Main.PREFIX + "The timer has started.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> toReturn = Lists.newArrayList();

        if (args.length != 1) {
            return toReturn;
        }

        toReturn.add("info");

        if (sender.hasPermission(PERMISSION)) {
            toReturn.add("enable");
            toReturn.add("start");
            toReturn.add("disable");
        }

        return StringUtil.copyPartialMatches(args[args.length - 1], toReturn, Lists.newArrayList());
    }
}