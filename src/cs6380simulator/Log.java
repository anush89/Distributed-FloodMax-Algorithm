package cs6380simulator;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Simple static class for outputting log messages
 *
 */
public final class Log {
	private Log(){
		
	}
	
	private static PrintStream outputStream = System.out;
	
	/**
	 * Outputs a logging message
	 * @param source The source of the message
	 * @param message The message to output
	 */
	public static void logMessage(String source, String message){
		
		if (outputStream == null){
			return;
		}
		
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.S");
		outputStream.println(String.format("%s [%s] - %s", df.format(new Date()), source, message));
	}
	
	/**
	 * Sets the output stream for the log messages
	 * @param outputStream The stream to use for future log messages
	 */
	public static void setOutputStream(PrintStream outputStream){
		Log.outputStream = outputStream;
	}
}
