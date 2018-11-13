package com.leontg77.timer.handling;

/**
 * Timer handler super interface.
 *
 * @author LeonTG
 */
public interface TimerHandler {
    byte ACTION_BAR_TYPE = 2;

    /**
     * Start the timer handler.
     *
     * @param text The text to display on it.
     */
    void startTimer(String text);

    /**
     * Stop the timer handler.
     */
    void onCancel();

    /**
     * Send a new packet of the text for the timer handler.
     *
     * @param text The new text.
     * @param remaining The amount of seconds remaining.
     * @param total The total amount of seconds for the timer.
     */
    void sendText(String text, int remaining, int total);
}