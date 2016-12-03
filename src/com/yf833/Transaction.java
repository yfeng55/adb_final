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

    public int transactionID;
    public int startTime;
    public Type type;
    public Status status;

    // for read-only transactions only -- a copy of the datatable
    HashMap<Integer, Integer> data_table_from_lastcommit = null;


    public Transaction(int transac_id, int createtime, Type transac_type){
        this.transactionID = transac_id;
        this.startTime = createtime;

        this.type = transac_type;

        //initialize status to running
        this.status = Status.RUNNING;

        if(transac_type == Type.READONLY){
            data_table_from_lastcommit = new HashMap<Integer, Integer>();
        }
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