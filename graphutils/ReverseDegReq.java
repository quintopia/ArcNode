package graphutils;

import java.util.Comparator;
import java.util.Hashtable;

public class ReverseDegReq implements Comparator<Integer> {
	private Hashtable<Integer,Integer> h = null;
	public ReverseDegReq(Hashtable<Integer,Integer> h) {
		this.h=h;
	}
	public int compare(Integer arg0, Integer arg1) {
		Integer out1 = h.get(arg0);
		Integer out2 = h.get(arg1);
		return out2.compareTo(out1);
	}

}
