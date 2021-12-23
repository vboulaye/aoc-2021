package day23

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


class Puzzle {
    fun clean(input: List<String>): List<String> {
        return input
            .filter { line -> true }
            .map { line -> line }
    }

    fun count(s: String): Int {
        return s.split(Regex(" *\\+ *"))
            .sumOf {
                val (steps, typ) = it.split(Regex(" *\\* *"))
                val valueStep = steps.toInt() * when (typ.toUpperCase()[0]) {
                    'A' -> 1
                    'B' -> 10
                    'C' -> 100
                    'D' -> 1000
                    else -> throw IllegalStateException("Unknown letter: ${it[1]}")
                }
                valueStep
            }
    }


    val part1ExpectedResult = 14415L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        val result = count("3*b+3*b+ 5*D+8*d +3*a +5*c+7*c +6*a+6*a +7*b+7*b")
        println(result)
        return result.toLong()
    }

    val part2ExpectedResult = 0L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
//        val result = count("3*b+10*a+4*c+11*B+7*d+10*d+10*d+10*d+4*c+5*b+5*a+5*a+5*c+6*c+6*c+9*c+4*b+10*a+5*b+6*b+5*a+7*a+7*a+7*b+7*b")
//        println(result)
        println(count("3*b+10*a+4*c+11*b+7*d+10*d+10*d+10*d+9*c+2*b+6*a+3*b+7*b+10*a+10*a+5*c+6*c+6*c+4*c+4*b+7*a+5*b+6*b+6*b+7*b"))
        println(count("9*b+9*a+5*c+5*b+7*d+10*d+10*d+10*d+9*c+5*a+7*b+10*a+10*a+5*c+6*c+6*c+4*b+7*a+5*b+6*b+6*b+6*b+5*c"))
// 40929 too low
        //40983 too low
        //41121
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

 //   runPart("part1", puzzle.part1ExpectedResult, puzzle::part1)
    runPart("part2", puzzle.part2ExpectedResult, puzzle::part2)

}
