package bgu.spl.app.Messages;

import bgu.spl.app.Receipt;
import bgu.spl.mics.Request;

/**
 * Created by dell on 12/23/2015.
 */
public class ManufacturingOrderRequest implements Request<Receipt> 
{
	private int time;
    private String shoeType;
    private int amounttoSupply;
    
    public ManufacturingOrderRequest( int time, int amount, String shoeType)
    {
        this.amounttoSupply =amount;
        this.shoeType=shoeType;
        this.time=time;
    }

    public int getamounttoSupply ()
    {
    	return amounttoSupply;    
    }
    public String getshoeType(){return shoeType;}
    public int getTime(){return time;}
   
}
