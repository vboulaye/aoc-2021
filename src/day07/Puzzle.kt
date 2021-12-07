package day07

import utils.max
import utils.min
import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime


typealias Result = Long

class Puzzle {

    fun clean(input: List<String>): List<Int> {
        return input.flatMap { it.split(",") }.map { it.toInt() }
    }

    data class FuelForPos(val index: Int, val counters: List<Result>, val total: Result)

    private fun computeFuelUse(positions: List<Int>, fuelUseComputer: (Int) -> Long): Long {
        val fuelConsumption = (positions.min()..positions.max()).map { index ->
            val counters = positions.map { fuelUseComputer(it - index) }
            FuelForPos(index, counters, counters.sum())
        }
        return fuelConsumption.minOf { it.total }
    }

    val part1ExpectedResult = 37L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        return computeFuelUse(input) { kotlin.math.abs(it).toLong() }
    }

    fun fact(i: Int): Long = if (0 == i) 0 else i + fact(i - 1)

    // https://www.baeldung.com/kotlin/tail-recursion
    tailrec fun factorial(n: Int, accum: Long = 0): Long {
        val soFar = n + accum
        return if (n <= 1) {
            soFar
        } else {
            factorial(n - 1, soFar)
        }
    }

    val part2ExpectedResult = 168L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        return computeFuelUse(input) { factorial(kotlin.math.abs(it)) }
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
            check(testResult == expectedTestResult)
            println("test ${part}: $testResult == ${expectedTestResult}")
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
