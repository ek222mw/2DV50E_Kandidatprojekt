/**
 * JsonObject.java
 * 27 feb 2016
 */
package lnu.sales;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * A simple abstract class defining common properties of all SalesSpecific objects.
 * 
 * @author Emil
 *
 */

public abstract class SalesSpecific {

	public abstract NewItem getNewItem(Document document, String url, Boolean New, Boolean firstSeen);

	public abstract NewItem getRemovedNewItem(NewItem ni, Boolean New);

	public abstract ListItem parseRow(Element e);

	public abstract ArrayList<ListItem> traverseAllObjects(Boolean pageLoaded, int IterationSleep10min);
	
	public abstract int getIterationSleep();

	public abstract Boolean retryToGetPage(int pageNumber, String page, ArrayList<ListItem> allObjects, int count,
			int IterationSleep1min, int IterationSleep2min);

	public abstract double getDouble(String s);

	public abstract int RandomizeSleepTime();
	
	public abstract Element getMissedPageTag(Document doc);

}
