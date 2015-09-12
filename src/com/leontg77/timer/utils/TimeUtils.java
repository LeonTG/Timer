package com.leontg77.timer.utils;

/**
 * Time utilities class
 * <p>
 * Contains method for converting time to String.
 * 
 * @author ghowden, modified by LeonTG77
 */
public class TimeUtils {
	private static final long SECONDS_PER_HOUR = 3600;
	private static final long SECONDS_PER_MINUTE = 60;

	/**
     * Converts the seconds into a string with hours, minutes and seconds.
     * 
     * @param ticks the number of seconds.
     * @return The converted seconds.
     */
    public static String timeToString(long ticks) {
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