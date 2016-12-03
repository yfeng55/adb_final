package com.yf833;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.File;
import java.util.*;


// the TransactionManager initializes the components of the system and accepts transactions
// - initializes the sites (set to 10 by default)
// - initializes variables (set to 20 by default)
// - creates copies of variables based on the following rule
//          -> odd indexed variables are at a single site (1 + i%10)
//          -> even indexed variables are at all sites
//
public class TransactionManager {

    public static final int NUM_SITES = 10;
    public static final int NUM_VARIABLES = 20;

    // list of sites
    public static DBSite[] sites;

    // list of sites that a variable is present at
    public static HashMap<Integer, ArrayList<Integer>> sitescontainingvar = new HashMap<>();

    //maintain a list of transaction objects to track: isCommitted, isAborted, isRunning, startTime
    //transac_id --> Trasnaction
    public static ArrayList<Transaction> transactions = new ArrayList<>();


    // track actions that are blocked
    public static ArrayList<Action> blocked_actions = new ArrayList<>();
    public static ArrayList<Action> newblocked_actions = new ArrayList<>();



    // keep a graph of conflicts between transactions (adjacency maetrix representation)
    public static ConflictGraph conflict_graph = new ConflictGraph();


    public static void main(String[] args) throws Exception {

        // (1) initialize sites (ids from 1 to N)
        sites = new DBSite[NUM_SITES];
        for(int site_id=1; site_id<=NUM_SITES; site_id++){
            sites[site_id-1] = new DBSite(site_id);
        }

        // (2) initialize variables and create copies at sites
        for(int var_id = 1; var_id<=NUM_VARIABLES; var_id++){

            sitescontainingvar.put(var_id, new ArrayList<Integer>());

            //if even variable, put in every site
            if(var_id%2 == 0){
                for(DBSite site : sites){
                    site.variables.add(var_id);
                    sitescontainingvar.get(var_id).add(site.id);
                    site.locktable.put(var_id, new ArrayList<LockEntry>());
                    site.datatable.put(var_id, var_id*10);
                }
            }
            //if odd variable, put in site (i%10+1)
            else{
                int oddvar_location = (var_id%10) + 1;
                sites[oddvar_location-1].variables.add(var_id);
                sites[oddvar_location-1].locktable.put(var_id, new ArrayList<LockEntry>());
                sites[oddvar_location-1].datatable.put(var_id, var_id*10);
                sitescontainingvar.get(var_id).add(oddvar_location);
            }
        }

        // (3) read in the list of transactions from the test file and place in a queue
        Scanner scan = new Scanner(new File(args[0]));

        // keep track of the current time
        int time=0;
        while(scan.hasNextLine()){

            // before processing new actions, try to process blocked actions
            newblocked_actions = new ArrayList<>(blocked_actions);
            for(Action a : blocked_actions){
                processAction(a, time);
            }
            blocked_actions = newblocked_actions;

            //get the next line and hold all actions to perform at the current time in a list
            String nextline = scan.nextLine();
            ArrayList<Action> currentactions = new ArrayList<>();

            //stop scanning after a newline is encountered (check for all whitespaces)
            if(nextline.trim().length() == 0){
                System.out.println("END SCAN");
                scan.close();
                break;
            }
            //parse the next line of actions
            else {
                System.out.println("time" + time + ": processing... " + nextline);
                String[] actions_arr = nextline.split(";");

                //initialize a list of actions for the current time
                for(String s : actions_arr){
                    System.out.println("parsing... " + s);
                    currentactions.add(Util.strToAction(s.trim(), time));
                }

                //process all actions at current time
                for(Action a : currentactions){
                    processAction(a, time);
                }
            }

            //increment time after processing each line
            time++;

            if(time == 7){
                querystate();
            }

        }

        dump();
        System.out.println();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    //process a single begin(), end(), R(), or W() action
    public static void processAction(Action a, int time) throws Exception {

        switch(a.type){
            case "begin":

                //create a new transaction
                transactions.add(new Transaction(a.transac_id, time, Transaction.Type.DEFAULT));
                conflict_graph.addTransac(a.transac_id);
                break;

            case "beginRO" :
                HashMap<Integer, Integer> tempdata = new HashMap<>();
                for (DBSite d : sites) {
                    for (Integer i : d.datatable.keySet())
                          tempdata.put(i, d.datatable.get(i));
                  }
                break;

            case "end":
                for(Transaction t : transactions){
                    if(t.transactionID == a.transac_id){
                        t.status = Transaction.Status.COMMITED;
                    }
                }

                //commit the current transaction -- (call commit at all sites -- some sites may not have any pending actions for this transaction)
                for(DBSite s : sites){
                    s.commit(a.transac_id);
                }
                //update the conflict graph
                conflict_graph.commit_or_abort(a.transac_id);
                break;

            case "W":
                //acquire write-lock for all sites containing the current variable
                for(int siteindex : sitescontainingvar.get(a.variable)){
                    //check if we can acquire a write lock
                    if(Util.canAcquire(sites[siteindex - 1].locktable.get(a.variable), "WRITE", a.transac_id)){
                        sites[siteindex-1].locktable.get(a.variable).add(new LockEntry(a.transac_id, "WRITE"));

                        //remove action from blocked (if blocked)
                        if(blocked_actions.contains(a)){
                            newblocked_actions.remove(a);
                        }

                        //write to temp data table at site containing the variable
                        if(sites[siteindex-1].pendingwrites.get(a.transac_id) == null){
                            sites[siteindex-1].pendingwrites.put(a.transac_id, new ArrayList<Action>());
                        }
                        sites[siteindex-1].pendingwrites.get(a.transac_id).add(a);

                    } else {
                        System.out.println("T" + a.transac_id + " CAN'T ACQUIRE WRITE LOCK FOR x" + a.variable + " AT SITE" + siteindex);

                        //add action to waiting queue (if not already there)
                        if(!blocked_actions.contains(a)) {
                            newblocked_actions.add(a);
                            for(Transaction t : transactions){
                                if(t.transactionID == a.transac_id){
                                    t.status = Transaction.Status.WAITING;
                                }
                            }
                        }

                        //update conflict graph
                        for(LockEntry le : sites[siteindex - 1].locktable.get(a.variable)){
                            //fill list with 0s and create an edge between T' and T
                            if(a.transac_id != le.transac_id){
                                conflict_graph.addEdge(a.transac_id-1, le.transac_id-1);
                            }
                        }
                    }
                }
                break;

            case "R":
                //acquire read-lock for all sites containing the current variable
                for(int siteindex : sitescontainingvar.get(a.variable)){

                    if(Util.canAcquire(sites[siteindex-1].locktable.get(a.variable), "READ", a.transac_id)) {
                        sites[siteindex-1].locktable.get(a.variable).add(new LockEntry(a.transac_id, "READ"));
                    }else{
                        System.out.println("T" + a.transac_id + " CAN'T ACQUIRE READ LOCK FOR x" + a.variable + " AT SITE" + siteindex);
                    }

                }
                break;

            case "dump":
                dump();
                break;

            default:
                System.out.println("ERROR: action contains an invalid type");
                throw new Exception();
        }

        //check for deadlock
        HashSet<Integer> cycle = conflict_graph.getCycle();
        if(cycle.size()>0){
            abortYoungestInCycle(cycle);
            System.out.println();
        }

    }



    public static void abortYoungestInCycle(HashSet<Integer> cycle) throws Exception {

        // take the set of transactions that are involved in the abortYoungestInCycle
        // get references to those transactions from the set of cycle IDs
        ArrayList<Transaction> abort_candidates = new ArrayList<>();
        for(Transaction t : transactions){
            //if a transaction is contained in the cycle, add it to abort candidates
            // (we subtract 1 from the transaction id since the graph's indices are transac_id -1)
            if(cycle.contains(t.transactionID-1)){
                abort_candidates.add(t);
            }
        }

        if(abort_candidates.size() == 0){
            System.out.println("ERROR: expected non empty abort_candidates list");
            throw new Exception();
        }

        //find the youngest of the abort candidates
        Transaction youngest_t = abort_candidates.get(0);
        for(Transaction t : abort_candidates){
            if(t.startTime > youngest_t.startTime){
                youngest_t = t;
            }
        }

        //remove this transaction's actions from the blocked list
        newblocked_actions = new ArrayList<>();
        for(Action a : blocked_actions){
            if(a.transac_id != youngest_t.transactionID){
                newblocked_actions.add(a);
            }
        }
        blocked_actions = newblocked_actions;


        System.out.println("--> aborting transaction T" + youngest_t.transactionID);

        youngest_t.status = Transaction.Status.ABORTED;

        // call the abort() function at all DBSites (clears the locktable of this transaction's locks, clear pending writes for this transaction)
        for(DBSite s : sites){
            s.abort(youngest_t.transactionID);
        }

        //update conflict graph to reflect the abort
        conflict_graph.commit_or_abort(youngest_t.transactionID);

    }


    //print the states of the TM and all DBSites
    public static void querystate(){
        for(Transaction t : transactions){
            System.out.println(t.toString());
        }
        System.out.println("Site locktables ");
        for (DBSite s : sites){
            System.out.println(s.locktable.toString());
        }
    }

    //TODO: process a write action
    public static void processW(){

    }

    //TODO: process a read action
    public static void processR(Action a){
        //search for a site that contains the variable
        for(DBSite site : sites){
            if(site.variables.contains(a.variable)){
                System.out.println(a.toString() + " from site ");
            }
        }
    }


    //dump the committed values of all copies of all variables at all sites, sorted by site
    public static void dump(){
        System.out.println("~Dumping all~");
        for (DBSite site : sites) {
            dump(site);
        }
    }

    public static void dump(DBSite i) {
        System.out.print("Dump For Site" + i.id + " : ");
        ArrayList<Integer> vars = new ArrayList<Integer>(i.datatable.keySet());
        Collections.sort(vars);
        for (Integer j : vars) {
            System.out.print("x" + j + ": " + i.datatable.get(j) + ", ");
        }
        System.out.println();
    }

    public static void dump(int i) {
        ArrayList<Integer> Sites = sitescontainingvar.get(i);
        System.out.println("Variable x" + i + "at => ");
        for (int j : Sites) {
            System.out.print("Site " + sites[j].id + " : " + sites[j].datatable.get(i));
        }
        System.out.println();
    }


    //return true if a transaction is blocked (i.e. any of the actions in blocked_actions belong to this transaction)
    public static boolean isBlocked(int transac_id){
        for(Action a : blocked_actions){
            if(a.transac_id == transac_id){
                return true;
            }
        }
        return false;
    }


}









