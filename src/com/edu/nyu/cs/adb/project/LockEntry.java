package com.edu.nyu.cs.adb.project;

/**
 * Class to maintain the type of lock and the transaction holding that lock
 * 
 * @author Abhineet & Yijie
 */
public class LockEntry {

  // read, write, none
  public String type;
  public int transac_id;

  public LockEntry(int transactionid, String type) {
    this.type = type;
    this.transac_id = transactionid;
  }

  @Override
  public String toString() {
    String output = "";
    output += "T" + this.transac_id + this.type;
    return output;
  }

  // compare if two LockEntries are equal
  @Override
  public boolean equals(Object le2) {
    boolean is_same = false;
    if (le2 != null && le2 instanceof LockEntry) {

      LockEntry lockentry2 = (LockEntry) le2;

      is_same = (this.transac_id == lockentry2.transac_id && this.type.equals(lockentry2.type));
    }
    return is_same;
  }
}
