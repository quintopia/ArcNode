package graphutils;

import java.util.Comparator;

public class DegreeComparator implements Comparator<GraphNode> {

	public int compare(GraphNode arg0, GraphNode arg1) {
		return (int)Math.signum(arg1.numEdges()-arg0.numEdges());
		
	}

}
