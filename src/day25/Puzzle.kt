package day25

import utils.readInput
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


data class Sea(val array: List<List<Char>>) {
    val width = array[0].size
    val height = array.size
    val hasMoved = AtomicBoolean()

    fun step(): Sea? {
        val moveEast = moveEast()

        val moveSouth = moveEast.moveSouth()
        if(hasMoved.get()||moveEast.hasMoved.get()) {
            return moveSouth
        }
        return null
    }

    private fun moveSouth(
    ): Sea {
        val moveSouth = array.mapIndexed { y, line ->
            line.mapIndexed { x, cucumber ->
                if (get(y, x) == 'v' && get(y + 1, x) == '.') {
                    hasMoved.set(true)
                    '.'
                } else if (get(y, x) == '.' && get(y - 1, x) == 'v') {
                    hasMoved.set(true)
                    'v'
                } else {
                    cucumber
                }
            }
        }
        return Sea(moveSouth)
    }

    private fun moveEast(): Sea {
        val moveEast = Sea(array.mapIndexed { y, line ->
            line.mapIndexed { x, cucumber ->
                if (get(y, x) == '>' && get(y, x + 1) == '.') {
                    hasMoved.set(true)
                    '.'
                } else if (get(y, x) == '.' && get(y, x - 1) == '>') {
                    hasMoved.set(true)
                    '>'
                } else {
                    cucumber
                }
            }
        })
        return moveEast
    }

    private fun get(y: Int, x: Int) = array[(y + height) % height][(x + width) % width]
}

class Puzzle {
    fun clean(input: List<String>): List<List<Char>> {
        return input
            .filter { line -> true }
            .map { line -> line.toList() }
    }

    val part1ExpectedResult = 58L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        var sea: Sea? = Sea(input)
        var i=0
        while(sea!=null) {
            i++
            sea = sea.step()
        }
        return i.toLong()
    }

    val part2ExpectedResult = 0L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)

        return 0
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
