package utils

import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max

fun euclidLength(from: Point, to: Point): Double = hypot((from.x - to.x).toDouble(), (from.y - to.y).toDouble())
fun gridLength(from: Point, to: Point): Int = max(abs(from.x - to.x), abs(from.y - to.y))

fun computeStep(from: Int, to: Int) = when {
    from < to -> 1
    from > to -> -1
    else -> 0
}
data class Vector(val from: Point, val to: Point) {
    fun euclidLength(): Double = euclidLength(from, to)
    fun gridLength(): Int = gridLength(from, to)
    fun isHorizontal(): Boolean = from.y == to.y
    fun isVertical(): Boolean = from.x == to.x
    fun isDiagonal(): Boolean = abs(from.x - to.x) == abs(from.y - to.y)

}
