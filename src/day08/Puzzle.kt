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
            val digits = digitcodes[0].split(" ").map { it.toList().sorted().joinToString ("") }
            check(digits.size == 10)
            val code = digitcodes[1].split(" ").map { it.toList().sorted().joinToString ("") }
            check(code.size == 4)
            Entry(digits, code)
        }
    }

    val part1ExpectedResult = 26L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        return input.map { it.code.fold(0) { acc, s -> acc + if (isUnique(s)) 1 else 0 } }.sum().toLong()
    }

//    data class Trad(
//        val List<List<Int>>
//    ) {
//
//    }

    private fun isUnique(s: String): Boolean {
        return when (s.length) {
            2, 3, 4, 7 -> true  // (1,4,7,8)
            5 -> false //2 3 5
            6 -> false // 0 6 9
            else -> false //0 2 3 5 6 9
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

    private fun possibleSegements(s: String): Map<Char, List<Char>> {
        return when (s.length) {
            2 -> possible(s, "cf") // (1,4,7,8)
            3 -> possible(s, "acf")  // (1,4,7,8)
            4 -> possible(s, "bcdf")  // (1,4,7,8)
            7 -> possible(s, "abcdefg")  // (1,4,7,8)
            5 -> possible(s, "acdegacdfgabdfg") //2 3 5
            6 -> possible(s, "abcefgabdefgabcdfg") // 0 6 9
            else -> throw IllegalStateException()
        }

    }

    private fun possible(s: String, mapped: String): Map<Char, List<Char>> {
        return s.map { it to mapped.toList().toSet().toList().sorted() }.toMap()
    }

    private fun trad(s: String): Int {
        return when (s) {
            "cf" -> 1
            "abcefg" -> 0
            "acdeg" -> 2
            "acdfg" -> 3
            "bcdf" -> 4
            "abdfg" -> 5
            "abdefg" -> 6
            "acf" -> 7
            "abcdefg" -> 8
            "abcdfg" -> 9
            else -> throw IllegalStateException()
        }

    }

    val dic = mapOf(
        "acedgfb" to 8,
        "cdfbe" to 5,
        "gcdfa" to 2,
        "fbcad" to 3,
        "dab" to 7,
        "cefabd" to 9,
        "cdfgeb" to 6,
        "eafb" to 4,
        "cagedb" to 0,
        "ab" to 1,
    )
    val numberToString: Map<Int, String> = mapOf(
        8 to "acedgfb",
        5 to "cdfbe",
        2 to "gcdfa",
        3 to "fbcad",
        7 to "dab",
        9 to "cefabd",
        6 to "cdfgeb",
        4 to "eafb",
        0 to "cagedb",
        1 to "ab",
    )

    val part2ExpectedResult = 61229L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val codes = input.map {

            val codeDigitToPossibleDigits = HashMap(it.digits.map {
                it to possibleDigits(it)
            }.toMap())

            val digitToCodeDigit = HashMap(codeDigitToPossibleDigits
                .filter { it.value.size == 1 }
                .map { it.value[0] to it.key }
                .toMap()
            )
            digitToCodeDigit.values.forEach { codeDigitToPossibleDigits.remove(it) }

            // 1 4 7 8
//            5 -> possible(s, "acdegacdfgabdfg") //2 3 5
//            6 -> possible(s, "abcefgabdefgabcdfg") // 0 6 9


            val threeList = codeDigitToPossibleDigits
                .filter { (it.key.length == 5) }
                .filter { isIncluded(it.key, digitToCodeDigit[1]!!) }
                .toList()
            digitToCodeDigit[3] = threeList[0].first
            digitToCodeDigit.values.forEach { codeDigitToPossibleDigits.remove(it) }

            val nineList = codeDigitToPossibleDigits
                .filter { (it.key.length == 6) }
                .filter { isIncluded(it.key, digitToCodeDigit[4]!!) }
                .toList()
            digitToCodeDigit[9] = nineList[0].first
            digitToCodeDigit.values.forEach { codeDigitToPossibleDigits.remove(it) }
            val zeroList = codeDigitToPossibleDigits
                .filter { (it.key.length == 6) }
                .filter { isIncluded(it.key, digitToCodeDigit[1]!!) }
                .toList()
            digitToCodeDigit[0] = zeroList[0].first
            digitToCodeDigit.values.forEach { codeDigitToPossibleDigits.remove(it) }
            val sixList = codeDigitToPossibleDigits
                .filter { (it.key.length == 6) }
                .toList()
            digitToCodeDigit[6] = sixList[0].first
            digitToCodeDigit.values.forEach { codeDigitToPossibleDigits.remove(it) }

            val fiveList = codeDigitToPossibleDigits
                .filter { (it.key.length == 5) }
                .filter { isIncluded(digitToCodeDigit[6]!!, it.key ) }
                .toList()
            digitToCodeDigit[5] = fiveList[0].first
            digitToCodeDigit.values.forEach { codeDigitToPossibleDigits.remove(it) }

            val twoList = codeDigitToPossibleDigits
                .filter { (it.key.length == 5) }
                .toList()
            digitToCodeDigit[2] = twoList[0].first
            digitToCodeDigit.values.forEach { codeDigitToPossibleDigits.remove(it) }

            val codeDigitToDigit = digitToCodeDigit.map { it.value to it.key }.toMap()

//            val map1 = it.digits.map { possibleSegements(it) }
//            val possibleCharDic: MutableMap<Char, List<Char>> = mutableMapOf(
//
//            )
//            map1.forEach {
//                it.forEach { (digit: Char, possibilities: List<Char>) ->
//                    val chars = possibleCharDic[digit]
//                    if (chars == null) {
//                        possibleCharDic[digit] = possibilities
//                    } else {
//                        possibleCharDic[digit] = possibilities.intersect(chars).toList().sorted()
//                    }
//                }
//                possibleCharDic
//            }
//            data class Mapper(val digit: Int, val size: Int, val possibleDigits: List<Char>)
//
//            val mappers = numberToString.map { it ->
//                val wishedString = it.value
//                val number = it.key
//                val flatMap: List<Char> =
//                    wishedString.fold("abcdefg".toList()) { acc, it -> acc.intersect(possibleCharDic[it]!!).toList() }
//                        .toSet().toList().sorted()
//                Mapper(number, wishedString.length, flatMap)
//            }
//be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe
            val codeValue = it.code.map { character ->

                //System.err.println(    codeDigitToDigit[character])
                val i = codeDigitToDigit[character]
                 i!!.toString()
//                val filter = mappers.filter { mapper ->
//                    mapper.size == character.length
//                            && character.toList()
//                        .filter {
//                            val possibleSegmentsForCodeSegment = possibleCharDic[it]!!
//                            val neededSegments = numberToString[mapper.digit]!!.toList()
//                            !possibleSegmentsForCodeSegment.intersect(neededSegments).isEmpty()
//                        }
//                        // .intersect(mapper.possibleDigits)
//                        .size == mapper.size
//                }
//                check(filter.size == 1)
//                val mapped = filter[0]
//                check(mapped != null)
//                mapped
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
