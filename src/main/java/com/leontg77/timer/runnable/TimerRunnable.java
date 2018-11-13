/*
 * Project: ActionTimer
 * Class: com.leontg77.timer.managers.TimerRunnable
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

package com.leontg77.timer.runnable;

import com.leontg77.timer.Main;
import com.leontg77.timer.handling.TimerHandler;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Timer runnable class.
 *
 * @author LeonTG & ghowden
 */
public class TimerRunnable implements Runnable {
    private final TimerHandler handler;
    private final Main plugin;

    public TimerRunnable(Main plugin, TimerHandler handler) {
        this.handler = handler;
        this.plugin = plugin;
    }

    private boolean countdown = true;
    private int jobId = -1;

    private String message;

    private int remaining = 0;
    private int total = 0;

    @Override
    public void run() {
        try {
            handler.sendText(message + (countdown ? " " + timeToString(remaining) : ""), remaining, total);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (countdown) {
            if (remaining == 0) {
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
        this.remaining = seconds;
        this.total = seconds;

        this.countdown = seconds > -1;
        this.message = message;

        handler.startTimer(message);

        cancel();
        jobId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 20);
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
}