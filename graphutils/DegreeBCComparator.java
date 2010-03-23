package graphutils;

import java.util.Comparator;
import java.util.Hashtable;

public class DegreeBCComparator implements Comparator<GraphNode> {
	private Hashtable<GraphNode,Double> bc;
	public DegreeBCComparator(Hashtable<GraphNode,Double> bc) {
		this.bc=bc;
	}
	public int compare(GraphNode arg0, GraphNode arg1) {
		int val= (int)Math.signum(arg1.numEdges()-arg0.numEdges());
		if (val==0) return (int)Math.signum(bc.get(arg1)-bc.get(arg0));
		else return val;
	}

}
