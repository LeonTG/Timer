/*
 * Project: Timer
 * Class: com.leontg77.timer.handling.handlers.BossBarHandler
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

package com.leontg77.timer.handling.handlers;

import com.leontg77.timer.Main;
import com.leontg77.timer.ReflectionUtils;
import com.leontg77.timer.handling.TimerHandler;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * Boss bar timer handler.
 *
 * @author LeonTG
 */
public class BossBarHandler implements TimerHandler, Listener {
    private final Main plugin;

    private final Class<?> colorClass;
    private final Class<?> styleClass;

    private final Method setProgress;
    private final Method setTitle;

    private final Method setVisible;
    private final Method addPlayer;

    private final Method setColor;
    private final Method setStyle;

    public BossBarHandler(Main plugin, String color, String style) throws NoSuchMethodException, ClassNotFoundException {
        this.plugin = plugin;

        this.colorClass = Class.forName("org.bukkit.boss.BarColor");
        this.styleClass = Class.forName("org.bukkit.boss.BarStyle");

        try {
            Field colorF = colorClass.getDeclaredField(color.toUpperCase());
            colorF.setAccessible(true);

            Field styleF = styleClass.getDeclaredField(style.toUpperCase());
            styleF.setAccessible(true);

            this.color = colorF.get(null);
            this.style = styleF.get(null);
        } catch (Exception ex) {
            throw new IllegalArgumentException();
        }

        Class<?> clazz = Class.forName("org.bukkit.boss.BossBar");

        this.setProgress = clazz.getMethod("setProgress", double.class);
        this.setProgress.setAccessible(true);

        this.setTitle = clazz.getMethod("setTitle", String.class);
        this.setTitle.setAccessible(true);

        this.setVisible = clazz.getMethod("setVisible", boolean.class);
        this.setVisible.setAccessible(true);

        this.addPlayer = clazz.getMethod("addPlayer", Player.class);
        this.addPlayer.setAccessible(true);

        this.setColor = clazz.getMethod("setColor", colorClass);
        this.setColor.setAccessible(true);

        this.setStyle = clazz.getMethod("setStyle", styleClass);
        this.setStyle.setAccessible(true);
    }

    private Object bossBar = null;
    private Object color;
    private Object style;

    @Override
    public void startTimer(String text) {
        try {
            Class<?> flagClass = Class.forName("[Lorg.bukkit.boss.BarFlag;");

            Method getBossBar = Server.class.getMethod("createBossBar", String.class, colorClass, styleClass, flagClass);
            getBossBar.setAccessible(true);

            bossBar = getBossBar.invoke(Bukkit.getServer(), text, color, style, Array.newInstance(flagClass.getComponentType(), 0));
            setProgress.invoke(bossBar, 1.0);

            for (Player online : Bukkit.getOnlinePlayers()) {
                addPlayer.invoke(bossBar, online);
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "An error occurred while starting boss bar timer, are you using Minecraft 1.9 or higher?", ex);
        }
    }

    @Override
    public void onCancel() {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            try {
                setVisible.invoke(bossBar, false);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                Bukkit.getLogger().log(Level.SEVERE, "An error occurred while stopping boss bar timer, are you using Minecraft 1.9 or higher?", ex);
            }

            bossBar = null;
        }, 20L);
    }

    @Override
    public void sendText(String text) {
        try {
            setTitle.invoke(bossBar, text);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "An error occurred while updating boss bar timer, are you using Minecraft 1.9 or higher?", ex);
        }
    }

    /**
     * Update the color and style of this boss bar.
     *
     * @param newColor The new color.
     * @param newStyle The new style.
     *
     * @throws NoSuchFieldException If it can't find the fields of the enums.
     * @throws IllegalAccessException If it can't access the fields.
     * @throws InvocationTargetException If, uhh, idk?
     */
    public void update(String newColor, String newStyle) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        Field colorF = colorClass.getDeclaredField(newColor);
        colorF.setAccessible(true);

        Field styleF = styleClass.getDeclaredField(newStyle);
        styleF.setAccessible(true);

        this.color = colorF.get(null);
        this.style = styleF.get(null);

        if (bossBar == null) {
            return;
        }

        setColor.invoke(bossBar, color);
        setStyle.invoke(bossBar, style);
    }

    /**
     * Update the progress bar on the dragon timer.
     *
     * @param remaining The remaining seconds.
     * @param total The total seconds.
     */
    public void updateProgress(int remaining, int total) {
        try {
            setProgress.invoke(bossBar, ((double) remaining) / ((double) total));
        } catch (IllegalAccessException | InvocationTargetException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "An error occurred while updating boss bar progress, are you using Minecraft 1.9 or higher?", ex);
        }
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (bossBar != null) {
                try {
                    addPlayer.invoke(bossBar, player);
                } catch (IllegalAccessException | InvocationTargetException ex) {
                    Bukkit.getLogger().log(Level.SEVERE, "An error occurred while adding boss bar timer for '" + player.getName() + "', are you using Minecraft 1.9 or higher?", ex);
                }
            }
        });
    }
}
