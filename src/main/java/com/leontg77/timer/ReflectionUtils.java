/*
 * Project: Timer
 * Class: com.leontg77.timer.ReflectionUtils
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2018 Leon Vaktskjold <leontg77@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
