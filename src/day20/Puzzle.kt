package day20

import utils.asBinary
import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


data class Input(val algo: List<Char>, val grid: List<List<Char>>, val defaultChar: Char = '.') {
    val s = grid.joinToString("\n") { it.joinToString("") }


    private fun makeEmptyLine(wished: Int) = (0 until wished).map { defaultChar }

    fun enhance(): Input {
        val newDots = 2
        val newLine: List<List<Char>> = (0 until newDots).map { makeEmptyLine(grid[0].size + 2 * newDots) }
        val addColumns: List<List<Char>> = grid.map {
            makeEmptyLine(newDots) + it + makeEmptyLine(newDots)
        }
        val newGrid: List<List<Char>> = newLine + addColumns + newLine

        val mutable = newGrid.map { it.toMutableList() }.toMutableList()
        val enhanced = newGrid.mapIndexed { y, line ->
            line.mapIndexed { x, c ->
                val w = getWeight(x, y, mutable)
                algo[w.asBinary()].toChar()
            }
        }
        val newDef: Char = if (defaultChar == '.') {
            algo[0]
        } else {
            algo["111111111".asBinary()]
        }
        return Input(algo, enhanced, newDef)
    }

    private fun getWeight(x: Int, y: Int, mutable: MutableList<MutableList<Char>>): String {
        return (-1..1).map { dy ->
            (-1..1).map { dx ->
                try {
                    mutable[y + dy][x + dx]
                } catch (e: IndexOutOfBoundsException) {
                    defaultChar
                }
            }.map { if (it == '.') '0' else '1' }

        }

            .joinToString("")
            { it.joinToString("") }
    }
}

class Puzzle {
    fun clean(input: List<String>): Input {
        val algo = input[0].toList()
        val grid: List<List<Char>> = input.subList(2, input.size)
            .filter { line -> true }
            .map { line -> line.toList() }
        return Input(algo, grid)
    }

    val part1ExpectedResult = 35L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

//        val enhance1 = input.enhance();
//        println("enhance1: \n${enhance1.s}")
//        val enhance2 = enhance1.enhance();
//        println("enhance2: \n${enhance2.s}")

        val enhanced = (0 until 2).fold(input) { acc, _ -> acc.enhance() }
        val pixelCount = enhanced.grid.sumOf { line -> line.count { it == '#' } }.toLong()
        // 5378 too low
        // 6175 too hiugh
        // 6782
        // 6036 too high
        return pixelCount
    }

    val part2ExpectedResult = 3351L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val enhanced = (0 until 50).fold(input) { acc, _ -> acc.enhance() }
        val pixelCount = enhanced.grid.sumOf { line -> line.count { it == '#' } }.toLong()
        return pixelCount
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
