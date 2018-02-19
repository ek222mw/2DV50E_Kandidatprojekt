package lnu.sales.blocketestate;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import lnu.sales.SalesCrawler;
import lnu.sales.FakeJsonConsumer;
import lnu.sales.JsonObject;


public class Main {

	public static void main(String[] args) {
		BlockingQueue<JsonObject> producer2consumer = new LinkedBlockingQueue<JsonObject>();
		BlocketEstateSpecific bs = new BlocketEstateSpecific();
		
		FakeJsonConsumer consumer = new FakeJsonConsumer(producer2consumer);
		consumer.start();
		
		
		SalesCrawler producer = new SalesCrawler(producer2consumer,bs);
		producer.start();
		

	}

}