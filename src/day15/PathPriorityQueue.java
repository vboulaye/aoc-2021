package day15;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class PathPriorityQueue<E, R> implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * map containing path elements (a path element is a node in the network,
     * the distance to the source and the current predecessor)
     */
    private final Map<E, WorkPathElement<E, R>> WorkPathElements = new HashMap<E, WorkPathElement<E, R>>();
    /**
     * a set of nodes that are still possible choices for the path
     */
    private final PriorityQueue<E> remainingNodesToEvaluate = new PriorityQueue<E>(100, new Comparator<E>() {
        @Override
        public int compare(E o1, E o2) {
            WorkPathElement<E, R> workPathElement = getWorkPathElement(o1);
            int d1 = workPathElement.isEvaluated() ? Integer.MAX_VALUE : workPathElement.getDistance();
            WorkPathElement<E, R> workPathElement2 = getWorkPathElement(o2);
            int d2 = workPathElement2.isEvaluated() ? Integer.MAX_VALUE : workPathElement2.getDistance();

            if (d1 < d2) {
                return -1;
            } else {
                return (d1 == d2) ? 0 : 1;
            }
        }
    });

    /**
     * add a node to the list of remaining nodes
     *
     * @param element
     */
    void push(E element) {
        remainingNodesToEvaluate.add(element);
    }

    /**
     * get the node from the remaining nodes that is the shortest path from
     * source the node is removed from the list of remaining nodes and returned
     *
     * @return the "closest" node
     */
    E pop() {
        E result = null;
        result = remainingNodesToEvaluate.poll();
        return result;
    }

    /**
     * returns the WorkPathElement from the working map. if a path element cannot be
     * found for the node, build a new one
     *
     * @param node for which the path element is sought
     * @return the path element describing how the node is used in the path
     * search
     */
    WorkPathElement<E, R> getWorkPathElement(E node) {
        WorkPathElement<E, R> element = WorkPathElements.get(node);
        if (element == null) {
            element = new WorkPathElement<E, R>(node);
            WorkPathElements.put(node, element);
        }
        return element;
    }

}
