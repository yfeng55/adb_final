package com.yf833;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

// each site represents a separate database -- it has its own lock table and variable copies
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

    boolean hasRecovered;

    public DBSite(int id) {
        this.id = id;

        this.locktable = new HashMap<>();
        this.variables = new HashSet<>();
        this.isFailed = false;
        pendingwrites = new HashMap<>();
        datatable = new HashMap<>();
        hasRecovered = false;
    }

    // commit the specified transaction
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

            ArrayList<LockEntry> new_lelist = new ArrayList<>();

            //find the indexes to remove
            for(LockEntry le : this.locktable.get(var_id)){
                //if this lockentry belongs to the committed transaction, then remove it
                if(le.transac_id != transac_id){
                    new_lelist.add(le);
                }
            }

            this.locktable.put(var_id, new_lelist);
        }
    }

    // TODO: abort the specified transaction
    public void abort(int transac_id) {

    }

    public void failure() {
        isFailed = true;
        pendingwrites.clear();
        locktable.clear();
        datatable.clear();
    }

}
