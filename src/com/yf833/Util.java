package com.yf833;


import java.util.ArrayList;

public class Util {

    //helper functions for parsing input strings
    public static Action strToAction(String inputstring, int time){

        //the substring inside the parentheses
        String inputargs = inputstring.substring(inputstring.indexOf('('), inputstring.indexOf(')'));
        String[] inputargsarr = inputargs.split(",");

        String actiontype = inputstring.substring(0, inputstring.indexOf('('));
        String transactionid;
        String variableid;
        String value;

        Action newaction = null;

        //check the number of args to determine which type of Action to create //

        //create a begin() or end() action
        if(inputargsarr.length == 1){
            transactionid = inputargsarr[0].replaceAll("\\D+","").trim();
            newaction = new Action(actiontype, Integer.parseInt(transactionid), time);
        }
        //create a R() action
        else if(inputargsarr.length == 2){
            transactionid = inputargsarr[0].replaceAll("\\D+","").trim();
            variableid = inputargsarr[1].replaceAll("\\D+","").trim();
            newaction = new Action(actiontype, Integer.parseInt(transactionid), time, Integer.parseInt(variableid));
        }
        //create a W() action
        else if(inputargsarr.length == 3){
            transactionid = inputargsarr[0].replaceAll("\\D+","").trim();
            variableid = inputargsarr[1].replaceAll("\\D+","").trim();
            value = inputargsarr[2].replaceAll("\\D+","").trim();
            newaction = new Action(actiontype, Integer.parseInt(transactionid), time, Integer.parseInt(variableid), Integer.parseInt(value));
        }
        else{
            System.out.println("ERROR: could not create a new Action, check that the input is well-formed");
            throw new ExceptionInInitializerError();
        }

        return newaction;
    }



    //TODO: check for deadlocks by checking if there is a cycle in the transactions graph
    public static boolean isDeadlocked(ArrayList<Action> currentactions){
        return true;
    }


}















