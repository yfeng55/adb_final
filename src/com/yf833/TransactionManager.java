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

        // (1) initialize sites


        // (2) initialize variables and create copies at sites

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
