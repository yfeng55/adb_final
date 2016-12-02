package com.yf833;

import java.util.HashMap;
import java.util.HashSet;

public class Transaction {

    public enum Status {
        RUNNING, WAITING, ABORTED, COMMITED
    }

    public enum Type {
        DEFAULT, READONLY
    }

    int transactionID;
    int startTime;
    Type type;
    Status status;

    // a map to snapshot all the variables before the readonly transaction begin.
    HashMap<Integer, Integer> readOnlyMap;


    public Transaction(int transac_id, int createtime, Type transac_type){
        this.transactionID = transac_id;
        this.startTime = createtime;

        this.type = transac_type;

        //initialize status to running
        this.status = Status.RUNNING;
    }


    public String toString(){
        String output = "";

        output += "T" + transactionID + "\n";
        output += "starttime: " + startTime + "\n";
        output += "type: " + this.type.toString() + "\n";
        output += "status: " + this.status.toString() + "\n";

        return output;
    }


}