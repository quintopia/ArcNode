package graphutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class GraphLoader {

	public static Graph loadGraph(File f) throws IOException {
		BufferedReader fi;
		String line = null;
		Graph g = new Graph();
		fi = new BufferedReader(new FileReader(f));
		try {
			int count = 1;
			while ((line = fi.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line);
				int sourceid = Integer.parseInt(st.nextToken());
				int destid = Integer.parseInt(st.nextToken());
				//double weight = Double.parseDouble(st.nextToken());
				GraphNode source = g.getNodeById(sourceid);
				GraphNode dest = g.getNodeById(destid);
				if (source == null) {
					source = g.addNode(sourceid);
				}
				if (dest == null) {
					dest = g.addNode(destid);
				}
				g.addEdge(source,dest,1);
				g.addEdge(dest,source,1);
				if (count%100 == 0) System.out.println("Loading. . ." + count++);
			}
		} catch (IdCollisionException idc) { 
			//can't happen by design--id's are assumed not to collide in file
		} finally {
			fi.close();
		}
		return g;
	}
}
