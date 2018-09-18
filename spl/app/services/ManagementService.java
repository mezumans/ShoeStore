package bgu.spl.app.services;

import bgu.spl.app.DiscountSchedule;
import bgu.spl.app.Store;
import bgu.spl.app.Messages.ManufacturingOrderRequest;
import bgu.spl.app.Messages.NewDiscountBroadcast;
import bgu.spl.app.Messages.PurchaseOrderRequest;
import bgu.spl.app.Messages.RestockRequest;
import bgu.spl.app.Messages.TerminateBrodacast;
import bgu.spl.app.Messages.TickBroadcast;
import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.RequestCompleted;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;
/**
 * extends MicroService.
 * This micro-service can add discount to shoes in the store and send NewDiscountBroadcast to notify
 * clients about them.
 * In addition, the ManagementService handles RestockRequests that is being sent by the SellingService.
 *
 */
public class ManagementService extends MicroService

{	
	private LinkedList discountSchedule;
	private int currentTick;
	private Store store;
	private Map<String,Integer> numofshoesonOrder;
	private Map<String,Queue<RestockRequest>> shoesonOrder;
	private boolean isNeeded;
	/**
	 * constructor.
	 * @param discountSchedule An object which describes a schedule of a single discount that the manager will add to a specific
	 * shoe at a specific tick.
	 * @param latch Number of services which are executing before timer starts.
	 *
	    */
	public ManagementService(LinkedList<DiscountSchedule>discountSchedule,CountDownLatch latch)
	{
		super("manager",latch);
		discountSchedule.sort(DiscountSchedule.DiscountScheduleComparator);
		this.discountSchedule=new LinkedList<DiscountSchedule>(discountSchedule);
		store=Store.getInstance();
		shoesonOrder=new HashMap<String,Queue<RestockRequest>>();
		numofshoesonOrder=new HashMap<String,Integer>();
		isNeeded=true;
	}

	/**
	 * Initialize the ManagementService.
	 * do subscribe to TickBroadcast and sent the NewDiscountBroadcast at the proper timming/
	 * do subscribe to RestockRequest and check if he is already ordered and sent ManufacturingOrderRequest to the factory if he didn't.
	 *
	 */
	@Override
	protected void initialize() {
		logger.info(getName() + " started");
		subscribeBroadcast(TickBroadcast.class, timeMessage->{
			currentTick=timeMessage.getTime();
			ListIterator<DiscountSchedule> itr = discountSchedule.listIterator();
			boolean atRange=true;
			int iterTick;
			DiscountSchedule currIter;
			while(itr.hasNext()&&atRange)
			{
				currIter=itr.next();
				iterTick=currIter.getTick();
				if(currentTick<iterTick)atRange=false;
				else if(iterTick==currentTick)
				{	
					sendBroadcast(new NewDiscountBroadcast(currIter.getShoeType(),currIter.getAmount(),currentTick ));
					store.addDiscount(currIter.getShoeType(),currIter.getAmount());
					itr.remove();
					logger.info("The manager sent a new discount on "+currIter.getAmount()+" "+currIter.getShoeType());
					;
				}
			 }});		

		
		subscribeRequest(RestockRequest.class, req->
		{	
			logger.info(getName()+ " got new restock request for" + req.getshoeType());
			String shoeType=req.getshoeType();
			int amounttoBuy=currentTick%5+1;
			if(shoesonOrder.containsKey(shoeType)&&numofshoesonOrder.containsKey(shoeType))
				{shoesonOrder.get(shoeType).add(req);
				isNeeded=shoesonOrder.get(shoeType).size()>numofshoesonOrder.get(shoeType);}
			if(!shoesonOrder.containsKey(shoeType)||isNeeded)
			{	
				if(!shoesonOrder.containsKey(shoeType)){
				Queue<RestockRequest> tmp=new LinkedList<RestockRequest>();
				tmp.add(req);
				shoesonOrder.put(shoeType,tmp);
				numofshoesonOrder.put(shoeType, amounttoBuy);}
				else
			 	{
					numofshoesonOrder.replace(shoeType, numofshoesonOrder.get(shoeType)+amounttoBuy);
			 	}
				
				sendRequest(new ManufacturingOrderRequest(currentTick,amounttoBuy,req.getshoeType()), receipt->
				{	if(receipt!=null){	
					int numofShoesGot=receipt.getAmountSold();
					numofshoesonOrder.replace(receipt.getShoeType(),numofshoesonOrder.get(receipt.getShoeType())-numofShoesGot);
					store.file(receipt);
					while(numofShoesGot>0&&!(shoesonOrder.get(shoeType).isEmpty()))
				{			int counter=0;
								RestockRequest reqCompleted=shoesonOrder.get(shoeType).poll();
					      complete(reqCompleted,true);
					      numofShoesGot--;
				}
					logger.info("The"+getName()+"new manfacturing request for"+getName()+"at :" +currentTick);
					if(numofShoesGot>0)store.add(shoeType, numofShoesGot);
					logger.fine(numofShoesGot+shoeType+"sent to the store");}
				else logger.severe("There is no answer from the factory receipt is NULL");
				});
					logger.info("the ManufacturingOrderRequest of"+amounttoBuy+" "+shoeType+"has been sent");	
			}
		});
		subscribeBroadcast(TerminateBrodacast.class,terminate ->{terminate();});
		latchObject.countDown();
	}
}
