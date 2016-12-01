package com.yf833;

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
    public static HashMap<Integer, DBSite> sites;

    // list of sites that a variable is present at
    public static HashMap<Integer, ArrayList<Integer>> sitescontainingvar = new HashMap<>();

    // lookuptable of all transactions that are currently running and their actions
    public static HashMap<Integer, Integer> transactions = new HashMap<>();
    public static HashSet<Integer> running_transactions = new HashSet<>();

    // track committed transactions and aborted transactions (store their ids in sets)
    public static HashSet<Integer> committed_transactions = new HashSet<>();
    public static HashSet<Integer> aborted_transactions = new HashSet<>();

    // a queue of transactions that are waiting
    public static Queue<Action> waiting;


    public static void main(String[] args) throws Exception {

        // (1) initialize sites (ids from 1 to N)
        sites = new HashMap<>();
        for(int site_id=1; site_id<=NUM_SITES; site_id++){
            sites.put(site_id, new DBSite(site_id));
        }

        // (2) initialize variables and create copies at sites
        for(int var_id = 1; var_id<=NUM_VARIABLES; var_id++){

            sitescontainingvar.put(var_id, new ArrayList<Integer>());

            //if even variable, put in every site
            if(var_id%2 == 0){
                for(int site_i : sites.keySet()){
                    sites.get(site_i).variables.add(var_id);
                    sitescontainingvar.get(var_id).add(sites.get(site_i).id);
                    sites.get(site_i).locktable.put(var_id, DBSite.Lock.NONE);
                }
            }
            //if odd variable, put in site (i%10+1)
            else{
                int insert_i = (var_id%10) + 1;
                sites.get(insert_i).variables.add(var_id);
                sitescontainingvar.get(var_id).add(insert_i);
            }
        }


        // (3) read in the list of transactions from the test file and place in a queue
        Scanner scan = new Scanner(new File(args[0]));

        // keep track of the current time
        int time=0;
        while(scan.hasNextLine()){

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
                    currentactions.add(Util.strToAction(s.trim(), time));
                }

                //process all actions at current time
                for(Action a : currentactions){
                    processAction(a, time);
                }
            }

            //increment time after processing each line
            time++;
        }


        System.out.println();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //process a single begin(), end(), R(), or W() action
    public static void processAction(Action a, int time) throws Exception {
        switch(a.type){
            case "begin":
                transactions.put(a.transactionid, time);
                running_transactions.add(a.transactionid);
                break;
            case "end":
                running_transactions.remove(a.transactionid);
                committed_transactions.add(a.transactionid);
                //TODO: commit transaction at all sites that contain it
                break;

            case "W":
                //acquire write-lock for all sites containing the current variable
                for(int siteindex : sitescontainingvar.get(a.variable)){
                    sites.get(siteindex).locktable.put(a.variable, DBSite.Lock.WRITE);
                }
                break;

            case "R":
                //acquire read-lock for all sites containing the current variable
                for(int siteindex : sitescontainingvar.get(a.variable)){
                    sites.get(siteindex).locktable.put(a.variable, DBSite.Lock.READ);
                }
                break;

            default:
                System.out.println("ERROR: action contains an invalid type");
                throw new Exception();
        }
    }


    //TODO: print the states of the TM and all DBSites
    public static void querystate(){

    }

    //TODO: process a write action
    public static void processW(){

    }

    //TODO: process a read action
    public static void processR(Action a){
        //search for a site that contains the variable
        for(int site_i : sites.keySet()){
            if(sites.get(site_i).variables.contains(a.variable)){
                System.out.println(a.toString() + " from site ");
            }
        }
    }


    //dump the committed values of all copies of all variables at all sites, sorted by site
    public static void dump(){

    }

    public static void dump(DBSite i){

    }

    public static void dump(int i){

    }




}









