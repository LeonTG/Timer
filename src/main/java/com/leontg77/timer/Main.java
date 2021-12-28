/*
 * Project: Timer
 * Class: com.leontg77.timer.Main
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

import com.leontg77.timer.commands.TimerCommand;
import com.leontg77.timer.handling.handlers.BossBarHandler;
import com.leontg77.timer.handling.handlers.NewActionBarHandler;
import com.leontg77.timer.handling.handlers.OldActionBarHandler;
import com.leontg77.timer.runnable.TimerRunnable;
import com.leontg77.timer.handling.PacketSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * Main class of the plugin.
 * 
 * @author LeonTG
 */
public class Main extends JavaPlugin {
    public static final String PREFIX = "§cTimer §8» §7";
    
    @Override
    public void onEnable() {
        reloadConfig();
        getCommand("timer").setExecutor(new TimerCommand(this));
    }

    private TimerRunnable runnable = null;

    /**
     * Get the current runnable for the timer.
     *
     * @return The current runnable.
     */
    public TimerRunnable getRunnable() {
        return runnable;
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();

        if (getConfig().getConfigurationSection("bossbar") == null) {
            getConfig().set("bossbar.enabled", true);
            getConfig().set("bossbar.color", "pink");
            getConfig().set("bossbar.style", "solid");
            saveConfig();
        }

        if (getConfig().getConfigurationSection("timer") == null) {
            getConfig().set("timer.command", "");
            saveConfig();
        }

        if (runnable != null && runnable.getHandler() instanceof Listener) {
            HandlerList.unregisterAll((Listener) runnable.getHandler());
        }

        try {
            FileConfiguration config = getConfig();

            if (config.getBoolean("bossbar.enabled")) {
                try {
                    runnable = new TimerRunnable(this, new BossBarHandler(this, config.getString("bossbar.color", "pink"), config.getString("bossbar.style", "solid")), config.getString("timer.command",""));
                    return;
                } catch (Exception ignored) {}

                getLogger().warning("BossBars are not supported in pre Minecraft 1.9, defaulting to action bar.");
            }
    
            try {
                runnable = new TimerRunnable(this, new NewActionBarHandler(), config.getString("timer.command",""));
            } catch (Exception ex) {
                runnable = new TimerRunnable(this, new OldActionBarHandler(), config.getString("timer.command",""));
            }
        } catch (Exception ex) {
            getLogger().log(Level.SEVERE, "Failed to setup action timer plugin, are you using Minecraft 1.8 or higher?", ex);
            setEnabled(false);
        }
    }
}
