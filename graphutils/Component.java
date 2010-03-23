package graphutils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ListIterator;

public class Component {
	private ArrayList<GraphNode> t = new ArrayList<GraphNode>();
	public void add(GraphNode gn) {
		t.add(gn);
	}
	public void remove(GraphNode gn) {
			t.remove(gn);
	}
	public void add(Component c) {
		
		ListIterator<GraphNode> l = c.t.listIterator();
		while (l.hasNext()) {
			add(l.next());
		}

	}
	public int degree(Hashtable<Integer,Integer> ht) {
		ListIterator<GraphNode> l = t.listIterator();
		int count = 0;
		while (l.hasNext()) {
			count+=ht.get(l.next().getId());
		}
		return count;
	}
	public boolean contains(GraphNode gn) {
		return t.contains(gn);
	}
	public GraphNode randomNode(Hashtable<Integer,Integer> degreeDist) {
		int choice = (int)Math.ceil(Math.random()*degree(degreeDist));
		ListIterator<GraphNode> l = t.listIterator();
		GraphNode gn=null;
		while (l.hasNext()) {
			gn = l.next();
			choice-=degreeDist.get(gn.getId());
			if (choice<=0) break;
		}
		return gn;
	}
	
	public boolean isConnected() {
		Graph g = new Graph();
		try {
		g.addAll(t);
		} catch (IdCollisionException idce) {
			//meh, it's a new graph...
		}
		return Connectivity.isConnected(g);
	}
}
