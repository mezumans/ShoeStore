package bgu.spl.app;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import bgu.spl.app.services.ManagementService;
import bgu.spl.app.services.SellingService;
import bgu.spl.app.services.ShoeFactoryService;
import bgu.spl.app.services.TimeService;
import bgu.spl.app.services.WebsiteClientService;

public class ShoeStoreRunner {
public static void main(String[]args){
	String file=args[0];
	//Scanner sc = new Scanner(System.in);
	//String file = sc.next();
	JsonParser jsonReader=new JsonParser(file);
	JsonParser.FileReader();
	Logger logger=Logger.getLogger(ShoeStoreRunner.class.getName());
	logger.setLevel(Level.INFO);
	
	ArrayList<ShoeStorageInfo> shoestmp=JsonParser.shoes;
	//converting arraylist to array
	ShoeStorageInfo[] shoes=new ShoeStorageInfo[shoestmp.size()];
	for(int i=0;i<shoes.length;i++){
		shoes[i]=shoestmp.get(i);
	}
	LinkedList<clientsHolder>customers=new LinkedList<clientsHolder>(JsonParser.customers);
	//LinkedList<WebsiteClientService>clients=new LinkedList<WebsiteClientService>();
	logger.info(customers.size()+" customers");
	//System.out.println(customers.get(3).wishList.size());


	
	int servicesCounter=2+customers.size()+JsonParser.numofSellers+JsonParser.numofFactories;
	ExecutorService exec= Executors.newFixedThreadPool(servicesCounter); 
	CountDownLatch latch=new CountDownLatch(servicesCounter-1);
	
	//Initiate store
	Store store=Store.getInstance();
	store.load(shoes);
//	logger.info(store.toString());
		
	//Initiate Factories
	for(int i=1;i<=JsonParser.numofFactories;i++)
	{
		ShoeFactoryService factory=new ShoeFactoryService("factory "+i, latch);
		exec.execute(factory);
	}
	
	//Initiate Sellers
	for(int i=1;i<=JsonParser.numofSellers;i++)
	{
			SellingService seller=new SellingService("seller "+i, latch);
			exec.execute(seller);

	}
		
	//Initiate Manager
	ManagementService manager=new ManagementService(JsonParser.manager,latch);
	exec.execute(manager);

	//Initiate Websiteclints
	
	LinkedList<PurchaseSchedule> purchaseSchedulelist;
	Set<String> wishList;
	
	//Initiate customers
	for(int i=0;i<customers.size();i++)
	{	
		purchaseSchedulelist=new LinkedList<PurchaseSchedule>(customers.get(i).purchaseSchedule);
		wishList=new HashSet<String>(customers.get(i).wishList);
		//logger.info(wishList.toString());
		//purchaseSchedulelist.get(0).getShoeType();
		WebsiteClientService newClient=(new WebsiteClientService(customers.get(i).getName(),purchaseSchedulelist,wishList, latch));
		//System.out.println(customers.get(i).getName());
		//printInput(purchaseSchedulelist);
		//System.out.println("     "+purchaseSchedulelist.toString()+"       ");
		//System.out.println(wishList.toString());
		exec.execute(newClient);
	}
	
	//Initiate Timer
	TimeService timer=new TimeService(JsonParser.speed, JsonParser.duration, latch);
	exec.execute(timer);
	//
	
	try {
		exec.awaitTermination(60*JsonParser.speed,TimeUnit.MILLISECONDS);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	exec.shutdown();
	store.print();
	System.out.println("there are : "+ store.receiptList.size()+" receipts");
	}


///*
public static void printInput(LinkedList<PurchaseSchedule> pslist){
	
	for(int i=0;i<pslist.size();i++){
	System.out.println(pslist.get(i).getShoeType()+"  "+"  "+pslist.get(i).getTick());
	}
}//*/
}