package bgu.spl.app.services;

import bgu.spl.app.Receipt;
import bgu.spl.app.Store;
import bgu.spl.app.Messages.ManufacturingOrderRequest;
import bgu.spl.app.Messages.TerminateBrodacast;
import bgu.spl.app.Messages.TickBroadcast;
import bgu.spl.mics.MicroService;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;
/**
 * extends MicroService.
 * This micro-service describes a shoe factory that manufacture shoes for the store. This micro-service
 * handles the ManufacturingOrderRequest.
 */
public class ShoeFactoryService extends MicroService {
Store store;
private int currentTick;
private int counter;
private ManufacturingOrderRequest currentRequest;
private Queue<ManufacturingOrderRequest> manufacturingQueue;
/**
 * constructor.
 * @param name the ShoeFactoryServices name.
 * @param latch Number of services which are executing before timer starts.
    */
	public ShoeFactoryService(String name,CountDownLatch latch) {
		super(name,latch);
		counter=-1;
		currentRequest=null;
		manufacturingQueue = new LinkedList<ManufacturingOrderRequest>();
		logger=Logger.getGlobal();
		store=store.getInstance();	}
	/**
	 * Initialize the ShoeFactoryService.
	 * Do subscribe to TickBroadcast and complete the ManufacturingOrderRequest with the proper receipt when the counter=0.
	 * Do subscribe to ManufacturingOrderRequest and works on the current request and initialize the counter.
	 */
	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, time->
		{
			currentTick=time.getTime();
			if(counter>0)counter--;
			if (counter==0){
				Receipt receipt=new Receipt(getName(), "store", currentRequest.getshoeType(),false,currentTick,currentRequest.getTime(),currentRequest.getamounttoSupply());
				complete(currentRequest,receipt);
				logger.info("new receipt for manifacturing new shoes has added to the store");
				if(manufacturingQueue.peek()!=null){
				currentRequest=manufacturingQueue.poll();
				counter=currentRequest.getamounttoSupply();}
				else {counter=-1;currentRequest=null;}				
			}
		});
		
		subscribeRequest(ManufacturingOrderRequest.class, manfacturingReq->
		{
			logger.info(getName()+"got a newmanfacturingorder request for "+manfacturingReq.getamounttoSupply());
			if (currentRequest!=null){manufacturingQueue.add(manfacturingReq);}
			else {currentRequest = manfacturingReq; logger.info("first manfacturing request in the "+getName());
			counter=currentRequest.getamounttoSupply();}

		});		
		subscribeBroadcast(TerminateBrodacast.class,terminate ->{terminate();});	
		latchObject.countDown();
	}
}
