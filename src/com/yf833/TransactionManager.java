package com.yf833;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;
import java.util.Scanner;


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
    public static ArrayList<DBSite> sites;

    // track running transactions, committed transactions, aborted transactions (store their ids in sets)
    public static HashSet<Integer> running_transactions = new HashSet<>();
    public static HashSet<Integer> committed_transactions = new HashSet<>();
    public static HashSet<Integer> aborted_transactions = new HashSet<>();

    // a queue of transactions that are waiting
    public static Queue<Action> waiting;



    public static void main(String[] args) throws Exception {

        // (1) initialize sites (ids from 1 to N)
        sites = new ArrayList<>();
        for(int site_id=1; site_id<=NUM_SITES; site_id++){
            sites.add(new DBSite(site_id));
        }

        // (2) initialize variables and create copies at sites
        for(int var_id = 1; var_id<=NUM_VARIABLES; var_id++){
            //if even variable, put in every site
            if(var_id%2 == 0){
                for(DBSite s : sites){
                    s.variables.add(var_id);
                }
            }
            //if odd variable, put in site (i%10+1)
            else{
                int insert_i = (var_id%10) + 1;
                sites.get(insert_i -1).variables.add(var_id);
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
                    processAction(a);
                }
            }

            //increment time after processing each line
            time++;
        }


        System.out.println();
    }



    //process a single begin(), end(), R(), or W() action
    public static void processAction(Action a) throws Exception {
        switch(a.type){
            case "begin":
                running_transactions.add(a.transactionid);
                break;
            case "end":
                running_transactions.remove(a.transactionid);
                committed_transactions.add(a.transactionid);
                break;
            case "W":
                break;
            case "R":
                break;
            default:
                System.out.println("ERROR: action contains an invalid type");
                throw new Exception();
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
