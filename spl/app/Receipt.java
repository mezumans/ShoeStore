package bgu.spl.app;

/**
 * Created by dell on 12/20/2015.
 */
public class Receipt {
    private String seller;
    private String customer;
    private String shoeType;
    private boolean discount;
    private int issuedTick;
    private int requestTick;
    private int amountSold;

    public Receipt (String seller, String customer, String shoeType, boolean discount, int issuedTick, int requestTick, int amountSold){
        this.seller = seller;
        this.customer = customer;
        this.shoeType = shoeType;
        this.discount = discount;
        this.issuedTick = issuedTick;
        this.requestTick = requestTick;
        this.amountSold = amountSold;
    }
    public void seller (String seller) {
        this.seller = seller;
    }
    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }



    public void customer (String customer) {
        this.customer = customer;
    }
    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }



    public void shoeType (String shoeType) {
        this.shoeType = shoeType;
    }
    public String getShoeType() {
        return shoeType;
    }

    public void setShoeType(String shoeType) {
        this.shoeType = shoeType;
    }




    public void discount (boolean discount) {
        this.discount = discount;
    }
    public boolean getDiscount() {
        return discount;
    }

    public void setDiscount(boolean discount) {
        this.discount = discount;
    }



    public void issuedTick (int issuedTick) {
        this.issuedTick = issuedTick;
    }
    public int getIssuedTick() {
        return issuedTick;
    }

    public void setIssuedTick(int issuedTick) {
        this.issuedTick = issuedTick;
    }




    public void requestTick (int requestTick) {
        this.requestTick = requestTick;
    }
    public int getRequestTick() {
        return requestTick;
    }

    public void setRequestTick(int requestTick) {
        this.requestTick = requestTick;
    }




    public void amountSold (int amountSold) {
        this.amountSold = amountSold;
    }
    public int getAmountSold() {
        return amountSold;
    }

    public void setAmountSold(int amountSold) {
        this.amountSold = amountSold;
    }

    public String toString(){
        return "********************Receipt*********************\nSeller:"+ this.seller +"\ncustomer:"+ this.customer+ "\nshoe type:" + this.shoeType + "\n discount:" + this.discount + "\n issuedTick:" + this.issuedTick +"\n requestTick:"+ this.requestTick+"\n amountSold:" + this.amountSold;

    }

}
