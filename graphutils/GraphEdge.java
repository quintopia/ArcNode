/*
 * Created on Nov 13, 2006
 *
 * TODO 
 */
package graphutils;

public class GraphEdge {
	private GraphNode target;
    private double weight;
    private Object object = null;
    /**
     * @param target
     * @param weight
     */
    public GraphEdge(GraphNode target, double weight) {
        super();
        this.target = target;
        this.weight = weight;
    }
    
    public GraphEdge(GraphNode target, double weight, Object object) {
        super();
        this.target=target;
        this.weight=weight;
        this.object = object;
    }
    /**
     * @return Returns the target.
     */
    public GraphNode getTarget() {
        return target;
    }
    /**
     * @param target The target to set.
     */
    public void setTarget(GraphNode target) {
        this.target = target;
    }
    /**
     * @return Returns the weight.
     */
    public double getWeight() {
        return weight;
    }
    /**
     * @param weight The weight to set.
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * @return Returns the object.
     */
    public Object getObject() {
        return object;
    }

    /**
     * @param object The object to set.
     */
    public void setObject(Object object) {
        this.object = object;
    }


    
}
