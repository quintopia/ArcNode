package graphutils;

import java.io.File;
import java.io.IOException;



public class WeightLoss {

	public static void dropWeights(File f, File f2) throws IOException {
			Graph g = GraphLoader.loadGraph(f);
			GraphSaverUnweighted.saveGraph(g, f2);
	}

}
