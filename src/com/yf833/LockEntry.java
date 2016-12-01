package com.yf833;


public class LockEntry {

    //read, write, none
    public String type;
    public int transac_id;

    public LockEntry(int transactionid, String type){
        this.type = type;
        this.transac_id = transactionid;
    }


    //compare if two LockEntries are equal
    @Override
    public boolean equals(Object le2){
        boolean is_same = false;
        if (le2 != null && le2 instanceof Action){

            Action lockentry2 = (Action) le2;

            is_same = (this.transac_id == lockentry2.transac_id
                    && this.type.equals(lockentry2.type));
        }
        return is_same;
    }
}
