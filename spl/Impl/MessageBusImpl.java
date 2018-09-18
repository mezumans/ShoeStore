package bgu.spl.Impl;

import java.util.concurrent.LinkedBlockingQueue; 
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import bgu.spl.mics.*;
import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.Request;
import bgu.spl.mics.RequestCompleted;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageBusImpl implements MessageBus {
	private ConcurrentHashMap <MicroService,LinkedBlockingQueue<Message>> MessageQueue;
	private ConcurrentHashMap<String,ArrayList<MicroService>> RequestMap;
	private ConcurrentHashMap<String,ArrayList<MicroService>> BrodcastMap;
	private ConcurrentHashMap<String,Integer> RequestIndexMap;
	private ConcurrentHashMap<Request<?>,MicroService> RequesterAndCompleting;	//maps the requesting ms with the ms who complete's the request.
	private Object someLock1;
	private Object somelock3;
	private Logger logger;
	private static class SingletonHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}
	private MessageBusImpl() {
		MessageQueue=new ConcurrentHashMap<MicroService,LinkedBlockingQueue<Message>>();
		RequestIndexMap=new ConcurrentHashMap<String,Integer>();
		RequesterAndCompleting=new ConcurrentHashMap<Request<?>,MicroService>();
		RequestMap=new ConcurrentHashMap<String,ArrayList<MicroService>>();
		BrodcastMap=new ConcurrentHashMap<String,ArrayList<MicroService>>();
		someLock1 = new Object();
		logger=Logger.getLogger(MessageBusImpl.class.getName());
		logger.setLevel(Level.INFO);
		somelock3=new Object();
	}
	public static MessageBusImpl getInstance() {
		return SingletonHolder.instance;
	}

	public void subscribeRequest(Class<? extends Request> type, MicroService m){

		if(RequestMap.get(type.getName())!=null&&MessageQueue.containsKey(m))
		{//checks if request type exist&& if the ms registered
			if(!RequestMap.get(type.getName()).contains(m))//false if the ms is not subscribed yet
				RequestMap.get(type.getName()).add(m);
		}
		else if(MessageQueue.containsKey(m))
		{	//The first request of ? type
			ArrayList<MicroService> tmp = new ArrayList<MicroService>();
			tmp.add(m);
			RequestMap.put(type.getName(),tmp);
			RequestIndexMap.put(type.getName(), 0);
		}
		else logger.info("The MicroService is not registered yet");
	}

	/**
	 * subscribes {@code m} to receive {@link Broadcast}s of type {@code type}.
	 * <p>
	 * @param type the type to subscribe to
	 * @param m    the subscribing micro-service
	 */
	synchronized public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m)
	{if(BrodcastMap.get(type.getName())!=null&&MessageQueue.containsKey(m))
	{
		if(!BrodcastMap.get(type.getName()).contains(m))//false if the ms is not subscribed yet
			BrodcastMap.get(type.getName()).add(m);
	}
	else if(MessageQueue.containsKey(m))
	{	//The first request of ? type
		ArrayList<MicroService> tmp = new ArrayList<MicroService>();
		tmp.add(m);
		BrodcastMap.put(type.getName(),tmp);
	}}
	
	/**
	 * Notifying the MessageBus that the request {@code r} is completed and its
	 * result was {@code result}.
	 * When this method is called, the message-bus will implicitly add the
	 * special {@link RequestCompleted} message to the queue
	 * of the requesting micro-service, the RequestCompleted message will also
	 * contain the result of the request ({@code result}).
	 * <p>
	 * @param <T>    the type of the result expected by the completed request
	 * @param r      the completed request
111	 * @param result the result of the completed request
	 */
	public <T> void complete(Request<T> r, T result){
		try {
			if( RequesterAndCompleting.get(r) == null)
				logger.severe("The request wasnt found on the message bus");
			MicroService a= RequesterAndCompleting.get(r);
			MessageQueue.get(RequesterAndCompleting.get(r)).put(new RequestCompleted<T>(r,result));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * add the {@link Broadcast} {@code b} to the message queues of all the
	 * micro-services subscribed to {@code b.getClass()}.
	 * <p>
	 * @param b the message to add to the queues.
	 */
	public void sendBroadcast(Broadcast b){
		Iterator<MicroService> it = BrodcastMap.get(b.getClass().getName()).iterator();
		while (it.hasNext()) {
			MicroService itered=(MicroService) it.next();
			try {
				MessageQueue.get(itered).put(b);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}}

	/**
	 * add the {@link Request} {@code r} to the message queue of one of the
	 * micro-services subscribed to {@code r.getClass()} in a round-robin
	 * fashion.
	 * <p>
	 * @param r         the request to add to the queue.
	 * @param requester the {@link MicroService} sending {@code r}.
	 * @return true if there was at least one micro-service subscribed to
	 *         {@code r.getClass()} and false otherwise.
	 */
	public boolean sendRequest(Request<?> r, MicroService requester){
		boolean ans=false;
synchronized (somelock3) {
		try {MessageQueue.get(RequestMap.get(r.getClass().getName()).get(RequestIndexMap.get(r.getClass().getName()))).put(r);
			ans=MessageQueue.get(RequestMap.get(r.getClass().getName()).get(RequestIndexMap.get(r.getClass().getName()))).isEmpty();
			RequesterAndCompleting.put(r,requester);
			RequestIndexMap.replace(r.getClass().getName(), (RequestIndexMap.get(r.getClass().getName())+1)%(RequestMap.get(r.getClass().getName()).size()));
		} 	catch (InterruptedException e) {
			e.printStackTrace();
		}
		ans= true;
		return ans;}}

	/**
	 * allocates a message-queue for the {@link MicroService} {@code m}.
	 * <p>
	 * @param m the micro-service to create a queue for.
	 */
	public void register(MicroService m){
		synchronized(someLock1){
			//Queue<Message> q=new LinkedList<Message>();
			LinkedBlockingQueue<Message> q = new LinkedBlockingQueue<Message>();
			this.MessageQueue.put(m,q);
		}}
	/**
	 * remove the message queue allocated to {@code m} via the call to
	 * {@link #register(bgu.spl.1mics.MicroService)} and clean all references
	 * related to {@code m} in this message-bus. If {@code m} was not
	 * registered, nothing should happen.
	 * <p>
		2	 * @param m the micro-service to unregister.
	 */
	public void unregister(MicroService m){
		if(MessageQueue.containsKey(m)){
			this.MessageQueue.remove(m);
			Iterator it = RequestMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String,ArrayList<MicroService>> pair = (Map.Entry)it.next();
				if( pair.getValue().contains(m)){
					if(pair.getValue().indexOf(m)<RequestIndexMap.get(m)){
						RequestIndexMap.replace(pair.getKey(),RequestIndexMap.get(m)-1);
					}
					pair.getValue().remove(m);   
				}
			}
		}
	}

	/**
	 * using this method, a <b>registered</b> micro-service can take message
	 * from its allocated queue.
	 * This method is blocking -meaning that if no messages
	 * are available in the micro-service queue it
	 * should wait until a message became available.
	 * The method should throw the {@link IllegalStateException} in the case
	 * where {@code m} was never registered.
	 * <p>
	 * @param m the micro-service requesting to take a message from its message
	 *          queue
	 * @return the next message in the {@code m}'s queue (blocking)
	 * @throws InterruptedException if interrupted while waiting for a message
	 *                              to became available.
	 */
	public  Message awaitMessage(MicroService m) throws InterruptedException{

		if(!this.MessageQueue.containsKey(m)){
			throw new IllegalStateException("The service is appearntly not registered");}
		else {
			
			return this.MessageQueue.get(m).take();
		}}
}



