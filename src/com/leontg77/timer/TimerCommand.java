package com.leontg77.timer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TimerCommand implements CommandExecutor {
	private String message;
	private int ticks;
	private boolean countdown = true;
	private BukkitRunnable run = new BukkitRunnable() {
		public void run() {
			if (countdown) {
				for (Player online : Bukkit.getServer().getOnlinePlayers()) {
					Main.sendAction(online, message + " " + Main.ticksToString(ticks)); 
				}
				ticks--;
				
				if (ticks == 0) {
					try {
						run.cancel();
						run = new BukkitRunnable() {
							public void run() {
								if (countdown) {
									for (Player online : Bukkit.getServer().getOnlinePlayers()) {
										Main.sendAction(online, message + " " + Main.ticksToString(ticks)); 
									}
									ticks--;
									
									if (ticks == 0) {
										try {
											run.cancel();
											cancel();
										} catch (Exception e) {}
									}
								} else {
									for (Player online : Bukkit.getServer().getOnlinePlayers()) {
										Main.sendAction(online, message); 
									}
								}
							}
						};
					} catch (Exception e) {}
				}
			} else {
				for (Player online : Bukkit.getServer().getOnlinePlayers()) {
					Main.sendAction(online, message); 
				}
			}
		}
	};
	
	public boolean onCommand(final CommandSender sender, Command cmd, String label,String[] args) {
		if (cmd.getName().equals("timer")) {
			if (sender.hasPermission("uhc.timer")) {
				if (args.length == 0) {
					sender.sendMessage(ChatColor.RED + "Usage: /timer <duration> <message>");
					return true;
				}
				
				if (args.length == 1 && !args[0].equalsIgnoreCase("cancel")) {
					sender.sendMessage(ChatColor.RED + "Usage: /timer <duration> <message>");
					return true;
				}

				if (args.length >= 1 && args[0].equalsIgnoreCase("cancel")) {
					try {
						run.cancel();
						run = new BukkitRunnable() {
							public void run() {
								if (countdown) {
									for (Player online : Bukkit.getServer().getOnlinePlayers()) {
										Main.sendAction(online, message + " " + Main.ticksToString(ticks)); 
									}
									ticks--;
									
									if (ticks == 0) {
										run.cancel();
										cancel();
									}
								} else {
									for (Player online : Bukkit.getServer().getOnlinePlayers()) {
										Main.sendAction(online, message); 
									}
								}
							}
						};
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Timer is not running.");
						return true;
					}
					sender.sendMessage(Main.prefix() + "Timer cancelled.");
					return true;
				}

				int millis;

				try {
					millis = Integer.parseInt(args[0]);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Invaild number.");
					return true;
				}
				
				if (millis < 0) {
					countdown = false;
				} else {
					countdown = true;
				}

				StringBuilder sb = new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					sb.append(args[i]).append(" ");
				}
				try {
					run.runTaskTimer(Main.plugin, 0, 20);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Timer is already running.");
					return true;
				}
				this.message = ChatColor.translateAlternateColorCodes('&', sb.toString().trim());
				this.ticks = millis;
				sender.sendMessage(Main.prefix() + "Timer started.");
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}