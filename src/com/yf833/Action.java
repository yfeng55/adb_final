package com.yf833;


public class Action {

    public int transac_id;
    public String type; //read, write, end, begin, ...
    public int time;

    //only for R() and W() actions
    public int variable;

    //only for W() actions
    public int value;

    //only for fail() and recover() actions
    public int site_id;


    //constructor for dump() action
    public Action(String type){
        this.type = type;
    }

    //constructor for fail() and recover() actions
    public Action(String type, int site_id){
        this.type = type;
        this.site_id = site_id;
    }

    //constructor for begin() and end() actions
    public Action(String type, int transactionid, int time){
        this.transac_id = transactionid;
        this.type = type;
        this.time = time;
    }

    //constructor for R() actions
    public Action(String type, int transactionid, int time, int variable){
        this.transac_id = transactionid;
        this.type = type;
        this.time = time;
        this.variable = variable;
    }

    //constructor for W() actions
    public Action(String type, int transactionid, int time, int variable, int value){
        this.transac_id = transactionid;
        this.type = type;
        this.time = time;
        this.variable = variable;
        this.value = value;
    }


    public String toString(){
        String output = "";
        output += "T" + transac_id + " | " + type;
        return output;
    }


    //compare if two Actions are equal
    @Override
    public boolean equals(Object a2){
        boolean is_same = false;
        if (a2 != null && a2 instanceof Action){

            Action action2 = (Action) a2;

            is_same = (this.transac_id == action2.transac_id
                    && this.time == action2.time
                    && this.type.equals(action2.type));
        }
        return is_same;
    }
    

}
