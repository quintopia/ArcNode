package graphutils;

import java.util.Iterator;

public class DistanceMetrics {
private Graph g;

public DistanceMetrics(Graph g) {
	this.g=g;
}

public double[] getDistanceDistribution() {
	Iterator<GraphNode> i = g.getNodeIterator();
	double[] dist = new double[g.size()];
	BreadthFirstSearch bfs;
	for(int j=0;j<g.size();j++) dist[j]=0;
	while (i.hasNext()) {
		GraphNode n = i.next();
		bfs = new BreadthFirstSearch(g,n);
		Iterator<GraphNode> i2 = g.getNodeIterator();
		while (i2.hasNext()) {
			GraphNode g = i2.next();
			dist[(int)bfs.getD(g)]++;
			
		}
	}
	for(int j=0;j<dist.length;j++) dist[j]=dist[j]/(g.size()*g.size());
	return dist;
}

public double[] avgAndStdDevDistance() {
	double out[] = new double[2];
	double[] dist = getDistanceDistribution();
	double total = 0;
	for (int i=0;i<dist.length;i++) total+=dist[i]*i;
	out[0]=total;
	total=0;
	for (int i=0;i<dist.length;i++) {
		total+=dist[i]*(i-out[0])*(i-out[0]);
	}
	out[1]=total;
	return out;
}

public double avgDistance() {
	double[] dist = getDistanceDistribution();
	double total = 0;
	for (int i=0;i<dist.length;i++) total+=dist[i]*i;
	return total;
}

public double stdDevDistance() {
	double[] dist = getDistanceDistribution();
	double avg = avgDistance();
	double total=0;
	for (int i=0;i<dist.length;i++) {
		total+=dist[i]*(i-avg)*(i-avg);
	}
	return Math.sqrt(total);
}

}
