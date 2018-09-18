package bgu.spl.app;

import java.util.Comparator;

/**
 * Created by dell on 12/22/2015.
 */
public class PurchaseSchedule {
    private String shoeType;
    private int tick;
public PurchaseSchedule(String shoeType,int tick){
	this.shoeType=shoeType;
	this.tick=tick;
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
public static Comparator<PurchaseSchedule> PurchaseScheduleComparator 
= new Comparator<PurchaseSchedule>() {

public int compare(PurchaseSchedule ps1, PurchaseSchedule ps2) {

return ps1.getTick()-ps2.getTick();
}};
}
