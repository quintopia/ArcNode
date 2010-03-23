package graphutils;

public class BFSData {
	private double d=-1;
    private int sigma=0;
    private GraphNode prev=null;
    private boolean marked=false;
    private GraphEdge incidentEdge;
	/**
     * @return Returns the incidentEdge.
     */
    public GraphEdge getIncidentEdge() {
        return incidentEdge;
    }
	/**
     * @return Returns the prev.
     */
    public GraphNode getPrev() {
        return prev;
    }
    public boolean isMarked() {
		return marked;
	}
    public double getD() {
		return d;
	}
	public void setD(double d) {
		this.d = d;
	}
	public int getSigma() {
		return sigma;
	}
	public void setSigma(int sigma) {
		this.sigma = sigma;
	}
	/**
	 * @param prev The prev to set.
	 */
	public void setPrev(GraphNode prev) {
	    this.prev = prev;
	}
	/**
	 * @param incidentEdge The incidentEdge to set.
	 */
	public void setIncidentEdge(GraphEdge incidentEdge) {
	    this.incidentEdge = incidentEdge;
	}
	public void setMarked(boolean marked) {
		this.marked = marked;
	}
}
