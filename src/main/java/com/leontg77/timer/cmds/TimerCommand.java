package com.leontg77.timer.cmds;

import com.google.common.base.Joiner;
import com.leontg77.timer.packets.ActionSender;
import com.leontg77.timer.utils.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

/**
 * Timer command class
 * 
 * @author LeonTG77
 */
public class TimerCommand implements CommandExecutor {
	
	private boolean countdown = true;
	private BukkitRunnable run;
	private String message;
	private int ticks;

	// Prefix to use when sending messages
	public static final String PREFIX = "" + ChatColor.RED + ChatColor.BOLD + "Timer " + ChatColor.DARK_GRAY + "» " + ChatColor.GRAY;
	public static final String PERMISSION = "uhc.timer";

	protected final ActionSender actionSender;
	protected final Plugin plugin;

	public TimerCommand(Plugin plugin, ActionSender actionSender) {
		this.actionSender = actionSender;
		this.plugin = plugin;
	}

	public boolean onCommand(final CommandSender sender, Command cmd, String label,String[] args) {
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
			if (run == null) {
				sender.sendMessage(PREFIX + "No timers are running.");
				return true;
			}

			run.cancel();
			run = null;

			sender.sendMessage(PREFIX + "Timer cancelled.");
			return true;
		}

		// check enough args for setting a timer
		if (args.length < 2) {
			sender.sendMessage(PREFIX + "Usage: /timer <seconds> <message>");
			return true;
		}

		// check existing timer
		if (run != null) {
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
		this.ticks = (time + 1);

		// set whether we have a countdown timer or not
		countdown = time >= 0;

		// build the message from the arguments
		this.message = Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length));

		// replace colour codes
		this.message = ChatColor.translateAlternateColorCodes('&', message);

		// start the countdown timer
		run = new BukkitRunnable() {
			public void run() {
				if (countdown) {
					ticks--;

					try {
						actionSender.sendToAll(message + " " + TimeUtils.timeToString(ticks));
					} catch (Exception ex) {
						ex.printStackTrace();
						plugin.getLogger().severe("Could not send action packets, are you using 1.8 or higher?");
					}

					if (ticks == 0) {
						cancel();
						run = null;
						return;
					}
				} else {
					try {
						actionSender.sendToAll(message + " " + TimeUtils.timeToString(ticks));
					} catch (Exception ex) {
						ex.printStackTrace();
						plugin.getLogger().severe("Could not send action packets, are you using 1.8 or higher?");
					}
				}
			}
		};
		run.runTaskTimer(plugin, 0, 20);

		sender.sendMessage(PREFIX + "Timer started.");
		return true;
	}
}