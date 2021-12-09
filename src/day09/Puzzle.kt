package day09

import utils.readInput
import kotlin.math.abs
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


fun List<List<Int>>.get(x: Int, y: Int, default: Int = 0): Int {
    return try {
        this[y][x]
    } catch (e: IndexOutOfBoundsException) {
        default
    }
}


data class Grid(val array: List<List<Int>>) {

    fun computeRisks(): List<List<Int>> {
        val risks: List<List<Int>> =
            array.mapIndexed { y, line ->
                line.mapIndexed eval@{ x, value ->
                    (-1..1).forEach { dy ->
                        (-1..1).forEach { dx ->
                            if (abs(dx) + abs(dy) == 1) {
                                if (array.get(x + dx, y + dy, 9) < value) return@eval 0
                            }
                        }
                    }
                    return@eval (value) + 1
                }
            }
        return risks
    }

    data class NeighbourSizer(val array: List<List<Int>>) {

        data class Point(val x: Int, val y: Int)

        private val testedPoints = HashSet<Point>()

        fun getNeighboursSize(x: Int, y: Int): Int {
            if (!testedPoints.add(Point(x, y))) return 0
            val sizePoints =
                (-1..1).map { dy ->
                    (-1..1).map { dx ->
                        when (abs(dx) + abs(dy)) {
                            0 -> 1 // count the center point
                            1 -> { // this is a close neighbour
                                val testY = y + dy
                                val testX = x + dx
                                if (array.get(testX, testY, 9) < 9) {
                                    getNeighboursSize(testX, testY)
                                } else {
                                    0
                                }
                            }
                            else -> 0 // this is a diagonal => do not test
                        }
                    }
                }
            return sizePoints.sumOf { it.sum() }
        }
    }

    fun computeBasins(): List<List<Int>> {
        val risks: List<List<Int>> = this.computeRisks()
        val basins: List<List<Int>> = risks.mapIndexed { y, line ->
            line.mapIndexed eval@{ x, value ->
                if (value > 0) {
                    NeighbourSizer(array).getNeighboursSize(x, y)
                } else {
                    0
                }
            }
        }
        return basins
    }


}


class Puzzle {

    fun clean(input: List<String>): Grid {
        return Grid(input.map { line -> line.toList().map { character -> character - '0' } })
    }

    val part1ExpectedResult = 15L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val risks = input.computeRisks()
        return risks.sumOf { it.sum() }.toLong()
    }

    val part2ExpectedResult = 1134L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val basins = input.computeBasins()
        return basins.flatMap { it.toList() }
            .sortedDescending()
            .take(3)
            .fold(1) { acc, i -> acc * i }
            .toLong()
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
            check(testResult == expectedTestResult)
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
