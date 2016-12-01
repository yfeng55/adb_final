package com.yf833;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

// the TransactionManager initializes the components of the system and accepts transactions
// - initializes the sites (set to 10 by default)
// - initializes variables (set to 20 by default)
// - creates copies of variables based on the following rule
// -> odd indexed variables are at a single site (1 + i%10)
// -> even indexed variables are at all sites
//
public class TransactionManager {

  public static final int NUM_SITES = 10;
  public static final int NUM_VARIABLES = 20;

  // list of sites
  public static DBSite[] sites;

  // list of sites that a variable is present at
  public static HashMap<Integer, ArrayList<Integer>> sitescontainingvar = new HashMap<>();

  // contains the set of all the variables which have changed from initial state
  public static HashSet<Integer> changedVariables = new HashSet<>();

  // lookuptable of all transactions that are currently running and their actions
  public static HashMap<Integer, Integer> transaction_starttimes = new HashMap<>();
  public static HashSet<Integer> running_transactions = new HashSet<>();

  // track actions that are blocked
  public static ArrayList<Action> blocked_actions = new ArrayList<>();

  // track committed transactions and aborted transactions (store their ids in sets)
  public static HashSet<Integer> committed_transactions = new HashSet<>();
  public static HashSet<Integer> aborted_transactions = new HashSet<>();

  // keep a graph of conflicts between transactions (adjacency maetrix representation)
  public static ArrayList<ArrayList<Integer>> conflictgraph = new ArrayList<>();

  public static void main(String[] args) throws Exception {

    // (1) initialize sites (ids from 1 to N)
    sites = new DBSite[NUM_SITES];
    for (int site_id = 1; site_id <= NUM_SITES; site_id++) {
      sites[site_id - 1] = new DBSite(site_id);
    }

    // (2) initialize variables and create copies at sites
    for (int var_id = 1; var_id <= NUM_VARIABLES; var_id++) {

      sitescontainingvar.put(var_id, new ArrayList<Integer>());

      // if even variable, put in every site
      if (var_id % 2 == 0) {
        for (DBSite site : sites) {
          site.variables.add(var_id);
          sitescontainingvar.get(var_id).add(site.id);
          site.locktable.put(var_id, new ArrayList<LockEntry>());
        }
      }
      // if odd variable, put in site (i%10+1)
      else {
        int oddvar_location = (var_id % 10) + 1;
        sites[oddvar_location - 1].variables.add(var_id);
        sites[oddvar_location - 1].locktable.put(var_id, new ArrayList<LockEntry>());
        sitescontainingvar.get(var_id).add(oddvar_location);
      }
    }

    // (3) read in the list of transactions from the test file and place in a queue
    Scanner scan = new Scanner(new File(args[0]));

    // keep track of the current time
    int time = 0;
    while (scan.hasNextLine()) {

      // before processing new actions, try to process blocked actions
      for (Action a : blocked_actions) {
        processAction(a, time);
      }

      // get the next line and hold all actions to perform at the current time in a list
      String nextline = scan.nextLine();
      ArrayList<Action> currentactions = new ArrayList<>();

      // stop scanning after a newline is encountered (check for all whitespaces)
      if (nextline.trim().length() == 0) {
        System.out.println("END SCAN");
        scan.close();
        break;
      }
      // parse the next line of actions
      else {
        System.out.println("time" + time + ": processing... " + nextline);
        String[] actions_arr = nextline.split(";");

        // initialize a list of actions for the current time
        for (String s : actions_arr) {
          currentactions.add(Util.strToAction(s.trim(), time));
        }

        // process all actions at current time
        for (Action a : currentactions) {
          processAction(a, time);
        }
      }

      // increment time after processing each line
      time++;
    }

    System.out.println();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  // process a single begin(), end(), R(), or W() action
  public static void processAction(Action a, int time) throws Exception {
    switch (a.type) {
    case "begin" :
      transaction_starttimes.put(a.transac_id, time);
      running_transactions.add(a.transac_id);
      conflictgraph.add(new ArrayList<Integer>());

      // fill conflictgraph with 0s
      for (ArrayList<Integer> row : conflictgraph) {
        Util.fillwithNZeroes(row, conflictgraph.size());
      }
      break;

    case "end" :
      running_transactions.remove(a.transac_id);
      committed_transactions.add(a.transac_id);

      // commit the current transaction -- (call commit at all sites -- some sites may not have any
      // pending actions for this transaction)
      for (DBSite s : sites) {
        s.commit(a.transac_id);
      }
      break;

    case "W" :
      changedVariables.add(a.variable);
      // acquire write-lock for all sites containing the current variable
      for (int siteindex : sitescontainingvar.get(a.variable)) {
        // check if we can acquire a write lock
        if (Util.canAcquire(sites[siteindex - 1].locktable.get(a.variable), "WRITE")) {
          sites[siteindex - 1].locktable.get(a.variable).add(new LockEntry(a.transac_id, "WRITE"));

          // remove action from blocked (if blocked)
          if (blocked_actions.contains(a)) {
            blocked_actions.remove(a);
          }

          // write to temp data table at site containing the variable
          if (sites[siteindex - 1].pendingwrites.get(a.transac_id) == null) {
            sites[siteindex - 1].pendingwrites.put(a.transac_id, new ArrayList<Action>());
          }
          sites[siteindex - 1].pendingwrites.get(a.transac_id).add(a);

        } else {
          System.out.println("T" + a.transac_id + " CAN'T ACQUIRE WRITE LOCK FOR x" + a.variable
              + " AT SITE" + siteindex);

          // add action to waiting queue (if not already there)
          if (!blocked_actions.contains(a)) {
            blocked_actions.add(a);
          }

          // update conflict graph
          for (LockEntry le : sites[siteindex - 1].locktable.get(a.variable)) {
            // fill list with 0s and create an edge between T' and T
            conflictgraph.get(a.transac_id - 1).set(le.transac_id - 1, 1);
          }
        }
      }
      break;

    case "R" :
      // acquire read-lock for all sites containing the current variable
      for (int siteindex : sitescontainingvar.get(a.variable)) {

        if (Util.canAcquire(sites[siteindex - 1].locktable.get(a.variable), "READ")) {
          sites[siteindex - 1].locktable.get(a.variable).add(new LockEntry(a.transac_id, "READ"));
        } else {
          System.out.println("T" + a.transac_id + " CAN'T ACQUIRE READ LOCK FOR x" + a.variable
              + " AT SITE" + siteindex);
        }

      }
      break;

    default :
      System.out.println("ERROR: action contains an invalid type");
      throw new Exception();
    }
  }

  // TODO: print the states of the TM and all DBSites
  public static void querystate() {

  }

  // TODO: process a write action
  public static void processW() {

  }

  // TODO: process a read action
  public static void processR(Action a) {
    // search for a site that contains the variable
    for (DBSite site : sites) {
      if (site.variables.contains(a.variable)) {
        System.out.println(a.toString() + " from site ");
      }
    }
  }

  // dump the committed values of all copies of all variables at all sites, sorted by site
  public static void dump() {
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

  // return true if a transaction is blocked (i.e. any of the actions in blocked_actions belong to
  // this transaction)
  public static boolean isBlocked(int transac_id) {
    for (Action a : blocked_actions) {
      if (a.transac_id == transac_id) {
        return true;
      }
    }
    return false;
  }

}
