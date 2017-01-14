package com.leontg77.timer;

import com.leontg77.timer.commands.TimerCommand;
import com.leontg77.timer.managers.ActionSender;
import com.leontg77.timer.managers.PacketSender;
import com.leontg77.timer.managers.TimerRunnable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class of the plugin.
 * 
 * @author LeonTG77
 */
public class Main extends JavaPlugin {
	public static final String PREFIX = "§cTimer §8» §7";
	
	@Override
	public void onEnable() {
		try {
			PacketSender packetSender = new PacketSender(this);
			ActionSender actionSender = new ActionSender(this, packetSender);

			TimerRunnable timer = new TimerRunnable(this, actionSender);
			getCommand("timer").setExecutor(new TimerCommand(timer));
		} catch (Exception e) {
			e.printStackTrace();
			getLogger().severe("Could not set up sending of action packets, are you using 1.8 or higher?");
			setEnabled(false);
		}
	}

	private static final long SECONDS_PER_HOUR = 3600;
	private static final long SECONDS_PER_MINUTE = 60;

	/**
	 * Converts the seconds into a string with hours, minutes and seconds.
	 *
	 * @param ticks the number of seconds.
	 * @return The converted seconds.
	 */
	public String timeToString(long ticks) {
		int hours = (int) Math.floor(ticks / (double) SECONDS_PER_HOUR);
		ticks -= hours * SECONDS_PER_HOUR;

		int minutes = (int) Math.floor(ticks / (double) SECONDS_PER_MINUTE);
		ticks -= minutes * SECONDS_PER_MINUTE;

		int seconds = (int) ticks;

		StringBuilder output = new StringBuilder();

		if (hours > 0) {
			output.append(hours).append('h');

			if (minutes == 0) {
				output.append(minutes).append('m');
			}
		}

		if (minutes > 0) {
			output.append(minutes).append('m');
		}

		output.append(seconds).append('s');

		return output.toString();
	}

	private String nmsPacketVersion = null;

	/**
	 * Ask ghowden, or just read the method, too lazy to make this.
	 */
	public Class<?> getNMSClass(String subPackage) throws ClassNotFoundException {
		return Class.forName("net.minecraft.server." + getNmsPacketVersion() + "." + subPackage);
	}

	/**
	 * Ask ghowden, or just read the method, too lazy to make this.
	 */
	public Class<?> getCraftBukkitClass(String subPackage) throws ClassNotFoundException {
		return Class.forName("org.bukkit.craftbukkit." + getNmsPacketVersion() + "." + subPackage);
	}

	/**
	 * Ask ghowden, or just read the method, too lazy to make this.
	 */
	private String getNmsPacketVersion() {
		if (null == nmsPacketVersion) { // set it if it doesn't exist currently
			// grab the version number from CraftServer implementation
			String packageName = Bukkit.getServer().getClass().getPackage().getName();
			nmsPacketVersion = packageName.substring(packageName.lastIndexOf(".") + 1);
		}

		return nmsPacketVersion;
	}
}