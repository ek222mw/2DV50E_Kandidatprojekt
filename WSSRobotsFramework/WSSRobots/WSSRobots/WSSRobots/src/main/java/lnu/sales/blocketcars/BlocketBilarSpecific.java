package lnu.sales.blocketcars;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import lnu.sales.ListItem;
import lnu.sales.NewItem;
import lnu.sales.SalesSpecific;
import lnu.sales.Util;

public class BlocketBilarSpecific extends SalesSpecific {
	/**
	 * Crawling tags for specific new blocket car details as NewItem type. 
	 */
	@Override
	public NewItem getNewItem(Document document, String url, Boolean New, Boolean firstSeen) {
		// Gets the title
		Element name = null;

		// If there is not article tag on the page the method return null
		try {
			name = (document.getElementsByAttributeValueContaining("role", "article")).get(0);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
		//Get the car title
		Element carTitle = name.select("h1").first();

		// Gets the area. If the area is not specified it will be set to -
		Element s0;
		String carArea;
		try {
			Element area = (document.getElementsByAttributeValueContaining("class", "area_label")).get(0);
			s0 = area.select("span").first();
			carArea = s0.text().replace("(", "").replace(")", "");
		} catch (IndexOutOfBoundsException e) {
			carArea = "-";
		}

		// Gets the seller info
		Element seller = (document.getElementsByAttributeValueContaining("id", "seller-info")).get(0);

		Element carSeller = seller.select("a").first();
		//Gets the date published.
		Element datePublished = seller.select("time").first();

		// Finds out if it is a store that sells it
		// Use storebox_header_container div tag to check
		boolean isCompany = false;
		try {
			document.getElementsByAttributeValueContaining("id", "storebox_header_container").get(0);
			isCompany = true;
		} catch (Exception exe) {
			isCompany = false;
		}

		// Gets the details.
		Element details = (document.getElementsByAttributeValueContaining("id", "item_details")).get(0);
		//Gets model year
		Element modelYear = details.select("dd").first();
		//Gets transmission.
		Element gearbox = details.select("dd").get(1);
		//Gets the milage.
		Element milage = details.select("dd").get(2);
		//Gets production year.
		Element productionYear = details.select("dd").get(3);
		//Gets fuel details.
		Element fuel = details.select("dd").get(4);

		// Gets the price
		Element price;
		String price2;
		try {
			price = (document.getElementsByAttributeValueContaining("id", "vi_price")).get(0);
			price2 = price.text();
		} catch (IndexOutOfBoundsException e) {
			price2 = "-";
		}
		//Gets discoverytime.
		long timeStamp = System.currentTimeMillis();
		NewItem newCar = new NewItem(carTitle.text(), New, firstSeen, datePublished.text(), price2, url, carArea,
				carSeller.text(), modelYear.text(), gearbox.text(), milage.text(), productionYear.text(), fuel.text(),
				isCompany, timeStamp);

		return newCar;
	}
	/**
	 * Crawling specific list object by tag on Blocket cars.
	 */
	public ListItem parseRow(Element carRow) {

		// Find car name and Url
		Element name = (carRow.getElementsByAttributeValueContaining("class", "h5 media-heading ptxs")).get(0);
		Element anchor = (name.getElementsByAttribute("href")).get(0);
		String url = anchor.attr("href");
		String title = anchor.attr("title");

		// Find current price
		Element nav = (carRow.getElementsByAttributeValueContaining("class", "list_price font-large")).get(0);
		String stringPrice = nav.text();
		String filtered = stringPrice.replaceAll("[^\\d.]", "");
		double price = getDouble(filtered);
		
		// Set time stamp
		long timeStamp = System.currentTimeMillis();

		ListItem car = new ListItem(title, url, price, timeStamp);

		return car;
	}
	/**
	 * Crawling all list objects(within website) for Blocket cars.
	 */
	public ArrayList<ListItem> traverseAllObjects(Boolean pageLoaded, int IterationSleep10min) {

		int IterationSleep1min = 1;
		int IterationSleep2min = 2;
		String firstPart = "http://www.blocket.se/hela_sverige?q=&cg=1020&w=3&st=s&ps=&pe=&mys=&mye=&ms=&me=&cxpf=&cxpt=&fu=&gb=&ca=18&is=1&l=0&md=th";
		int pageNumber = 0;
		boolean keepOn = true;
		int count = 0;
		ArrayList<ListItem> allCars = new ArrayList<ListItem>();

		while (keepOn && pageLoaded) {
			pageNumber++;
			String page = firstPart + "&o=" + pageNumber; // Same url but different pagenumber
			Document doc = null;
			Elements cars = null;
			try {
				// Getting each page with a small delay
				Util.sleep(IterationSleep10min * 60 * RandomizeSleepTime());
				count = 0;

				doc = Jsoup.connect(page).timeout(6000).get();
				cars = doc.select("article[id*=item_]");
				int size = cars.size();
				if (size > 0) { // size=0 ==> no more pages available

					for (Element row : cars) {
						ListItem c = parseRow(row);
						allCars.add(c);
					}

				} else {
					keepOn = false;
				}
			} catch (SocketTimeoutException ex) {
				count++;
				pageLoaded = retryToGetPage(pageNumber, page, allCars, count, IterationSleep1min, IterationSleep2min);
			} catch (IOException e) {
				System.out.println("No internet connection or could not reach website.)");
			}
		}
		return allCars;
	}
	/**
	 * Uses this method when a page times out after 6seconds, to retry to get the page(s) that have timed out.
	 */
	public Boolean retryToGetPage(int pageNumber, String page, ArrayList<ListItem> allCars, int count,int IterationSleep1min, int IterationSleep2min) {
		System.out.println("\tTIME-OUT for page " + pageNumber);
		// The first retry will be done instantly
		System.out.println("\n" + Util.getCurrentTime() + ":First attempt to get missed page " + pageNumber);
		Document doc = null;
		Elements cars = null;
		boolean pageLoaded = true;

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
				System.out.println("Could not establish connection to page.");
			}

		}

		if (doc != null) {
			cars = doc.select("article[id*=item_]");
			for (Element row : cars) {
				ListItem c = parseRow(row);
				allCars.add(c);
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
	 * Sends saved NewItem blocket car object details from hashmap, because can't be crawled again(removed from website), 
	 * returns the details in a NewItem object with correct details for the output stream.
	 */
	@Override
	public NewItem getRemovedNewItem(NewItem ni, Boolean New) {
		ni.setNew(New);
		// Get the status from car object
		boolean booln = ni.getNew();
		// Set new timestamp time.
		ni.setTimestamp();
		// Get the new timestamp time.
		long ts = ni.getTimestamp();
		// Get all of the parameters of the new car from hashmap and put them into the NewItem.
		NewItem n = new NewItem(ni.getTitle(), booln, ni.getFirstseen(), ni.getDate(), ni.getPrice(), ni.getURL(),
				ni.getArea(), ni.getSeller(), ni.getModelYear(), ni.getGearbox(), ni.getMilage(),
				ni.getProductionYear(), ni.getFuel(), ni.getisCompany(), ts);
		// returns the NewItem object.
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
		return (doc.getElementsByAttributeValueContaining("role", "article")).get(0);	
	}

}
