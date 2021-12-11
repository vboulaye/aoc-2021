package day11

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


class Puzzle {

    fun clean(input: List<String>): List<List<Int>> {
        return input.map { it.toList().map { it.code - '0'.code } }
    }

    val part1ExpectedResult = 1656L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        var count: Long = 0
        (0..99).fold(input) { acc, it ->
            val newState: List<List<Int>> = step(acc)
            val flashes = countFlashes(newState)
            count += flashes
            newState
        }

        return count;
    }

    private fun step(input: List<List<Int>>): List<List<Int>> {


        val newState = input.map { line ->
            line.map { octopus ->
                octopus + 1
            }.toMutableList()
        }.toMutableList()

        val hasFlashed = input.map { line ->
            line.map { octopus ->
                false
            }.toMutableList()
        }.toMutableList()

        //    val printed=         newState.joinToString("\n") {  it.joinToString { it.toString() } }
        //  System.err.println(printed        +"\n")

        newState.forEachIndexed { y, line ->
            line.forEachIndexed { x, octopus ->
                if (octopus > 9 && !hasFlashed[y][x]) {
                    hasFlashed[y][x] = true
                    flashAround(y, x, newState, hasFlashed)
                }

            }
        }
        val newsState2 = newState.map { line ->
            line.map { octopus ->
                if (octopus > 9) 0 else octopus
            }
        }
        //val printed2=         newsState2.joinToString("\n") {  it.joinToString { it.toString() } }
        //  System.err.println(printed2        +"\n")
        return newsState2
    }

    private fun flashAround(
        y: Int,
        x: Int,
        newState: MutableList<MutableList<Int>>,
        hasFlashed: MutableList<MutableList<Boolean>>
    ) {
        (-1..1).forEach { dy ->
            (-1..1).forEach { dx ->
                val testX = x + dx
                val testY = y + dy
                if (!(dx == 0 && dy == 0)) {
                    try {
                        val i = newState[testY][testX]
                        newState[testY][testX] = i + 1
                        if (newState[testY][testX] > 9 && !hasFlashed[testY][testX]) {
                            hasFlashed[testY][testX] = true
                            flashAround(testY, testX, newState, hasFlashed)
                        }
                    } catch (e: Exception) {

                    }
                }
            }

        }
    }

    val part2ExpectedResult = 195L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        var count: Long = 0
        var index = 0
        var newState = input
        while (countFlashes(newState) != 100) {

             newState= step(newState)
            index++
        }

        return index.toLong();
    }

    private fun countFlashes(newState: List<List<Int>>) =
        newState.sumOf { line: List<Int> -> line.count { it == 0 } }

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
