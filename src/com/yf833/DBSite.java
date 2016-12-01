package com.yf833;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

//each site represents a separate database -- it has its own lock table and variable copies
public class DBSite {

    public enum Lock{
        READ, WRITE, NONE
    }

    // the id for this site
    public int id;

    // the locktable for this site
    public HashMap<Integer, Lock> locktable;

    // the variables contained at this site
    public HashSet<Integer> variables;


    public DBSite(int id){
        this.id = id;
        this.locktable = new HashMap<>();
        this.variables = new HashSet<>();
    }


    //TODO: commit the specified transaction
    public void commit(int transactionid){

    }

    //TODO: abort the specified transaction
    public void abort(int transactionid){

    }



}
