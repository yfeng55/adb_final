package com.yf833;


import java.util.*;

//each site represents a separate database -- it has its own lock table and variable copies
public class DBSite {

    // the id for this site
    public int id;

    // the variables contained at this site
    public HashSet<Integer> variables;

    // the locktable for this site
    public HashMap<Integer, ArrayList<LockEntry>> locktable;

    // the queue of lock requests for this site
    public Queue<LockEntry> waiting_lockrequests;





    public DBSite(int id){
        this.id = id;
        this.locktable = new HashMap<>();
        this.variables = new HashSet<>();
        this.waiting_lockrequests = new LinkedList<>();
    }


    //TODO: commit the specified transaction
    public void commit(int transactionid){

    }

    //TODO: abort the specified transaction
    public void abort(int transactionid){

    }



}
