package lnu.sales.blocketestate;

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

public class BlocketEstateSpecific extends SalesSpecific {
	/**
	 * Crawling tags for specific new blocket estate details as NewItem type. 
	 */
	public NewItem getNewItem(Document document, String url, Boolean New, Boolean firstSeen) {
		// Gets the title
		String title;
		Element detail = null;
		// If there is not article tag on the page the method return null
		try {
			//Gets the title.
			title = (document.getElementsByAttributeValueContaining("class", "subject_large")).get(0).text();
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
		//Get details.
		detail = (document.getElementsByAttributeValueContaining("class", "col-xs-3 params-wrapper")).get(0);
		String estatePrice = "-";
		try {
			//Gets the price.
			estatePrice = detail.select("div").first().select("h4").first().select("span").first().text();
		} catch (IndexOutOfBoundsException e) {

		} catch (NullPointerException e1) {

		}

		String rooms = "-";
		try {
			//Checks if tag contains numbers is so then it's a room.
			if (Character.isDigit(detail.select("div").first().select("span").get(1).text().charAt(0))) {
				//Gets room details.
				rooms = detail.select("div").first().select("span").get(1).text();
			}
		} catch (IndexOutOfBoundsException e) {

		}

		String squarem2 = "-";
		try {
			//Checks if spuarem2 contains a number if so then get the details.
			if (Character.isDigit(detail.select("div").first().select("span").get(2).text().charAt(0))) {
				//Gets the m2 details.
				squarem2 = detail.select("div").first().select("span").get(2).text();
			}
		} catch (IndexOutOfBoundsException e) {

		}

		String monthlyfee = "-";
		String yardarea = "-";
		try {
			//Checks if tag contains the correct name
			if (detail.select("dl").first().select("dt").first().text().equals("Månadsavgift")) {
				//Gets monthlyfee
				monthlyfee = detail.select("dl").first().select("dd").first().text();
				//Else if check if tag contains correct name
			} else if (detail.select("dl").first().select("dt").first().text().equals("Tomtarea")) {
				//Gets yardarea.
				yardarea = detail.select("dl").first().select("dd").first().text();
			}
		} catch (IndexOutOfBoundsException e) {

		} catch (NullPointerException e) {

		}

		String pricem2 = "-";
		String biarea = "-";
		String buildyear = "-";
		//checking if tag contains right title then get the details.
		try {
			if (detail.select("dl").first().select("dt").get(1).text().equals("Byggår")) {
				//Gets buildyear
				buildyear = detail.select("dl").first().select("dd").get(1).text();
			}
			if (detail.select("dl").first().select("dt").get(1).text().equals("Pris/m²")) {
				//Gets price/m2
				pricem2 = detail.select("dl").first().select("dd").get(1).text();
			} else if (detail.select("dl").first().select("dt").get(1).text().equals("Biarea")) {
				//Gets biarea.
				biarea = detail.select("dl").first().select("dd").get(1).text();
			}
		} catch (IndexOutOfBoundsException e) {

		} catch (NullPointerException e) {
			try {
				if (detail.select("dl").first().select("dt").get(1).text().equals("Pris/m²")) {
					pricem2 = detail.select("dl").first().select("dd").get(1).text();
				} else if (detail.select("dl").first().select("dt").get(1).text().equals("Biarea")) {
					biarea = detail.select("dl").first().select("dd").get(1).text();
				}
			} catch (NullPointerException e1) {
				try {
					if (detail.select("dl").first().select("dt").get(1).text().equals("Biarea")) {
						biarea = detail.select("dl").first().select("dd").get(1).text();
					}
				} catch (NullPointerException e2) {

				}
			}
		}

		String floor = "-";
		//Checks if tag contains right title then get details.
		try {
			if (detail.select("dl").first().select("dt").get(2).text().equals("Byggår")) {
				buildyear = detail.select("dl").first().select("dd").get(2).text();
			} else if (detail.select("dl").first().select("dt").get(2).text().equals("Våning")) {
				floor = detail.select("dl").first().select("dd").get(2).text();
			}
		} catch (IndexOutOfBoundsException e) {
			buildyear = "-";
			floor = "-";
		} catch (NullPointerException e) {
			buildyear = "-";
			floor = "-";
		}

		String energyyear = "-";

		try {
			if (detail.select("dl").first().select("dt").get(3).text().equals("Byggår")) {
				buildyear = detail.select("dl").first().select("dd").get(3).text();
			} else if (detail.select("dl").first().select("dt").get(3).text().equals("Energiprestanda/år")) {
				energyyear = detail.select("dl").first().select("dd").get(3).text();
			}
		} catch (IndexOutOfBoundsException e) {

		} catch (NullPointerException e) {

		}
		// Gets the area. If the area is not specified it will be set to -
		String area = "-";
		try {
			area = (document.getElementsByAttributeValueContaining("class", "subject-param address separator")).get(0)
					.text();

		} catch (IndexOutOfBoundsException e) {
			
		}

		String type;
		try {
			type = (document.getElementsByAttributeValueContaining("class", "subject-param category")).get(0).text();
		} catch (IndexOutOfBoundsException e) {
			type = "-";
		}

		Element seller = null;
		try {
			seller = (document.getElementsByAttributeValueContaining("class", "broker-contact-info clearfix")).get(0);
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Seller IndexException");

		}

		String broker = "-";
		Element br;
		try {
			// Gets the seller info
			br = seller.select("div").first().select("br").first();
			Node node = br.nextSibling();
			broker = node.toString();

		} catch (IndexOutOfBoundsException e) {
			
		}
		//Gets discoverytime in milliseconds.
		long timeStamp = System.currentTimeMillis();
		NewItem newEstate = new NewItem(title, New, firstSeen, estatePrice, url, area, broker, buildyear, monthlyfee,
				yardarea, biarea, floor, energyyear, rooms, squarem2,pricem2, timeStamp, type);
		//Returns the newEstate.
		return newEstate;
	}
	/**
	 * Crawling specific list object by tag on Blocket estates.
	 */
	public ListItem parseRow(Element Row) {

		// Find car name and Url
		Element name = (Row.getElementsByAttributeValueContaining("class", "item_link xiti_ad_heading")).get(0);
		Element anchor = (name.getElementsByAttribute("href")).get(0);
		String url = anchor.attr("href");
		String title = anchor.text();

		double price = 0;
		// Find current price
		try {
			Element nav = (Row.getElementsByAttributeValueContaining("itemprop", "price")).get(0);
			String stringPrice = nav.text();
			String filtered = stringPrice.replaceAll("[^\\d.]", "");
			price = getDouble(filtered);

		} catch (Exception e) {
	
		}
		// Get time stamp
		long timeStamp = System.currentTimeMillis();

		ListItem estate = new ListItem(title, url, price, timeStamp);
		//Returns listitem estate.
		return estate;
	}
	/**
	 * Crawling all list objects(within website) for Blocket estates.
	 */
	public ArrayList<ListItem> traverseAllObjects(Boolean pageLoaded, int IterationSleep10min) {

		int IterationSleep1min = 1;
		int IterationSleep2min = 2;
		//url to crawled, has first and second part that is combined with pagenumber.
		String firstPart = "http://www.blocket.se/bostad/saljes?o=";
		String secondPart = "&ca=18&w=3";

		int pageNumber = 0;
		//bool set to keepon until no pages left.
		boolean keepOn = true;
		//TODO
		int count = 0;
		ArrayList<ListItem> allEstates = new ArrayList<ListItem>();

		while (keepOn && pageLoaded) {
			pageNumber++;
			String page = firstPart + pageNumber + secondPart; // Combined url just but different page numbers
			Document doc = null;
			Elements estates = null;

			try {

				// Getting each page with a small delay
				Util.sleep(IterationSleep10min * 60 * RandomizeSleepTime());

				count = 0;
				if (count == 0) {
					doc = Jsoup.connect(page).get();
					estates = doc.select("div[class*=media item_row]");
					int size = estates.size();
					if (size > 0) {
						for (Element row : estates) {
							ListItem c = parseRow(row);
							allEstates.add(c);

						}
					}
				}
				doc = Jsoup.connect(page).get();
				estates = doc.select("div[class*=media  item_row]");

				int size = estates.size();
				if (size > 0) { // size=0 ==> no more pages available

					for (Element row : estates) {
						ListItem c = parseRow(row);
						allEstates.add(c);
					}

				} else {
					keepOn = false;
				}
			} catch (SocketTimeoutException ex) {
				count++;
				pageLoaded = retryToGetPage(pageNumber, page, allEstates, count, IterationSleep1min,
						IterationSleep2min);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//Returns arraylist of ListItems.
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
			// The second attempt will be done 1 minute after the first attempt
			// fails
			System.out.println("\n" + Util.getCurrentTime() + ":Second attempt to get missed page " + pageNumber
					+ " after 1 minute");
			Util.sleep(IterationSleep1min * 60 * 1000);
			// e.printStackTrace();
			try {
				doc = Jsoup.connect(page).timeout(6000).get();
			} catch (IOException e1) {
				e1.printStackTrace();
				System.out.println("Could not establish connection to page.");
			}

		}
		if (doc != null) {
			count = 0;
			if (count == 0) {
				//Different tag name on first item on website on every page.
				estates = doc.select("div[class*=media item_row]");
				int size = estates.size();
				if (size > 0) {
					for (Element row : estates) {
						ListItem e = parseRow(row);
						allEstates.add(e);

					}
				}
			}
			estates = doc.select("div[class*=media  item_row]");

			for (Element row : estates) {
				ListItem e = parseRow(row);
				System.out.println(e);
				allEstates.add(e);
			}
		} else {
			// The third attempt will be done 2 minutes after the second attempt has failed
			System.out.println("\n" + Util.getCurrentTime() + " Third attempt to get missed page " + pageNumber
					+ " after 2 minute");
			Util.sleep(IterationSleep2min * 60 * 1000);
			try {
				doc = Jsoup.connect(page).timeout(6000).get();
			} catch (IOException e) {
				System.out.println("Could not get page " + pageNumber + " after 4 attempts, trying again in a while.");
				pageLoaded = false;

			}

		}

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
	 * Sends saved NewItem blocketestate object details from hashmap, because can't be crawled again(removed from website), 
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
		
		
		NewItem n = new NewItem(ni.getTitle(), booln, ni.getFirstseen(), ni.getPrice(), ni.getUrl(), ni.getArea(),
				ni.getSeller(), ni.getBuildYear(), ni.getMonthlyFee(), ni.getYardArea(), ni.getBiArea(),
				ni.getFloors(), ni.getEnergyYear(), ni.getRooms(), ni.getM2(),ni.getPricem2(), ts,ni.getType());
		// Add the new object to the stream.
		return n;
	}
	/**
	 * returns iteration sleep time
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
		return (doc.getElementsByAttributeValueContaining("role", "article")).get(0);
	}

}
