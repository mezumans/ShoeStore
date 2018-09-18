package bgu.spl.app.Messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Request;

/**
 * Created by dell on 12/24/2015.
 */
public class StoreBroadcast implements Broadcast{
    private int time;

    public StoreBroadcast(int time){
        this.time = time;
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
}
