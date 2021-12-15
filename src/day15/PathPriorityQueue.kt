package day15

import java.io.Serializable
import java.util.*

class PathPriorityQueue<E> : Serializable {
    /**
     * map containing path elements (a path element is a node in the network,
     * the distance to the source and the current predecessor)
     */
    private val workPathElements: MutableMap<E, WorkPathElement<E>> = HashMap()

    /**
     * a set of nodes that are still possible choices for the path, sorted by lowest distance
     */
    private val remainingNodesToEvaluate = PriorityQueue<E>(100, compareBy { getDistance(it) })

    private fun getDistance(o1: E): Int {
        val workPathElement = workPathElements[o1]!!
        return if (workPathElement.isEvaluated) Int.MAX_VALUE else workPathElement.distance
    }

    /**
     * add a node to the list of remaining nodes
     *
     * @param element
     */
    fun push(element: E) {
        remainingNodesToEvaluate.add(element)
    }

    /**
     * get the node from the remaining nodes that is the shortest path from
     * source the node is removed from the list of remaining nodes and returned
     *
     * @return the "closest" node
     */
    fun pop(): E? {
        return remainingNodesToEvaluate.poll()
    }

    /**
     * returns the WorkPathElement from the working map. if a path element cannot be
     * found for the node, build a new one
     *
     * @param node for which the path element is sought
     * @return the path element describing how the node is used in the path
     * search
     */

    fun getWorkPathElement(node: E): WorkPathElement<E> {
        return workPathElements.getOrPut(node) { WorkPathElement(node) }
    }

}
