package util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;

public class Graph<T> {

  private Map<T, Node<T>> nodeMap;
  private Set<Node<T>> nodes;

  public Graph() {
    nodes = new HashSet<>();
    nodeMap = new HashMap<>();
  }

  //------------------------Old Methods----------------------------//

  public Set<T> getNodeValues() {
    return nodeMap.keySet();
  }

  public Node<T> getNode(T value) {
    return nodeMap.get(value);
  }

  public void addNewEdge(T start, T end) {
    Node<T> s = findOrConstructNode(start);
    Node<T> e = findOrConstructNode(end);
    Edge<T> edge = s.addAdjacent(e);
    e.addIncomingAdjacent(s, edge);
  }

  private Node<T> findOrConstructNode(T value) {
    if (!nodeMap.containsKey(value)) {
      Node<T> node = new Node<>(value);
      nodeMap.put(value, node);
    }
    return getNode(value);
  }

  public int mapSize() {
    return nodeMap.size();
  }

  //---------------------------------------------------------------//

  public Node<T> addNode(Node<T> node) {
    Node<T> nNode = addNode(node.getValue());
    nNode.setEntry(node.isEntryNode());
    nNode.setExit(node.isExitNode());
    nodes.add(nNode);
    System.out.println(nNode + " adj " + nNode.getAdjacents());
    return nNode;
  }

  public Node<T> addNode(T value) {
    Node<T> node = new Node<>(value);
    nodes.add(node);
    return node;
  }

  public void addEdge(Node<T> start, Node<T> end) {
    Node<T> s = findOrConstructNode(start);
    Node<T> e = findOrConstructNode(end);
    Edge<T> edge = s.addAdjacent(e);
    e.addIncomingAdjacent(s, edge);
  }

  public void addEdge(T start, T end) {
    Node<T> s = addNode(start);
    Node<T> e = addNode(end);
    Edge<T> edge = s.addAdjacent(e);
    e.addIncomingAdjacent(s, edge);
  }

  public void addSuperEdge(Node<T> start, Node<T> end, double num, double length) {
    Node<T> s = findOrConstructNode(start);
    Node<T> e = findOrConstructNode(end);
    Edge<T> edge = s.addSuperAdjacent(e, num, length);
    e.addIncomingAdjacent(s, edge);
  }

  public void addSuperEdge(T start, T end, double num, double length) {
    Node<T> s = addNode(start);
    Node<T> e = addNode(end);
    Edge<T> edge = s.addSuperAdjacent(e, num, length);
    e.addIncomingAdjacent(s, edge);
  }

  private Node<T> findOrConstructNode(Node<T> node) {
    System.out.println("Looking for " + node);
    if (node.isEntryNode()) {
      Node<T> found = findEntryNode(node.getValue());
      if (found != null) {
        return found;
      }
    } else if (node.isExitNode()) {
      Node<T> found = findExitNode(node.getValue());
      if (found != null) {
        return found;
      }
    } else {
      for (Node<T> n : nodes) {
        if (n.getValue() == node.getValue()) {
          return n;
        }
      }
    }

    System.out.println("Creating " + node);
    return addNode(node);
  }

  public Node<T> findNode(Node<T> node) {
    for (Node<T> n : nodes) {
      if (n.equals(node)) {
        return n;
      }
    }
    return null;
  }

  public Node<T> findEntryNode(T value) {
    for (Node<T> node : nodes) {
      if (node.getValue() == value) {
        if (node.isEntryNode()) {
          return node;
        }
      }
    }
    System.out.println("Graph#findEntryNode not found " + value);
    return null;
  }

  public Node<T> findExitNode(T value) {
    for (Node<T> node : nodes) {
      if (node.getValue() == value) {
        if (node.isExitNode()) {
          return node;
        }
      }
    }
    System.out.println("Graph#findExitNode not found " + value);
    return null;
  }

  public Set<Node<T>> getNodes() {
    return nodes;
  }

  public int size() {
    return nodes.size();
  }

  // public boolean containsNode(T value) {
  //   return nodeMap.containsKey(value);
  // }

  public String toMapString() {
    StringBuilder strBldr = new StringBuilder();
    for (Map.Entry<T, Node<T>> entry : nodeMap.entrySet()) {
      strBldr.append(entry.getValue());
      strBldr.append(": ");
      strBldr.append(entry.getValue().getAdjacents());
      strBldr.append("\n");
    }
    return strBldr.toString();
  }

  @Override
  public String toString() {
    StringBuilder strBldr = new StringBuilder();
    for (Node<T> node : nodes) {
      strBldr.append(node);
      strBldr.append(": ");
      strBldr.append(node.getAdjacents());
      strBldr.append("\n");
    }
    return strBldr.toString();
  }
}
