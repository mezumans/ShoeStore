package bgu.spl.app.services;

import bgu.spl.mics.MicroService;

import bgu.spl.app.Store;
import bgu.spl.app.Messages.PurchaseOrderRequest;
import bgu.spl.app.Messages.RestockRequest;
import bgu.spl.app.Messages.TerminateBrodacast;
import bgu.spl.app.Messages.TickBroadcast;
import java.util.concurrent.CountDownLatch;
import bgu.spl.app.Receipt;
import bgu.spl.app.Store.BuyResult;

/**
 * extends MicroService.
 * This micro-service handles PurchaseOrderRequest.
 * He trying to take the required shoe from the storage.
 */
public class SellingService extends MicroService {
	private Store store;
	BuyResult buyResultGot;
	private int currentTick;
	int counter2;
	int counter3;
	/**
	 * constructor.
	 * @param name the sellingServices name.
	 * @param latch Number of services which are executing before timer starts.
	    */
	public SellingService(String name,CountDownLatch latch){
		super(name,latch);
		store=Store.getInstance();
		currentTick=0;
		 counter3=1;
		 counter2=1;
	}
	/**
	 * Initialize the sellingService.
	 * Do subscribe to TickBroadcast.
	 * Do subscribe to PurchaseOrderRequest and check if the shoe exist in the stock and if their is discount.
	 * If the shoe not exist it sent RestockRequest.
	 * if their was purchase it return receipt.
	 */
	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, time->{
		currentTick=time.getTime();
		});
		subscribeRequest(PurchaseOrderRequest.class, req -> 
		{	
			logger.info( getName() + " got a new request from " + req.getSenderName() + "! (The current tick is: "  + +req.getTime());
			buyResultGot=store.take(req.getShoeType(), req.getOnlyDiscount());
			if (req.getOnlyDiscount()){
				if(buyResultGot==BuyResult.DISCOUNTED_PRICE)
				{
					Receipt receipt=new Receipt(getName(), req.getSenderName(), req.getShoeType(), true, currentTick, req.getTime(),1);
					store.file(receipt);
					complete(req,receipt);
					logger.warning(req.getShoeType()+"sold suscessfully to"+req.getSenderName()+"by"+getName()+"on discount"+"new receipt has added");

				}
				if(buyResultGot==BuyResult.NOT_ON_DISCOUNT)
				{
					complete(req,null);
					logger.info(req.getShoeType()+"won't be sold to"+req.getSenderName()+"by"+getName()+"on discount");	
				}
				if(buyResultGot==BuyResult.NOT_IN_STOCK){
					complete(req,null);
					logger.warning(req.getShoeType()+"won't be sold to"+req.getSenderName()+"by"+getName()+ "on discount");	
				}
				
			}
			else{ 
				if(buyResultGot==BuyResult.REGULAR_PRICE)
				{
					Receipt receipt=new Receipt(getName(), req.getSenderName(), req.getShoeType(), false, currentTick, req.getTime(),1);
					store.file(receipt);
					complete(req,receipt);
					logger.info(req.getShoeType()+"sold suscessfully to"+req.getSenderName()+"by"+getName());

				}
				
				else if(buyResultGot==BuyResult.DISCOUNTED_PRICE)
				{
					Receipt receipt=new Receipt(getName(), req.getSenderName(), req.getShoeType(), true, currentTick, req.getTime(),1);
					store.file(receipt);
					complete(req,receipt);
					logger.info(req.getShoeType()+"sold suscessfully to"+req.getSenderName()+"by"+getName());
				}
				
				
				else if(buyResultGot==BuyResult.NOT_IN_STOCK)
				{
					sendRequest(new RestockRequest(currentTick, "factory", getName(), req.getShoeType()), onComplete->{
						if(onComplete){
							Receipt receipt=new Receipt(getName(), req.getSenderName(), req.getShoeType(), false, currentTick, req.getTime(),1);
							store.file(receipt);
							complete(req,receipt);
							logger.info(req.getShoeType()+"sold suscessfully to"+req.getSenderName()+"by"+getName());
						}
						else
						{
						complete(req,null);
						logger.severe("No receipt the result was NULL");
						}
					});
					logger.info("seller"+getName()+"sent restockRequest to the manager for"+req.getShoeType()+"at" +currentTick+"  times:");
					logger.info(req.getShoeType()+"won't be sold to"+req.getSenderName()+"by"+getName()+"not in stock");}
				}
				
		});
		subscribeBroadcast(TerminateBrodacast.class,terminate ->{terminate();});
		latchObject.countDown();
}}
		








