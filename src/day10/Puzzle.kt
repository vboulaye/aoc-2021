package day10

import utils.readInput
import java.math.BigInteger
import kotlin.math.abs
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


class Puzzle {
    val cars = mapOf(
        ')' to 3,
        ']' to 57,
        '}' to 1197,
        '>' to 25137,
    )

    val matching = mapOf(
        '(' to ')',
        '[' to ']',
        '{' to '}',
        '<' to '>',
    )

    fun clean(input: List<String>): List<List<Char>> {
        return input.map { it.toList() }
    }


    val part1ExpectedResult = 26397L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val errors: List<Char> = input.map line@{
            return@line incompletChar(it)
        }
        val frequency: Map<Char, Int> = errors.groupingBy { it }.eachCount()
        return frequency.map { it.value*(cars[it.key]?:0) }.sum().toLong();
    }

    private fun incompletChar(it: List<Char>): Char {
        val stack = ArrayDeque<Char>()
        it.forEach { char ->
            if (matching.containsKey(char)) {
                stack.addFirst(char)
            } else {
                val top = stack[0]
                if (char == matching[top]!!) {
                    stack.removeFirst()
                } else {
                    return char;
                }
            }

        }
        return ' '
    }

    private fun missingChar(it: List<Char>): ArrayDeque<Char> {
        val stack = ArrayDeque<Char>()
        it.forEach { char ->
            if (matching.containsKey(char)) {
                stack.addFirst(char)
            } else {
                val top = stack[0]
                if (char == matching[top]!!) {
                    stack.removeFirst()
                } else {
                    return ArrayDeque<Char>();
                }
            }

        }
        return stack
    }

    val cars2 = mapOf(
        ')' to 1,
        ']' to 2,
        '}' to 3,
        '>' to 4,
    )
    val part2ExpectedResult = 288957L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val completed = input
            .filter { incompletChar(it) == ' ' }
        val missing: List<Long> = completed
            .map {
            missingChar(it).map { matching[it]!! } .map { cars2[it]!! }
                .fold(BigInteger.ZERO) { acc, it -> acc.multiply(BigInteger("5")).plus(BigInteger(it.toString()))  }
                .toLong()
        }

        return missing.sorted()[missing.size/2].toLong()
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
