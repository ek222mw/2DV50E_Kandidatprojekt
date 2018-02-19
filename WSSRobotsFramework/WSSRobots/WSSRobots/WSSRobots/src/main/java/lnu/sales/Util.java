/**
 * Util.java
 * 28 feb 2016
 */
package lnu.sales;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

/**
 * @author Emil.
 *
 */
public class Util {
	private static SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	/**
	 * Method that returns current time.
	 */
	public static String getCurrentTime() {
		Date date = new Date();
		String dateTime = timeFormat.format( date);
		return dateTime;
	}
	/**
	 * Method that returns time in current days.
	 */
	public static String getCurrentDay() {
		Date date = new Date();
		String dateTime = dayFormat.format( date);
		return dateTime;
	}
	/**
	 * Adds object to queue.
	 */
	public static void addToQueue(BlockingQueue<JsonObject> jsonQueue, JsonObject string) {
		try {  jsonQueue.put( string ); } 
		catch (InterruptedException ex) { ex.printStackTrace();}
	}
	/**
	 * Method that makes program sleep.
	 */
	public static void sleep(int ms) {
		try { Thread.sleep(ms);	   
		} catch (InterruptedException e) { e.printStackTrace();}  
	}
}
