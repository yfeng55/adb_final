package com.edu.nyu.cs.adb.project;

import java.util.ArrayList;

public class Util {

  /**
   * helper functions for parsing input strings
   * 
   * @param inputstring String to be parsed
   * @param time time of parsing
   * @return Action converted from string
   * @author Abhineet & Yijie
   */
  public static Action strToAction(String inputstring, int time) {

    // the substring inside the parentheses
    String inputargs = inputstring.substring(inputstring.indexOf('('), inputstring.indexOf(')'));
    String[] inputargsarr = inputargs.split(",");

    String actiontype = inputstring.substring(0, inputstring.indexOf('('));
    String transactionid;
    String variableid;
    String value;

    Action newaction = null;

    // check the number of args to determine which type of Action to create //

    // create a begin() or end() action
    if (inputargsarr.length == 1) {
      transactionid = inputargsarr[0].replaceAll("\\D+", "").trim();

      // dump() case
      if (transactionid.equals("")) {
        newaction = new Action(actiontype);
      } else {
        if (actiontype.equalsIgnoreCase("begin") || actiontype.equalsIgnoreCase("beginRO")
            || actiontype.equalsIgnoreCase("end")) {
          // begin() / end() action
          newaction = new Action(actiontype, Integer.parseInt(transactionid), time);
        } else {
          // fail() / recover() action
          newaction = new Action(actiontype, Integer.parseInt(transactionid));
        }
      }
    }
    // create a R() action
    else if (inputargsarr.length == 2) {
      transactionid = inputargsarr[0].replaceAll("\\D+", "").trim();
      variableid = inputargsarr[1].replaceAll("\\D+", "").trim();
      newaction = new Action(actiontype, Integer.parseInt(transactionid), time,
          Integer.parseInt(variableid));
    }
    // create a W() action
    else if (inputargsarr.length == 3) {
      transactionid = inputargsarr[0].replaceAll("\\D+", "").trim();
      variableid = inputargsarr[1].replaceAll("\\D+", "").trim();
      value = inputargsarr[2].replaceAll("\\D+", "").trim();
      newaction = new Action(actiontype, Integer.parseInt(transactionid), time,
          Integer.parseInt(variableid), Integer.parseInt(value));
    } else {
      System.out
          .println("ERROR: could not create a new Action, check that the input is well-formed");
      throw new ExceptionInInitializerError();
    }
    return newaction;
  }

  /**
   * check if a locktable entry is available (i.e. we can acquire a lock for that variable at that
   * site)
   * 
   * @param lock_entries List of lock entries
   * @param requestedtype type of requested lock
   * @param transac_id id of transaction holding the lock
   * @return true if lock can be acquired else false
   * @throws Exception
   * @author Abhineet & Yijie
   */
  public static boolean canAcquire(ArrayList<LockEntry> lock_entries, String requestedtype,
      int transac_id) throws Exception {

    // check if lock_entries is null, if so, return true (no lock has ever been set)
    if (lock_entries == null) {
      return true;
    }

    // if there is only one Read lock in the list, and it has the same transac_id as the requested
    // lock
    // in this case, upgrade the read lock to a write lock (overwrite it)
    if (lock_entries.size() == 1) {
      if (lock_entries.get(0).transac_id == transac_id) {
        return true;
      }
    }

    // this loop is guaranteed to return after one iteration but we only need to check the first
    // entry since
    // lock_entries with multiple locks will only be reads
    for (LockEntry lock_entry : lock_entries) {

      if (requestedtype.equals("READ") && lock_entry.type.equals("READ")) {
        return true;
      } else if (requestedtype.equals("WRITE") && lock_entry.type.equals("READ")
          && transac_id != lock_entry.transac_id) {
        return false;
      } else if (lock_entry.type == null) {
        return true;
      } else if (lock_entry.type.equals("WRITE") && lock_entry.type != null) {
        return false;
      } else {
        System.out.println("ERROR: did not find a matching case for canAcquire()");
        throw new Exception();
      }
    }
    return true;
  }

  /**
   * fill an ArrayList with N zeroes
   * 
   * @param arr array to be filled
   * @param n number of zeros
   * @author Abhineet & Yijie
   */
  public static void fillwithNZeroes(ArrayList<Integer> arr, int n) {
    int numzeroes = n - arr.size();
    for (int i = 0; i < numzeroes; i++) {
      arr.add(0);
    }
  }

}
