package com.leontg77.timer;

import org.bukkit.Bukkit;

/**
 * Reflection utilities class.
 *
 * @author ghowden
 */
public class ReflectionUtils {
    private static String nmsPacketVersion = null;

    /**
     * Get the current NMS version.
     *
     * @return The NMS version.
     */
    private static String getNMSVersion() {
        if (null == nmsPacketVersion) {
            String packageName = Bukkit.getServer().getClass().getPackage().getName();
            nmsPacketVersion = packageName.substring(packageName.lastIndexOf(".") + 1);
        }

        return nmsPacketVersion;
    }

    /**
     * Get the NMS class of the given name.
     *
     * @param name The name to get it by.
     * @return The class.
     * @throws ClassNotFoundException If class is not found.
     */
    public static Class<?> getNMSClass(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + getNMSVersion() + "." + name);
    }

    /**
     * Get the craft bukkit class of the given name.
     *
     * @param name The name to get it by.
     * @return The class.
     * @throws ClassNotFoundException If class is not found.
     */
    public static Class<?> getCraftBukkitClass(String name) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + getNMSVersion() + "." + name);
    }
}