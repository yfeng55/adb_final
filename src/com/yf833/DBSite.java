package com.yf833;


import java.util.*;

//each site represents a separate database -- it has its own lock table and variable copies
public class DBSite {

    // the id for this site
    public int id;

    // track if the site is up or not
    public boolean isFailed;

    // the variables contained at this site
    public HashSet<Integer> variables;

    // hashmaps containing the temp buffer and actual data values stored at this site (variable --> value)
    public HashMap<Integer, Integer> datatowrite;
    public HashMap<Integer, Integer> datatable;


    // the locktable for this site
    public HashMap<Integer, ArrayList<LockEntry>> locktable;




    public DBSite(int id){
        this.id = id;

        this.locktable = new HashMap<>();
        this.variables = new HashSet<>();
        this.isFailed = false;
        datatowrite = new HashMap<>();
        datatable = new HashMap<>();
    }


    //TODO: commit the specified transaction
    public void commit(int transactionid){

    }

    //TODO: abort the specified transaction
    public void abort(int transactionid){

    }



}
