package com.leontg77.timer;

import com.leontg77.timer.cmds.TimerCommand;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * Main class of the UHC plugin.
 * <p>
 * This class contains methods for enabling, disabling and getting the prefix.
 * 
 * @author LeonTG77
 */
public class Main extends JavaPlugin {

	private Logger logger = getLogger();
	public static Main plugin;

	// Prefix to use when sending messages
	public static final String PREFIX = "" + ChatColor.RED + ChatColor.BOLD + "Timer " + ChatColor.DARK_GRAY + "» " + ChatColor.GRAY;

	@Override
	public void onDisable() {
		PluginDescriptionFile file = getDescription();
		logger.info(file.getName() + " is now disabled.");
		
		plugin = null;
	}
	
	@Override
	public void onEnable() {
		PluginDescriptionFile file = getDescription();
		logger.info(file.getName() + " v" + file.getVersion() + " is now enabled.");
		logger.info(file.getName() + " was made by LeonTG77.");
		plugin = this;

		getCommand("timer").setExecutor(new TimerCommand());
	}
}