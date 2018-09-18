package bgu.spl.app.Messages;

/**
 * Created by dell on 12/24/2015.
 */
public class NewDiscountBroadcast extends StoreBroadcast {

    private String shoeType;
  //  private int percentage;
    private int discountAmount;
    private int time;


    public NewDiscountBroadcast(String shoeType,  int discountAmount, int time){
        super(time);
        this.shoeType = shoeType;
        
        this.discountAmount = discountAmount;
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


    public void discountAmount (int discountAmount) {
        this.discountAmount = discountAmount;
    }
    public int getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(int discountAmount) {
        this.discountAmount = discountAmount;
    }


}
