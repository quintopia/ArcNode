/*
 * Created on Nov 13, 2006
 *
 * Last modified: Feb. 2009
 */
package graphutils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;

public class Graph {
	private Hashtable<Integer, GraphNode> nodes = new Hashtable<Integer, GraphNode>();
	private int auto_id = 0;

	public Graph() {

	}

	public boolean addEdge(GraphNode source, GraphNode target, double weight) {
		if (source != null && target != null && nodes.contains(source)
				&& nodes.contains(target) && source.getEdgeByTargetId(target.getId())==null) {
			source.addEdge(target, weight);

			return true;
		} else return false;
	}

	public boolean addEdge(GraphNode source, GraphNode target, double weight,
			Object o) {
		if (source != null && target != null  && contains(source) && contains(target) && source.getEdgeByTargetId(target.getId())==null) {
			source.addEdge(target, weight, o);
			return true;
		} else return false;
	}

	public boolean addEdge(int node1, int node2, double weight) {
		return addEdge(getNodeById(node1), getNodeById(node2), weight);
	}

	public boolean addNEdge(int node1, int node2, double weight) {
		return addEdge(node1, node2, weight) && addEdge(node2, node1, weight);
	}

	public GraphNode addNode(GraphNode g) throws IdCollisionException {
		if (nodes.containsKey(new Integer(g.getId()))) {
			throw new IdCollisionException(
					"A node already exists with that ID.");
		}
		nodes.put(new Integer(g.getId()), g);

		if (g.getId() >= auto_id)
			auto_id = g.getId() + 1;
		return g;
	}

	public GraphNode addNode(Object o, double x, double y) {
		return new GraphNode(o, x, y, auto_id);
	}

	public GraphNode addNode(Object o, double x, double y, int id)
	throws IdCollisionException {
		GraphNode out = new GraphNode(o, x, y, id);
		if (nodes.containsKey(new Integer(id))) {
			throw new IdCollisionException(
					"A node already exists with that ID.");
		}
		if (id >= auto_id)
			auto_id = id + 1;
		nodes.put(new Integer(id), out);

		return out;
	}

	public GraphNode addNode(int id) throws IdCollisionException {
		GraphNode out = new GraphNode(id);
		if (nodes.containsKey(new Integer(id))) {
			throw new IdCollisionException(
			"A node already exists with that ID.");
		}
		nodes.put(new Integer(id), out);

		if (id >= auto_id)
			auto_id = id + 1;
		return out;
	}

	public GraphNode addNode() {
		boolean success = false;
		GraphNode out = null;
		while(!success) {
			try {
				out = addNode(auto_id++);
				success = true;
			} catch (IdCollisionException ide) {
				//keep on tryin'!
			}
		}
		return out;
	}

	public void addNodes(int numNodes) {
		for (int i = 0; i < numNodes; i++) {
			try {
				addNode(auto_id);
			} catch (IdCollisionException e) {
				// this shouldn't happen, but it's easy enough to increment
				// auto_id
				// and try again
				auto_id++;
				i--;
			}
		}
	}

	public void addAll(Collection<GraphNode> c) throws IdCollisionException {
		Iterator<GraphNode> i = c.iterator();
		while (i.hasNext()) {
			addNode(i.next());
		}
	}

	public double avgDegree() {
		return (double) numEdges() / size();
	}








	public static Graph completeGraph(int n) {
		Graph out = new Graph();
		out.addNodes(n);
		return out.completeGraph();
	}

	public Graph completeGraph() {
		Iterator<GraphNode> i = getNodeIterator();
		Graph out = Graph.deepCopy(this);
		while (i.hasNext()) {
			Iterator<GraphNode> j = getNodeIterator();
			GraphNode gn1 = i.next();
			while (j.hasNext()) {
				GraphNode gn2 = j.next();
				if (gn1.getId()<gn2.getId()) {
					out.addNEdge(gn1.getId(),gn2.getId(),1);
				}
			}
		}
		return out;
	}


	public boolean contains(GraphNode gn) {
		return nodes.contains(gn);
	}
	/**
	 * produce a copy of the topology of the input graph
	 * 
	 * @param g
	 *            a graph to copy
	 * @return a copy graph of the input graph's topology
	 */
	public static Graph deepCopy(Graph g) {
		Iterator<GraphNode> n = g.getNodeIterator();
		Graph out = new Graph();
		while (n.hasNext()) {
			GraphNode gn = n.next();
			try {
				out.addNode(gn.getId());
			} catch (IdCollisionException e) {
				// doesn't happen
			}
		}
		n = g.getNodeIterator();
		while (n.hasNext()) {
			GraphNode gn = n.next();
			ListIterator<GraphEdge> l = gn.getEdgeIterator();
			while (l.hasNext()) {
				GraphEdge e = l.next();
				out.addEdge(gn.getId(), e.getTarget().getId(), e.getWeight());
			}
		}
		return out;
	}


	public int degree(GraphNode gn) {
		if (nodes.contains(gn)) {
			ListIterator<GraphEdge> l = gn.getEdgeIterator();
			int count =0;
			while (l.hasNext())
				if (nodes.contains(l.next().getTarget()))
					count++;
			return count;
		} else
			return 0;
	}

	public int degree(Graph g) {
		Iterator<GraphNode> i = g.getNodeIterator();

		int count =0;
		while (i.hasNext()) {
			GraphNode gn = i.next();
			if (nodes.contains(gn)) {
				ListIterator<GraphEdge> l = gn.getEdgeIterator();

				while (l.hasNext()) {
					GraphEdge e = l.next();
					if (nodes.contains(e.getTarget())
							&&!g.nodes.contains(e.getTarget()))
						count++;
				}
			}
		}
		return count;
	}

	/**
	 * Basically performs a DFS from the input node, returns all nodes reached
	 * 
	 * @param gn
	 *            a graph node in the connected component
	 * @return a graph with the nodes reachable from gn
	 */


	public int[] getDegreeDistribution() {
		int[] dist = new int[maxDegree()+1];
		for (int i=0;i<dist.length;i++) dist[i]=0;
		Iterator<GraphNode> i = getNodeIterator();
		while (i.hasNext()) {
			GraphNode g = i.next();
			dist[g.numEdges()]++;
		}
		return dist;
	}

	public Hashtable<Integer,Integer> getDegreeTable() {
		Iterator<GraphNode> i = getNodeIterator();
		Hashtable<Integer,Integer> out = new Hashtable<Integer,Integer>();
		while(i.hasNext()) {
			GraphNode gn = i.next();
			out.put(gn.getId(),gn.numEdges());
		}
		return out;
	}

	public double getEdgeDensity() {
		return numEdges()/(size()*(size()-1.));
	}








	/**
	 * just any old node will do
	 * 
	 * @return a node from the graph, or null if the graph is empty
	 */
	public GraphNode getNode() {
		return nodes.values().iterator().next();
	}

	public GraphNode getNodeAt(double x, double y) {
		Iterator<GraphNode> l = nodes.values().iterator();
		while (l.hasNext()) {
			GraphNode gn = l.next();
			if (gn.getX() == x && gn.getY() == y)
				return gn;
		}
		return null;
	}

	public GraphNode getNodeContaining(Object o) {
		Iterator<GraphNode> i = nodes.values().iterator();
		GraphNode out = null, temp;
		while (i.hasNext()) {
			temp = i.next();
			if (temp.getObject() == o)
				out = temp;
		}
		return out;
	}

	public GraphNode getNodeById(int id) {
		return nodes.get(new Integer(id));
	}

	public Iterator<GraphNode> getNodeIterator() {
		return nodes.values().iterator();
	}

	/**
	 * get a node uniformly at random from the graph
	 * 
	 * @return a random node
	 */
	public GraphNode getRandomNode() {
		int nodenum = (int) Math.floor(Math.random() * size());
		return getNodeArray()[nodenum];
	}

	public GraphEdge getRandomEdge() {
		/*this is trickier than previously thought, since we have to choose
		  a random node that isn't isolated somehow*/
		return getRandomNodeByDegree().getRandomEdge();
	}

	public GraphNode getRandomConnectedNode() {
		Iterator<GraphNode> iter= getNodeIterator();
		ArrayList<GraphNode> choices = new ArrayList<GraphNode>();
		while (iter.hasNext()) {
			GraphNode gn = iter.next();
			if (gn.numEdges()>0)
				choices.add(gn);
		}
		GraphNode[] ca = new GraphNode[choices.size()];
		return choices.toArray(ca)[(int)Math.floor(Math.random()*choices.size())];
	}

	public GraphNode getRandomNodeByDegree() {

		int choice = (int)Math.floor(Math.random()*numEdges()+1);
		Iterator<GraphNode> i = getNodeIterator();
		GraphNode out = null;
		while (i.hasNext()&&choice>0) {
			out = i.next();
			choice-=out.numEdges();
			if (choice<=0) break;
		}
		return out;
	}










	/**
	 * @return whether this graph is connected
	 */







	public int maxDegree() {
		Iterator<GraphNode> i = getNodeIterator();
		int max = 0;
		while (i.hasNext()) {
			GraphNode g = i.next();
			if (g.numEdges() > max)
				max = g.numEdges();
		}
		return max;
	}

	public int minDegree() {
		Iterator<GraphNode> i = getNodeIterator();
		int min = Integer.MAX_VALUE;
		while (i.hasNext()) {
			GraphNode g = i.next();
			if (g.numEdges() < min)
				min = g.numEdges();
		}
		return min;
	}


	public int numEdges() {
		int count = 0;
		Iterator<GraphNode> i = getNodeIterator();
		while (i.hasNext()) {
			count += i.next().numEdges();
		}
		return count;
	}

	public void printTree() {
		printTree(getNode(), -1,null);
	}

	private void printTree(GraphNode g, int k,Hashtable<GraphNode,Boolean> mark) {
		if (mark==null)
			mark = new Hashtable<GraphNode,Boolean>();
		mark.put(g,true);
		k++;
		for (int i = 0; i < k; i++)
			System.out.print(" ");
		if (g.getObject() == null)
			System.out.println(g.getId());
		else
			System.out.println(g.getObject());
		ListIterator<GraphEdge> l = g.getEdgeIterator();
		while (l.hasNext()) {
			GraphEdge e = (GraphEdge) l.next();
			if (e.getObject() != null)
				System.out.println(e.getObject());
			if (mark.get(e.getTarget())!=null)
				printTree(e.getTarget(), k,mark);
		}

		k--;
	}

	public void printGraph() {
		Iterator<GraphNode> i = getNodeIterator();
		while (i.hasNext()) {
			GraphNode gn = i.next();
			System.out.println(gn.getId());
			ListIterator<GraphEdge> l = gn.getEdgeIterator();
			while (l.hasNext()) {
				System.out.println("-->"+l.next().getTarget().getId());
			}
		}
	}

	public GraphNode[] getNodeArray() {
		return nodes.values().toArray(new GraphNode[size()]);
	}

	/**
	 * @param g
	 *            the node to remove
	 * @return the node that was input (whether or not it was in the graph)
	 */
	public GraphNode removeNode(GraphNode g) {
		if (nodes.get(new Integer(g.getId())) == null)
			return g;
		return removeNode(new Integer(g.getId()));
	}

	/**
	 * @param id
	 *            id of the node to remove
	 * @return the node that was removed
	 */
	public GraphNode removeNode(int id) {
		GraphNode gn = nodes.remove(new Integer(id));

		return gn;
	}



	/**
	 * @return number of nodes in the graph
	 */
	public int size() {
		return nodes.size();
	}




	/**
	 * The union of two graphs contains all nodes and edges in both graphs.
	 * Nodes with the same ID are mapped to a single node in the output graph.
	 * 
	 * @param g1
	 *            a graph
	 * @param g2
	 *            another graph
	 * @return the union of the two graphs
	 */
	public static Graph unionById(Graph g1, Graph g2) {
		Graph out = Graph.deepCopy(g1);
		Graph temp = Graph.deepCopy(g2);
		Iterator<GraphNode> i = temp.getNodeIterator();
		while (i.hasNext()) {
			GraphNode gn = i.next();
			// see if this node already exists in the graph
			if (out.getNodeById(gn.getId()) == null) {
				try {
					// add it to the graph
					out.addNode(gn);
					ListIterator<GraphEdge> l = gn.getEdgeIterator();
					while (l.hasNext()) {
						GraphEdge e = l.next();
						// if there is an edge pointing to a node corresponding
						// to a node in the graph
						GraphNode nt = out.getNodeById(e.getTarget().getId());
						if (nt != null) {
							// redirect it to the pre-existing node
							e.setTarget(nt);
						}// all other edges point to nodes that will
						// eventually be added
					}
				} catch (IdCollisionException e) {
					// doesn't happen
				}
			} else {
				ListIterator<GraphEdge> l = gn.getEdgeIterator();
				// don't add the node
				// but get and modify the corresponding node
				// that already exists
				gn = out.getNodeById(gn.getId());
				// for every edge in the node being copied,
				// check if it already exists in the node being modified
				// if it doesn't, add it
				while (l.hasNext()) {
					GraphEdge e = l.next();
					if (gn.getEdgeByTargetId(e.getTarget().getId()) == null) {
						gn.addEdge(e);
						// now check to make sure the node it points to
						// doesn't have a mirror in the modified graph
						if (out.getNodeById(e.getTarget().getId()) != null) {
							// if it does, point the edge at the pre-existing
							// node
							e.setTarget(out.getNodeById(e.getTarget().getId()));
						}
					}
				}
				// it may point to a node not in the graph
				// but all nodes not in the graph eventually get added
				// so no prob, bob
			}
		}
		return out;
	}

	public static Graph unionByRandomCut(Graph left, Graph right, double edgeDensity) {
		Iterator<GraphNode> i = left.getNodeIterator();
		Graph out = Graph.deepCopy(left);
		int id = Integer.MIN_VALUE;
		while (i.hasNext()) {
			GraphNode g = i.next();
			if (g.getId()>id) id = g.getId();
		}
		int id2 = Integer.MAX_VALUE;
		i = right.getNodeIterator();
		while (i.hasNext()){
			GraphNode g = i.next();
			if (g.getId()<id2) id2 = g.getId();
		}
		int shift = 0;
		if (id2<=id) shift = id-id2+1;
		i = right.getNodeIterator();
		while (i.hasNext()) {
			GraphNode g = i.next();
			try {
				out.addNode(g.getId()+shift);
			} catch (IdCollisionException ide) {
				//can't happen
			}
		}
		i = right.getNodeIterator();
		while (i.hasNext()) {
			GraphNode gn = i.next();
			ListIterator<GraphEdge> l = gn.getEdgeIterator();
			while (l.hasNext()) {
				GraphEdge e = l.next();
				out.addEdge(gn.getId()+shift, e.getTarget().getId()+shift, e.getWeight());
			}
		}
		i = out.getNodeIterator();
		int toadd = (int)Math.round(left.size()*right.size()*edgeDensity);
		if (toadd==0) toadd=1;
		for (int l=0;l<toadd;l++) {
			int leftnode = left.getRandomNode().getId();
			int rightnode = right.getRandomNode().getId()+shift;
			out.addNEdge(leftnode, rightnode, 1);
		}
		return out;
	}

	/**
	 * @param id1 the source node's id
	 * @param id2 the target node's id
	 * @return the edge from node id1 to node id2, or null if this graph doesn't contain the edge
	 */
	public GraphEdge getEdge(int id1, int id2) {
		GraphNode node1 = getNodeById(id1);
		return node1.getEdgeByTargetId(id2);
	}

	/**
	 * @return the maximum likelihood estimator for the exponent of the pareto distribution best fitting the degree distribution
	 */
	public double getPowerLaw() {
		int[] degrees = getDegreeDistribution();
		double m = Math.log(minDegree());
		double total = 0;
		for (int i=0;i<size();i++) {
			total+=Math.log(degrees[i])-m;
		}
		return size()/total;
	}



	public static Graph unionByPowerCut(Graph left, Graph right,
			double cutAcrossDensity, int[] degreeDist) {
		Iterator<GraphNode> i = left.getNodeIterator();
		Graph out = Graph.deepCopy(left);
		int id = Integer.MIN_VALUE;
		while (i.hasNext()) {
			GraphNode g = i.next();
			if (g.getId()>id) id = g.getId();
		}
		int id2 = Integer.MAX_VALUE;
		i = right.getNodeIterator();
		while (i.hasNext()){
			GraphNode g = i.next();
			if (g.getId()<id2) id2 = g.getId();
		}
		int shift = 0;
		if (id2<=id) shift = id-id2+1;
		i = right.getNodeIterator();
		while (i.hasNext()) {
			GraphNode g = i.next();
			try {
				out.addNode(g.getId()+shift);
			} catch (IdCollisionException ide) {
				//can't happen
			}
		}
		i = right.getNodeIterator();
		while (i.hasNext()) {
			GraphNode gn = i.next();
			ListIterator<GraphEdge> l = gn.getEdgeIterator();
			while (l.hasNext()) {
				GraphEdge e = l.next();
				out.addEdge(gn.getId()+shift, e.getTarget().getId()+shift, e.getWeight());
			}
		}
		ArrayList<GraphNode> nodelist = new ArrayList<GraphNode>(out.nodes.values());
		Collections.sort(nodelist,new DegreeComparator());
		for (int p=0;p<degreeDist.length;p++) {
			degreeDist[p]-=nodelist.get(p).numEdges();
			if (degreeDist[p]<0) degreeDist[p]=0;
		}

		for (int p=0;p<degreeDist.length;p++) {
			GraphNode g = nodelist.get(p);
			for (int q=0;q<degreeDist.length;p++) {
				if (g.getEdgeByTargetId(nodelist.get(q).getId())!=null) continue;

			}
		}

		return null;
	}
}
