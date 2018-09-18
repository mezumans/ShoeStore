package bgu.spl.app.Messages;

import bgu.spl.mics.Request;

/**
 * Created by dell on 12/23/2015.
 */
public class RestockRequest implements Request<Boolean> 
{
	private String buyer;
    private String seller;
    private int time;
    private String shoeType;

    public RestockRequest(int time,String seller,String buyer, String shoeType){
        this.seller=seller;
        this.time=time;
        this.buyer=buyer;
        this.shoeType=shoeType;   
    }
  public String getSellerName() {
        return seller;
    }
    public void setSellerName(String sellerName) 
    {
        this.seller = sellerName;
    }
    public String getshoeType(){return shoeType;
    }
}
