package bgu.spl.app.Messages;

import bgu.spl.app.Receipt;
import bgu.spl.mics.Request;

public class PurchaseOrderRequest implements Request<Receipt> {
	
	private boolean onlyDiscount;
	private String typeRequested;
	private String costumerName;
	private int requestedAtTick;
	
	
	public boolean getOnlyDiscount() {
		return onlyDiscount;
	}
	public String getSenderName(){
	return costumerName;}

	public String getShoeType() {
		return typeRequested;
	}
    
	public int getTime(){
		return requestedAtTick;
	}
	public void setTypeRequested(String typeRequested) {
		this.typeRequested = typeRequested;
	}

	
	public PurchaseOrderRequest(boolean onlyDiscount, int requestedAtTick, String typeRequested, String costumerName) {
		this.onlyDiscount = onlyDiscount;
		this.typeRequested = typeRequested;
		this.costumerName = costumerName;
		this.requestedAtTick = requestedAtTick;
	}


}
