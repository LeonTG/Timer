package com.leontg77.timer.cmds;

import com.google.common.base.Joiner;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

/**
 * Timer command class
 * 
 * @author LeonTG77
 */
public class TimerCommand implements CommandExecutor {

	// Prefix to use when sending messages
	public static final String PREFIX = "" + ChatColor.RED + ChatColor.BOLD + "Timer " + ChatColor.DARK_GRAY + "» " + ChatColor.GRAY;
	public static final String PERMISSION = "uhc.timer";

	private final TimerRunnable timer;

	public TimerCommand(TimerRunnable timer) {
		this.timer = timer;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,String[] args) {
		// check permissions
		if (!sender.hasPermission(PERMISSION)) {
			sender.sendMessage(PREFIX + "You can't use that command.");
			return true;
		}

		// check no-arg, send usage
		if (args.length == 0) {
			sender.sendMessage(PREFIX + "Usage: /timer <seconds> <message> OR /timer cancel");
			return true;
		}

		// check for cancelling
		if (args[0].equalsIgnoreCase("cancel")) {
			if (!timer.isRunning()) {
				sender.sendMessage(PREFIX + "Timers is not running.");
				return true;
			}

			timer.cancel();

			sender.sendMessage(PREFIX + "Timer cancelled.");
			return true;
		}

		// check enough args for setting a timer
		if (args.length < 2) {
			sender.sendMessage(PREFIX + "Usage: /timer <seconds> <message>");
			return true;
		}

		// check existing timer
		if (timer.isRunning()) {
			sender.sendMessage(PREFIX + "A timer is already running.");
			return true;
		}

		// read the amount of time to run for
		int time;
		try {
			time = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			sender.sendMessage(PREFIX + "Invaild time.");
			return true;
		}
		long ticks = (time + 1);

		// build the message from the arguments
		String message = Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length));

		// replace colour codes
		message = ChatColor.translateAlternateColorCodes('&', message);

		// start the countdown timer
		timer.startSendingMessage(message, ticks);

		sender.sendMessage(PREFIX + "Timer started.");
		return true;
	}
}