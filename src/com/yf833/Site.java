package com.yf833;


import java.util.ArrayList;
import java.util.HashMap;

//each site represents a separate database -- it has its own lock table and variable copies
public class Site {

    // the id for this site
    public int id;
    // the locktable for this site
    public HashMap<Integer, ArrayList<String>> locktable;


    public Site(int id){
        this.id = id;
        this.locktable = new HashMap<>();
    }


}
