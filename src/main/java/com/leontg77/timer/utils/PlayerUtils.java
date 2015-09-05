package com.leontg77.timer.utils;

import com.leontg77.timer.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

/**
 * Player utilities class
 * <p>
 * Contains method for sending an action packet to a player.
 * 
 * @author LeonTG77
 */
public class PlayerUtils {

	/**
	 * Sends a message in the action bar to the given player.
	 * 
	 * @param player the player sending it to.
	 * @param message the message to send.
	 */
	public static void sendAction(Player player, String message) {
		try {
			Object msg = ReflectionUtils.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{text:'" + message + "'}");

			Constructor<?> constructor = ReflectionUtils.getNMSClass("PacketPlayOutChat").getConstructor(ReflectionUtils.getNMSClass("IChatBaseComponent"), byte.class);
			Object packet = constructor.newInstance(msg, (byte) 2);

			PacketUtils.sendPacket(player, packet);
		} catch (Exception e) {
			Main.plugin.getLogger().severe(ChatColor.RED + "Could not send action packet to " + player.getName() + ", are you using 1.8 or higher?");
		}
	}
}