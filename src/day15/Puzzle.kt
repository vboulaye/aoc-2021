package day15

import utils.Point
import utils.readInput
import kotlin.math.abs
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


data class Grid(val array: List<List<Int>>) {
    val height = array.size
    val width = array[0].size

    val sourceNode = Point(0, 0);
    val targetNode = Point(width - 1, height - 1)

    val pathFinder = PathFinder(Finder(this))

    fun findDistance(): Long {
        return pathFinder.findDistance(sourceNode, targetNode).toLong()
    }

    fun expandGrid(
        expansionFactor: Int
    ): Grid {
        val array2 = MutableList(expansionFactor * height) { MutableList(expansionFactor * width) { 0 } }
        array.forEachIndexed { y, line ->
            line.forEachIndexed { x, risk ->
                (0 until expansionFactor).forEach { yFactor ->
                    (0 until expansionFactor).forEach { xFactor ->
                        val newRisk = (risk + xFactor + yFactor - 1) % 9 + 1
                        val newY = y + yFactor * height
                        val newX = x + xFactor * width
                        array2[newY][newX] = newRisk
                    }
                }
            }
        }
        return Grid(array2)
    }
}

class Finder(val grid: Grid) : FindRelated<Point, Pair<Point, Point>> {

    override fun findRelated(p: WorkPathElement<Point>?): List<WorkPathElement<Point>> {
        if (p == null) {
            return emptyList()
        }

        val y = p.element.y
        val x = p.element.x
        val neighbours: List<WorkPathElement<Point>> = (-1..1).flatMap { dy ->
            (-1..1).filter { dx ->
                (abs(dx) + abs(dy) == 1)
            }
                .mapNotNull { dx ->
                    try {
                        val (x1, y1) = Point(x + dx, y + dy)
                        val point = Point(x + dx, y + dy)
                        val workPathElement = WorkPathElement(point)
                        workPathElement.distance = grid.array[y1][x1]
                        workPathElement
                    } catch (e: Exception) {
                        null
                    }
                }
        }


        return neighbours
    }
}

class Puzzle {

    fun clean(input: List<String>): Grid {
        return Grid(input.filter { true }.map { it.toList().map { it.code - '0'.code } })
    }

    val part1ExpectedResult = 40L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        return input.findDistance()
    }

    val part2ExpectedResult = 315L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val expandedGrid = input.expandGrid(5)
        // val joinToString = array2.joinToString("\n") { it.joinToString("") { it.toString() } }
        // System.err.println(joinToString)
        return expandedGrid.findDistance()
    }


}

@OptIn(ExperimentalTime::class)
fun main() {
    val puzzle = Puzzle()
    println(Puzzle::class.qualifiedName)

    val testInput = readInput("00test", Puzzle::class)
    val input = readInput("zzdata", Puzzle::class)

    fun runPart(part: String, expectedTestResult: Result, partEvaluator: (List<String>) -> Result) {
        val testDuration = measureTime {
            val testResult = partEvaluator(testInput)
            println("test ${part}: $testResult == ${expectedTestResult}")
            check(testResult == expectedTestResult) { "$testResult != ${expectedTestResult}" }
        }
        val fullDuration = measureTime {
            val fullResult = partEvaluator(input)
            println("${part}: $fullResult")
        }
        println("${part}: test took ${testDuration.inWholeMilliseconds}ms, full took ${fullDuration.inWholeMilliseconds}ms")
    }

    runPart("part1", puzzle.part1ExpectedResult, puzzle::part1)
    runPart("part2", puzzle.part2ExpectedResult, puzzle::part2)

}
