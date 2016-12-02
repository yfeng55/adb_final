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

    // pending write requests for this site (transaction_id --> W(x,val))
    public HashMap<Integer, ArrayList<Action>> pendingwrites;

    // the actual data values stored at this site (variable --> value)
    public HashMap<Integer, Integer> datatable;


    // the locktable for this site (variable --> {type: W, transac_id: i})
    public HashMap<Integer, ArrayList<LockEntry>> locktable;




    public DBSite(int id){
        this.id = id;

        this.locktable = new HashMap<>();
        this.variables = new HashSet<>();
        this.isFailed = false;
        this.pendingwrites = new HashMap<>();
        this.datatable = new HashMap<>();
    }


    //TODO: commit the specified transaction
    public void commit(int transac_id){

        //if this site doesn't have any pending actions for this transaction, then exit (nothing to commit)
        if(!this.pendingwrites.keySet().contains(transac_id)){
            return;
        }

        //execute all pending writes for this transaction
        for(Action write : this.pendingwrites.get(transac_id)){
            this.datatable.put(write.variable, write.value);
        }
        this.pendingwrites.remove(transac_id);

        //free all locks that the committed transaction holds
        for(int var_id : this.locktable.keySet()){
            for(LockEntry le : this.locktable.get(var_id)){

                //if this lockentry belongs to the committed transaction, then remove it
                if(le.transac_id == transac_id){
                    this.locktable.get(var_id).remove(le);
                }
            }
        }
    }

    //TODO: abort the specified transaction
    public void abort(int transac_id){

    }



}
