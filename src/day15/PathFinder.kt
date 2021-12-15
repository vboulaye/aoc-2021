package day15


/**
 * interface that must be implemented by callers to PathFinder in order to
 * describe the network For each node the find related shows where to go next
 */
interface FindRelated<E, R> {
    /**
     * return the list of path elements that exist in the network from the node
     * o the path element contains related objects and the relation to gets
     * there
     *
     * @param o
     * @return
     */
    fun findRelated(o: WorkPathElement<E>?): List<WorkPathElement<E>?>?
}

/**
 * utility class used to find the shortest path between 2 nodes inside a network
 * the FindRelated implementation gives a node by node description of the
 * network for one node the findRelated() method returns all related nodes The
 * search of the shortest path is a simplification of the dijkstra algorithm
 */
class PathFinder<E, R>(findRelated: FindRelated<E, R>?) {
    /**
     * the FindRelated implementation that describes the network
     */
    private val findRelated: FindRelated<E, R>

    /**
     * constructor, sets the FindRelated implementation
     *
     * @param findRelated
     */
    init {
        requireNotNull(findRelated) { "findRelated implementation must be not null" }
        this.findRelated = findRelated
    }

    /**
     * build the shortest path from one node to another in the network described
     * by the FindRelated implementation
     *
     * @param sourceNode source node object
     * @param targetNode target node object
     * @return the path as a list of nodes
     */
    fun findPath(sourceNode: E, targetNode: E): List<WorkPathElement<E>> {
        val remainingNodes = computePath(sourceNode, targetNode)
        return buildPath(sourceNode, targetNode, remainingNodes)
    }

    fun findDistance(sourceNode: E, targetNode: E): Int {
        val remainingNodes = computePath(sourceNode, targetNode)
        return remainingNodes.getWorkPathElement(targetNode).distance
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
    private fun computePath(sourceNode: E, targetNode: E): PathPriorityQueue<E> {
        // set of nodes that are still possible choices for the path
        val remainingNodes = PathPriorityQueue<E>()

        // add source node with a distance of 0
        remainingNodes.getWorkPathElement(sourceNode).distance = 0
        remainingNodes.push(sourceNode)

        // work on the remaining node that is currently seen as the closest to
        // the source
        var closest = remainingNodes.pop()

        // loop until there is no more nodes or if the destination is reached
        while (closest != null && closest != targetNode) {


            // flag the node in order not to evaluate it twice
            val closestNodeWorkPathElement = remainingNodes.getWorkPathElement(closest)
            closestNodeWorkPathElement.isEvaluated = true

            // update the distance of all related nodes of the work node by
            // evaluating the impact of using it as a predecessor
            val related = findRelated.findRelated(closestNodeWorkPathElement)
            if (related != null && !related.isEmpty()) {
                for (relatedPath in related) {
                    processRelatedNode(remainingNodes, closestNodeWorkPathElement, relatedPath)
                } // end for all related nodes
            } // if there exist related nodes
            closest = remainingNodes.pop()
        } // while there are still some nodes to evaluate
        return remainingNodes
    }

    /**
     * build shortest path from calculated nodes path elements
     *
     * @param sourceNode     source node
     * @param targetNode     target node
     * @param remainingNodes Map of evaluated nodes
     * @return List of WorkPathElement from source to target
     */
    private fun buildPath(
        sourceNode: E,
        targetNode: E,
        remainingNodes: PathPriorityQueue<E>
    ): List<WorkPathElement<E>> {


        // only build the path if the distance if not infinite (a path was  actually found)
        if (remainingNodes.getWorkPathElement(targetNode).distance == Int.MAX_VALUE) {
            return emptyList<WorkPathElement<E>>()
        }

        val path = emptyList<WorkPathElement<E>>().toMutableList()

        // we add the destination node and all its predecessors (processing
        // backwards)
        var predecessor: E? = targetNode
        while (predecessor != null && predecessor != sourceNode) {
            // put the predecessor as the first node
            val predecessorElement = remainingNodes.getWorkPathElement(predecessor)
            path.add(predecessorElement)
            predecessor = predecessorElement.predecessor
        }

        // add the init node
        path.add(remainingNodes.getWorkPathElement(sourceNode))
        return path
    }

    private fun processRelatedNode(
        remainingNodes: PathPriorityQueue<E>, closestNodeWorkPathElement: WorkPathElement<E>,
        relatedPath: WorkPathElement<E>?
    ) {
        val relatedNode = relatedPath!!.element
        val closest = closestNodeWorkPathElement.element

        // Do not check items that are already evaluated
        val relatedNodeWorkPathElement = remainingNodes.getWorkPathElement(relatedNode)
        if (!relatedNodeWorkPathElement.isEvaluated) {

            // distance = current distance from source + distance(current note, neighbour)
            val distance = closestNodeWorkPathElement.distance + relatedPath.distance

            // check if the new distance is closer
            val oldDistance = relatedNodeWorkPathElement.distance
            if (oldDistance > distance) {
                // update the path element with ne distance/predecessor info
                relatedNodeWorkPathElement.distance = distance
                relatedNodeWorkPathElement.predecessor = closest

                // re-balance the related Node using the new shortest distance
                // found
                remainingNodes.push(relatedNode)
            }
        }
    }
}
