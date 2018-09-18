package bgu.spl.app.Messages;

import bgu.spl.mics.Request;

/**
 * Created by dell on 12/23/2015.
 */
public abstract class StoreRequest implements Request {
    private int time;
    private String resultNeeded;
    private String shoeType;
    //private int amount;

    public StoreRequest (int time, String resultNeeded, String shoeType){
        this.time = time;
        this.resultNeeded = resultNeeded;
        this.shoeType = shoeType;
       // this.amount = amount;
    }


    public void time (int time) {
        this.time = time;
    }
    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void resultNeeded(String resultNeeded) {
        this.resultNeeded = resultNeeded;
    }

    public String getResultNeeded() {
        return resultNeeded;
    }

    public void setResultNeeded(String resultNeeded) {
        this.resultNeeded = resultNeeded;
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



    /*public void amount (int amount) {
        this.amount = amount;
    }
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }*/

}
