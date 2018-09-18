package bgu.spl.app;

import java.awt.List;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bgu.spl.app.services.ManagementService;
import bgu.spl.app.services.SellingService;
import bgu.spl.app.services.ShoeFactoryService;
import bgu.spl.app.services.TimeService;
import bgu.spl.app.services.WebsiteClientService;

public class ThreadStarter {
	private Store store;
	private TimeService timer ;
	private ManagementService manager;
	private LinkedList<ShoeFactoryService> factories; 
	private LinkedList<SellingService> sellers;
	private LinkedList<WebsiteClientService> customers;
	ExecutorService exec;
	CountDownLatch countDown;
	int servicesCounter;

public ThreadStarter(TimeService timer ,LinkedList<SellingService> sellers,LinkedList<WebsiteClientService> customers,ManagementService manager,LinkedList<ShoeFactoryService> factories,Store store){
	this.timer=timer;
	this.manager=manager;
	this.customers=new LinkedList<WebsiteClientService>(customers);//(customers);
	this.sellers=new LinkedList<SellingService>(sellers);
	this.factories=new LinkedList<ShoeFactoryService>(factories);
	servicesCounter=2+customers.size()+sellers.size()+factories.size();
	exec= Executors.newFixedThreadPool(servicesCounter); 
	countDown=new CountDownLatch(servicesCounter-1);
	this.store=store;
}

	public void startServices(){
		startTimer();
		startManager();
		startSellers();
		startCustomers();
		startFactories();
		shutDown();
	}
	
	private void startTimer()
	{
		exec.execute(timer);	
	}
	
	private void startManager()
	{
		exec.execute(manager);	
	}
	
	private void startFactories()
	{
		for (int index=0; index<factories.size(); index++){
			exec.execute(factories.get(index));
		}
	}
	
	private void startSellers()
	{
		for (int index=0; index<sellers.size(); index++){
			exec.execute(sellers.get(index));
		}
	}
	
	private void startCustomers()
	{
		for (int index=0; index<customers.size(); index++){
			exec.execute(customers.get(index));		
		}		
	}
	private void shutDown(){exec.shutdown();}
}
