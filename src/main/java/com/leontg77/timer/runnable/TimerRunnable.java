/*
 * Project: Timer
 * Class: com.leontg77.timer.runnable.TimerRunnable
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

package com.leontg77.timer.runnable;

import com.leontg77.timer.Main;
import com.leontg77.timer.handling.TimerHandler;
import com.leontg77.timer.handling.handlers.BossBarHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Timer runnable class.
 *
 * @author LeonTG & ghowden
 */
public class TimerRunnable implements Runnable {
    private final TimerHandler handler;
    private final Main plugin;

    public TimerRunnable(Main plugin, TimerHandler handler, String command) {
        this.handler = handler;
        this.plugin = plugin;
        this.command = command;

        if (handler instanceof Listener) {
            Bukkit.getPluginManager().registerEvents((Listener) handler, plugin);
        }
    }

    private boolean countdown = true;
    private int jobId = -1;

    private String message;
    private String command;

    private int remaining = 0;
    private int total = 0;

    @Override
    public void run() {
        if (handler instanceof BossBarHandler) {
            ((BossBarHandler) handler).updateProgress(remaining, total);
        }

        if (remaining % 20 == 0) {
            handler.sendText(message + (countdown ? " " + timeToString(remaining / 20) : ""));
        }

        if (countdown) {
            if (remaining == 0) {
                if (command.length() > 0) {
                    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                    Bukkit.dispatchCommand(console, command);
                }
                cancel();
                return;
            }

            remaining--;
        }
    }

    /**
     * Starts this timer with the given message for the amount of invocations.
     * Once complete the task will cancel itself.
     *
     * Overwrites any previous settings
     *
     * @param message the message to send
     * @param seconds the amount of seconds to send the message for
     */
    public void startSendingMessage(String message, int seconds) {
        this.remaining = (seconds * 20);
        this.total = (seconds * 20);

        this.countdown = seconds > -1;
        this.message = message;

        handler.startTimer(message + (countdown ? " " + timeToString(remaining / 20) : ""));

        cancel();
        jobId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 1L);
    }

    /**
     * Cancel the timer task if it's running.
     */
    public void cancel() {
        if (jobId == -1) {
            return;
        }

        Bukkit.getScheduler().cancelTask(jobId);
        jobId = -1;

        handler.onCancel();
    }

    /**
     * Check if the timer is currently running.
     *
     * @return True if it is, false otherwise.
     */
    public boolean isRunning() {
        BukkitScheduler sch = Bukkit.getScheduler();
        return sch.isCurrentlyRunning(jobId) || sch.isQueued(jobId);
    }

    /**
     * Get the handler for the timer.
     *
     * @return The timer handler.
     */
    public TimerHandler getHandler() {
        return handler;
    }

    private static final long SECONDS_PER_HOUR = 3600;
    private static final long SECONDS_PER_MINUTE = 60;

    /**
     * Converts the seconds into a string with hours, minutes and seconds.
     *
     * @param ticks the number of seconds.
     * @return The converted seconds.
     */
    private String timeToString(long ticks) {
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

    /**
     * Updates the command to run on completion.
     *
     * @param command The command to run.
     */
    public void update(String command) {
        this.command = command;
    }

    /**
     * Updates the message shown.
     * @param message The message
     */
    public void updateMessage(String message) {
        this.message = message;
    }
}
