package com.yf833;


public class LockEntry {

    //read, write, none
    public String type;
    public int transaction_id;

    public LockEntry(int transactionid, String type){
        this.type = type;
        this.transaction_id = transactionid;
    }


}
