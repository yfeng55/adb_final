package com.edu.nyu.cs.adb.project;

import java.util.HashMap;

/**
 * A class to maintain information about a particular transaction which includes its status, its
 * type, its ID and start time.
 * 
 * @author Abhineet & Yijie
 */
public class Transaction {

  public enum Status {
    RUNNING, WAITING, ABORTED, COMMITED
  }

  public enum Type {
    DEFAULT, READONLY
  }

  public int transactionID;
  public int startTime;
  public Type type;
  public Status status;

  // for read-only transactions only -- a copy of the datatable
  HashMap<Integer, Integer> data_table_from_lastcommit = null;

  public Transaction(int transac_id, int createtime, Type transac_type) {
    this.transactionID = transac_id;
    this.startTime = createtime;

    this.type = transac_type;

    // initialize status to running
    this.status = Status.RUNNING;

    if (transac_type == Type.READONLY) {
      data_table_from_lastcommit = new HashMap<Integer, Integer>();
    }
  }

  @Override
  public String toString() {
    String output = "";

    output += "T" + transactionID + "\n";
    output += "starttime: " + startTime + "\n";
    output += "type: " + this.type.toString() + "\n";
    output += "status: " + this.status.toString() + "\n";

    return output;
  }

  // compare if two Transactions are equal
  @Override
  public boolean equals(Object tr2) {
    boolean is_same = false;
    if (tr2 != null && tr2 instanceof Transaction) {

      Transaction transaction2 = (Transaction) tr2;

      is_same = (this.transactionID == transaction2.transactionID);
    }
    return is_same;
  }

}