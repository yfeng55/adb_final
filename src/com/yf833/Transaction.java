package com.yf833;

import java.util.HashMap;
import java.util.HashSet;

public class Transaction {
    public enum Status {
        RUNNING, WAITING, ABORTED, COMMITED
    }

    public enum Type {
        DEFAULT, READONLY
    }

    int transactionID;
    int startTime;
    boolean isReadOnly;
    HashSet<Integer> commitedVars;
    HashSet<Integer> uncommitedVars;

    // a map to snapshot all the variables before the readonly transaction begin.
    HashMap<Integer, Integer> readOnlyMap;

}