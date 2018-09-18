package bgu.spl.app;

import java.util.Comparator;

/**
 * Created by dell on 12/22/2015.
 */
public class DiscountSchedule {
    private String shoeType;
    private int tick;
    private int amount;
public DiscountSchedule(String shoeType,int tick,int amount)
{
	this.shoeType=shoeType;
	this.tick=tick;
	this.amount=amount;
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



    public void tick(int tick) {
        this.tick = tick;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }



    public void amount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
    public static Comparator<DiscountSchedule> DiscountScheduleComparator  = new Comparator<DiscountSchedule>() 
    {

    public int compare(DiscountSchedule ds1, DiscountSchedule ds2) {

    return ds1.getTick()-ds2.getTick();
    }};



}
