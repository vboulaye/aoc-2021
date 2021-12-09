package day09

import utils.readInput
import java.lang.Math.abs
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


data class Point(val x: Int, val y: Int)

data class Grid(val array:List<List<Int>> ) {

    fun computeRisks(): List<List<Int>> {
        val risks: List<List<Int>> = array.mapIndexed { y, line ->
            line.mapIndexed eval@{ x, value ->
                val current = value

                loop@ (-1..1).forEach { dy ->
                    (-1..1).forEach { dx ->
                        if (abs(dx) + abs(dy) <= 1) {
                            if (!(dx == 0 && dy == 0)) {
                                try {
                                    if (array[y + dy][x + dx] < current) return@eval 0
                                } catch (e: Exception) {

                                }
                            }
                        }
                    }
                }
                return@eval (current) + 1
            }
        }
        return risks
    }

    fun computeBasins(): List<List<Int>> {
        val risks: List<List<Int>> = this.computeRisks()
        val basins: List<List<Int>> = risks.mapIndexed { y, line ->
            line
                .mapIndexed eval@{ x, value ->
                    if (value > 0) {
                        tested.clear()
                        val size = getneighboursSize(x, y, input)
                        size
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
        return Grid(input.map { it.toList().map { it.toString().toInt() } })
    }

    private fun getSafe(input: List<List<Int>>, y: Int, i1: Int): Int {
        try {
            return input[y][i1]
        } catch (e: Exception) {
            return 9
        }
    }
    val part1ExpectedResult = 15L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val risks: List<List<Int>> = input.computeRisks()
        return risks.flatMap { it.toList() }.sum().toLong()
    }



    val part2ExpectedResult = 1134L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val basins: List<List<Int>> = input.computeBasins(input)
        return basins.flatMap { it.toList() }.sorted().reversed().subList(0, 3).fold(1) { acc, i -> acc * i }.toLong()
    }




    val tested = HashSet<Point>()
    private fun getneighboursSize(x: Int, y: Int, input: List<List<Int>>): Int {
        if (!tested.add(Point(x, y))) return 0
        val sizePoints = (-1..1).map { dy ->
            (-1..1).map { dx ->
                if (abs(dx) + abs(dy) <= 1) {
                    if (!(dx == 0 && dy == 0)) {
                        try {
                            val testY = y + dy
                            val testX = x + dx
                            if (input[testY][x + dx] < 9) {
                                getneighboursSize(testX, testY, input)
                            } else 0
                        } catch (e: Exception) {
                            0
                        }
                    } else {
                        val r = input[y][x]
                        1
                    }

                } else 0
            }
        }
        return sizePoints.sumOf { it.sum() }
    }

    private fun getMax(dy: Int, dx: Int, input: List<List<Int>>, x: Int, y: Int): Int {
        var testval = 0
        var i = 0
        while (testval < 9) {
            i++
            testval = getSafe(input, y + i * dy, x + i * dx)
        }
        return i - 1
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
