package lnu.sales;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


public class ListItem {
	//private fields.
	private final String name;
	private final String url;
	private final double price;
	private final long discoveryTime;

	
	public ListItem(String name, String url, double price, long time) {
		//Set private fields to input values.
		this.name = name;
		this.url = url;
		this.price = price;
		discoveryTime = time;
		
	}
	//public get methods.
	public String getName() { return name; }
	public String getUrl() { return url; }
	public double getPrice() { return price; }
	public long getDiscoveryTime() { return discoveryTime; }
	
	/**
	 * public methods that converts all data into one printable string variable. 
	 */
	@Override
	public String toString() {
		return  name+"\t"+url +"\t"+ price +"\t"+ discoveryTime;
	}
	/**
	 * 
	 * Checks if object has same url and price then return true else false.
	 *
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof ListItem) {
			
			ListItem other = (ListItem) o;
			if((url.equals(other.url)) && (price==other.price))
			{
				return true;
			}
			
		}
		
		return false;
	}
	
	/*
	 * Implementing the JsonObject interface.
	 * 
	 */
	public String toJsonString() {
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = null;
		try {			
			jsonString =  mapper.writeValueAsString(this);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonString;
	}
	
	public JsonNode toJsonNode() {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode =  mapper.convertValue(this, JsonNode.class);	
		return jsonNode;
	}
}