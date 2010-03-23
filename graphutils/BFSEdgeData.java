package graphutils;

public class BFSEdgeData {
	private boolean marked;
	public BFSEdgeData(boolean marked) {
		this.marked = marked;
	}
	public boolean isMarked() {
		return marked;
	}

	public void setMarked(boolean marked) {
		this.marked = marked;
	}
}
