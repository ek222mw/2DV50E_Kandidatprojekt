/**
 * FakeJsonConsumer.java
 * 27 feb 2016
 */
package lnu.sales;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

/**
 * A fake Json consumer just saving all Json objects read from a queue 
 * in an ordinary text file.
 * 
 * @author Emil.
 *
 */
public class FakeJsonConsumer extends Thread {
		
	private final BlockingQueue<JsonObject> jsonQueue;
	/**
	 *constructor
	 * @param queue
	 * setting input queue to variable jsonQueue. 
	 */
	public FakeJsonConsumer( BlockingQueue<JsonObject> queue) {
		super("NewSalesClientThread");
		jsonQueue = queue;
		
		System.out.println("Enter "+this.getClass().getName());
	}
	
	
	@Override
	public void run() {
		System.out.println("FakeJsonConsumer thread up and running ...");
		BufferedWriter out = null;
		FileWriter fw = null;
		try {

			// Open output file
			String dumpPath = getNewObjectConsumerDir();
			String dumpFile = dumpPath + File.separator + "Crawlerdata.txt";
			

			try {
				while (true) { // Save json object 
					
					fw = new FileWriter(dumpFile,true);
					out = new BufferedWriter(fw);
					JsonObject obj = jsonQueue.take(); // Read from input queue
					out.write(obj.toJsonString()+ "\n"); //Writes to textfile
					out.close();
					
					
				}
			} catch (InterruptedException ex) { ex.printStackTrace();}
		}catch (IOException e) {
			System.err.println(e);
		}
		finally{
			
				
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			
		}
		
	}
	/**
	 * Creates textfile folder for saving new objects.
	 */
	private static String dump_path = null;
	public static String getNewObjectConsumerDir() {
		if (dump_path == null) {
			String user_path = System.getProperty("user.dir");
			dump_path = user_path + File.separator + "NewSalesData";
			File file = new File(dump_path);
			if (!file.exists())
				file.mkdirs();
		}
		System.out.println("New Sales dir: "+dump_path);
		return dump_path;
	}
}
