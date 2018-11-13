/*
 * Project: ActionTimer
 * Class: com.leontg77.timer.Main
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Leon Vaktskjold <leontg77@gmail.com>.
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
import com.leontg77.timer.handling.TimerHandler;
import com.leontg77.timer.handling.handlers.BossBarHandler;
import com.leontg77.timer.handling.handlers.NewActionBarHandler;
import com.leontg77.timer.handling.handlers.OldActionBarHandler;
import com.leontg77.timer.runnable.TimerRunnable;
import com.leontg77.timer.handling.PacketSender;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
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
		TimerRunnable timer;

		try {
			timer = setupTimer();
		} catch (Exception ex) {
			getLogger().log(Level.SEVERE, "Failed to setup action timer plugin, are you using Minecraft 1.8 or higher?", ex);
			setEnabled(false);
			return;
		}

		getCommand("timer").setExecutor(new TimerCommand(timer));
	}

    /**
     * Setup the timer runnable object.
     *
     * @return The created timer runnable.
     * @throws NoSuchMethodException If no methods exist for sending packets or handling the timer.
     * @throws NoSuchFieldException If no fields exist for sending packets or handling the timer.
     * @throws ClassNotFoundException If no classes exist for sending packets or handling the timer.
     */
	private TimerRunnable setupTimer() throws NoSuchMethodException, NoSuchFieldException, ClassNotFoundException {
		PacketSender packetSender = new PacketSender();
		FileConfiguration config = getConfig();

		if (config.getBoolean("bossbar.enabled")) {
			try {
				return new TimerRunnable(this, new BossBarHandler(config.getString("bossbar.color", "pink"), config.getString("bossbar.style", "solid")));
			} catch (Exception ignored) {}

			getLogger().warning("BossBars are not supported in pre Minecraft 1.9, defaulting to action bar.");
		}

		try {
			return new TimerRunnable(this, new NewActionBarHandler(packetSender));
		} catch (Exception ex) {
			return new TimerRunnable(this, new OldActionBarHandler(packetSender));
		}
	}
}