package bgu.spl.app;

import java.lang.System;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * The store object holds a collection of ShoeStorageInfo: One for each shoe type the store offers. In
 * addition, it contains a list of receipts issued to and by the store.
 */
public class Store{
	public enum BuyResult {
		NOT_IN_STOCK, NOT_ON_DISCOUNT, REGULAR_PRICE, DISCOUNTED_PRICE
	}
	private ConcurrentHashMap<String,ShoeStorageInfo> storeInfo;
	private Logger logger;
	private Object somelock1;
	ArrayList<Receipt> receiptList ;
	BuyResult result;
	private static class StoreHolder {
		private static Store instance = new Store();
	}
	private Store() {
		result=null;
		storeInfo=new ConcurrentHashMap<String,ShoeStorageInfo>();
		logger=Logger.getLogger(Store.class.getName());
		somelock1=new Object();
		receiptList= new ArrayList<Receipt>();

	}
	public static Store getInstance() {
		return StoreHolder.instance;
	}    
	/**
	 * Initialize the store. The method will add the items in the given array to
	 * the store with ConcurrentMap named storeInfo that holds the stores information.
	 * @param storage an array that containing the stores information.
	 */

	public void load ( ShoeStorageInfo [] storage ){

		for(int i=0;i<storage.length;i++){
			String shoeType = storage[i].getShoeType();
			storeInfo.put(shoeType,storage[i]);
		}	
	}
	/**
	 * This method will attempt to take a single showType from the store and if the shoe will be take it will remove one shoe
	 * from the stock.
	 * @param shoeType the name of shoe type to take.
	 * @param onlyDiscount a boolean which indicates that the caller wish to take the item only if it is in discount.
	 * @return an enum which tell if the shoe is in the stock and if their is a discount.
	 */
	public  BuyResult take(String shoeType, boolean onlyDiscount){
		synchronized (somelock1) {
			ShoeStorageInfo shoeStorageInfo = storeInfo.get(shoeType);
			if(onlyDiscount){
				if(!storeInfo.containsKey(shoeType)){logger.info("shoeType is not on discount");
				result = BuyResult.NOT_ON_DISCOUNT;}
			else if ( storeInfo.get(shoeType).getDiscountedAmount()==0||storeInfo.get(shoeType).getAmountOnStorage()==0){
				logger.info("shoeType is not on discount");
				result = BuyResult.NOT_ON_DISCOUNT;}
			else if (storeInfo.containsKey(shoeType)&&storeInfo.get(shoeType).getDiscountedAmount()>0&&storeInfo.get(shoeType).getAmountOnStorage()>0)
			{
				result = BuyResult.DISCOUNTED_PRICE;
				storeInfo.get(shoeType).setAmountOnStorage(storeInfo.get(shoeType).getAmountOnStorage()-1);
				storeInfo.get(shoeType).setDiscountedAmount(storeInfo.get(shoeType).getDiscountedAmount()-1);
			}}

			else{
				if (!storeInfo.containsKey(shoeType)||storeInfo.get(shoeType).getAmountOnStorage()==0){
					logger.warning(shoeType+" is out of order");
					result = BuyResult.NOT_IN_STOCK;
				}
				
				else if (storeInfo.get(shoeType).getAmountOnStorage()>0 && storeInfo.get(shoeType).getDiscountedAmount()==0){
					result= BuyResult.REGULAR_PRICE;
					storeInfo.get(shoeType).setAmountOnStorage(storeInfo.get(shoeType).getAmountOnStorage()-1);
					logger.fine("1 " +shoeType+"was taken" );
				}
				else   if (storeInfo.get(shoeType).getDiscountedAmount()>0)
				{
					result = BuyResult.DISCOUNTED_PRICE;
					storeInfo.get(shoeType).setAmountOnStorage(storeInfo.get(shoeType).getAmountOnStorage()-1);
					storeInfo.get(shoeType).setDiscountedAmount(storeInfo.get(shoeType).getDiscountedAmount()-1);
				}}
			return result;

		}}
	/**
	 * This method adds the given amount to the ShoeStorageInfo of the given shoeType.
	 * @param shoeType a String of the shoes name that the method add to.
	 * @param amount an int of the amount that need to add to the ShoeStorageInfo of the given shoe type.
	 */
	public void add (String shoeType , int amount )
	{	synchronized (somelock1) {
		if (storeInfo.containsKey(shoeType)) {
			int curramount = storeInfo.get(shoeType).getAmountOnStorage();
			storeInfo.get(shoeType).setAmountOnStorage(curramount+amount);
			logger.warning(amount+ " "+shoeType+"added succsessfully to the store");
		}
		if (!storeInfo.containsKey(shoeType)){
			ShoeStorageInfo temp = new ShoeStorageInfo(shoeType,amount,0);
			storeInfo.put(shoeType,temp);
			logger.warning(amount+ " "+shoeType+"added succsessfully to the store");
		}
		logger.info("We added"+amount+shoeType+"to the stock");
	}}
	/**
	 * This method adds the given amount to the discount method of the ShoeStorageInfo of the given shoeType.
	 * @param shoeType a String of the shoes name that the method add to.
	 * @param amount an int of the amount that need to add to the discount method of the ShoeStorageInfo of the given shoe type.
	 */
	public void addDiscount (String shoeType , int amount){
		if (storeInfo.containsKey(shoeType)) {
			int curramount = storeInfo.get(shoeType).getDiscountedAmount();
			storeInfo.get(shoeType).setDiscountedAmount(curramount+amount);
		}
		else logger.warning("Discount shoe is not on stock");
	}
	/**
	 * Save the given receipt in the store. it do this whith a LinkedList.
	 * @param receipt the receipt that need to be saved.
	 */
	public void file(Receipt receipt){
		if(receipt==null)logger.severe("The receipt receieved is null");

		else receiptList.add(receipt);
	}
	/**
	 * Print all the shoeStorageInfo and the receipt that in the store.
	 */
	public void print(){		
		ListIterator<Receipt> itList = receiptList.listIterator();        	
		while(itList.hasNext())
		{	
		System.out.println(itList.next().toString());
		}
		Iterator itMap = storeInfo.entrySet().iterator();

		while(itMap.hasNext()) {
			System.out.println(itMap.next().toString());
		}
	}
}






