package com.yf833;

import java.lang.reflect.Array;
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
    public static ArrayList<Transaction> transactions;
    // list of sites
    public static ArrayList<Site> sites;
    // a queue of transactions that are waiting
    public static ArrayList<Transaction> waiting;


    public static void main(String[] args) {

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


        System.out.println();
    }


    //reads in the next line of input
    public static void getNextTransaction(Scanner scan){

    }

    //dump the committed values of all copies of all variables at all sites, sorted by site
    public static void dump(){

    }

    public static void dump(Site i){

    }

    public static void dump(int i){

    }




}
