package lnu.sales;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

@JsonPropertyOrder(alphabetic = true)
public class NewItem extends JsonObject {
	//private fields
	private final String Title;
	private final Boolean Firstseen;
	private String Datepublished = "-";
	private final String Price;
	private final String Url;
	private String Area = "-";
	private String Seller = "-";
	private String Modelyear = "-";
	private String Gearbox = "-";
	private String Milage = "-";
	private String Productionyear = "-";
	private String Fuel = "-";
	private long Timestamp;
	private Boolean New;
	private Boolean IsCompany;
	private String channelName;
	private String m2 = "-";
	private String yardarea = "-";
	private String biarea = "-";
	private String floors = "-";
	private String energyyear = "-";
	private String buildYear = "-";
	private String monthlyFee = "-";
	private String rooms = "-";
	private String type = "-";
	private String pricem2 ="-";
	private final static String hemnetEstateStr = "HemnetEstate";
	private final static String blocketEstateStr = "BlocketEstate";
	private final static String blocketCarStr = "BlocketCar";

	/**
	 * Constructor for blocket car NewItem, can be used for other websites with simular tag parameters within cars.
	 */
	public NewItem(String carTitle, Boolean New, Boolean firstSeen, String datePublished, String price2, String url,
			String carArea, String carSeller, String modelYear, String gearbox, String milage, String productionYear,
			String fuel, boolean isCompany, long timeStamp) {
		//Set the private fields to car values.
		this.Title = carTitle;
		this.Firstseen = firstSeen;
		this.Datepublished = datePublished;
		this.Price = price2;
		this.Url = url;
		this.Area = carArea;
		this.Seller = carSeller;
		this.Modelyear = modelYear;
		this.Gearbox = gearbox;
		this.Milage = milage;
		this.Productionyear = productionYear;
		this.Fuel = fuel;
		this.Timestamp = timeStamp;
		this.New = New;
		this.IsCompany = isCompany;
		this.channelName = blocketCarStr;

	}

	/**
	 * Constructor for hemnet and blocket estates, can be used for other websites with simular tag parameters within estates.
	 */
	public NewItem(String estateTitle, Boolean New, Boolean firstSeen, String price2, String url, String Area,
			String broker, String buildYear, String monthlyFee, String yardarea, String biarea, String floor,
			String energyyear, String rooms, String m2,String pricem2,long timeStamp, String type) {
		//Set the private variables to hemnet or blocket estate values.
		this.Title = estateTitle;
		this.Firstseen = firstSeen;
		this.yardarea = yardarea;
		this.biarea = biarea;
		this.floors = floor;
		this.energyyear = energyyear;
		this.Price = price2;
		this.Url = url;
		this.Area = Area;
		this.Seller = broker;
		this.buildYear = buildYear;
		this.monthlyFee = monthlyFee;
		this.rooms = rooms;
		this.Timestamp = timeStamp;
		this.m2 = m2;
		this.pricem2 = pricem2;
		this.New = New;
		this.type = type;
		if(this.Url.indexOf("hemnet.se") > -1)
		{
			this.channelName =  hemnetEstateStr;
		}
		else{
			this.channelName = blocketEstateStr;
		}
		

	}
	//public set and get methods. Used when a NewItem can't be crawled from website(has been removed), used to get values on a saved NewItem.
	public String getTitle() {return Title;}
	public String getPrice() {return Price;}
	public String getUrl() {return Url;}
	public String getArea() {return Area;}
	public String getSeller() {return this.Seller;}
	public String getBuildYear() {return this.buildYear;}
	public String getMonthlyFee() {return this.monthlyFee;}
	public String getRooms() {return this.rooms;}
	public String getM2() {return this.m2;}
	public String getPricem2(){return this.pricem2;}
	public String getYardArea() {return this.yardarea;}
	public String getBiArea() {return this.biarea;}
	public String getFloors() {return this.floors;}
	public String getEnergyYear() {return this.energyyear;}
	public Boolean getNew() {return this.New;}
	public Long getTimeStamp() {return this.Timestamp;}
	public Boolean getFirstseen() {return this.Firstseen;}
	public void setNew(boolean n) {this.New = n;}
	public void setTimestamp() {this.Timestamp = System.currentTimeMillis();}
	public String getDate() {return this.Datepublished;}
	public String getURL() {return Url;}
	public String getModelYear() {return this.Modelyear;}
	public String getGearbox() {return this.Gearbox;}
	public String getMilage() {return this.Milage;}
	public String getProductionYear() {return this.Productionyear;}
	public String getFuel() {return this.Fuel;}
	public Long getTimestamp() {return this.Timestamp;}
	public Boolean getisCompany() {return this.IsCompany;}
	public String getType(){return this.type;}

	@Override
	public String toString() {
		return Title + "\t" + Area + "\t" + Url + "\t" + "Säljare: " + Seller + "\t" + "Modellår: " + this.Modelyear
				+ "\t" + "Produktionsår: " + this.Productionyear + "\t" + this.Gearbox + "\t" + "Miltal: " + this.Milage
				+ "\t" + "Bränsle: " + this.Fuel + "\t" + "Pris: " + this.Price + "\t" + "Tidigare sedd: "
				+ this.Firstseen + "\t" + "Datum: " + this.Datepublished + "\t" + "time: " + this.Timestamp + "\t"
				+ "Ny: " + this.New;
	}
	/**
	 * Checks if object has same url and price then return true else false.
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof NewItem) {
			NewItem other = (NewItem) o;
			return Url.equals(other.Url) && this.Price == other.Price;
		}
		return false;
	}

	/**
	 * Implementing the JsonObject interface. Translating get methods and their values to an JsonObject String when writing to output stream, in this case a textfile.
	 * 
	 */
	public String toJsonString() {
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = null;
		try {
			if (channelName == blocketCarStr) {
				jsonString = mapper.writeValueAsString(new NewBlocketCar(this.Title, this.Firstseen, this.Datepublished,
						this.Price, this.Url, this.Area, this.Seller, this.Modelyear, this.Gearbox, this.Milage,
						this.Productionyear, this.Fuel, this.Timestamp, this.New, this.IsCompany));
			} else if (channelName == blocketEstateStr || channelName == hemnetEstateStr) {
				jsonString = mapper.writeValueAsString(new NewEstate(this.Title, this.New, this.Firstseen,
						this.Price, this.Url, this.Area, this.Seller, this.buildYear, this.monthlyFee, this.yardarea,
						this.biarea, this.floors, this.energyyear, this.rooms, this.m2,this.pricem2, this.Timestamp, this.type, this.channelName));

			}
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonString;
	}
	/**
	 * 
	 * Class used to set values for the Blocket cars or other sites with simular parameters on cars. Translates get method as parameter
	 * name and value is the private field in the get method.
	 *JsonPropertOrder is declaring that stream order is channelname followed by title, area, url and then random order.
	 */
	@JsonPropertyOrder({ "channelname", "title", "area", "url" })
	static class NewBlocketCar {
		//private fields.
		private final String m_Title;
		private final Boolean m_Firstseen;
		private final String m_Datepublished;
		private final String m_Price;
		private final String m_Url;
		private final String m_Area;
		private final String m_Seller;
		private final String m_Modelyear;
		private final String m_Gearbox;
		private final String m_Milage;
		private final String m_Productionyear;
		private final String m_Fuel;
		private long m_Timestamp;
		private Boolean m_New;
		private Boolean m_IsCompany;
		private final String channelName;

		public NewBlocketCar(String carTitle, boolean firstSeen, String datePublished, String price2, String url,
				String carArea, String carSeller, String modelYear, String gearbox, String milage,
				String productionYear, String fuel, long timeStamp, Boolean n, Boolean isCompany) {
			//setting private fields to input values.
			m_Title = carTitle;
			m_Firstseen = firstSeen;
			m_Datepublished = datePublished;
			m_Price = price2;
			m_Url = url;
			m_Area = carArea;
			m_Seller = carSeller;
			m_Modelyear = modelYear;
			m_Gearbox = gearbox;
			m_Milage = milage;
			m_Productionyear = productionYear;
			m_Fuel = fuel;
			m_Timestamp = timeStamp;
			m_New = n;
			m_IsCompany = isCompany;
			this.channelName = "BlocketCar";
		}
		//private get methods which is used when to send values as jsonobject strings to stream.
		public String getChannelname() {return this.channelName;}
		public String getTitle() {return m_Title;}
		public String getDate() {return m_Datepublished;}
		public String getPrice() {return m_Price;}
		public String getUrl() {return m_Url;}
		public String getArea() {return m_Area;}
		public String getSeller() {return m_Seller;}
		public String getModelYear() {return m_Modelyear;}
		public String getGearbox() {return m_Gearbox;}
		public String getMilage() {return m_Milage;}
		public String getProductionYear() {return m_Productionyear;}
		public String getFuel() {return m_Fuel;}
		public Boolean getFirstSeen() {return m_Firstseen;}
		public Long getTimestamp() {return m_Timestamp;}
		public Boolean getNew() {return m_New;}
		public Boolean getisCompany() {return m_IsCompany;}
		public void setNew(boolean n) {m_New = n;}
		public void setTimestamp() {m_Timestamp = System.currentTimeMillis();}
	}
	/**
	 * 
	 * Class used to set values for the Blocket estate and hemnet estate or other sites with simular parameters on estates. Translates get method as parameter
	 * name and value is the private field in the get method.
	 *JsonPropertOrder is declaring that stream order is channelname followed by type, title, area, url and then random order.
	 */
	@JsonPropertyOrder({ "channelname","type", "title", "area", "url" })
	static class NewEstate {
		//private fields.
		private final String Title;
		private final Boolean Firstseen;
		private final String Price;
		private final String Url;
		private String m2;
		private String yardarea;
		private String biarea;
		private String floors;
		private String energyyear;
		private String seller_broker;
		private String buildYear;
		private String monthlyFee;
		private String rooms;
		private String Area;
		private Boolean New;
		private long Timestamp;
		private final String channelName;
		private final String type;
		private final String pricem2;

		public NewEstate(String estateTitle, Boolean New, Boolean firstSeen, String price2, String url,
				String Area, String broker, String buildYear, String monthlyFee, String yardarea, String biarea,
				String floor, String energyyear, String rooms, String m2,String pricem2, long timeStamp, String type,String channelName) {
			//Sets the private fields to input values.
			this.Title = estateTitle;
			this.Firstseen = firstSeen;
			this.yardarea = yardarea;
			this.biarea = biarea;
			this.floors = floor;
			this.energyyear = energyyear;
			Price = price2;
			Url = url;
			this.Area = Area;
			this.seller_broker = broker;
			this.buildYear = buildYear;
			this.monthlyFee = monthlyFee;
			this.rooms = rooms;
			this.Timestamp = timeStamp;
			this.m2 = m2;
			this.New = New;
			this.type = type;
			this.channelName = channelName;
			this.pricem2 = pricem2;

		}
		//private get methods which is used when to send values as jsonobject strings to stream.
		public String getChannelname() {return this.channelName;}
		public String getTitle() {return Title;}
		public Boolean getFirstseen() {return this.Firstseen;}
		public String getYardarea() {return this.yardarea;}
		public String getBiarea() {return this.biarea;}
		public String getFloors() {return this.floors;}
		public String getEnergyyear() {return this.energyyear;}
		public String getPrice() {return Price;}
		public String getUrl() {return Url;}
		public String getArea() {return Area;}
		public String getSeller_broker() {return seller_broker;}
		public String getBuildyear() {return this.buildYear;}
		public String getMonthlyfee() {return this.monthlyFee;}
		public String getRooms() {return this.rooms;}
		public Long getTimestamp() {return this.Timestamp;}
		public String getM2() {return this.m2;}
		public Boolean getNew() {return this.New;}
		public String getType(){return this.type;}
		public String getPricem2(){return this.pricem2;}

	}
	/**
	 * Abstract method used to write to a Json Node.
	 */
	public JsonNode toJsonNode() {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.convertValue(this, JsonNode.class);
		return jsonNode;
	}
	/**
	 * Abstract method used to identify the current running iteration. Can be used in SalesCrawler or in FakeJsonConsumer.
	 */
	@Override
	public String getChannelName() {
		
		return this.channelName;
	}

}