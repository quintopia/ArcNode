package graphutils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Stack;

public class BreadthFirstSearch {
private Hashtable<GraphNode,BFSData> nodedata = new Hashtable<GraphNode,BFSData>();
private GraphNode source = null;
private Stack<GraphNode> nodeStack;
private Graph g;

/**
 * returns the source of a dijkstra's run, if any
 * 
 * @return the source
 */
public GraphNode getSource() {
	return source;
}


public BreadthFirstSearch(Graph g,GraphNode x) {
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

private void clearAllNodeMarks() {
	Iterator<GraphNode> i = g.getNodeIterator();
	while (i.hasNext()) {
		GraphNode gn = i.next();
		data(gn).setMarked(false);
	}
}

public void clearMark(GraphNode g) {
	data(g).setMarked(false);
	data(g).setD(-1);
	data(g).setSigma(0);
	ListIterator<GraphEdge> l = g.getEdgeIterator();
	while (l.hasNext()) {
		GraphEdge e = l.next();
		if (this.g.getNodeById(e.getTarget().getId()) == null) {
			l.remove();
			continue;
		}
		if (data(e.getTarget()).isMarked())
			clearMark(e.getTarget());
	}
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

private void bfsFrom(GraphNode g) {
	LinkedList<GraphNode> q = new LinkedList<GraphNode>();
	clearAllNodeMarks();
	nodeStack=new Stack<GraphNode>();
	source = g;
	q.add(source);
	Iterator<GraphNode> i = this.g.getNodeIterator();
	while (i.hasNext()) {
		GraphNode t = i.next();
		data(t).setPrev(null);
		data(t).setIncidentEdge(null);
		data(t).setD(-1);
		data(t).setSigma(0);
	}
	data(source).setD(0);
	data(source).setSigma(1);
	data(source).setMarked(true);
	while ((g = q.poll()) != null) {
		nodeStack.push(g);
		ListIterator<GraphEdge> l = g.getEdgeIterator();
		while (l.hasNext()) {
			GraphEdge e = l.next();
			if (this.g.getNodeById(e.getTarget().getId()) == null) {
				l.remove();
				continue;
			}
			if (!data(e.getTarget()).isMarked()) {
				data(e.getTarget()).setMarked(true);
				data(e.getTarget()).setIncidentEdge(e);
				data(e.getTarget()).setPrev(g);
				data(e.getTarget()).setD(data(g).getD()+1);
				q.add(e.getTarget());
			}
			if (data(e.getTarget()).getD()==data(g).getD()+1) {
				data(e.getTarget()).setSigma(data(g).getSigma()+data(e.getTarget()).getSigma());
				e.getTarget().addSPNode(g);
			}
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

public double getD(GraphNode gn) {
	return data(gn).getD();
}

public void setD(GraphNode gn,double d) {
	data(gn).setD(d);
}

public int getSigma(GraphNode gn) {
	return data(gn).getSigma();
}

public void setSigma(GraphNode gn,int s) {
	data(gn).setSigma(s);
}

public GraphNode getRandomUnmarkedNode() {
	Iterator<GraphNode> i = g.getNodeIterator();
	ArrayList<GraphNode> a = new ArrayList<GraphNode>();
	while (i.hasNext()) {
		GraphNode gn = i.next();
		if (!data(gn).isMarked())
			a.add(gn);
	}
	int ind = (int) Math.floor(Math.random()*a.size());
	if (a.isEmpty()) return null;
	else return a.get(ind);
}

public Stack<GraphNode> getStack() {
	return nodeStack;
}

}
