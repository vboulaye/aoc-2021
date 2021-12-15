package day15

import utils.Point
import utils.readInput
import java.util.*
import kotlin.math.abs
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


data class Grid(val array: List<List<Int>>) {

}

class Finder(val grid: Grid) : FindRelated<Point, Pair<Point, Point>> {

    override fun findRelated(p: WorkPathElement<Point, Pair<Point, Point>>?): List<WorkPathElement<Point, Pair<Point, Point>>> {
        if (p == null) {
            return emptyList()
        }

        val y = p.element.y
        val x = p.element.x
        val neighbours: List<WorkPathElement<Point, Pair<Point, Point>>> = (-1..1).flatMap { dy ->
            (-1..1).filter { dx ->
                (abs(dx) + abs(dy) == 1)
            }
                .mapNotNull { dx ->
                    try {
                        val (x1, y1) = Point(x + dx, y + dy)
                        val point = Point(x + dx, y + dy)
                        val workPathElement = WorkPathElement<Point, Pair<Point, Point>>(point)
                        workPathElement.distance = grid.array[y1][x1]// - grid.array[y][x]
                        workPathElement.relation = p.element to point
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
        val finder = Finder(input)
        val pathFinder = PathFinder(finder)
        val pos = Point(0, 0);
        val findPath = pathFinder.findPath(pos, Point(input.array[0].size - 1, input.array.size - 1))
        return findPath[0].distance.toLong()
        //return ( findPath.sumOf { it.distance }).toLong()
    }

    val part2ExpectedResult = 315L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val height = input.array.size
        val width = input.array[0].size
        val array2 = MutableList(5 * height) { MutableList(5 * width) { 0 } }
        input.array.forEachIndexed { y, line ->
            line.forEachIndexed { x, risk ->
                (0..4).forEach { yFactor ->
                    (0..4).forEach { xFactor ->
                        val newRisk = (risk + xFactor + yFactor-1) % 9 +1
                        val newY = y + yFactor * height
                        val newX = x + xFactor * width
                        array2[newY][newX] = newRisk
                    }
                }
            }
        }
        val joinToString = array2.joinToString("\n") { it.joinToString("") { it.toString() } }
       // System.err.println(joinToString)
        val finder = Finder(Grid(array2))
        val pathFinder = PathFinder(finder)
        val pos = Point(0, 0);
        val findPath = pathFinder.findPath(pos, Point(array2[0].size - 1, array2.size - 1))
        return findPath[0].distance.toLong()
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
