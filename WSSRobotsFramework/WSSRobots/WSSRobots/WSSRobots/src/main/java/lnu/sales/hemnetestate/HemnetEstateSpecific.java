package lnu.sales.hemnetestate;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import lnu.sales.ListItem;
import lnu.sales.NewItem;
import lnu.sales.SalesSpecific;
import lnu.sales.Util;

public class HemnetEstateSpecific extends SalesSpecific {
	/**
	 * Crawling tags for specific new Hemnet estate details as NewItem type.
	 */
	public NewItem getNewItem(Document document, String url, Boolean New, Boolean firstSeen) {
		Element content = null;

		// If there is not an article tag on the page the method return null
		try {
			//Gets name
			content = (document.getElementsByAttributeValueContaining("class", "content")).get(0);
		} catch (IndexOutOfBoundsException e) {
			content = (document.getElementsByAttributeValueContaining("class", "content")).get(1);
		}
		Element propertytable = null;
		try {
			propertytable = content.getElementsByAttributeValueContaining("class", "property__container item clear-children").get(0);
		} catch (IndexOutOfBoundsException ex) {
			
		}
		
		String location = null;
		try {
			location = (document.getElementsByAttributeValueContaining("class", "mr2")).get(0).text();
		} catch (IndexOutOfBoundsException e) {
			// System.out.println("Article does not exist.");
			location = "-";
		}

		// Gets the title
		Element title = propertytable.select("h1").get(0);
		// Gets the price
		Element estatePrice = propertytable.getElementsByAttributeValueContaining("class", "property__price").get(0);
		// Gets the seller
		Element getSellerTag = content.getElementsByAttributeValueContaining("class", "broker-info clear-children").get(0);
		Element seller = getSellerTag.select("a").get(0);

		// Gets the properties
		Element properties = propertytable.getElementsByAttributeValueContaining("class", "property__attributes").get(0);
		Element estateType = properties.select("dd").get(0);
		int propertyCount = 0;

		// Checks how many properties
		for (int i = 1; i < 15; i++) {
			try {
				Element property = properties.select("dt").get(i);
				propertyCount++;
			} catch (Exception e) {
				
			}
		}

		Element property = null;
		String livingArea = "-";
		String numberOfRooms = "-";
		String monthlyFee = "-";
		String squarePrice = "-";
		String operatingCost = "-";
		String buildYear = "-";
		String biArea = "-";
		String yardArea = "-";
		String floors = "-";

		// Checks what properties that exists and gets them
		for (int i = 1; i <= propertyCount; i++) {
			property = properties.select("dt").get(i);

			if (property.text().equals("Boarea")) {
				livingArea = properties.select("dd").get(i).text();
			}
			if (property.text().equals("Antal rum")) {
				numberOfRooms = properties.select("dd").get(i).text();
			}
			if (property.text().equals("Avgift/månad")) {
				monthlyFee = properties.select("dd").get(i).text();
			}
			if (property.text().equals("Pris/m²")) {
				squarePrice = properties.select("dd").get(i).text();
			}
			if (property.text().equals("Driftkostnad")) {
				operatingCost = properties.select("dd").get(i).text();
			}
			if (property.text().equals("Byggår")) {
				buildYear = properties.select("dd").get(i).text();
			}
			if (property.text().equals("Biarea")) {
				biArea = properties.select("dd").get(i).text();
			}
			if (property.text().equals("Tomtarea")) {
				yardArea = properties.select("dd").get(i).text();
			}
		}
		//Gets timestemp
		long timeStamp = System.currentTimeMillis();
		NewItem newEstate = new NewItem(title.text(), New, firstSeen, estatePrice.text(), url,location, seller.text(),
				buildYear,monthlyFee,yardArea, biArea,floors,operatingCost, numberOfRooms,livingArea,
				squarePrice, timeStamp,estateType.text());
		//returns new Hemnet estate
		return newEstate;
	}
	/**
	 * Crawls tags for specific List object for Hemnet estate as ListItem type
	 */
	public ListItem parseRow(Element Row) {

				// Get estate title
				Element t = (Row.getElementsByAttributeValueContaining("class",
						"area item-result-meta-attribute-is-bold")).get(0);
				String title = t.text();
	
				Element u = null;
				String url = "";
				// Get estate url
				try {
					u = (Row.getElementsByAttributeValueContaining("class", "item-link-container")).get(0);
					url = "http://www.hemnet.se" + u.attr("href");
				} catch (IndexOutOfBoundsException e) {
					return null;
				}
				// Get estate price
				Element p = (Row.getElementsByAttributeValueContaining("class",
						"price item-result-meta-attribute-is-bold")).get(0);
				String stringPrice = p.text();
				String filtered = stringPrice.replaceAll("[^\\d.]", "");
				double price = getDouble(filtered);
				// System.out.println(price);

				// Set time stamp
				long timeStamp = System.currentTimeMillis();

				ListItem estate = new ListItem(title, url, price, timeStamp);

				return estate;
	}
	/**
	 * Crawling all list objects(within website) for Hemnet estates.
	 */
	public ArrayList<ListItem> traverseAllObjects(Boolean pageLoaded, int IterationSleep10min) {

		int IterationSleep1min = 1;
		int IterationSleep2min = 2;
		String firstPart = "http://www.hemnet.se/bostader?advanced=1&deactivated_before_open_house_day=0&new_construction=0&page=";
		String secondPart = "&row_house=1&upcoming=0";
		int pageNumber = 0;
		boolean keepOn = true;
		int count = 0;
		ArrayList<ListItem> allEstates = new ArrayList<ListItem>();

		while (keepOn && pageLoaded) {
			pageNumber++;
			String page = firstPart + pageNumber + secondPart; // Same url but different page number
			Document doc = null;
			Elements estates = null;
			

			try {
	
				count = 0;
				//Connect and get hemnet estates
				doc = Jsoup.connect(page).timeout(6000).get();
				estates = doc.getElementsByAttributeValueContaining("class", "item result normal  ");

				int size = estates.size();
				if (size > 0) { // size=0 ==> no more pages available

					for (Element row : estates) {
						ListItem e = parseRow(row);
						if (e != null) {
							allEstates.add(e);
						} else {
							
						}

					}
				} else {
					keepOn = false;
				}

			} catch (SocketTimeoutException ex) {
				count++;
				pageLoaded = retryToGetPage(pageNumber, page, allEstates, count,IterationSleep1min,IterationSleep2min);
			} catch (IOException e) {
				System.out.println("No internet connection or could not reach website.)");
			}
		}
		//returns all hemnet estates
		return allEstates;
	}
	/**
	 * Uses this method when a page times out after 6seconds, to retry to get the page(s) that have timed out.
	 */
	public Boolean retryToGetPage(int pageNumber, String page, ArrayList<ListItem> allEstates, int count,
			int IterationSleep1min, int IterationSleep2min) {
		
		System.out.println("\tTIME-OUT for page " + pageNumber);
		// The first retry will be done instantly
		System.out.println("\n" + Util.getCurrentTime() + ":First attempt to get missed page " + pageNumber);
		Document doc = null;
		Elements estates = null;
		Boolean pageLoaded = true;

		try {
			doc = Jsoup.connect(page).timeout(6000).get();
		} catch (IOException e) {
			// The second attempt will be done 1 minute after the first attempt fails
			System.out.println("\n" + Util.getCurrentTime() + ":Second attempt to get missed page " + pageNumber
					+ " after 1 minute");
			Util.sleep(IterationSleep1min * 60 * 1000);
			try {
				doc = Jsoup.connect(page).timeout(6000).get();
			} catch (IOException e1) {
				e1.printStackTrace();
				System.out.println("Could not establish connection to page.");
			}

		}
		if (doc != null) {
			estates = doc.getElementsByAttributeValueContaining("class", "item result normal  ");
			for (Element row : estates) {
				ListItem c = parseRow(row);
				allEstates.add(c);
			}
		} else {
			// The third attempt will be done 2 minutes after the second attempt has failed
			System.out.println("\n" + Util.getCurrentTime() + " Third attempt to get missed page " + pageNumber
					+ " after 2 minute");
			Util.sleep(IterationSleep2min * 60 * 1000);
			try {
				doc = Jsoup.connect(page).timeout(6000).get();
			} catch (IOException e) {
				// Prints an error message and moves on to the next page.
				System.out.println("Could not get page " + pageNumber + " after 4 attempts, trying again in a while.");
				pageLoaded = false;

			}

		}
		//returns the status for pageLoaded.
		return pageLoaded;

	}
	/**
	 * Method that converts string to double.
	 */
	public double getDouble(String s) {
		// Replace , with , and remove whitespace
		String str = "";
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == ',')
				str += '.';
			else if (Character.isDigit(c))
				str += c;
		}
		if (str == "") {
			return 0.0;
		} else {
			return Double.parseDouble(str);
		}
	}
	/**
	 * returns a random sleep time so that page not will be crawled to intense.
	 */
	public int RandomizeSleepTime() {
		Random r = new Random();
		int low = 1;
		int high = 3;
		int result = r.nextInt(high - low) + low;
		return result;
	}
	/**
	 * Sends saved NewItem hemnetestate object details from hashmap, because can't be crawled again(removed from website), 
	 * returns the details in a NewItem object with correct details for the output stream.
	 */
	@Override
	public NewItem getRemovedNewItem(NewItem ni, Boolean New) {
		// Set new status in Object to variable value.
		ni.setNew(New);
		// Get the status from object
		boolean booln = ni.getNew();
		// Set new timestamp time.
		ni.setTimestamp();
		// Get the new timestamp time.
		long ts = ni.getTimeStamp();
		// Get all of the parameters of the new object from hashmap and put them
		// into the new object
		NewItem n = new NewItem(ni.getTitle(), booln, ni.getFirstseen(), ni.getPrice(), ni.getUrl(),ni.getArea(), ni.getSeller(),
				ni.getBuildYear(), ni.getMonthlyFee(), ni.getYardArea(), ni.getBiArea(),
				ni.getFloors(), ni.getEnergyYear(), ni.getRooms(), ni.getM2(),ni.getPricem2(),ts,ni.getType());
		// Add the new object to the stream.
		return n;
	}
	/**
	 * returns iteration sleep time.
	 */
	@Override
	public int getIterationSleep() {
		
		return 240;
	}
	/**
	 * trying to find a specific tag in page.
	 */
	@Override
	public Element getMissedPageTag(Document doc) {	
		return (doc.getElementsByAttributeValueContaining("class", "content")).get(0);
	}

}
