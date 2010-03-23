package graphutils;

public abstract class GraphDifference {
	public static double compare(Graph g1, Graph g2) {
		Graph g = Graph.unionById(g1, g2);
		int total = g.numEdges();
		int ptotal = g1.numEdges()+g2.numEdges()-total;
		return ptotal/(double)total;
	}
}
