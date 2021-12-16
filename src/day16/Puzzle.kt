package day16

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


class Puzzle {

    fun clean(input: List<String>): List<String> {
        return input.map { x: String -> x.toList() }
            .flatMap { a: List<Char> ->
                a.map {
                    Integer.toBinaryString(it.toString().toInt(16))
                        .padStart(4, '0')
                }
            }
    }

    val part1ExpectedResult = 31L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        check(getVersionSum(clean(listOf("8A004A801A8002F478"))) == 16L)
        check(getVersionSum(clean(listOf("620080001611562C8802118E34"))) == 12L)
        check(getVersionSum(clean(listOf("C0015000016115A2E0802F182340"))) == 23L)
        check(getVersionSum(clean(listOf("A0016C880162017C3686B18A3D4780"))) == 31L)
        return getVersionSum(input)
    }

    private fun getVersionSum(input: List<String>): Long {
        return Parser(input).packet.sumVersions()
    }

    val part2ExpectedResult = 54L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        check(calc(clean(listOf("C200B40A82"))) == 3L)
        check(calc(clean(listOf("04005AC33890"))) == 54L)
        check(calc(clean(listOf("880086C3E88112"))) == 7L)
        check(calc(clean(listOf("CE00C43D881120"))) == 9L)
        check(calc(clean(listOf("D8005AC2A8F0"))) == 1L)
        check(calc(clean(listOf("F600BC2D8F"))) == 0L)
        check(calc(clean(listOf("9C005AC2F8F0"))) == 0L)
        check(calc(clean(listOf("9C0141080250320F1802104A08"))) == 1L)
        return calc(input)
    }

    private fun calc(input: List<String>): Long {
        return Parser(input).packet.calculate()
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
