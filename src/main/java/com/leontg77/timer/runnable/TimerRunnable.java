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

    private long ticksRemaining = 0;
    private String message;

    @Override
    public void run() {
        try {
            handler.sendText(message + (countdown ? " " + plugin.timeToString(ticksRemaining) : ""));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (countdown) {
            if (ticksRemaining == 0) {
                cancel();
                return;
            }

            ticksRemaining--;
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
    public void startSendingMessage(String message, long seconds) {
        this.ticksRemaining = seconds;
        this.message = message;

        this.countdown = seconds > -1;

        handler.startTimer();

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
}