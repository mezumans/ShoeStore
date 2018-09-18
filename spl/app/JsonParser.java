package bgu.spl.app;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.google.gson.stream.JsonReader;

import bgu.spl.app.services.SellingService;
import bgu.spl.app.services.ShoeFactoryService;
import bgu.spl.app.services.TimeService;

import java.util.logging.Logger;

class clientsHolder{
	private String name;
	public LinkedList<PurchaseSchedule> purchaseSchedule;
	public Set<String> wishList;
	public clientsHolder(String name,LinkedList<PurchaseSchedule> purchaseSchedule,HashSet<String> wishList)
	{			
		this.name=name;
		this.purchaseSchedule=new LinkedList<PurchaseSchedule>(purchaseSchedule);
		this.wishList=new HashSet<String>(wishList);
	}
	public String getName(){return name;}
}


public class JsonParser {
	private static String file;
	public static ArrayList<ShoeStorageInfo> shoes;
	public static TimeService timer;
	public static LinkedList<ShoeFactoryService> factories;
	public static LinkedList<SellingService> sellers;
	public static  LinkedList<DiscountSchedule> manager;
	public static LinkedList<clientsHolder> customers;
	public static int numofFactories;
	public static int numofSellers;
	public static int speed=0;
	public static int duration=0;
	static LinkedList<PurchaseSchedule> purchaseSchedulelist;
	static HashSet<String>wishList;

	public JsonParser(String file){
		this.file=file;
		customers=new LinkedList<clientsHolder>();
		this.manager=new LinkedList<DiscountSchedule>();
		purchaseSchedulelist=new LinkedList<PurchaseSchedule>();
		wishList=new HashSet<String>();

	}
	//URL url=F:\EclipseWorkSpace\Assignment2\sample.json;
	public static  void FileReader(){
		try{

			JsonReader jsonReader = new JsonReader(new FileReader(file));
			jsonReader.beginObject();
			while(jsonReader.hasNext())
			{
				String name = jsonReader.nextName();
				if(name.equals("initialStorage")){
					shoes=readShoes(jsonReader);
					String a=shoes.toString();
				}
				else if(name.equals("services")) 
				{	jsonReader.beginObject();
					readServices(jsonReader);
					jsonReader.endObject();
				}
			}	
		}catch(IOException e){e.printStackTrace();}}

	public static ArrayList<ShoeStorageInfo> readShoes(JsonReader reader)  {
		String shoeType="";
		int amount=0;
		ShoeStorageInfo newShoe;
		ArrayList<ShoeStorageInfo> tmp= new ArrayList<ShoeStorageInfo>();
		try{
			reader.beginArray();
			while (reader.hasNext())
			{	reader.beginObject();
			while(reader.hasNext())
			{
				String name = reader.nextName();
				if(name.equals("shoeType")){
					shoeType=reader.nextString();

				}
				else if(name.equals("amount")){
					amount=reader.nextInt();
				}
				else reader.skipValue();
			}
			newShoe=new ShoeStorageInfo(shoeType, amount, 0);
			tmp.add(newShoe);
			reader.endObject();
			}
			reader.endArray();}
		catch(IOException e){e.printStackTrace();}
		return tmp;}

	public static void readServices(JsonReader reader) throws IOException
	{
		while(reader.hasNext())
		{
			String name=reader.nextName();
			if(name.equals("time")){
				readTimer(reader);
			}
			else if(name.equals("manager"))
			{
				manager=(readManager(reader));
			}
			else if(name.equals("factories")){
				numofFactories=reader.nextInt();
			}
			else if (name.equals("sellers")){
				numofSellers=reader.nextInt();
			}
			else if (name.equals("customers")){
				reader.beginArray();
				customers=readCustomer(reader);
				reader.endArray();
			}
			else reader.skipValue();
		}
	}
	public static void readTimer(JsonReader reader) throws IOException{

		reader.beginObject();
		while(reader.hasNext())
		{
			String name=reader.nextName();
			if(name.equals("speed"))
			{
				speed=reader.nextInt();
			}
			else if (name.equals("duration"))
			{
				duration=reader.nextInt();
			}
			else reader.skipValue();

		}
		reader.endObject();
	}
	public static LinkedList<DiscountSchedule> readManager(JsonReader reader) throws IOException{
		LinkedList<DiscountSchedule> discountSchedule=new LinkedList<DiscountSchedule>();
		String shoeType = "";
		int amount=0;
		int tick=0;
		reader.beginObject();
		String name=reader.nextName();
		if(name.equals("discountSchedule"))
			reader.beginArray();
		while(reader.hasNext())
		{
			reader.beginObject();
			while(reader.hasNext())
			{
				name=reader.nextName();
				if(name.equals("shoeType"))
				{
					shoeType=reader.nextString();
				}
				else if(name.equals("amount"))
				{
					amount=reader.nextInt();
				}
				else if(name.equals("tick"))
				{
					tick=reader.nextInt();
				}
				else reader.skipValue();
			}
			reader.endObject();
			DiscountSchedule tmp=new DiscountSchedule(shoeType, tick, amount);
			discountSchedule.add(tmp);
		}
		reader.endArray();
		reader.endObject(); 
		return discountSchedule;
	}
	public static LinkedList<clientsHolder> readCustomer(JsonReader reader) throws IOException
	{
	
	LinkedList<clientsHolder> customers=new LinkedList<clientsHolder>(); 
	String clientName = null;
	String shoeType = "";
	int tick=0;
	
	while(reader.hasNext())
	{
		reader.beginObject();
		while(reader.hasNext())
		{	
			String name=reader.nextName();
			if(name.equals("name"))
			{
				clientName=reader.nextString();
			}							
			else if(name.equals("wishList"))
			{
				reader.beginArray();
				if(reader.hasNext())
				{	
					wishList=createwishList(reader);
				}
				else wishList=new HashSet<String>();
				reader.endArray();
			}
			else if(name.equals("purchaseSchedule"))
			{int counter=0;
			//LinkedList<PurchaseSchedule>try1=new  LinkedList<PurchaseSchedule>();
			LinkedList<PurchaseSchedule>purchaseScheduleList = new LinkedList<PurchaseSchedule>();;
				reader.beginArray();
						while(reader.hasNext())
				{	
				purchaseScheduleList.add(getPurchaseSchedulelist(reader));//=new LinkedList<PurchaseSchedule>((getPurchaseSchedulelist(reader)));
				}
				
				
				reader.endArray();
				clientsHolder newCustomer=new clientsHolder(clientName, purchaseScheduleList, wishList);
				customers.add(newCustomer);
			}
			
		}	reader.endObject();	
	}
	return customers;
	}
	public static PurchaseSchedule getPurchaseSchedulelist(JsonReader reader) throws IOException
	{
		LinkedList<PurchaseSchedule> purchaseSchedule=new LinkedList<PurchaseSchedule>();
		String name;
		String shoeType = null;
		int tick=0;
		reader.beginObject();
		int counter=0;
		while(reader.hasNext())
		{	
				name=reader.nextName();
				if(name.equals("shoeType"))
				{
					shoeType=reader.nextString();
				}

				else if(name.equals("tick"))
				{
					tick=reader.nextInt();

				}
				else reader.skipValue();
		}
			reader.endObject();
			PurchaseSchedule tmp=new PurchaseSchedule(shoeType, tick);
			//purchaseSchedule.add(tmp);
			return tmp;
	}
	public static  HashSet<String> createwishList(JsonReader reader) throws IOException{
		HashSet<String>newwishList=new HashSet<String>();
		while(reader.hasNext()){
			newwishList.add(reader.nextString());
		}
		return newwishList;
	}

}