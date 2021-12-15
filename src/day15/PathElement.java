package day15;

import java.io.Serializable;

public class PathElement<E, R> implements Serializable {
    private static final long serialVersionUID = 1L;

    private final E element;

    private final R relation;

    public PathElement(E element, R relation) {
        this.element = element;
        this.relation = relation;
    }

    /**
     * any object associated to this vertex
     */
    public E getElement() {
        return element;
    }

    /**
     * relation used to go from predecessor to element
     */
    public R getRelation() {
        return relation;
    }


}
