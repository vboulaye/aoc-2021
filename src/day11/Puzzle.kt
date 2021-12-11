package day11

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


class Puzzle {

    fun clean(input: List<String>): List<List<Int>> {
        return input.map { line -> line.windowed(1).map { numberString -> numberString.toInt() } }
    }

    val part1ExpectedResult = 1656L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        return iterate(input, 100);
    }

    private fun iterate(input: List<List<Int>>, i: Int): Long {
        if (i == 0) return 0
        val newState: List<List<Int>> = OctopusGrid(input).flashes()
        val flashes = countFlashes(newState)
        return flashes + iterate(newState, i - 1)
    }

    data class OctopusGrid(val input: List<List<Int>>) {
        val array: MutableList<MutableList<Int>> = input.map { line -> line.toMutableList() }.toMutableList()

        fun flashes(): List<List<Int>> {
            array.forEachIndexed { y, line ->
                line.forEachIndexed { x, octopusLevel ->
                    increaseLevel(y, x)
                }
            }
            return resetFlashedLevels()
        }

        private fun resetFlashedLevels() = array.map { line -> line.map { octopus -> if (octopus > 9) 0 else octopus } }

        private fun flashAround(y: Int, x: Int) {
            (-1..1).forEach { dy ->
                (-1..1).forEach { dx ->
                    if (!(dx == 0 && dy == 0)) {
                        val testX = x + dx
                        val testY = y + dy
                        increaseLevel(testY, testX)
                    }
                }
            }
        }

        private fun increaseLevel(y: Int, x: Int) {
            val level = try {
                array[y][x]
            } catch (e: Exception) {
                return
            }
            array[y][x] = level + 1
            if (array[y][x] == 10) {
                flashAround(y, x)
            }
        }
    }

    private fun getSynchronizedIndex(input: List<List<Int>>, index: Long = 0): Long {
        if (countFlashes(input) == 100) return index
        val newState: List<List<Int>> = OctopusGrid(input).flashes()
        return getSynchronizedIndex(newState, index + 1)
    }

    val part2ExpectedResult = 195L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        return getSynchronizedIndex(input);
    }

    private fun countFlashes(newState: List<List<Int>>) = newState.sumOf { line -> line.count { it == 0 } }

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
