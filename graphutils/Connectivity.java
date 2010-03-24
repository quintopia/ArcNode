package graphutils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Stack;

public class Connectivity {
	private Graph g;
	private ArrayList<GraphNode> mark = new ArrayList<GraphNode>();
	public Connectivity(Graph g) {
		this.g=g;

	}


	public GraphNode[] getComponentSample() {
		mark = new ArrayList<GraphNode>();
		Iterator<GraphNode> i = g.getNodeIterator();
		ArrayList<GraphNode> out = new ArrayList<GraphNode>();
		while (i.hasNext()) {
			GraphNode g = i.next();
			if (!mark.contains(g)) {
				getConnectedComponent(g);
				out.add(g);				
			}
		}
		GraphNode[] outa = new GraphNode[out.size()];
		return out.toArray(outa);
	}

	public Graph getConnectedComponent(GraphNode gn) {
		return getConnectedComponent(gn,false);
	}

	private Graph getConnectedComponent(GraphNode gn, boolean remove) {
		return getConnectedComponent(gn, remove, -1);
	}

	public boolean isConnected(GraphNode gn, int k) {
		return getConnectedComponent(gn,false,k).size()>=k;
	}

	private Graph getConnectedComponent(GraphNode gn, boolean remove, int maxSize) {
		if (!g.contains(gn)) return null;
		mark = new ArrayList<GraphNode>();
		Stack<GraphNode> stack = new Stack<GraphNode>();
		Graph out = new Graph();
		stack.add(gn);
		mark.add(gn);
		while (stack.size()>0) {
			GraphNode c = stack.pop();
			try {
				out.addNode(c.getObject(), c.getX(), c.getY(), c.getId());
			} catch (IdCollisionException e1) {
				//this just means we already added it, so we cool
			}
			ListIterator<GraphEdge> l = c.getEdgeIterator();
			while (l.hasNext()) {
				GraphEdge e = l.next();
				if (!out.addEdge(c.getId(), e.getTarget().getId(), e.getWeight())) {
					try {
						out.addNode(e.getTarget().getId());
					} catch (IdCollisionException ide) {
						//it's a new graph!
					}
					out.addEdge(c.getId(), e.getTarget().getId(), e.getWeight());
				}
				GraphNode d = e.getTarget();
				if (!mark.contains(d)) {
					stack.add(d);
					mark.add(d);
				}
			}
			if (remove)
				g.removeNode(c);
			if (maxSize>0&&out.size()>=maxSize) break;
		}
		return out;
	}




	/**
	 * Find all connected components, return the largest
	 * 
	 * @param g
	 *            a graph
	 * @return a graph containing the largest connected component of g
	 */
	public static Graph getLargestConnectedComponent(Graph g) {
		Graph working = Graph.deepCopy(g);
		Connectivity workingCC = new Connectivity(working);
		Graph out = new Graph();
		while (working.size() > 0) {
			GraphNode curNode = working.getNode();
			Graph temp = workingCC.removeConnectedComponent(curNode);
			if (temp.size() > out.size())
				out = temp;
		}
		return out;
	}

	public ArrayList<Graph> getAllComponents() {
		ArrayList<Graph> out = new ArrayList<Graph>();
		Graph working = Graph.deepCopy(g);
		Connectivity workingCC = new Connectivity(working);
		while (working.size() > 0) {
			GraphNode curNode = working.getNode();
			out.add(workingCC.removeConnectedComponent(curNode));
		}
		return out;
	}

	public boolean isConnected() {
		// get a connected component
		Graph g = getConnectedComponent(this.g.getNode());
		// check if it's the whole graph
		return (g.size() >= this.g.size());
	}
	public static boolean isConnected(Graph g) {
		return new Connectivity(g).isConnected();
	}
	public boolean isConnected(GraphNode x, GraphNode y) {
		return (getConnectedComponent(x).getNodeById(y.getId()) != null);
	}

	public int numComponents() {
		return getComponentSample().length;
	}

	public Graph removeConnectedComponent(GraphNode gn) {
		return getConnectedComponent(gn,true);
	}

	public GraphNode[] getBiconnectedEdge() {
		GraphNode[] out = {null,null};
		GraphNode gn = g.getNode();
		mark = new ArrayList<GraphNode>();
		Stack<GraphNode> stack = new Stack<GraphNode>();
		stack.add(gn);
		mark.add(gn);
		Hashtable<GraphNode,GraphNode> parent = new Hashtable<GraphNode,GraphNode>();
		while (stack.size()>0) {
			GraphNode c = stack.pop();
			ListIterator<GraphEdge> l = c.getEdgeIterator();
			while (l.hasNext()) {
				GraphNode d = l.next().getTarget();
				if (!mark.contains(d)) {
					stack.add(d);
					mark.add(d);
					parent.put(d, c);
				} else {
					if (parent.get(c)!=d) {
						out[0]=c;
						out[1]=d;
						break;
					}
				}
			}
			if (out[0]!=null) break;
		}
		return out;
	}
}
