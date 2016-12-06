package com.edu.nyu.cs.adb.project;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

/**
 * class to represent conflict graph used to detect cycles
 * 
 * @author Abhineet & Yijie
 */
public class ConflictGraph {

  // graph is represented by an array of lists (adjacency list representation)
  public ArrayList<ArrayList<Integer>> adj_list;

  public ConflictGraph() {
    this.adj_list = new ArrayList();
  }

  public ConflictGraph(int n) {
    this.adj_list = new ArrayList();
    for (int i = 0; i < n; i++) {
      this.adj_list.add(new ArrayList<Integer>());
    }
  }

  /**
   * add another transaction to the graph
   * 
   * @param transac_id transaction to be added
   * @author Abhineet & Yijie
   */
  public void addTransac(int transac_id) {

    int num_lists_to_add = transac_id - adj_list.size();

    for (int i = 0; i < num_lists_to_add; i++) {
      this.adj_list.add(new ArrayList<Integer>());
    }
  }

  /**
   * add edge from node1 --> node2
   * nodes are represented as transac_id-1
   * 
   * @param node1 edge from node
   * @param node2 edge to node
   * @author Abhineet & Yijie
   */
  public void addEdge(int node1, int node2) {
    if (!this.adj_list.get(node1).contains(node2)) {
      this.adj_list.get(node1).add(node2);
    }
  }

  /**
   * traverse the nodes of the conflict graph in DFS order
   * 
   * @param node node to be traversed
   * @param visited array of visited nodes
   * @param component_members maintain a set of member nodes for the current component
   * @author Abhineet & Yijie
   */
  private void dfsTraverse(int node, boolean visited[], HashSet<Integer> component_members) {

    // track visited nodes as we traverse
    visited[node] = true;
    component_members.add(node);

    // DFS traverse all neighbors of this node
    int neighbor;
    Iterator<Integer> iter = adj_list.get(node).iterator();
    while (iter.hasNext()) {
      neighbor = iter.next();
      if (!visited[neighbor]) {
        dfsTraverse(neighbor, visited, component_members);
      }
    }
  }

  /**
   * return the transpose of the current conflict graph
   * 
   * @return transposed graph
   * @author Abhineet & Yijie
   */
  private ConflictGraph getTranspose() {
    ConflictGraph gr_transpose = new ConflictGraph(this.adj_list.size());

    for (int i = 0; i < this.adj_list.size(); i++) {
      Iterator<Integer> iter = adj_list.get(i).listIterator();
      while (iter.hasNext()) {
        gr_transpose.adj_list.get(iter.next()).add(i);
      }
    }
    return gr_transpose;
  }

  /**
   * fill the stack with nodes in the correct order for the Kosaraju algorithm
   * 
   * @param node node to be filled
   * @param visited visited array matrix
   * @param stack stack to be filled
   * @author Abhineet & Yijie
   */
  private void fillStack(int node, boolean visited[], Stack stack) {
    // initialize nodes to visited
    visited[node] = true;

    Iterator<Integer> iter = adj_list.get(node).iterator();
    while (iter.hasNext()) {
      int n = iter.next();
      if (!visited[n]) {
        fillStack(n, visited, stack);
      }
    }

    stack.push(new Integer(node));
  }

  /**
   * from a list of strongly connected components, return the set of transactions that form a cycle
   * 
   * @return set of transactions forming cycle
   * @author Abhineet & Yijie
   */
  public HashSet<Integer> getCycle() {

    Stack stack = new Stack();

    // initialize nodes to unvisited
    boolean visited[] = new boolean[this.adj_list.size()];
    for (int i = 0; i < this.adj_list.size(); i++) {
      visited[i] = false;
    }

    // Fill vertices in stack according to their finishing
    // times
    for (int i = 0; i < this.adj_list.size(); i++) {
      if (!visited[i]) {
        fillStack(i, visited, stack);
      }
    }

    ConflictGraph gr = getTranspose();

    // Second DFS traversal
    // initialize all nodes to unvisited again
    for (int i = 0; i < this.adj_list.size(); i++) {
      visited[i] = false;
    }

    // keep a list of components that are found during second DFS
    ArrayList<HashSet<Integer>> components = new ArrayList<>();
    while (!stack.empty()) {
      int v = (int) stack.pop();
      HashSet<Integer> component = new HashSet<>();
      if (!visited[v]) {
        gr.dfsTraverse(v, visited, component);
      }
      components.add(component);
    }

    // if there is a component with a size greater than 1, we consider that a cycle and return it
    for (HashSet<Integer> component : components) {
      if (component.size() > 1) {
        return component;
      }
    }

    // if no transactions involved in a cycle, return an empty set
    return new HashSet<>();
  }

  /**
   * update the graph to reflect a committed transaction
   * 
   * @param transac_id id to be commited or aborted
   * @author Abhineet & Yijie
   */
  public void commit_or_abort(int transac_id) {

    transac_id = transac_id - 1;

    // clear the row for this transac_id
    this.adj_list.get(transac_id).clear();

    // remove all rows in the conflict graph that contain transac_id
    for (ArrayList<Integer> row : this.adj_list) {
      if (row.contains((Integer) transac_id)) {
        row.remove((Integer) transac_id);
        break;
      }
    }

  }

}
