package graphutils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public final class GraphModels {
	public static Graph connectedGnp(int n, double p) {
		Graph out = new Graph();
		out.addNodes(n);
		int toadd = (int)Math.round(p*n*(n-1)/2);
		for (int i=0;i<toadd;i++) {
			GraphNode gn = out.getRandomNode();
			GraphNode gn2 = out.getRandomNode();
			if (out.getEdge(gn.getId(), gn2.getId())==null)
				out.addNEdge(gn.getId(),gn2.getId(),1);
		}
		BreadthFirstSearch bfs = new BreadthFirstSearch(out,out.getNode());
		GraphNode gp;
		while ((gp = bfs.getRandomUnmarkedNode()) != null) {
			GraphNode g=out.getRandomNode();
			/*if (g.numEdges()>gp.numEdges()) {
				GraphEdge e = g.getRandomEdge();
				g.removeEdge(e);
				e.getTarget().removeEdge(e.getTarget().getEdgeByTargetId(g.getId()));
			} else {
				GraphEdge e = gp.getRandomEdge();
				gp.removeEdge(e);
				e.getTarget().removeEdge(e.getTarget().getEdgeByTargetId(gp.getId()));
			}*/
			out.addNEdge(g.getId(),gp.getId(),1);
			bfs = new BreadthFirstSearch(out,out.getNode());
		}
		return out;
	}

	/**
	 * generate G(n,p)
	 * 
	 * @param n number of nodes
	 *        p edge density
	 * @return a graph
	 */
	public static Graph gnp(int n,double p) {
     	Graph out = new Graph();
		 out.addNodes(n);
		Iterator<GraphNode> i = out.getNodeIterator();
		while (i.hasNext()) {
			Iterator<GraphNode> j = out.getNodeIterator();
			GraphNode gn1 = i.next();
			while (j.hasNext()) {
				GraphNode gn2 = j.next();
				if (gn1.getId()<gn2.getId()&&Math.random()<p) {
					out.addNEdge(gn1.getId(),gn2.getId(),1);
				}
			}
		}
		return out;
	}
	
	/*public static Graph connectedByDegree(Hashtable<Integer,Integer> degreeDist) throws Exception {
		Graph out = new Graph();
		Set<Integer> ks = degreeDist.keySet();
		Iterator<Integer> d = ks.iterator();
		while (d.hasNext()) {
			out.addNode(d.next());
		}
		ArrayList<Component> components = new ArrayList<Component>();
		ArrayList<Component> hdcomps = new ArrayList<Component>();
		ArrayList<Component> comp1s = new ArrayList<Component>();
		ArrayList<GraphNode> u = new ArrayList<GraphNode>();
		Iterator<GraphNode> i = out.getNodeIterator();
		while(i.hasNext()) {
				
				GraphNode gg = (GraphNode)i.next();
				Component c = new Component();
				c.add(gg);
				components.add(c);
				if (degreeDist.get(gg.getId())>1) hdcomps.add(c); 
				if (degreeDist.get(gg.getId())==1) comp1s.add(c);
				if (degreeDist.get(gg.getId())==0) throw new Exception("Uhh...bad degree distribution: "+gg.getId());
				u.add(gg);
			
		}
		
		while (components.size()>2) {
			Component c;
			if (comp1s.size()>0) {
				int choice =(int)Math.floor(Math.random()*comp1s.size());
				c = comp1s.get(choice);
				comp1s.remove(c);
			} else {
				int choice =(int)Math.floor(Math.random()*components.size());
				c = components.get(choice);
				hdcomps.remove(c);
			}
			if (!c.isConnected()) throw new Exception("disconnected component");
			components.remove(c);
			GraphNode sel = c.randomNode(degreeDist);
			int choice =(int)Math.floor(Math.random()*hdcomps.size());
			Component c2 = hdcomps.get(choice);
			GraphNode sel2 = c2.randomNode(degreeDist);
			sel.addEdge(sel2,1);
			sel2.addEdge(sel,1);
			int sc = degreeDist.get(sel.getId())-1;
			degreeDist.put(sel.getId(),sc);
			int sc2 = degreeDist.get(sel2.getId())-1;
			degreeDist.put(sel2.getId(),sc2);
			c2.add(c);
			if (c2.degree(degreeDist)<2) {
				hdcomps.remove(c2);
				if (c2.degree(degreeDist)==1) comp1s.add(c2);
			}
		}
		//sanity check
		Component c = components.get(0);
		Component c1 = components.get(1);
		GraphNode sel = c.randomNode(degreeDist);
		GraphNode sel2 = c1.randomNode(degreeDist);
		sel.addEdge(sel2,1);
		sel2.addEdge(sel,1);
		int sc = degreeDist.get(sel.getId())-1;
		degreeDist.put(sel.getId(),sc);
		int sc2 = degreeDist.get(sel2.getId())-1;
		degreeDist.put(sel2.getId(),sc2);
		c.add(c1);
		while (sum(degreeDist.values())>1) {
			GraphNode gn1 = c.randomNode(degreeDist);
			degreeDist.put(gn1.getId(), degreeDist.get(gn1.getId())-1);
			GraphNode gn2 = c.randomNode(degreeDist);
			degreeDist.put(gn2.getId(), degreeDist.get(gn2.getId())-1);
			gn1.addEdge(gn2, 1);
			gn2.addEdge(gn1, 1);
		}
		Iterator<GraphNode> l = out.getNodeIterator();
		while (l.hasNext()) {
			GraphNode gn = l.next();
			if (degreeDist.get(gn.getId())!=0) 
				throw new Exception("some node's degree req not met:"+gn.getId()+","+degreeDist.get(gn.getId()));
		}
		
		return out;
	}*/
	
	public static Graph connectedByDegree(Hashtable<Integer,Integer> degreeDist) throws InvalidDegreeSeqException,GraphNotConnectedException {
		Graph out = detByDegree(degreeDist,true);
		return out;
	}
	
	public static Graph byDegree(Hashtable<Integer,Integer> degreeDist) throws InvalidDegreeSeqException {
		Graph out = null;
		try {
		out = detByDegree(degreeDist,false);
		} catch (GraphNotConnectedException gnce) {
			//this exception is only thrown when second argument is true
		}
		out = edgeSwitch(out,out.numEdges(),0);
		return out;
	}
	
	private static int sum(Collection<Integer> list) {
		Iterator<Integer> i = list.iterator();
		int count = 0;
		while (i.hasNext()) {
			count += i.next();
		}
		return count;
	}
	/* An implementation of Havel-Hakimi */
	private static Graph detByDegree(Hashtable<Integer,Integer> degrees, boolean ensureConnected) throws InvalidDegreeSeqException,GraphNotConnectedException {
		Graph out = new Graph();
		Set<Integer> ks = degrees.keySet();
		ArrayList<Integer> list = new ArrayList<Integer>();
		int oddcount=0;
		for (int id:ks) {
			try {
			out.addNode(id);
			} catch (IdCollisionException idce) {
				//the ids are the keys to a hashtable!
				//not a multiset!
				//can't be a duplicate!
			}
			list.add(id);
			if (degrees.get(id)>degrees.size()-1||degrees.get(id)<0) throw new InvalidDegreeSeqException();
			if (ensureConnected&&degrees.get(id)<1) throw new GraphNotConnectedException();
			if (degrees.get(id)%2==1) oddcount++;
		}
		if (oddcount%2==1) throw new InvalidDegreeSeqException();
		ReverseDegReq r = new ReverseDegReq(degrees);
		while (sum(list)>0) {
			Collections.sort(list,r);
			int top = list.get(0);
			for (int i=0;i<degrees.get(top);i++) {
				int id = list.get(1+i);
				out.addNEdge(top, id, 1);
				int old = degrees.get(id);
				if (old==0) throw new InvalidDegreeSeqException();
				degrees.put(id, old-1);
			}
			list.remove(0);
		}
		if (ensureConnected&&!(new Connectivity(out).isConnected())) throw new GraphNotConnectedException();
		return out;
	}
	
	/*
	 * do T edge switchings on the graph,
	 * ensuring simpleness is maintained
	 * and no components of size <=k are generated
	 */
	private static Graph edgeSwitch(Graph g, int t, int k) {
		g = Graph.deepCopy(g);
		for (int i=0;i<t;i++) {
			//get two independent edges
			GraphNode u = g.getRandomNodeByDegree();
			GraphEdge e1 = u.getRandomEdge();
			GraphNode v = e1.getTarget();
			GraphNode x = g.getRandomNodeByDegree();
			GraphEdge e2 = x.getRandomEdge();
			GraphNode y = e2.getTarget();
			//keep trying until they are independent, and not too close to a clique
			while (x==u||x==v||y==u||y==v||((x.getEdgeByTargetId(u.getId())!=null
					||y.getEdgeByTargetId(v.getId())!=null)&&(x.getEdgeByTargetId(v.getId())!=null
					||y.getEdgeByTargetId(u.getId())!=null))) {
				x = g.getRandomNodeByDegree();
				e2 = x.getRandomEdge(); 
				y = e2.getTarget();
			}
			int whichone = 0;
			//do whichever edge switching we can
			if (x.getEdgeByTargetId(v.getId())==null&&y.getEdgeByTargetId(u.getId())==null) {
				g.addNEdge(x.getId(), v.getId(), 1);
				g.addNEdge(y.getId(), u.getId(), 1);
			} else {
				g.addNEdge(x.getId(), u.getId(), 1);
				g.addNEdge(y.getId(), v.getId(), 1);
				whichone = 1;
			}
			//remove original edges
			u.deleteEdge(e1);
			x.deleteEdge(e2);
			v.deleteEdgeByTargetId(u.getId());
			y.deleteEdgeByTargetId(x.getId());
			//do k-isolation test
			Connectivity c = new Connectivity(g);
			if (!c.isConnected(x, k)||!c.isConnected(y, k)) {
				//if it failed, revert changes
				g.addNEdge(u.getId(),v.getId(),1);
				g.addNEdge(x.getId(),y.getId(),1);
				if (whichone==0) {
					x.deleteEdgeByTargetId(v.getId());
					v.deleteEdgeByTargetId(x.getId());
					y.deleteEdgeByTargetId(u.getId());
					u.deleteEdgeByTargetId(y.getId());
				} else {
					x.deleteEdgeByTargetId(u.getId());
					u.deleteEdgeByTargetId(x.getId());
					y.deleteEdgeByTargetId(v.getId());
					v.deleteEdgeByTargetId(y.getId());
				}
				i--;
			}
		}
		return g;
	}
	
	/**
	 * @param k number of nodes in tree
	 * @return a tree on k nodes
	 */
	public static Graph ktree(int k) {
		Graph out = new Graph();
		out.addNodes(k);
		if (k==1) return out;
		if (k==2) {
			out.addNEdge(0,1,1);
			return out;
		}
		int[] prufer = new int[2*k-2];
		boolean[] present = new boolean[k];
		for (int i=0;i<k-2;i++) {
			prufer[i]=(int)(Math.floor(Math.random()*k));
			present[prufer[i]]=true;
		}
		prufer[k-2]=k-1;
		for (int i=0;i<k-1;i++) {
			int j;
			for (j=0;present[j];j++);
			prufer[i+k-1]=j;
			out.addNEdge(prufer[i],j,1);
			present[j]=true;
			present[prufer[i]]=false;
			for (j=i+1;j<i+k-1;j++) {
				if (prufer[j]==prufer[i])
					present[prufer[i]]=true;
			}
		}
		out.addNEdge(k-2, 2*k-3, 1);
		return out;
	}
}
