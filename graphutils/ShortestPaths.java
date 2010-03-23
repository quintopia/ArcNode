package graphutils;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;

public class ShortestPaths {
	private Hashtable<GraphNode, SPData> nodedata = new Hashtable<GraphNode, SPData>();
	private Graph g;
	private GraphNode source = null;

	public ShortestPaths(Graph g,GraphNode x) throws NegativeEdgeException {
		this.g = g;
		Iterator<GraphNode> i = g.getNodeIterator();
		while (i.hasNext()) {
			nodedata.put(i.next(), new SPData());
		}
		generateShortestPathsFrom(x);
	}

	private SPData data(GraphNode gn) {
		return nodedata.get(gn);
	}

	private void generateShortestPathsFrom(GraphNode source)
			throws NegativeEdgeException {
		Iterator<GraphNode> l = g.getNodeIterator();
		ListIterator<GraphEdge> m;
		FibonacciHeap<GraphNode> h = new FibonacciHeap<GraphNode>();
		GraphNode t, v;
		GraphEdge e;
		h.insert(source, 0);
		data(source).setPrev(null);
		data(source).setIncidentEdge(null);
		this.source = source;
		while (l.hasNext()) {
			t = l.next();
			if (t == source)
				continue;
			data(t).setPrev(null);
			data(t).setIncidentEdge(null);
			h.insert(t, Integer.MAX_VALUE);
		}
		while (!h.isEmpty()) {
			t = h.extractMin();
			m = t.getEdgeIterator();
			while (m.hasNext()) {
				e = m.next();
				if (g.getNodeById(e.getTarget().getId()) == null) {
					m.remove();
					continue;
				}
				if (e.getWeight() < 0) {
					throw new NegativeEdgeException(
							"Dijkstra's algorithm won't work on graphs with negative edge weights!");
				}
				v = e.getTarget();
				if (h.getKey(v) > h.getKey(t) + e.getWeight()) {
					try {
						h.decreaseKey(v, h.getKey(t) + e.getWeight());
					} catch (KeyNotLessException x) {
						// can't happen!
					}
					data(v).setPrev(t);
					data(v).setIncidentEdge(e);
				}
			}
		}
	}

	private Graph traceTreeBackwards(GraphNode t) {
		GraphNode q;
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
	/**
	 * @param x
	 *            a node in this graph
	 * @param y
	 *            a node in this graph
	 * @return a graph containing only the shortest path from x to y w/o weights
	 */
	public Graph getShortestPath(GraphNode y)
			throws NegativeEdgeException {
		return traceTreeBackwards(y);
	}
	
	public int getShortestPathLength(GraphNode g2) throws NegativeEdgeException {
		Graph g = getShortestPath(g2);
		GraphNode curNode = g.getNodeById(source.getId());
		int out = 0;
		while (curNode.getId()!=g2.getId()) {
			out += curNode.getRandomEdge().getWeight();
			curNode = curNode.getRandomEdge().getTarget();
		}
		return out;
	}

	/**
	 * returns the source of a dijkstra's run, if any
	 * 
	 * @return the source
	 */
	public GraphNode getSource() {
		return source;
	}

}
