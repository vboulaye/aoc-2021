package day03

import utils.asBinary
import utils.readInput
import java.lang.IllegalStateException

fun matchesDigit(c: Char, c1: Char): Byte {
    return if (c == c1) 1 else 0;
}

data class Popularity(val ones: Int = 0, val zeroes: Int = 0) {

    fun mergeRecord(char: Char): Popularity {
        return Popularity(
            this.ones + matchesDigit(char, '1'),
            this.zeroes + matchesDigit(char, '0')
        )
    }

    fun mostPresent() = when {
        ones >= zeroes -> '1'
        else -> '0'
    }

    fun leastPresent() = when {
        ones >= zeroes -> '0'
        else -> '1'
    }
}

private fun gePopularityForIndex(input: List<String>, index: Int) =
    input.fold(Popularity()) { acc, record -> acc.mergeRecord(record[index]) }


class Puzzle {

    fun clean(input: List<String>): List<String> {
        return input
    }

    fun part1(rawInput: List<String>): Int {
        val input = clean(rawInput)
        val popularities = (0 until input[0].length)
            .map { gePopularityForIndex(rawInput, it) }

        val gamma = extractMatchingPopularities(popularities) { it.mostPresent() }
        val epsilon = extractMatchingPopularities(popularities) { it.leastPresent() }

        return gamma * epsilon
    }

    private fun extractMatchingPopularities(
        popularities: List<Popularity>,
        transform: (Popularity) -> Char
    ) = popularities
        .map(transform)
        .joinToString("")
        .asBinary()

    fun part2(rawInput: List<String>): Int {
        val input = clean(rawInput)

        val oxygenString = findRecordMatchingPopularity(input, 0, Popularity::mostPresent)
        val co2String = findRecordMatchingPopularity(input, 0, Popularity::leastPresent)

        val oxygen = oxygenString.asBinary()
        val co2 = co2String.asBinary()
        return oxygen * co2
    }

    private tailrec fun findRecordMatchingPopularity(
        input: List<String>,
        index: Int,
        popularityGetter: (Popularity) -> Char
    ): String {
        val popularityForIndex = gePopularityForIndex(input, index)
        val matchingChar = popularityGetter(popularityForIndex)
        val filtered = input.filter { it[index] == matchingChar }
        return when {
            filtered.isEmpty() -> throw IllegalStateException("no more data")
            filtered.size == 1 -> filtered[0]
            else -> findRecordMatchingPopularity(filtered, index + 1, popularityGetter)
        }
    }

}


fun main() {
    val puzzle = Puzzle()
    println(Puzzle::class.qualifiedName)

    val testInput = readInput("00test", Puzzle::class)
    val input = readInput("zzdata", Puzzle::class)

    val part1ExpectedResult = 198

    println("test1: ${puzzle.part1(testInput)} == $part1ExpectedResult")
    println("part1: ${puzzle.part1(input)}")
    check(puzzle.part1(testInput) == part1ExpectedResult)

    val part2ExpectedResult = 230

    println("test2: ${puzzle.part2(testInput)} == $part2ExpectedResult")
    println("part2: ${puzzle.part2(input)}")
    check(puzzle.part2(testInput) == part2ExpectedResult)

}
