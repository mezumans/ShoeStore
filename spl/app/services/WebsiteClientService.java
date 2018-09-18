package bgu.spl.app.services;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import bgu.spl.app.PurchaseSchedule;
import bgu.spl.app.Messages.NewDiscountBroadcast;
import bgu.spl.app.Messages.PurchaseOrderRequest;
import bgu.spl.app.Messages.TerminateBrodacast;
import bgu.spl.app.Messages.TickBroadcast;
import bgu.spl.mics.MicroService;
import java.util.LinkedList;
import java.util.ListIterator;
/**
 * extends MicroService.
 * describes one client connected to the web-site.
 */
public class WebsiteClientService extends MicroService {
	private int currentTick;
	LinkedList<PurchaseSchedule> purchaseSchedule;
	Set<String> wishList;
	/**
	 * constractor and sort the purchaseSchedule
	 * @param name of the WebsiteClientService.
	 * @param purchaseSchedule a List that contains purchases that the client needs to make.
	 * @param wishList a set that contains name of shoe types that the client
	 * will buy only when there is a discount on them.
	 * @param latch Number of services which are executing before timer starts.
	    */
	public WebsiteClientService(String name,LinkedList<PurchaseSchedule> purchaseSchedule,Set<String> wishList,CountDownLatch latch) 
	{
		super(name,latch);
		currentTick=0;
		purchaseSchedule.sort(PurchaseSchedule.PurchaseScheduleComparator);
		this.purchaseSchedule=new LinkedList<PurchaseSchedule>(purchaseSchedule);
		this.wishList=new HashSet<String>(wishList);
	}
	/**
	 * Initialize the WebsiteClientService.
	 * Do subscribe to TickBroadcast and in the proper tick according to the PurchaseSchedule it send PurchaseOrderRequest.
	 * Do subscribe to NewDiscountBroadcast and if the shoe type that the dhscount is on it exist in the wish list the method
	 * sent PurchaseOrderRequest with the same shoe type.
	 */
	@Override
	protected void initialize() 
	{
			subscribeBroadcast(TickBroadcast.class,time->{
			if(purchaseSchedule.isEmpty()&&wishList.isEmpty()) terminate();
			currentTick=time.getTime();		
			ListIterator<PurchaseSchedule> itr = purchaseSchedule.listIterator();
			boolean atRange=true;
			int iterTick;
			PurchaseSchedule currIter;
			while(itr.hasNext()&&atRange)
			{
				currIter=itr.next();
				iterTick=currIter.getTick();
				if(currentTick<iterTick)atRange=false;
				else if(iterTick==currentTick){
					sendRequest(new PurchaseOrderRequest(false,time.getTime(),currIter.getShoeType(),getName() ),
					receipt->{
					if(receipt!=null)logger.info("requested completed for: "+getName()+" on discount : "+receipt.getDiscount());
					else logger.severe("No receipt result: NULL");
					});
					logger.info("Customer "+getName()+"sent request for purchasing "+"new"+currIter.getShoeType()+"at tick : "+currentTick);
					if(itr.hasPrevious()){
					itr.remove();}
					logger.info("Customer "+getName()+"got his shoes");}}
			
		});
		subscribeBroadcast(NewDiscountBroadcast.class,(NewDiscountBroadcast Message)->{
			String shoeTypeonDiscount=Message.getShoeType();
			if(wishList.contains(shoeTypeonDiscount)){
				sendRequest(new PurchaseOrderRequest(true, Message.getTime(),Message.getShoeType(),getName() ),(onComplete )->{
					if(onComplete != null){logger.info("Customer "+getName()+" completed his/her request for new "+Message.getShoeType()+" on discount "+" at tick: "+currentTick);
					wishList.remove(shoeTypeonDiscount);}
					else logger.warning(getName()+ "didn't get a receipt and will not get the shoe");
					});
					{	
				logger.info("Customer "+getName()+" sent a order request for : " + Message.getShoeType());}
			}});
		subscribeBroadcast(TerminateBrodacast.class,terminate ->{terminate();});	
		latchObject.countDown();
	}
}