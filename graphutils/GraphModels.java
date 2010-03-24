package graphutils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

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
	
	/*
	 * Viger-Latapy's final heuristic exploiting power law
	 * degree sequences to achieve loglinear running time
	 */
	public static Graph connectedByDegree(Hashtable<Integer,Integer> degreeDist) throws InvalidDegreeSeqException,GraphNotConnectedException {
		Graph out = detByDegree(degreeDist);
		
		//need to connect result
		out = makeConnected(out);
		System.out.println(Connectivity.isConnected(out));
		//now randomize
		double times = out.numEdges()/20;
		double k = 2.4;
		
		int swapstoadd = 0;
		for (int i=0;i<out.numEdges();i+=swapstoadd) {
			Graph temp = edgeSwitch(out,(int)Math.min(1,Math.floor(times)),(int)k);
			boolean ok = Connectivity.isConnected(temp);
			if (ok) {
				swapstoadd=(int)Math.min(1,Math.floor(times));
				if ((k+10.0)*times>2.5*out.numEdges()) k/=1.03;
				else times*=2;
				out = temp;
			} else {
				temp = null;
				swapstoadd=0;
				k*=1.35;
			}
		}
		return out;
	}
	
	public static Graph byDegree(Hashtable<Integer,Integer> degreeDist) throws InvalidDegreeSeqException {
		Graph out = null;
		out = detByDegree(degreeDist);
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
	private static Graph detByDegree(Hashtable<Integer,Integer> degrees) throws InvalidDegreeSeqException {
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
	
	private static Graph makeConnected(Graph g) throws GraphNotConnectedException {
		//check there are enough edges to connect
		if (g.numEdges()/2+1<g.size()) throw new GraphNotConnectedException();
		//get connected components
		ArrayList<Graph> components = new Connectivity(g).getAllComponents();
		ArrayList<Graph> fat = new ArrayList<Graph>();
		ArrayList<Graph> tree = new ArrayList<Graph>();
		for (Graph o:components) {
			if (o.numEdges()>2*o.size()-2)
				fat.add(o);
			else
				tree.add(o);
		}
		while (components.size()>1) {
		//find one with extra edges
			Graph fatGraph = fat.remove(0);
			components.remove(fatGraph);
		//find one with no extra edges if it exists
			Graph other;
			if (tree.size()>0)
				other = tree.remove(0);
			else
				other = fat.remove(0);
			components.remove(other);
		//find a biconnected edge in the fat one
			GraphNode[] pair = new Connectivity(fatGraph).getBiconnectedEdge();
			GraphNode f1 = pair[0];
			GraphNode f2 = pair[1];
		//take any edge in the other
			GraphNode o1 = other.getNode();
			GraphNode o2 = o1.getRandomEdge().getTarget();
		//remove them from their graphs
			f1.deleteEdgeByTargetId(f2.getId());
			f2.deleteEdgeByTargetId(f1.getId());
			o1.deleteEdgeByTargetId(o2.getId());
			o2.deleteEdgeByTargetId(o1.getId());
		//combine the graphs
			Graph newGraph = Graph.unionById(fatGraph, other);
		//add in the other two edges
			newGraph.addNEdge(f1.getId(), o1.getId(), 1);
			newGraph.addNEdge(f2.getId(), o2.getId(), 1);
		//put component back in relevant lists
			if (newGraph.numEdges()>2*newGraph.size()-2) {
				fat.add(newGraph);
			} else {
				tree.add(newGraph);
			}
			components.add(newGraph);
		}
		return components.get(0);
	}
}
