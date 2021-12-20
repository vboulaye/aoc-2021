package day20

import utils.asBinary
import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


data class Input(val algo: List<Char>, val grid: List<List<Char>>, val defaultChar: Char = '.') {
    val s = grid.joinToString("\n") { it.joinToString("") }
    override fun toString(): String = s

    private fun makeEmptyLine(wished: Int) = List(wished) { defaultChar }

    fun enhance(): Input {
        val newGrid: List<List<Char>> = expandGrid(2)
        val enhanced = newGrid.mapIndexed { y, line ->
            line.mapIndexed { x, _ ->
                algo[getWeight(x, y, newGrid).asBinary()]
            }
        }
        val newDef: Char = if (defaultChar == '.') {
            algo["0".repeat(9).asBinary()]
        } else {
            algo["1".repeat(9).asBinary()]
        }
        return Input(algo, enhanced, newDef)
    }

    private fun expandGrid(newDots: Int): List<List<Char>> {
        val newLine: List<List<Char>> = List(newDots) { makeEmptyLine(grid.size + 2 * newDots) }
        val addColumns: List<List<Char>> = grid.map {
            makeEmptyLine(newDots) + it + makeEmptyLine(newDots)
        }
        val newGrid: List<List<Char>> = newLine + addColumns + newLine
        return newGrid
    }

    private fun getWeight(x: Int, y: Int, grid: List<List<Char>>): String {
        val viewDimension = -1..1
        val view = viewDimension.map { dy ->
            viewDimension.map { dx ->
                get(y + dy, x + dx, grid)
            }.map { if (it == '.') '0' else '1' }
        }
        val viewAsString = view.joinToString("") { it.joinToString("") }
        return viewAsString
    }

    private fun get(y1: Int, x1: Int, grid: List<List<Char>>) = try {
        grid[y1][x1]
    } catch (e: IndexOutOfBoundsException) {
        defaultChar
    }

    fun count() = grid.sumOf { line -> line.count { it == '#' } }.toLong()

}

class Puzzle {
    fun clean(input: List<String>): Input {
        val algo = input[0].toList()
        val grid: List<List<Char>> = input
            .subList(2, input.size)
            .map { line -> line.toList() }
        return Input(algo, grid)
    }

    val part1ExpectedResult = 35L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val enhanced = (0 until 2).fold(input) { acc, _ -> acc.enhance() }
        return enhanced.count()
    }

    val part2ExpectedResult = 3351L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val enhanced = (0 until 50).fold(input) { acc, _ -> acc.enhance() }
        return enhanced.count()
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
