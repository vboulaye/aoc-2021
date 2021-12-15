package day15;

import java.util.List;

/**
 * interface that must be implemented by callers to PathFinder in order to
 * describe the network For each node the find related shows where to go next
 */
public interface FindRelated<E, R> {

    /**
     * return the list of path elements that exist in the network from the node
     * o the path element contains related objects and the relation to gets
     * there
     *
     * @param o
     * @return
     */
    List<WorkPathElement<E, R>> findRelated(WorkPathElement<E, R> o);
}
