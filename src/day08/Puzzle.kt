package day08

import utils.readInput
import utils.sortString
import java.util.function.Predicate
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime


//0:      1:      2:      3:      4:
//aaaa    ....    aaaa    aaaa    ....
//b    c  .    c  .    c  .    c  b    c
//b    c  .    c  .    c  .    c  b    c
//....    ....    dddd    dddd    dddd
//e    f  .    f  e    .  .    f  .    f
//e    f  .    f  e    .  .    f  .    f
//gggg    ....    gggg    gggg    ....
//
//5:      6:      7:      8:      9:
//aaaa    aaaa    aaaa    aaaa    aaaa
//b    .  b    .  .    c  b    c  b    c
//b    .  b    .  .    c  b    c  b    c
//dddd    dddd    ....    dddd    dddd
//.    f  e    f  .    f  e    f  .    f
//.    f  e    f  .    f  e    f  .    f
//gggg    gggg    ....    gggg    gggg

typealias Result = Long


data class Entry(val digits: List<String>, val code: List<String>) {

}


data class DictionaryComputer(val entry: Entry) {

    fun computeEncodedDigitDictionary(): Map<String, Int> {

        // map of encoded -> possible real digits
        // we put everything in it t the beginning then progressively remove the ones we know
        val encodedDigitToPossibleDigits: MutableMap<String, List<Int>> =
            entry.digits
                .associateWith { encodedDigit -> getPossibleDigits(encodedDigit) }
                .toMutableMap()

        // reverse map real digit -> encoded digit
        // we feed it progressively with the mappings we find

        // start by the ones that can be guessed by their sizes
        val digitToCodeDigit = encodedDigitToPossibleDigits
            .filter { (_, possibleDigits) -> possibleDigits.size == 1 }
            .map { it.value[0] to it.key }
            .toMap()
            .toMutableMap()

        // remove the encoded strings we know
        removeKnownDigits(digitToCodeDigit, encodedDigitToPossibleDigits)

        //  among length 5 (2,3,5) 3 is the one that "includes" 1
        digitToCodeDigit[3] = extractMatchingEncodedDigit(
            encodedDigitToPossibleDigits,
            listOf(
                hasEncodedSize(5),
                encodedDigitIncludes(digitToCodeDigit[1]),

                )
        )
        removeKnownDigits(digitToCodeDigit, encodedDigitToPossibleDigits)

        //  among length 6 (0,6,9) 9 is the one that "includes" 4
        digitToCodeDigit[9] = extractMatchingEncodedDigit(
            encodedDigitToPossibleDigits,
            listOf(
                hasEncodedSize(6),
                encodedDigitIncludes(digitToCodeDigit[4]),
            )
        )
        removeKnownDigits(digitToCodeDigit, encodedDigitToPossibleDigits)

        //  among remaining length 6 (0,6) 0 is the one that "includes" 1
        digitToCodeDigit[0] = extractMatchingEncodedDigit(
            encodedDigitToPossibleDigits,
            listOf(
                hasEncodedSize(6),
                encodedDigitIncludes(digitToCodeDigit[1]),
            )
        )
        removeKnownDigits(digitToCodeDigit, encodedDigitToPossibleDigits)

        //  among remaining length 6 (6) only 6 is remaining
        digitToCodeDigit[6] = extractMatchingEncodedDigit(
            encodedDigitToPossibleDigits,
            listOf(
                hasEncodedSize(6),
            )
        )
        removeKnownDigits(digitToCodeDigit, encodedDigitToPossibleDigits)

        //  among remaining length 5 (2,5) 5 is the one is "included" in 6
        digitToCodeDigit[5] = extractMatchingEncodedDigit(
            encodedDigitToPossibleDigits,
            listOf(
                hasEncodedSize(5),
                encodedDigitIsIncludedIn(digitToCodeDigit[6]),
            )
        )
        removeKnownDigits(digitToCodeDigit, encodedDigitToPossibleDigits)

        //  among remaining length 5 (2) only 2 is remaining

        digitToCodeDigit[2] = extractMatchingEncodedDigit(
            encodedDigitToPossibleDigits,
            listOf(
                hasEncodedSize(5),
            )
        )
        removeKnownDigits(digitToCodeDigit, encodedDigitToPossibleDigits)

        check(encodedDigitToPossibleDigits.size == 0)
        check(digitToCodeDigit.size == 10)
        val codeDigitToDigit = digitToCodeDigit.map { it.value to it.key }.toMap()
        return codeDigitToDigit
    }


    private fun hasEncodedSizePredicate(checkSize: Int): Predicate<String> =
        Predicate<String> { encodedDigit -> encodedDigit.length == checkSize }
    private fun encodedDigitIncludesPredicate(includedString: String?):Predicate<String> =
        Predicate<String>  { encodedDigit -> isIncluded(encodedDigit, includedString!!) }
    private fun hasEncodedSize(checkSize: Int): (String) -> Boolean =
        { encodedDigit -> encodedDigit.length == checkSize }

    private fun encodedDigitIncludes(includedString: String?): (String) -> Boolean =
        { encodedDigit -> isIncluded(encodedDigit, includedString!!) }

    private fun encodedDigitIsIncludedIn(includingString: String?): (String) -> Boolean =
        { encodedDigit -> isIncluded(includingString!!, encodedDigit) }

    private fun removeKnownDigits(
        digitToCodeDigit: MutableMap<Int, String>,
        encodedDigitToPossibleDigits: MutableMap<String, List<Int>>
    ) {
        digitToCodeDigit.values.forEach { encodedDigitToPossibleDigits.remove(it) }
    }

    private fun getPossibleDigits(encodedDigit: String): List<Int> = when (encodedDigit.length) {
        2 -> listOf(1)
        3 -> listOf(7)
        4 -> listOf(4)
        7 -> listOf(8)
        5 -> listOf(2, 3, 5)
        6 -> listOf(0, 6, 9)
        else -> throw IllegalStateException("invalid length for code $encodedDigit")
    }


    fun extractMatchingEncodedDigit(
        encodedDigitToPossibleDigits: Map<String, List<Int>>,
        predicates: List<(String) -> Boolean>
    ): String {

        val matchingList = encodedDigitToPossibleDigits.filter { encodedDigitToPossibleDigit ->
            predicates.all { predicate ->
                predicate(encodedDigitToPossibleDigit.key)
            }
        }
            .toList()
        check(matchingList.size == 1) { "no exact match $matchingList" }
        return matchingList[0].first
    }

    private fun isIncluded(key: String, includedCharacter: String): Boolean {
        val possibleChars = key.toList()
        val other = includedCharacter.toList()

        return possibleChars.intersect(other).size == includedCharacter.length
    }
}

class Puzzle {

    fun clean(input: List<String>): List<Entry> {
        return input.map {
            val digitCodes = it.split(" | ")
            check(digitCodes.size == 2)

            val digits = digitCodes[0].split(" ")
                .map { encodedDigit -> encodedDigit.sortString() }
            check(digits.size == 10)

            val codes = digitCodes[1].split(" ")
                .map { encodedDigit -> encodedDigit.sortString() }
            check(codes.size == 4)

            Entry(digits, codes)
        }
    }


    val part1ExpectedResult = 26L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        return input.sumOf { entry ->
            entry.code.count { encodedDigit -> isUnique(encodedDigit) }
        }
            .toLong()
    }

    private fun isUnique(encodedDigit: String): Boolean = when (encodedDigit.length) {
        2, 3, 4, 7 -> true  // (1,4,7,8)
        5 -> false //2 3 5
        6 -> false // 0 6 9
        else -> throw IllegalStateException("invalid length for code $encodedDigit")
    }


    val part2ExpectedResult = 61229L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val codes = input.map { entry ->

            //val codeDigitToDigit = computeEncodedDigitDictionary(entry)
            val codeDigitToDigit = DictionaryComputer(entry).computeEncodedDigitDictionary()

            val codeValue = entry.code.map { character ->
                val i = codeDigitToDigit[character]
                i!!.toString()

            }.joinToString("")
            codeValue.toInt()
        }
        return codes.sum().toLong()
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
