package day10

import utils.readInput
import java.math.BigInteger
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


val matchingCharacters = mapOf(
    '(' to ')',
    '[' to ']',
    '{' to '}',
    '<' to '>',
)

class Puzzle {

    fun clean(input: List<String>): List<List<Char>> {
        return input.map { it.toList() }
    }

    object Part1 {
        val points = mapOf(
            ')' to 3,
            ']' to 57,
            '}' to 1197,
            '>' to 25137,
        )
    }

    val part1ExpectedResult = 26397L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val errorLines: List<Char> = input.mapNotNull { parseLine(it, GetErrorCharacter()) }
        val frequency: Map<Char, Int> = errorLines.groupingBy { it }.eachCount()
        return frequency.map { it.value * (Part1.points[it.key]!!) }.sum().toLong();
    }

    interface ParsingHandler<ResultType> {
        fun onError(stack: ArrayDeque<Char>, currentCharacter: Char): ResultType
        fun onEnd(stack: ArrayDeque<Char>): ResultType
    }

    private fun <T> parseLine(it: List<Char>, parsingHandler: ParsingHandler<T>): T {
        val stack = ArrayDeque<Char>()
        it.forEach { currentCharacter ->
            if (matchingCharacters.containsKey(currentCharacter)) {
                // open chunk
                stack.addFirst(currentCharacter)
            } else {
                val top = stack[0]
                if (currentCharacter == matchingCharacters[top]!!) {
                    // close chunk
                    stack.removeFirst()
                } else {
                    // error chunk
                    return parsingHandler.onError(stack, currentCharacter)
                }
            }
        }
        return parsingHandler.onEnd(stack)
    }

    class GetErrorCharacter : ParsingHandler<Char?> {
        override fun onError(stack: ArrayDeque<Char>, currentCharacter: Char): Char? {
            return currentCharacter;
        }

        override fun onEnd(stack: ArrayDeque<Char>): Char? {
            return null
        }
    }

    class GetRemainingOpenCharacters : ParsingHandler<ArrayDeque<Char>?> {
        override fun onError(stack: ArrayDeque<Char>, currentCharacter: Char): ArrayDeque<Char>? {
            return null;
        }

        override fun onEnd(stack: ArrayDeque<Char>): ArrayDeque<Char>? {
            return stack
        }
    }

    object Part2 {
        val fiveBigInteger = BigInteger("5")
        val points = mapOf(
            ')' to 1,
            ']' to 2,
            '}' to 3,
            '>' to 4,
        )
    }

    val part2ExpectedResult = 288957L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val missingCharactersScores: List<Long> = input
            .mapNotNull { parseLine(it, GetRemainingOpenCharacters()) }
            .map {
                it.map { remainingOpenCharacters -> matchingCharacters[remainingOpenCharacters]!! }
                    .map { closeCharacter -> Part2.points[closeCharacter]!! }
                    .fold(BigInteger.ZERO) { acc, points ->
                        acc.multiply(Part2.fiveBigInteger).plus(BigInteger.valueOf(points.toLong()))
                    }
                    .toLong()
            }
        return missingCharactersScores.sorted()[missingCharactersScores.size / 2]
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
