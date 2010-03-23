package graphutils;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class UnmarkedBFS {
	private Hashtable<GraphNode,BFSData> nodedata = new Hashtable<GraphNode,BFSData>();
	private Hashtable<GraphEdge,BFSEdgeData> edgedata = new Hashtable<GraphEdge,BFSEdgeData>();
	private GraphNode source = null;
	private Graph g;

	public UnmarkedBFS(Graph g,GraphNode x) {
		this.g = g;
		Iterator<GraphNode> i = g.getNodeIterator();
		while (i.hasNext()) {
			GraphNode gn = i.next();
			nodedata.put(gn, new BFSData());
		}
		bfsFrom(x);
	}

	private BFSData data(GraphNode gn) {
		return nodedata.get(gn);
	}

	private BFSEdgeData data(GraphEdge ge) {
		return edgedata.get(ge);
	}

	private void clearAllNodeMarks() {
		Iterator<GraphNode> i = g.getNodeIterator();
		while (i.hasNext()) {
			GraphNode gn = i.next();
			data(gn).setMarked(false);
		}
	}

	public void markEdge(GraphEdge e,boolean marked) {
		if (edgedata.get(e)==null)
			edgedata.put(e,new BFSEdgeData(marked));
		else
			data(e).setMarked(marked);
	}



	public int getFewestHopsDistance(GraphNode y) {
		return (int)data(y).getD();
	}

	/**
	 * @param x
	 *            a node in this graph
	 * @param y
	 *            a node in this graph
	 * @return a graph containing only a path from x to y in the fewest hops w/o
	 *         weights
	 */
	public Graph getFewestHopsPath(GraphNode y) {
		return traceTreeBackwards(y);
	}

	public void bfsFrom(GraphNode g) {
		LinkedList<GraphNode> q = new LinkedList<GraphNode>();
		clearAllNodeMarks();
		source = g;
		q.add(source);
		Iterator<GraphNode> i = this.g.getNodeIterator();
		while (i.hasNext()) {
			GraphNode t = i.next();
			data(t).setPrev(null);
			data(t).setIncidentEdge(null);
		}
		data(source).setMarked(true);
		while ((g = q.poll()) != null) {
			ListIterator<GraphEdge> l = g.getEdgeIterator();
			while (l.hasNext()) {
				GraphEdge e = l.next();
				if (this.g.getNodeById(e.getTarget().getId()) == null) {
					l.remove();
					continue;
				}
				if (!data(e.getTarget()).isMarked()&&(data(e)==null||!data(e).isMarked())) {
					data(e.getTarget()).setMarked(true);
					data(e.getTarget()).setIncidentEdge(e);
					data(e.getTarget()).setPrev(g);
					q.add(e.getTarget());
				}
			}
		}
	}
	public void clearEdgeMarks() {
		Iterator<GraphNode> i = g.getNodeIterator();
		while (i.hasNext()) {
			GraphNode gn = i.next();
			ListIterator<GraphEdge> l = gn.getEdgeIterator();
			while (l.hasNext()) {
				markEdge(l.next(),false);
			}
		}
	}

	private Graph traceTreeBackwards(GraphNode t) {
		GraphNode q;
		GraphNode source = null;
		Graph out = new Graph();
		while (t != null) {
			try {
				q = out.addNode(t.getObject(), t.getX(), t.getY(), t.getId());
				data(q).setIncidentEdge(data(t).getIncidentEdge());
				if (source != null)
					q.addEdge(source, data(source).getIncidentEdge()
							.getWeight(), data(source).getIncidentEdge()
							.getObject());
				source = q;
				t = data(t).getPrev();
			} catch (IdCollisionException e) {
				// can't happen unless this graph has collisions (impossible)
			}
		}
		return out;
	}

}
