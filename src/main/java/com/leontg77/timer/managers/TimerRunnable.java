package com.leontg77.timer.managers;

import com.leontg77.timer.Main;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Timer runnable class.
 *
 * @author LeonTG77 & ghowden
 */
public class TimerRunnable implements Runnable {
    private final ActionSender sender;
    private final Main plugin;

    public TimerRunnable(Main plugin, ActionSender sender) {
        this.plugin = plugin;
        this.sender = sender;
    }

    private boolean countdown = true;
    private int jobId = -1;

    private long ticksRemaining = 0;
    private String message;

    @Override
    public void run() {
        // send to all
        try {
            sender.sendToAll(message + (countdown ? " " + plugin.timeToString(ticksRemaining) : ""));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (countdown) {
            // cancel the task when we're complete
            if (ticksRemaining == 0) {
                cancel();
                return;
            }

            // remove one from our time left
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

        this.ticksRemaining = seconds;
        this.countdown = seconds > -1;
        this.message = message;

        // start once per second
        cancel();
        jobId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 20);
    }

    /**
     * Cancel the timer task if it's running.
     */
    public void cancel() {
        if (jobId != -1) {
            Bukkit.getScheduler().cancelTask(jobId);
            jobId = -1;
        }
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
