package com.yf833;


public class LockEntry {

    //read, write, none
    public String type;
    public int transac_id;

    public LockEntry(int transactionid, String type){
        this.type = type;
        this.transac_id = transactionid;
    }


}
