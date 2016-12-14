package util;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public class SCC<T> {

  List<Node<T>> entryNodes;
  List<Node<T>> exitNodes;
  List<Node<T>> nodes;
  boolean isEntryNodesComputed;
  boolean isExitNodesComputed;

  public SCC() {
    entryNodes = new ArrayList<>();
    exitNodes = new ArrayList<>();
    nodes = new ArrayList<>();
    isEntryNodesComputed = false;
    isExitNodesComputed = false;
  }

  public void add(Node<T> node) {
    if (!contains(node)) {
      nodes.add(node);
    }
  }

  public boolean contains(Node<T> node) {
    return nodes.contains(node);
  }

  public void computeSpecialNodes() {
    // Computes Entry Nodes
    // Entry Node: Has at least one incoming edge from outside self's SCC
    for (Node<T> node : nodes) {
      for (Edge<T> inEdge : node.getIncomingEdges()) {
        Node<T> inAdjNode = inEdge.getEntryNode();
        if (!nodes.contains(inAdjNode)) {
          // System.out.println("Adding from compute");
          // addEntryNode(node);
          node.setEntry(true);
          node.addCrossIncoming(inAdjNode, inEdge);
        }
      }
    }

    // Computes Exit Nodes
    // Exit Node: Has at least one outgoing edge to outside self's SCC
    for (Node<T> node : nodes) {
      for (Edge<T> outEdge : node.getOutgoingEdges()) {
        Node<T> adjNode = outEdge.getExitNode();
        if (!nodes.contains(adjNode)) {
          node.setExit(true);
          node.addCrossOutgoing(adjNode, outEdge);
        }
      }
    }

    // List of newly created exit nodes created from splitting entry/exit nodes
    List<Node<T>> exitNodesToAdd = new ArrayList<>();
    List<Node<T>> entryNodesToAdd = new ArrayList<>();

    for (Node<T> node : nodes) {
      // System.out.println("node: " + node);
      // Node is entry/exit node, will be split
      // Original node is kept as entry node, split node will be exit node
      if (node.isEntryNode() && node.isExitNode()) {
        // System.out.println("Entry and Exit");
        Node<T> exitNode = node.exitTransfer();
        // addEntryNode(node);
        entryNodesToAdd.add(node);
        exitNodesToAdd.add(exitNode);
      } else if (!node.isEntryNode() && node.isExitNode()) {
        // System.out.println("Exit");
        addExitNode(node);
      } else if (node.isEntryNode() && !node.isExitNode()) {
        // System.out.println("Entry");
        addEntryNode(node);
      }
    }

    for (Node<T> node : entryNodesToAdd) {
      addEntryNode(node);
    }

    for (Node<T> node : exitNodesToAdd) {
      add(node);
      // System.out.println("Adding from compute");
      addExitNode(node);
    }

    isEntryNodesComputed = true;

  }

  public List<Node<T>> getEntryNodes() {
    if (!isEntryNodesComputed) {
      computeSpecialNodes();
    }
    return entryNodes;
  }

  public List<Node<T>> getExitNodes() {
    if (!isExitNodesComputed) {
      computeSpecialNodes();
    }
    return exitNodes;
  }

  public List<Node<T>> getNodes() {
    return nodes;
  }

  public void addEntryNode(Node<T> node) {
    // System.out.println("EntryNode: " + node);
    if (!entryNodes.contains(node)) {
      entryNodes.add(node);
      node.setEntry(true);
    }
  }

  public void addExitNode(Node<T> node) {
    // System.out.println("ExitNode: " + node);
    if (!exitNodes.contains(node)) {
      exitNodes.add(node);
      node.setExit(true);
    }
  }

  public boolean containsEntryNode(Node<T> node) {
    if (!isEntryNodesComputed) {
      getEntryNodes();
    }
    return entryNodes.contains(node);
  }

  public boolean containsExitNode(Node<T> node) {
    if (!isExitNodesComputed) {
      getExitNodes();
    }
    return exitNodes.contains(node);
  }

  public int size() {
    return nodes.size();
  }

  @Override
  public String toString() {
    return nodes.toString();
  }

}
