package day15;

import java.io.Serializable;

public class WorkPathElement<E, R> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * any object associated to this vertex
     */
    private final E element;

    /**
     * distance to the source object, updated continuously during processing
     */
    private int distance = Integer.MAX_VALUE;

    /**
     * predecessor to this node, updated continuously during processing
     */
    private E predecessor = null;

    /**
     * relation used to go from predecessor to element
     */
    private R relation = null;

    /**
     * flag set to true when the node has been processed
     */
    private boolean evaluated = false;

    protected WorkPathElement(E element) {
        this.element = element;
    }

    public E getElement() {
        return element;
    }

    protected int getDistance() {
        return distance;
    }

    protected void setDistance(int distance) {
        this.distance = distance;
    }

    protected E getPredecessor() {
        return predecessor;
    }

    protected void setPredecessor(E predecessor) {
        this.predecessor = predecessor;
    }

    protected boolean isEvaluated() {
        return evaluated;
    }

    protected void setEvaluated(boolean evaluated) {
        this.evaluated = evaluated;
    }

    /**
     * @return the relation
     */
    public R getRelation() {
        return relation;
    }

    /**
     * @param relation the relation to set
     */
    public void setRelation(R relation) {
        this.relation = relation;
    }

    @Override
    public String toString() {
        return "PathElement: to:{" + element + "} predecessor:{" + predecessor + "} using:{" + relation + "} distance:"
                + distance;
    }

}
