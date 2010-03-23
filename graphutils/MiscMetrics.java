package graphutils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;

public class MiscMetrics {
	public static double clustering(Graph gin, int k) {
		int tricount=0,nodecount=0;
		Iterator<GraphNode> i = gin.getNodeIterator();
		while (i.hasNext()) {
			GraphNode g = i.next();
			if (g.numEdges()==k) {
				GraphNode[] neighbors = g.getNeighbors();
				for (int j=0;j<neighbors.length;j++) {
					for (int l=j+1;l<neighbors.length;l++) {
						if (neighbors[j].getEdgeByTargetId(neighbors[l].getId())!=null) {
							tricount++;
						}
					}
				}
				nodecount++;
			}
		}
		if (nodecount==0||k<2) return 0;
		else return tricount/(nodecount*k*(k-1)/2);
	}
	
	public static int likelihood(Graph g) {
		int total=0;
		Iterator<GraphNode> i = g.getNodeIterator();
		while (i.hasNext()) {
			GraphNode n = i.next();
			ListIterator<GraphEdge> ei = n.getEdgeIterator();
			while (ei.hasNext()) {
				GraphEdge e = ei.next();
				total += n.numEdges()*e.getTarget().numEdges();
			}
		}
		return total/2;
	}
	
	public Hashtable<GraphNode,Double> betweenness(Graph gin) {
		Iterator<GraphNode> i = gin.getNodeIterator();
		Hashtable<GraphNode,Double> bc = new Hashtable<GraphNode,Double>();
		while (i.hasNext()) {
			bc.put(i.next(),0.0);
		}
		while (i.hasNext()) {
			GraphNode g = i.next();
			System.out.println("Node "+g.getId());
			BreadthFirstSearch bfs = new BreadthFirstSearch(gin,g);
			Iterator<GraphNode> i2 = gin.getNodeIterator();
			while (i2.hasNext()) {
				bfs.setD(i2.next(),0);
			}
			while(!bfs.getStack().empty()) {
				GraphNode y = bfs.getStack().pop();
				ListIterator<GraphNode> sp = y.getSPIterator();
				while (sp.hasNext()) {
					GraphNode v = sp.next();
					bfs.setD(v,bfs.getD(v)+bfs.getSigma(v)/bfs.getSigma(y)*(1+bfs.getD(y)));
					if (y!=g) {
						bc.put(y,bc.get(y)+bfs.getD(y));
					}
				}
			}
		}
		return bc;
	}
	
	public double[] bcsort(Hashtable<GraphNode,Double> bc) {
		ArrayList<GraphNode> nodelist = new ArrayList<GraphNode>(bc.keySet());
		Collections.sort(nodelist,new DegreeBCComparator(bc));
		ListIterator<GraphNode> i = nodelist.listIterator();
		double out[] = new double[nodelist.size()];
		int j=0;
		while (i.hasNext()) {
			out[j++]=bc.get(i.next());
		}
		return out;
	}
}
