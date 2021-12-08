package day08

import utils.readInput
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

class Puzzle {

    fun clean(input: List<String>): List<Entry> {
        return input.map {
            val digitcodes = it.split(" | ")
            check(digitcodes.size == 2)
            val digits = digitcodes[0].split(" ").map { it.toList().sorted().joinToString("") }
            check(digits.size == 10)
            val code = digitcodes[1].split(" ").map { it.toList().sorted().joinToString("") }
            check(code.size == 4)
            Entry(digits, code)
        }
    }

    val part1ExpectedResult = 26L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        return input.map { it.code.fold(0) { acc, s -> acc + if (isUnique(s)) 1 else 0 } }.sum().toLong()
    }

    private fun isUnique(s: String): Boolean {
        return when (s.length) {
            2, 3, 4, 7 -> true  // (1,4,7,8)
            5 -> false //2 3 5
            6 -> false // 0 6 9
            else -> throw IllegalStateException()
        }

    }


    private fun possibleDigits(s: String): List<Int> {
        return when (s.length) {
            2 -> listOf(1)  // (1,4,7,8)
            3 -> listOf(7)  // (1,4,7,8)
            4 -> listOf(4)  // (1,4,7,8)
            7 -> listOf(8)  // (1,4,7,8)
            5 -> listOf(2, 3, 5) //2 3 5
            6 -> listOf(0, 6, 9) // 0 6 9
            else -> throw IllegalStateException()
        }

    }


    fun extractMatchingEncodedDigit(
        encodedDigitToPossibleDigits: Map<String, List<Int>>,
        predicates: List<(Map.Entry<String, List<Int>>) -> Boolean>
    ): String {

        val matchingList = encodedDigitToPossibleDigits.filter { encodedDigitToPossibleDigit ->
            predicates.all { predicate ->
                predicate(encodedDigitToPossibleDigit)
            }
        }
            .toList()
        check(matchingList.size == 1) { "no exact match $matchingList" }
        return matchingList[0].first
    }

    val part2ExpectedResult = 61229L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val codes = input.map { encodedDigitsAndCode ->

            val encodedDigitToPossibleDigits: java.util.HashMap<String, List<Int>> =
                HashMap(encodedDigitsAndCode.digits.map {
                    it to possibleDigits(it)
                }.toMap())

            val digitToCodeDigit = HashMap(encodedDigitToPossibleDigits
                .filter({ (encodedDigit, possibleDigits) -> possibleDigits.size == 1 })
                .map { it.value[0] to it.key }
                .toMap()
            )
            digitToCodeDigit.values.forEach { encodedDigitToPossibleDigits.remove(it) }

            digitToCodeDigit[3] = extractMatchingEncodedDigit(
                encodedDigitToPossibleDigits,
                listOf(
                    { it.key.length == 5 } ,
                    { isIncluded(it.key, digitToCodeDigit[1]!!) } ,
                )
            )
            digitToCodeDigit.values.forEach { encodedDigitToPossibleDigits.remove(it) }

            digitToCodeDigit[9] = extractMatchingEncodedDigit(
                encodedDigitToPossibleDigits,
                listOf(
                    { it.key.length == 6 } ,
                    { isIncluded(it.key, digitToCodeDigit[4]!!) } ,
                )
            )
            digitToCodeDigit.values.forEach { encodedDigitToPossibleDigits.remove(it) }

            val zeroList = encodedDigitToPossibleDigits
                .filter { (it.key.length == 6) }
                .filter { isIncluded(it.key, digitToCodeDigit[1]!!) }
                .toList()
            digitToCodeDigit[0] = zeroList[0].first
            digitToCodeDigit.values.forEach { encodedDigitToPossibleDigits.remove(it) }
            val sixList = encodedDigitToPossibleDigits
                .filter { (it.key.length == 6) }
                .toList()
            digitToCodeDigit[6] = sixList[0].first
            digitToCodeDigit.values.forEach { encodedDigitToPossibleDigits.remove(it) }

            val fiveList = encodedDigitToPossibleDigits
                .filter { (it.key.length == 5) }
                .filter { isIncluded(digitToCodeDigit[6]!!, it.key) }
                .toList()
            digitToCodeDigit[5] = fiveList[0].first
            digitToCodeDigit.values.forEach { encodedDigitToPossibleDigits.remove(it) }

            val twoList = encodedDigitToPossibleDigits
                .filter { (it.key.length == 5) }
                .toList()
            digitToCodeDigit[2] = twoList[0].first
            digitToCodeDigit.values.forEach { encodedDigitToPossibleDigits.remove(it) }

            val codeDigitToDigit = digitToCodeDigit.map { it.value to it.key }.toMap()

            val codeValue = encodedDigitsAndCode.code.map { character ->

                //System.err.println(    codeDigitToDigit[character])
                val i = codeDigitToDigit[character]
                i!!.toString()

            }.joinToString("")
            codeValue.toInt()
        }
        return codes.sum().toLong()
    }

    private fun isIncluded(key: String, includedCharacter: String): Boolean {
        val possibleChars = key.toList()
        val other = includedCharacter.toList()

        return possibleChars.intersect(other).size == includedCharacter.length
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
