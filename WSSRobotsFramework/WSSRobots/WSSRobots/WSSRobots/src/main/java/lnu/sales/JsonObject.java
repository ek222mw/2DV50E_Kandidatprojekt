/**
 * JsonObject.java
 * 27 feb 2016
 */
package lnu.sales;

import org.codehaus.jackson.JsonNode;

/**
 * A simple abstract class defining common properties of all Json objects streamed
 * from producer to consumer.
 * 
 * @author Emil.
 *
 */

public abstract class JsonObject {

	public abstract String toJsonString();

	public abstract JsonNode toJsonNode();
	
	public abstract String getChannelName();

}
