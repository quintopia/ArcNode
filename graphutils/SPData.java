package graphutils;

public class SPData {
	private GraphNode prev=null;
    private boolean marked=false;
    private GraphEdge incidentEdge;
    public GraphNode getPrev() {
		return prev;
	}
	public void setPrev(GraphNode prev) {
		this.prev = prev;
	}
	public boolean isMarked() {
		return marked;
	}
	public void setMarked(boolean marked) {
		this.marked = marked;
	}
	public GraphEdge getIncidentEdge() {
		return incidentEdge;
	}
	public void setIncidentEdge(GraphEdge incidentEdge) {
		this.incidentEdge = incidentEdge;
	}
}
