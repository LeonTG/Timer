package com.leontg77.timer.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.timer.Main;
import com.leontg77.timer.utils.PlayerUtils;
import com.leontg77.timer.utils.TimeUtils;

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
	
	public boolean onCommand(final CommandSender sender, Command cmd, String label,String[] args) {
		if (cmd.getName().equalsIgnoreCase("timer")) {
			if (sender.hasPermission("uhc.timer")) {
				if (args.length == 0) {
					sender.sendMessage(Main.PREFIX + "Usage: /timer <seconds> <message>");
					return true;
				}
				
				if (args.length == 1 && !args[0].equalsIgnoreCase("cancel")) {
					sender.sendMessage(Main.PREFIX + "Usage: /timer <seconds> <message>");
					return true;
				}

				if (args.length >= 1 && args[0].equalsIgnoreCase("cancel")) {
					if (run == null) {
						sender.sendMessage(ChatColor.RED + "No timers are running.");
						return true;
					}

					run.cancel();
					run = null;
					
					sender.sendMessage(Main.PREFIX + "Timer cancelled.");
					return true;
				}

				if (run != null) {
					sender.sendMessage(ChatColor.RED + "A timer is already running.");
					return true;
				}
				
				int time;

				try {
					time = Integer.parseInt(args[0]);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Invaild time.");
					return true;
				}
				
				if (time < 0) {
					countdown = false;
				} else {
					countdown = true;
				}

				StringBuilder msg = new StringBuilder();
				
				for (int i = 1; i < args.length; i++) {
					msg.append(args[i]).append(" ");
				}
				
				run = new BukkitRunnable() {
					public void run() {
						if (countdown) {
							ticks--;
							
							for (Player online : Bukkit.getServer().getOnlinePlayers()) {
								PlayerUtils.sendAction(online, message + " " + TimeUtils.timeToString(ticks)); 
							}
							
							if (ticks == 0) {
								cancel();
								run = null;
								return;
							}
						} else {
							for (Player online : Bukkit.getServer().getOnlinePlayers()) {
								PlayerUtils.sendAction(online, message); 
							}
						}
					}
				};
				run.runTaskTimer(Main.plugin, 0, 20);
				
				this.message = ChatColor.translateAlternateColorCodes('&', msg.toString().trim());
				this.ticks = (time + 1);
				
				sender.sendMessage(Main.PREFIX + "Timer started.");
			} else {
				sender.sendMessage(Main.PREFIX + "You can't use that command.");
			}
		}
		return true;
	}
}