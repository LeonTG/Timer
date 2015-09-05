package com.leontg77.timer.utils;

import org.bukkit.Bukkit;

public class ReflectionUtils {

    protected static String nmsPacketVersion = null;

    public static Class<?> getNMSClass(String subPackage) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + getNmsPacketVersion() + "." + subPackage);
    }

    public static Class<?> getCraftBukkitClass(String subPackage) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + getNmsPacketVersion() + "." + subPackage);
    }

    public static String getNmsPacketVersion() {
        // set it if it doesn't exist currently
        if (null == nmsPacketVersion) {
            // grab the version number from CraftServer implementation
            String packageName = Bukkit.getServer().getClass().getPackage().getName();
            nmsPacketVersion = packageName.substring(packageName.lastIndexOf(".") + 1);
        }

        return nmsPacketVersion;
    }
}
