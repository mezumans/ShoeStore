package bgu.spl.app.services;
import bgu.spl.app.Messages.TerminateBrodacast;
import bgu.spl.app.Messages.TickBroadcast;
import bgu.spl.mics.Broadcast;
import  bgu.spl.mics.MicroService;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
/**
 * extends MicroService.
 * It is responsible
 * for counting how much clock ticks passed since the beginning of its execution and notifying every
 * other microservice (thats interested) about it using the TickBroadcast.
 */
public class TimeService extends MicroService {
	private CountDownLatch latch;
	private int currentTime;
	private int duration;
	private int speed;
	private Timer timer;
	private Broadcast b;
	volatile boolean terminate;
	/**
	 * constructor.
	 * @param speed of the TimeService.
	 * @param duration of the TimeService.
	 * @param latch Number of services which are executing before timer starts.
	    */
	public TimeService(int speed,int duration,CountDownLatch latch)
	{
		super("timer",latch);
		this.speed=speed;
		this.duration=duration;
		currentTime=1;
		timer=new Timer();
		//terminate=false;
		
	}
	
	class SendTimeBroadcast extends TimerTask {
		@Override
		public void run() {
			b=new TickBroadcast(currentTime++);
			sendBroadcast(b);
			if(currentTime<=duration)
			System.out.println("Tick : "+ currentTime);
			if(currentTime==duration+2){
				sendBroadcast(new TerminateBrodacast());
				}
		}}
	//@Override
	protected void initialize(){
		subscribeBroadcast(TerminateBrodacast.class,terminate ->{timer.cancel();terminate();});
		try {
			latchObject.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		timer.scheduleAtFixedRate( new SendTimeBroadcast(),0, speed);
		logger.info("timer working"); 
	}
	}


