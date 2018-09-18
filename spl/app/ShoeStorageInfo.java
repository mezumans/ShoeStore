package bgu.spl.app;

/**
 * Created by dell on 12/20/2015.
 */
public class ShoeStorageInfo {
    private String shoeType;
    private int amountOnStorage;
    private int discountedAmount;

    public ShoeStorageInfo (String shoeType, int amountOnStorage, int discountedAmount){
        this.shoeType = shoeType;
        this.amountOnStorage = amountOnStorage;
        this.discountedAmount = discountedAmount;
    }

    public void shoeType(String shoeType) {
        this.shoeType = shoeType;
    }

    public String getShoeType() {
        return shoeType;
    }

    public void setShoeType(String shoeType) {
        this.shoeType = shoeType;
    }



    public void amountOnStorage (int amountOnStorage) {
        this.amountOnStorage = amountOnStorage;
    }
    public int getAmountOnStorage() {
        return amountOnStorage;
    }

    public void setAmountOnStorage(int amountOnStorage) {
        this.amountOnStorage = amountOnStorage;
    }

    


    public void discountedAmount (int discountedAmount) {
        this.discountedAmount = discountedAmount;

    }
    public int getDiscountedAmount() {
        return discountedAmount;
    }

    public void setDiscountedAmount(int discountedAmount) {
        
    	this.discountedAmount = discountedAmount;
    }

    public String toString (){
        return "\namount on storage:"+ this.amountOnStorage + "\ndiscounted amount:"+ this.discountedAmount+"\n****************************";
    }



}
