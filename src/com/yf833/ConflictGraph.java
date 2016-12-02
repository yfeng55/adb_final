package com.yf833;
import java.util.ArrayList;


public class ConflictGraph {

    //graph is represented by an array of lists
    public ArrayList<ArrayList<Integer>> adj_list;

    public ConflictGraph(){
        this.adj_list = new ArrayList();
    }


    //add another transaction to the graph
    public void addTransac(int transac_id){

        int num_lists_to_add = transac_id - adj_list.size();

        for(int i=0; i<num_lists_to_add; i++){
            this.adj_list.add(new ArrayList<Integer>());
        }
    }

    //add edge from node1 --> node2
    //nodes are represented as transac_id-1
    public void addEdge(int node1, int node2){
        this.adj_list.get(node1).add(node2);
    }

}
