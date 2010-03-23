package graphutils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;

public class GraphSaver {

	public static void saveGraph(Graph g, File f) throws FileNotFoundException,SecurityException {
		ArrayList<GraphEdge> edgemarks = new ArrayList<GraphEdge>();
		
		PrintStream p = new PrintStream(new FileOutputStream(f));
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
						p.println("" + (node.getId()+1) + " " + (edge.getTarget().getId()+1) + " " + edge.getWeight());
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
