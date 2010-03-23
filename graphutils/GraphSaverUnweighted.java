package graphutils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

public class GraphSaverUnweighted {

	public static void saveGraph(Graph g, File f) throws FileNotFoundException,SecurityException {
		PrintStream p = new PrintStream(new FileOutputStream(f));
		ArrayList<GraphEdge> edgemarks = new ArrayList<GraphEdge>();
		try {
			Iterator<GraphNode> n = g.getNodeIterator();
			int count = 1;
			while (n.hasNext()) {
				GraphNode node = n.next();
				ListIterator<GraphEdge> e = node.getEdgeIterator();
				while (e.hasNext()) {
					GraphEdge edge = e.next();
					if (g.getNodeById(edge.getTarget().getId()) == null) {
						e.remove();
						continue;
					}
					if (!edgemarks.contains(edge))
						p.println("" + (node.getId()+1) + " " + (edge.getTarget().getId()+1));
					edgemarks.add(edge.getTarget().getEdgeByTargetId(node.getId()));
					edgemarks.add(edge);
				}
				if (count%100 == 0) System.out.println("Saving. . ." + count++);
			}
		} finally {
			p.close();
		}
	}
}
