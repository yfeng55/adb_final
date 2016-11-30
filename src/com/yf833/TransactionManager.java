package com.yf833;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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

    // list of transactions
    public static ArrayList<Action> transactions;
    // list of sites
    public static ArrayList<Site> sites;
    // a queue of transactions that are waiting
    public static ArrayList<Action> waiting;
    // keep track of the current time
    public static int time=0;



    public static void main(String[] args) throws FileNotFoundException {

        // (1) initialize sites (ids from 1 to N)
        sites = new ArrayList<>();
        for(int site_id=1; site_id<=NUM_SITES; site_id++){
            sites.add(new Site(site_id));
        }

        // (2) initialize variables and create copies at sites
        for(int var_id = 1; var_id<=NUM_VARIABLES; var_id++){
            //if even variable, put in every site
            if(var_id%2 == 0){
                for(Site s : sites){
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
                String[] actionsarr = nextline.split(";");

                for(String s : actionsarr){
                    currentactions.add(Util.strToAction(s.trim(), time));
                }
            }

            //increment time after processing each line
            time++;
        }


        System.out.println();
    }


    //reads in the next line of input
    public static void processNextTransactions(Scanner scan){

    }


    //dump the committed values of all copies of all variables at all sites, sorted by site
    public static void dump(){

    }

    public static void dump(Site i){

    }

    public static void dump(int i){

    }




}
