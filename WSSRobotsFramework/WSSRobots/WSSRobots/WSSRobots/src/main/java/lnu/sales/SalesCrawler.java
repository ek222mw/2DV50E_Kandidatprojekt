package lnu.sales;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Extracting objects data from different sales websites.
 * 
 * This is rather straight forward since each page in the list has their own url
 * and we can move from page 1 to 2 by simply switch from 1 to 2 in the url.
 * 
 * Basic idea =========== 1. Initialize by traversing all pages to find (and
 * save) current details for each new object 2. Every N:th minute 2.1 Traverse all
 * pages to find the details for each new object 2.2 If object details changed ==>
 * save new details
 * 
 * Save ==> store in map id2object and add to output queue jsonQueueNewObject
 *
 * @author Emil & Mikael
 *
 */
public class SalesCrawler extends Thread {
	
	private final int IterationFailSleep = 10;
	// Time out sleep times
	private final int IterationSleep10min = 10;
	private final BlockingQueue<JsonObject> jsonQueueNewObject;// Our output channel
	private HashMap<String, ListItem> id2object = new HashMap<String, ListItem>(); //Hashmap saving all objects used to identify new objects.
	private HashMap<String, ListItem> id2newobject = new HashMap<String, ListItem>();// Hashmap containing new objects.
	private ArrayList<String> objectsToBeRemoved = new ArrayList<String>(); //Arraylist containg objects to be removed.
	private HashMap<String, NewItem> newObjectsToBeCrawledIfNotExisting = new HashMap<String, NewItem>(); //Hashmap saving objects that has been removed from webbsite(can't be recrawled) that can be used to get the details from stage when existed on webbsite.
	private Boolean firstSeen = false; //Field identifying is object has been seen before or not.
	private Boolean New = false; //Field used to identify if object is new or has been removed(status = false).
	private boolean pageLoaded = true; //Field used to steer the program.
	private boolean waitForConnection = false;
	private SalesSpecific ss; //Field this is holding current SalesSpecific that is running.

	// Constructor
	public SalesCrawler(BlockingQueue<JsonObject> queue2, SalesSpecific ss) {
		super("SalesCrawlerThread");
		jsonQueueNewObject = queue2;
		this.ss = ss;
		System.out.println("Enter " + this.getClass().getName());
	}

	@Override
	public void run() {
		boolean doClearNewObjectList = false;
		
		 // Time between iterations in	minutes
		int IterationSleep = ss.getIterationSleep();
		// Initialize all objects.
		System.out.println("System date: " + new Date());
		System.out.println("\n" + Util.getCurrentTime() + ": Initializing all objects");
		ArrayList<ListItem> allObjects = ss.traverseAllObjects(pageLoaded, IterationSleep10min);
		int totalCountNewObjects = 0;
		int totalCountRemovedCars = 0;
		// adds objects to hashmaps in initialize state runned 1 time.
		for (ListItem c : allObjects) {
			String id = c.getUrl();
			id2object.put(id, c);
		}
		// Repeat forever
		while (true) {
			// if could not load page sleep with set failtime and try again.
			if (pageLoaded == false) {
				System.out.println(Util.getCurrentTime() + "	" + ">>>>>Waiting " + IterationFailSleep
						+ " minutes for restart<<<<<");
				Util.sleep(IterationFailSleep * 60 * 1000);
			} else {
				// page could be reloaded, sleep and then continue the
				// iteration.
				System.out.println(Util.getCurrentTime() + "	" + ">>>>>Waiting " + IterationSleep
						+ " minutes before next run<<<<<");
				Util.sleep(IterationSleep * 60 * 1000);
			}
			// Set the pageLoaded variable
			pageLoaded = true;
			System.out.println("\n" + Util.getCurrentTime() + ": Starting a new iteration");

			// Getting current objects
			allObjects = ss.traverseAllObjects(pageLoaded, totalCountRemovedCars);
			// sets all counts to 0;
			int newCount = 0; // Counts new objects.
			int updateCount = 0; // Counts updated objects.
			int newObjectsRemovedCount = 0; // Counts objects that are new that has been removed.

			// Foreach current objects in arraylist
			for (ListItem li : allObjects) {
				String id = li.getUrl();
				ListItem oldObject = id2object.get(id);
				
				// If old object is null(not existing) then equals new object
				if (oldObject == null) {
					firstSeen = true; // If new then not seen before in queue then change the variable to false.
					New = true; // Set variable New to true.
					id2object.put(id, li); // Add to hashmap for all objects.
					id2newobject.put(id, li); // Add to hashmap for just new objects.

					Document doc = null;
					// Trying to get a specific new car url
					try {
						doc = Jsoup.connect(li.getUrl()).timeout(6000).get();
						NewItem nObject = ss.getNewItem(doc, li.getUrl(), New, firstSeen);
						// If the object is null nothing will be added to the list
						if (nObject != null) {
							newObjectsToBeCrawledIfNotExisting.put(li.getUrl(), nObject);
							Util.addToQueue(jsonQueueNewObject, nObject);
						} else {
							// if nObject object is null then decrease counters with 1.
							newCount--;
							totalCountNewObjects--;
						}
					} catch (IOException e) {
						System.out.println("Could not connect while trying to crawl new object");
					}
					System.out.println("New object found: " + li.toJsonString());
					// Increase variables with 1.
					newCount++;
					totalCountNewObjects++;
				}

				// Checking if existing new object details have been changed
				else if (!oldObject.equals(li)) {

					try {
						String url = li.getUrl();

						if (!id2newobject.containsKey(url)) {
							updateCount++; //Increase counter with 1.
							id2newobject.put(li.getUrl(), li); //add to hashmap.
							id2object.put(li.getUrl(), li); //add to hashmap.
							// Set variable to false because object have been found but with different details.
							firstSeen = false;
							New = true; //Set field to true.
							Document doc = null;
							doc = Jsoup.connect(li.getUrl()).timeout(6000).get(); //connects to webbsite trying to get page
							NewItem nObject = ss.getNewItem(doc, li.getUrl(), New, firstSeen); //Send page to specific to be crawled.
							// If the object is null nothing will be added to the list
							if (nObject != null) {
								// Adds updated object to stream.
								Util.addToQueue(jsonQueueNewObject, nObject);
							} else {
								updateCount--;
							}
						}
					} catch (IOException e) {
						System.out.println("Could not connect while trying to crawl updated object");
					}

				}
			}

			// Foreach all new objects in hashmap to see if any of the new objects added have been removed during the run.
			for (ListItem newobject : id2newobject.values()) {
				String url = newobject.getUrl();

				if (!newObjectExistsInList(url, allObjects)) {
					// if removed then print the objekt in console and increase the counter.
					doClearNewObjectList = true;
					System.out.println("New object that has been removed was found: " + newobject.toJsonString());
					Document doc = null;

					try {
						// set New to false to indicate that the new object have been removed from website.
						New = false;
						//Connecting to webbsite trying to get page.
						doc = Jsoup.connect(newobject.getUrl()).timeout(6000).get();
						//Send to Specific to be crawled.
						NewItem nObjectRemoved = ss.getNewItem(doc, newobject.getUrl(), New, firstSeen);
						if (nObjectRemoved == null) {
							// If object removed from website, get it from saved list
							NewItem ni = newObjectsToBeCrawledIfNotExisting.get(newobject.getUrl());
							if (ni != null) {
								// Set new status in Object to variable value.
								NewItem n = ss.getRemovedNewItem(ni, New);
								Util.addToQueue(jsonQueueNewObject, n);
							} else {
								System.out.println("ni = null");
							}
						}

						// If the object is null nothing will be added to the list
						if (nObjectRemoved != null) {
							Util.addToQueue(jsonQueueNewObject, nObjectRemoved);
						} else {
							// Decrease counter with 1.
							newObjectsRemovedCount--;
						}

					} catch (IOException e) {
						System.out.println("Could not connect while trying to crawl new objects that have been removed");
					}
					// Adds new objects that have been removed from website.
					objectsToBeRemoved.add(newobject.getUrl());
					newObjectsRemovedCount++;
				}

			}
			// Removes new objects from hashmap id2newobject that have been removed during this iteration.
			for (String str : objectsToBeRemoved) {
				id2newobject.remove(str);
			}

			// Clears the hashmap with new objects that have been removed so that they do not appear again in next iteration.
			if (doClearNewObjectList) {
				objectsToBeRemoved.clear();
				doClearNewObjectList = false;
			}
			
			if (newObjectsRemovedCount > 0) // if larger then 0 then print the count
			{
				System.out.println("New object that has been removed count: " + newObjectsRemovedCount);
			}

			if (newCount > 0 || updateCount > 0) // if larger then 0 then print the count
			{
				System.out.println("Object count: " + allObjects.size() + ", New objects: " + newCount + ", Updated objects: " + updateCount);
				System.out.println("Total New objects: " + totalCountNewObjects);
			} else {
				System.out.println("Object count: " + allObjects.size());
			}
			System.out.println("Id2object size: " + id2object.size());

		}

	}

	/**
	 * Checking if a url belongs to a object in a list. If it does it will return true, otherwise false.
	 */
	public boolean objectExistsInList(String url, ArrayList<ListItem> list) {
		ArrayList<String> urls = new ArrayList<String>();
		// Adds all of the urls to a list.
		for (ListItem c : list) {
			urls.add(c.getUrl());
		}

		// Checks if the list contains the url
		if (urls.contains(url)) {
			return true;
		}
		return false;
	}
	/**
	 * Checking if a url belongs to a object in a list. If it does it will return true, otherwise false.
	 */
	public boolean newObjectExistsInList(String url, ArrayList<ListItem> list) {
		if (waitForConnection) {
			System.out.println("Waiting " + IterationFailSleep + " minutes before checking if object still exists.");
			Util.sleep(IterationFailSleep * 60 * 1000);
		}
		ArrayList<String> urls = new ArrayList<String>();
		// Adds all of the urls to a list.
		for (ListItem c : list) {
			urls.add(c.getUrl());
		}
		// Checks if the list contains the url
		if (urls.contains(url)) {
			return true;
		} else {
			// If the list does not contains the url waitForConnection variable indicates if the program should wait a while before trying to reconnect
			Document doc = null;
			try {
				try {
					// Tries to connect. If it succeeds waitForConnection will be set to false
					doc = Jsoup.connect(url).timeout(6000).get();
					waitForConnection = false;
				} catch (IOException e) {
					// If it fails waitForConnection will be set to true
					waitForConnection = true;
					System.out.println("Could not connect");
				}
				// Checks if there is specific tag on the page if the connection have succeeded
				if (doc != null) {
					Element name;
					//Check If there is not a specific tag on the page
					name = ss.getMissedPageTag(doc);
				}
			} catch (IndexOutOfBoundsException e) {
				return false;
			}

		}
		return true;
	}

}
