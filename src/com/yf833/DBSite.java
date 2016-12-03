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

    // pending write requests for this site (transaction_id --> [W(x1,val), W(x2,val), ...])
    public HashMap<Integer, ArrayList<Action>> pendingactions;

    // the actual data values stored at this site (variable --> value)
    public HashMap<Integer, Integer> datatable;

    // the locktable for this site (variable --> {type: W, transac_id: i})
    public HashMap<Integer, ArrayList<LockEntry>> locktable;

    //the set of transactions that are currently accessing this site
    public HashSet<Transaction> current_transactions;


    boolean hasRecovered;

    public DBSite(int id) {
        this.id = id;

        this.locktable = new HashMap<>();
        this.variables = new HashSet<>();
        this.isFailed = false;
        pendingactions = new HashMap<>();

        // var_id --> value
        datatable = new HashMap<>();
        hasRecovered = false;
    }

    // commit the specified transaction
    public void commit(int transac_id, Transaction.Type transac_type) throws Exception {

        removeAllLocksForTransaction(transac_id);

        //if this site doesn't have any pending actions for this transaction, then exit (nothing to commit)
        if(!this.pendingactions.keySet().contains(transac_id)){
            return;
        }

        //execute all pending actions for this transaction
        for(Action a : this.pendingactions.get(transac_id)){

            if(a.type.equalsIgnoreCase("W")){
                this.datatable.put(a.variable, a.value);
            }
            else if(a.type.equalsIgnoreCase("R") && transac_type == Transaction.Type.DEFAULT){
                System.out.println("\nREAD: T" + a.transac_id + " reads x" + a.variable + "=" + this.datatable.get(a.variable));
            }
            else if(a.type.equalsIgnoreCase("R") && transac_type == Transaction.Type.READONLY){
                //do nothing
            }
            else{
                System.out.println("ERROR: Invalid action type in the pendingactions queue");
                throw new Exception();
            }
        }
        this.pendingactions.remove(transac_id);

    }


    // abort the specified transaction
    public void abort(int transac_id) {
        // clear the transaction's pending writes at this site

        //if site s contains pending for this transaction, remove that transaction's row from the pending writes table
        if(this.pendingactions.keySet().contains(transac_id)){
            this.pendingactions.remove(transac_id);
        }

        removeAllLocksForTransaction(transac_id);

    }


    // free all locks for this transaction
    private void removeAllLocksForTransaction(int transac_id){
        for(int var_id : this.locktable.keySet()) {
            ArrayList<LockEntry> new_lelist = new ArrayList<>();
            //find the indexes to remove
            for (LockEntry le : this.locktable.get(var_id)) {
                //if this lockentry belongs to the committed transaction, then remove it
                if (le.transac_id != transac_id) {
                    new_lelist.add(le);
                }
            }
            this.locktable.put(var_id, new_lelist);
        }
    }





    public void failure() {
        isFailed = true;
        pendingactions.clear();
        locktable.clear();
        datatable.clear();
    }

}
