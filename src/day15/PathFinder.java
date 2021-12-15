package day15;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * utility class used to find the shortest path between 2 nodes inside a network
 * the FindRelated implementation gives a node by node description of the
 * network for one node the findRelated() method returns all related nodes The
 * search of the shortest path is a simplification of the dijkstra algorithm
 */
public class PathFinder<E, R> {

    /**
     * the FindRelated implementation that describes the network
     */
    private final FindRelated<E, R> findRelated;

    /**
     * constructor, sets the FindRelated implementation
     *
     * @param findRelated
     */
    public PathFinder(FindRelated<E, R> findRelated) {

        if (findRelated == null) {
            throw new IllegalArgumentException("findRelated implementation must be not null");
        }

        this.findRelated = findRelated;
    }

    /**
     * build the shortest path from one node to another in the network described
     * by the FindRelated implementation
     *
     * @param sourceNode source node object
     * @param targetNode target node object
     * @return the path as a list of nodes
     */
    public List<WorkPathElement<E, R>> findPath(E sourceNode, E targetNode) {



        PathPriorityQueue<E, R> remainingNodes = computePath(sourceNode, targetNode);

        List<WorkPathElement<E, R>> path = buildPath(sourceNode, targetNode, remainingNodes);


        return path;
    }

    /**
     * build the set of all nodes in the path and their distance from source the
     * evaluation stops when target is reached by following the shortest path
     *
     * @param sourceNode source of the searched path
     * @param targetNode target of the searched path
     * @return the queue with all evaluated nodes, the path can be build from
     * there
     */
    private PathPriorityQueue<E, R> computePath(E sourceNode, E targetNode) {
        // set of nodes that are still possible choices for the path
        PathPriorityQueue<E, R> remainingNodes = new PathPriorityQueue<E, R>();

        // add source node with a distance of 0
        remainingNodes.getWorkPathElement(sourceNode).setDistance(0);
        remainingNodes.push(sourceNode);

        // work on the remaining node that is currently seen as the closest to
        // the source
        E closest = remainingNodes.pop();

        // loop until there is no more nodes or if the destination is reached
        while ((closest != null) && (!closest.equals(targetNode))) {


            // flag the node in order not to evaluate it twice
            WorkPathElement<E, R> closestNodeWorkPathElement = remainingNodes.getWorkPathElement(closest);
            closestNodeWorkPathElement.setEvaluated(true);

            // update the distance of all related nodes of the work node by
            // evaluating the impact of using it as a predecessor
            List<WorkPathElement<E, R>> related = findRelated.findRelated(closestNodeWorkPathElement);

            if ((related != null) && (!related.isEmpty())) {

                for (WorkPathElement<E, R> relatedPath : related) {
                    processRelatedNode(remainingNodes, closestNodeWorkPathElement, relatedPath);

                } // end for all related nodes

            } // if there exist related nodes

            closest = remainingNodes.pop();

        } // while there are still some nodes to evaluate

        return remainingNodes;
    }

    /**
     * build shortest path from calculated nodes path elements
     *
     * @param sourceNode     source node
     * @param targetNode     target node
     * @param remainingNodes Map of evaluated nodes
     * @return List of WorkPathElement from source to target
     */
    private List<WorkPathElement<E, R>> buildPath(E sourceNode, E targetNode, PathPriorityQueue<E, R> remainingNodes) {
        List<WorkPathElement<E, R>> path = Collections.emptyList();


        // only build the path if the distance if not infinite (a path was
        // actually found)
        if (remainingNodes.getWorkPathElement(targetNode).getDistance() != Integer.MAX_VALUE) {
            path = new ArrayList<>();

            // we add the destination node and all its predecessors (processing
            // backwards)
            E predecessor = targetNode;
            while ((predecessor != null) && !predecessor.equals(sourceNode)) {
                // put the predecessor as the first node
                WorkPathElement<E, R> predecessorElement = remainingNodes.getWorkPathElement(predecessor);
                path.add(predecessorElement);
                predecessor = predecessorElement.getPredecessor();
            }

            // add the init node
            path.add(remainingNodes.getWorkPathElement(sourceNode));
        }
        return path;
    }

    private void processRelatedNode(PathPriorityQueue<E, R> remainingNodes, WorkPathElement<E, R> closestNodeWorkPathElement,
                                    WorkPathElement<E, R> relatedPath) {
        E relatedNode = relatedPath.getElement();

        E closest = closestNodeWorkPathElement.getElement();

        // Do not check items that are already evaluated
        WorkPathElement<E, R> relatedNodeWorkPathElement = remainingNodes.getWorkPathElement(relatedNode);
     //   relatedNodeWorkPathElement.setDistance(relatedPath.getDistance());

        if (!relatedNodeWorkPathElement.isEvaluated()) {

            // distance = current distance from source + distance(current note,
            // neighbour) (always 1 in OneCRMCore)
            int distance = closestNodeWorkPathElement.getDistance() + relatedPath.getDistance();

            // check if the new distance is closer
            int oldDistance = relatedNodeWorkPathElement.getDistance();

            if (oldDistance > distance) {
                // update the path element with ne distance/predecessor info
                relatedNodeWorkPathElement.setDistance(distance);
                relatedNodeWorkPathElement.setPredecessor(closest);
                relatedNodeWorkPathElement.setRelation(relatedPath.getRelation());

                // re-balance the related Node using the new shortest distance
                // found
                remainingNodes.push(relatedNode);
            }
        }
    }

}
