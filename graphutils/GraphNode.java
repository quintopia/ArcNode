/*
 * Created on Nov 13, 2006
 *
 * TODO 
 */
package graphutils;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.ListIterator;

public class GraphNode{
    private double x,y;
    private int id;
    private ArrayList<GraphEdge> edges = new ArrayList<GraphEdge>();
    private ArrayList<GraphNode> shortestPathList = new ArrayList<GraphNode>();
    private Object object;
    public void addEdge(GraphNode v, double w, Object o) {
	    edges.add(new GraphEdge(v,w,o));
	}
	public void addEdge(GraphNode v, double w) {
	    edges.add(new GraphEdge(v,w));
	}
	public void addEdge(GraphEdge e) {
		edges.add(e);
	}
	public void addSPNode(GraphNode g) {
		shortestPathList.add(g);
	}
	public void clearSPList() {
		shortestPathList = new ArrayList<GraphNode>();
	}
	/**
	 * @param e the edge to be deleted
	 * @return the edge that was deleted (null if it never existed)
	 */
	public GraphEdge deleteEdge(GraphEdge e) {
		return (edges.remove(e))?e:null;
	}
	/**
	 * @param id the id of the target node of the edge to be deleted
	 * @return the edge that was deleted
	 */
	public GraphEdge deleteEdgeByTargetId(int id) {
		GraphEdge e = getEdgeByTargetId(id);
		return deleteEdge(e);
	}
	/**
	 * @param id the id of the target node
	 * @return an edge connecting this node to the identified node
	 */
	public GraphEdge getEdgeByTargetId(int id) {
		ListIterator<GraphEdge> l = getEdgeIterator();
		GraphEdge out = null;
		while (l.hasNext()) {
			GraphEdge e = l.next();
			if (e.getTarget().getId()==id) {
				out = e;
				break;
			}
		}
		return out;
	}
	public ListIterator<GraphEdge> getEdgeIterator() {
	    return edges.listIterator();
	}

    public int getId() {
		return id;
	}
	/**
     * @return Returns the x.
     */
    public double getX() {
        return x;
    }
    /**
     * @return Returns the y.
     */
    public double getY() {
        return y;
    }
    
    public GraphNode[] getNeighbors() {
    	GraphNode[] neighbors = new GraphNode[numEdges()];
    	ListIterator<GraphEdge> ie = getEdgeIterator();
    	int i=0;
    	while(ie.hasNext()) {
    		GraphEdge e = ie.next();
    		neighbors[i++]=e.getTarget();
    	}
    	return neighbors;
    }

	/**
	 * @return Returns the object.
	 */
	public Object getObject() {
	    return object;
	}
	public Point2D getPoint() {
	    return new Point2D.Double(x,y);
	}

    
    public GraphEdge getRandomEdge() {
    	if (edges.size()>0)
    		return edges.get((int)Math.floor(Math.random()*edges.size()));
    	else
    		return null;
	}
    

    

    
    public ListIterator<GraphNode> getSPIterator() {
    	return shortestPathList.listIterator();
    }
	/**
	 * Basic constructor
	 * @param o an object for this node to carry around
	 * @param x the x position of this node
	 * @param y the y position of this node
	 * @param id identifier
	 */
	public GraphNode(Object o, double x, double y, int id) {
	    object=o;
	    this.x=x;
	    this.y=y;
	    this.id=id;
	}
	/**
	 * Basic constructor
	 * @param o an object for this node to carry around
	 * @param x the x position of this node
	 * @param y the y position of this node
	 */
	public GraphNode(Object o, double x, double y) {
	    object=o;
	    this.x=x;
	    this.y=y;
	    this.id=0;
	}
	/**
	 * @param o the object that node contains
	 * @param id identifier
	 */
	public GraphNode(Object o, int id) {
	    this(o,0.0,0.0,id);
	}
	/**
	 * Constructor with default x,y position
	 * @param o an object for this node to carry around
	 */
	public GraphNode(Object o) {
	    this(o,0);
	}
	/**
	 * @param id identifier
	 */
	public GraphNode(int id) {
		this(null,id);
	}
	/**
	 * Constructor with all default parameters
	 */
	public GraphNode() {
		this(0);
	}
	public int numEdges() {
	    return edges.size();
	}
	public boolean removeEdge(GraphEdge e) {
	    return edges.remove(e);
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public void setObject(Object object) {
		this.object = object;
	}
	
}
