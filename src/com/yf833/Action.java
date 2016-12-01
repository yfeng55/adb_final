package com.yf833;


public class Action {

    public int transactionid;
    public String type;
    public int time;

    //only for R() and W() actions
    public int variable;

    //only for W() actions
    public int value;


    //constructor for begin() and end() actions
    public Action(String type, int transactionid, int time){
        this.transactionid = transactionid;
        this.type = type;
        this.time = time;
    }

    //constructor for R() actions
    public Action(String type, int transactionid, int time, int variable){
        this.transactionid = transactionid;
        this.type = type;
        this.time = time;
        this.variable = variable;
    }

    //constructor for W() actions
    public Action(String type, int transactionid, int time, int variable, int value){
        this.transactionid = transactionid;
        this.type = type;
        this.time = time;
        this.variable = variable;
        this.value = value;
    }


    public String toString(){
        String output = "";
        output += "T" + transactionid + " | " + type;
        return output;
    }


}
