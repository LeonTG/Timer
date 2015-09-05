package com.leontg77.timer;

import com.leontg77.timer.cmds.TimerCommand;
import com.leontg77.timer.packets.ActionSender;
import com.leontg77.timer.packets.PacketSender;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class of the UHC plugin.
 * <p>
 * This class contains methods for enabling, disabling and getting the prefix.
 * 
 * @author LeonTG77
 */
public class Main extends JavaPlugin {

	// Prefix to use when sending messages
	public static final String PREFIX = "" + ChatColor.RED + ChatColor.BOLD + "Timer " + ChatColor.DARK_GRAY + "� " + ChatColor.GRAY;

	@Override
	public void onDisable() {
		PluginDescriptionFile file = getDescription();
		getLogger().info(file.getName() + " is now disabled.");
	}
	
	@Override
	public void onEnable() {
		PluginDescriptionFile file = getDescription();
		getLogger().info(file.getName() + " v" + file.getVersion() + " is now enabled.");
		getLogger().info(file.getName() + " was made by LeonTG77.");

		try {
			PacketSender packetSender = new PacketSender();
			ActionSender actionSender = new ActionSender(packetSender);

			getCommand("timer").setExecutor(new TimerCommand(this, actionSender));
		} catch (Exception e) {
			e.printStackTrace();
			getLogger().severe("Could not set up sending of action packets, are you using 1.8 or higher?");
			setEnabled(false);
		}
	}
}