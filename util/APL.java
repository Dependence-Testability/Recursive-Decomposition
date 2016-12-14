package util;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class APL {

  public static <T> double[] compute(Graph<T> graph, T u, T v) {
    System.out.println("Graph: " + graph.toMapString());
    Node<T> start = graph.getNode(u);
    Node<T> end = graph.getNode(v);
    
    if (start == null || end == null) {
      throw new IllegalArgumentException();
    }
    return compute(graph, start, end);
  }

  public static <T> double[] compute(Graph<T> graph, Node<T> u, Node<T> v) {
    double[] result = computeHelper(graph, u, v);
    result[1] = result[1] / result[0];
    return result;
  }

  public static <T> double[] computeHelper(Graph<T> graph, Node<T> u, Node<T> v) {
    TarjanSCC<T> tarjan = new TarjanSCC<>(graph);
    System.out.println("Tarjan's Algorithm: " + tarjan);
    List<SCC<T>> sccs = tarjan.getSCCs();

    // graph.size might no longer be a reliable way to tell sccs.size()
    if (sccs.size() == graph.size()) {
      return PathFinder.superDagTraversal(graph, u, v);
    } else {
      // System.out.println("Constructing Supergraph");
      Graph<T> supergraph = new Graph<>();
      List<Node<T>> nodes = populateGraph(supergraph, sccs, u, v);
      u = nodes.get(0);
      v = nodes.get(1);
      System.out.println("SuperGraph: \n" + supergraph);
      decomposeGraph(supergraph, sccs, u, v);
      // System.out.println("SuperGraph: \n" + supergraph);
      return PathFinder.superDagTraversal(supergraph, u, v);
    }
  }

  private static <T> void decomposeGraph(Graph<T> graph, List<SCC<T>> sccs, Node<T> u, Node<T> v) {
    for (SCC<T> scc : sccs) {
      for (Node<T> entryNode : scc.getEntryNodes()) {
        for (Node<T> exitNode : scc.getExitNodes()) {
          System.out.println("X Node: " + entryNode);
          System.out.println("Y Node: " + exitNode);
          if (!entryNode.equalValue(exitNode)) {
            Graph<T> subgraph = new Graph<>();
            List<Node<T>> nodes = makeSubgraph(subgraph, scc, entryNode, exitNode);
            // add edge with value found in APL calculation
            double[] values = APL.computeHelper(subgraph, nodes.get(0), nodes.get(1));
            System.out.println("Values: " + values[0] + " " + values[1]);
            System.out.println("Adding super edge between " + entryNode + " and " + exitNode);

            // split
            // check if exit should be split
            // if so, split, make edge between split parts, create edge between entry and new exit
            // move cross edge to that new node
            // else, add super edge
            Node<T> x = graph.findEntryNode(entryNode.getValue());
            Node<T> y = graph.findExitNode(exitNode.getValue());
            graph.addSuperEdge(entryNode, exitNode, values[0], values[1]);
          }
        }
      }
    }
  }

  // subgraph = copy of SCC with (*, x), (y, *), crossEdges, and nodes with no edges removed
  // does not include split node of y. Fakes that you are trying to get from x to y.splitNode
  private static <T> List<Node<T>> makeSubgraph(Graph<T> subgraph, SCC<T> scc, Node<T> x, Node<T> y) {
    Node<T> originalY = y;
    y = y.getSplitNode() != null ? y.getSplitNode() : y;
    for (Node<T> node : scc.getNodes()) {
      if (!node.equals(y)) {
        for (Node<T> adjNode : node.getAdjacents()) {
          System.out.println("Node: " + node + " adj: " + adjNode);
          if (scc.contains(adjNode) && !adjNode.equals(x)) {
            if (node.equalValue(adjNode)) {
              // do not add self (so it doesn't confuse path finder)? this might break supergraph??
              // subgraph.addSuperEdge(node, adjNode, 1.0, 0.0);
            } else {
              System.out.println("Adding edge " + node + " - " + adjNode);
              subgraph.addEdge(node, adjNode); // adding edge will also create the nodes
            }
          }
        }
      }
    }

    System.out.println("Subgraph:\n" + subgraph);
    Node<T> start = subgraph.findNode(x);
    Node<T> end = subgraph.findNode(y);
    if (start == null || end == null) {
      throw new IllegalArgumentException();
    }
    return new ArrayList<>(Arrays.asList(start, end));
  }

  private static <T> List<Node<T>> populateGraph(Graph<T> graph, List<SCC<T>> sccs, Node<T> u, Node<T> v) {
    List<Node<T>> nodes = addSpecialNodes(graph, sccs, u, v);
    addCrossEdges(graph, sccs);
    addSelfEdges(graph, sccs);
    return nodes;
  }

  private static <T> List<Node<T>> addSpecialNodes(Graph<T> graph, List<SCC<T>> sccs, Node<T> u, Node<T> v) {
    for (SCC<T> scc : sccs) {
      // Flagging the nodes to be considered as entry or exit
      if (scc.contains(u)) {
        u.setEntry(true);
      }
      if (scc.contains(v)) {
        v.setExit(true);
      }

      int i = 0;
      for (Node<T> entryNode : scc.getEntryNodes()) {
        // System.out.println("Adding " + entryNode + " to the graph");
        Node<T> n = graph.addNode(entryNode);
      }
      int j = 0;
      for (Node<T> exitNode : scc.getExitNodes()) {
        // System.out.println("Adding " + exitNode + " to the graph");
        Node<T> n = graph.addNode(exitNode);
      }
    }
    Node<T> entryU = graph.findEntryNode(u.getValue());
    Node<T> exitV = graph.findExitNode(v.getValue());
    return new ArrayList<>(Arrays.asList(entryU, exitV));
  }

  private static <T> void addCrossEdges(Graph<T> graph, List<SCC<T>> sccs) {
    for (SCC<T> scc : sccs) {
      for (Node<T> exitNode : scc.getExitNodes()) {
        for (Node<T> adjNode : exitNode.getAdjacents()) {
          // if (!scc.contains(adjNode) && graph.containsNode(adjNode.getValue())) {
          if (!scc.contains(adjNode)) {
            graph.addEdge(exitNode, adjNode);
          }
        }
      }
    }
  }

  private static <T> void addSelfEdges(Graph<T> graph, List<SCC<T>> sccs) {
    for (SCC<T> scc : sccs) {
      for (Node<T> entryNode : scc.getEntryNodes()) {
        Node<T> splitNode = entryNode.getSplitNode();
        if (splitNode != null) {
          graph.addSuperEdge(entryNode, splitNode, 1.0, 0.0);
        }
      }
    }
  }

}
