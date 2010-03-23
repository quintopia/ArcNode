package graphutils;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;

public class RandomSpanningTree {
	private Hashtable<GraphEdge,Boolean> edgedata = new Hashtable<GraphEdge,Boolean>();
	private Hashtable<GraphNode,TreeData> nodedata = new Hashtable<GraphNode,TreeData>();
	private Graph g;
	/**
	 * An implementation of Wilson's Algorithm
	 * 
	 * @param g
	 *            an input graph
	 * @return a spanning tree chosen uniformly at random, or null if graph is
	 *         disconnected
	 */
	public RandomSpanningTree(Graph g) {
		this.g=g;
	}
	public Graph randomSpanningTree() {
		Graph out = null;
		Connectivity gc = new Connectivity(g);
		if (gc.isConnected()) {
			Graph unFound = Graph.deepCopy(g);
			Iterator<GraphNode> i = g.getNodeIterator();
			while (i.hasNext()) {
				GraphNode gn = i.next();
				nodedata.put(gn, new TreeData());
				ListIterator<GraphEdge> l = gn.getEdgeIterator();
				while (l.hasNext()) {
					edgedata.put(l.next(), false);
				}
			}
			GraphNode root=unFound.addNode();
			out = new Graph();
			i = g.getNodeIterator();
			while (i.hasNext()) {
				GraphNode gn = i.next();
				try {
					if (gn!=root) out.addNode(gn.getObject(), gn.getX(), gn.getY(), gn
							.getId());
				} catch (IdCollisionException e) {
					// really frickin impossible
				}
				
				gn.addEdge(root,1./unFound.numEdges());
			}
			i = g.getNodeIterator();
			//add all leaves of graph to tree
			while (i.hasNext()) {
				GraphNode gn = i.next();
				if (gn.numEdges()==1) { 
					out.addNEdge(gn.getId(), gn.getRandomEdge().getTarget().getId(), gn.getRandomEdge().getWeight());
					markEdge(gn.getRandomEdge().getTarget().getEdgeByTargetId(gn.getId()),true);
					unFound.removeNode(gn.getId());
				}
			}
			unFound.removeNode(root.getId());
			clearMark(g,root);
			while (unFound.size() > 0) {
				GraphNode start = g
						.getNodeById(unFound.getRandomNode().getId());
				GraphNode t = start;
				//go until we get to a node that's not in unFound
				//it must have been found
				//and therefore must be a part of the tree already
				while (unFound.getNodeById(t.getId())!=null) {
					GraphEdge e = getRandomUnmarkedEdgeByWeight(t);
					data(t).setPrev(e.getTarget());
					data(t).setIncidentEdge(e);
					t = e.getTarget();
				}
				//no need to add edges to death node, since
				//it's not gonna be in the output anyway
				if (t==root) t=data(t).getPrev();
				// now we have arrived at the current subtree
				// insert the edges of the path from start
				// and remove all the nodes from unFound
				while (unFound.getNodeById(start.getId())!=null) {
					unFound.removeNode(unFound.getNodeById(start.getId()));
					if (out.getNodeById(start.getId()).getEdgeByTargetId(data(start).getPrev().getId())!=null)
						break;
					out.addNEdge(start.getId(), data(start).getPrev().getId(), data(start).getIncidentEdge()
							.getWeight());
					start = data(start).getPrev();
				}
			}
		}
		return out;
	}
	
	/*public void clearEdgeMarks(Graph gr) {
		Iterator<GraphNode> i = gr.getNodeIterator();
		while (i.hasNext()) {
			GraphNode gn = i.next();
			ListIterator<GraphEdge> l = gn.getEdgeIterator();
			while (l.hasNext()) {
				markEdge(l.next(),false);
			}
		}
	}*/
	public void markEdge(GraphEdge e,boolean marked) {
		edgedata.put(e,marked);
	}
	
	private Boolean data(GraphEdge ge) {
		return edgedata.get(ge);
	}
	
	private TreeData data(GraphNode gn) {
		return nodedata.get(gn);
	}
	
	private void clearMark(Graph gr, GraphNode g) {
		data(g).setMarked(false);
		ListIterator<GraphEdge> l = g.getEdgeIterator();
		while (l.hasNext()) {
			GraphEdge e = l.next();
			if (gr.getNodeById(e.getTarget().getId()) == null) {
				l.remove();
				continue;
			}
			if (data(e.getTarget()).isMarked())
				clearMark(gr,e.getTarget());
		}
	}
    private GraphEdge getRandomUnmarkedEdgeByWeight(GraphNode gn) {
    	ListIterator<GraphEdge> i = gn.getEdgeIterator();
    	double total=0;
    	while (i.hasNext()) {
    		GraphEdge e = i.next();
    		if (data(e)==null||!data(e))
    			total+=e.getWeight();
    	}
    	double val = total-Math.random()*total;
    	total=0;
    	i = gn.getEdgeIterator();
    	GraphEdge e=null;
    	while (i.hasNext()) {
    		e = i.next();
    		if (data(e)==null|!data(e))
    			total+=e.getWeight();
    		if (total>val) break;
    	}
    	return e;
    }
    
    
}
