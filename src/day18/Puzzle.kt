package day18

import utils.readInput
import java.lang.Integer.max
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


data class SnailFishNumber(var line: String, val start: Int) {
    var end: Int = 0
    var comma: Int = 0
    var parentSnail: SnailFishNumber? = null
    var leftSnail: SnailFishNumber? = null
    var leftValue: Int = -1
    var rightSnail: SnailFishNumber? = null
    var rightValue: Int = -1
    fun left() = line.substring(start + 1, comma)
    fun right() = line.substring(comma + 1, end)

    override fun toString(): String {
        return "[${leftSnail ?: leftValue},${rightSnail ?: rightValue}]"
    }
}


//data class ParsedSnailFishNumber(val arentSnail: SnailFishNumber? = null) {
//    var leftSnail: SnailFishNumber? = null
//    var leftValue: Int? = -1
//    var rightSnail: SnailFishNumber? = null
//    var rightValue: Int? = -1
//
//    override fun toString(): String {
//        return "($leftSnail) ($rightSnail)"
//    }
//}


class Puzzle {
    fun clean(input: List<String>): List<SnailFishNumber> {

        return input.filter { true }.map { line ->
            parseLine(line)
            val stack = Stack<SnailFishNumber>()
            val reverseStack = Stack<SnailFishNumber>()

            line.forEachIndexed { index, c ->

                if (c == '[') {
                    stack.push(SnailFishNumber(line = line, start = index))
                }
                if (c == ',') {
                    val fishNumber = stack.peek()
                    fishNumber.comma = index
                    if (reverseStack.isEmpty()) {
                        fishNumber.leftValue = fishNumber.left().toInt()
                    } else {
                        val childFishNumber = reverseStack.pop()
                        fishNumber.leftSnail = childFishNumber
                        childFishNumber.parentSnail = fishNumber
                    }
                }
                if (c == ']') {
                    val fishNumber = stack.pop()
                    fishNumber.end = index
                    if (reverseStack.isEmpty()) {
                        fishNumber.rightValue = fishNumber.right().toInt()
                    } else {
                        val snailFishNumber = reverseStack.pop()
                        fishNumber.rightSnail = snailFishNumber
                        snailFishNumber.parentSnail = fishNumber
                    }
                    reverseStack.push(fishNumber)
                    //     println(fishNumber)
                }
            }

            val snailFishNumber = reverseStack.pop()
            snailFishNumber

        }
    }

    private fun parseLine(line: String) {
//        return if (line.startsWith('[') && line.endsWith(']')) {
//            parseLine(line.substring(1, line.lastIndex))
//        } else if line{
//            line.split[0]
//        }

    }

    val part1ExpectedResult = 4140L
//    fun part1X(rawInput: List<String>): Result {
//        rawInput.reduce { acc, s ->
//            "[" + acc + "," + s + "]"
//        }
//    }

    fun part1(rawInput: List<String>): Result {


        val input = clean(rawInput)
//        val explodes = clean(readInput("explode", Puzzle::class))
//        explode(explodes[0], 0)
//        check(explodes[0].toString() == "[[[[0,9],2],3],4]") { explodes[0].toString() }
//        explode(explodes[1], 0)
//        check(explodes[1].toString() == "[7,[6,[5,[7,0]]]]") { explodes[1].toString() }
//        explode(explodes[2], 0)
//        check(explodes[2].toString() == "[[6,[5,[7,0]]],3]") { explodes[2].toString() }
//        explode(explodes[3], 0)
//        check(explodes[3].toString() == "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]") { explodes[3].toString() }
//        //                                     [[3,[2,[8,0]]],[6,[5,[4,[3,2]]]]]
//        //[[3,[2,[8,0]]],[6,[5,[4,[3,2]]]]]
//        explode(explodes[4], 0)
//        check(explodes[4].toString() == "[[3,[2,[8,0]]],[9,[5,[7,0]]]]") { explodes[4].toString() }

//        hcek("000", "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]")
//        hcek("00", "[[[[1,1],[2,2]],[3,3]],[4,4]]")
//        hcek("01", "[[[[3,0],[5,3]],[4,4]],[5,5]]")
//        hcek("02", "[[[[5,0],[7,4]],[5,5]],[6,6]]")
        hcek("another", "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]")
        val reduceSnail = reduceSnail(input)
        return magnitude(reduceSnail)
    }

    private fun hcek(file: String, result: String) {
        val reduced = reduceSnail(clean(readInput(file, Puzzle::class))).toString()
        check(reduced.toString() == result) { reduced.toString() }
    }

    fun magnitude(reduceSnail: SnailFishNumber): Result {
        val l: Long = if (reduceSnail.leftValue > -1) {
            reduceSnail.leftValue.toLong()
        } else {
            magnitude(reduceSnail.leftSnail!!)
        }
        val r = if (reduceSnail.rightValue > -1) {
            reduceSnail.rightValue.toLong()
        } else {
            magnitude(reduceSnail.rightSnail!!)
        }
        return 3 * l + 2 * r
    }

    private fun reduceSnail(input: List<SnailFishNumber>): SnailFishNumber {
        val fishNumber = input.reduce { acc, snailFishNumber ->
            reduceBoth(acc, snailFishNumber)
        }

        println("sum: " + fishNumber.toString())
        return fishNumber
    }

    private fun reduceBoth(acc: SnailFishNumber, snailFishNumber: SnailFishNumber): SnailFishNumber {
        val result = SnailFishNumber(acc.line, 0)
        result.leftSnail = acc
        acc.parentSnail = result
        result.rightSnail = snailFishNumber
        snailFishNumber.parentSnail = result

        println("adding : " + acc.toString() + " and " + snailFishNumber.toString())
        doExplode(result)
        return result
    }

    private fun doExplode(result: SnailFishNumber) {
        println("before: " + result.toString())

        if (result.toString().contains("[[[[7,0],[7,7]],[[7,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]]")) {
            println("xxxx")
            println("xxxx")
            println("xxxx")
            println("xxxx")
            println("xxxx")
            println("xxxx")
            println("xxxx")

        }
        while (explode(result, 0) || split(result)) {
            println("exploding or splitting: " + result.toString())
        }
        //[[[[[7,0],[7,7]],[[7,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]],[7,[5,[[3,8],[1,4]]]]]
        //[[[[0,[7,7]],[[7,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]],[7,[5,[[3,8],[1,4]]]]]
        //[[[[7,0],[[14,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]],[7,[5,[[3,8],[1,4]]]]]

        // [[[[7,7],[7,8]],[[9,5],[8,7]]],[[[7,8],[0,8]],[[8,9],[9,0]]]]
        // shoudl be
        // [[[[7,7],[7,8]],[[9,5],[8,7]]],[[[6,8],[0,8]],[[9,9],[9,0]]]]
        println("after sum: " + result.toString())
    }

    fun getTop(source: SnailFishNumber): SnailFishNumber? {
        var parent = source
        while (parent.parentSnail != null) {
            parent = parent.parentSnail!!
        }
        return parent
    }

    fun getAllValues(source: SnailFishNumber): List<Pair<SnailFishNumber, Int>> {
        val result = mutableListOf<Pair<SnailFishNumber, Int>>()
        if (source.leftValue > -1) {
            result.add(Pair(source, source.leftValue))
        } else {
            result.addAll(getAllValues(source.leftSnail!!))
        }
        if (source.rightValue > -1) {
            result.add(Pair(source, source.rightValue))
        } else {
            result.addAll(getAllValues(source.rightSnail!!))
        }
        return result
    }
//
//    fun getLeftSnail(source: SnailFishNumber?, firstBranch: Boolean = true): SnailFishNumber? {
//        val parentSnail = source
//        if (parentSnail == null) {
//            return null
//        }
//        if (parentSnail.rightValue != -1 && !firstBranch) {
//            return parentSnail
//        }
//        if (parentSnail.rightSnail != null && !firstBranch) {
//            val fromRight = getLeftSnail(parentSnail.rightSnail!!, false)
//            if (fromRight != null) {
//                return fromRight
//            }
//        }
//        if (parentSnail.leftValue != -1) {
//            return parentSnail
//        }
//        if (parentSnail.leftSnail != null) {
//            val fromLeft = getLeftSnail(parentSnail.leftSnail!!, false)
//            if (fromLeft != null) {
//                return fromLeft
//            }
//        }
//        if (parentSnail.parentSnail != null) {
//            return getLeftSnail(parentSnail.parentSnail!!, true)
//        }
//        return null
//    }
//
//
//    fun getRightSnail(
//        source: SnailFishNumber?,
//        firstBranch: Boolean = true,
//        firstLevel: Boolean = false
//    ): SnailFishNumber? {
//        val parentSnail = source
//        if (parentSnail == null) {
//            return null
//        }
//        if (parentSnail.leftValue != -1 && !firstBranch) {
//            return parentSnail
//        }
//        if (parentSnail.leftSnail != null && !firstBranch) {
//            val fromRight = getRightSnail(parentSnail.leftSnail!!, false)
//            if (fromRight != null) {
//                return fromRight
//            }
//        }
//        if (parentSnail.rightValue != -1) {
//            return parentSnail
//        }
//        if (parentSnail.rightSnail != null) {
//            val fromLeft = getLeftSnail(parentSnail.rightSnail!!, false)
//            if (fromLeft != null) {
//                return fromLeft
//            }
//        }
//        if (parentSnail.parentSnail != null) {
//            return getRightSnail(parentSnail.parentSnail!!, true)
//        }
//        return null
//    }


//    fun getRightSnail(source: SnailFishNumber): SnailFishNumber? {
//        var snail = source
//        while (snail.parentSnail != null) {
//            snail = snail.parentSnail!!
//            if (snail.leftValue != -1 || snail.rightValue != -1) {
//                return snail
//            }
//        }
//        return null
//    }

    private fun explode(current: SnailFishNumber, depth: Int): Boolean {
        if (depth == 4 && (current.leftSnail != null || current.rightSnail != null)) {
            throw java.lang.IllegalStateException()
        }
        if (depth == 4 && current.leftSnail == null && current.rightSnail == null) {
            println("explode")
            val allValues = getAllValues(getTop(current)!!)
            val indexOfFirst = allValues.indexOfFirst { it.first === current }
            if (indexOfFirst > 0) {
                //allValues[indexOfFirst - 1].first.rightValue += current.leftValue
                val exploding = allValues[indexOfFirst - 1].first
                if (exploding.rightValue != -1) {
                    exploding.rightValue += current.leftValue
                } else {
                    exploding.leftValue += current.leftValue
                }
            }
            if (indexOfFirst + 2 < allValues.size) {
                val exploding = allValues[indexOfFirst + 2].first
                if (exploding.leftValue != -1) {
                    exploding.leftValue += current.rightValue
                } else {
                    exploding.rightValue += current.rightValue
                }
            }
//            getLeftSnail(current.parentSnail)?.let {
//                if (it.rightValue > -1 && it.leftSnail != current) {
//                    it.rightValue += current.leftValue
//                }
//                if (it.leftValue > -1) {
//                    it.leftValue += current.leftValue
//                }
//            }
//            getRightSnail(current.parentSnail)?.let {
//                if (it.leftValue > -1 && it.rightSnail != current) {
//                    it.leftValue += current.rightValue
//                }
//                if (it.rightValue > -1) {
//                    it.rightValue += current.rightValue
//                }


//                it.rightValue += current.rightValue
//            }
            val parentSnail = current.parentSnail!!
            if (parentSnail.leftSnail == current) {
                parentSnail.leftSnail = null
                parentSnail.leftValue = 0
            } else if (parentSnail.rightSnail == current) {
                parentSnail.rightSnail = null
                parentSnail.rightValue = 0
            } else {
                throw IllegalStateException("Could not find parent snail")
            }
            return true
        }
        if (current.leftSnail != null && explode(current.leftSnail!!, depth + 1)) {
            return true
        }
        if (current.rightSnail != null && explode(current.rightSnail!!, depth + 1)) {
            return true
        }
        return false
    }

    private fun split(result: SnailFishNumber): Boolean {
        if (result.leftValue >= 10) {
            println("split left")
            val newSnail = splitValueIntoSnailFishNumber(result.leftValue)
            result.leftSnail = newSnail
            newSnail.parentSnail = result
            result.leftValue = -1
            return true
        }
        if (result.leftSnail != null && split(result.leftSnail!!)) {
            return true
        }
        if (result.rightValue >= 10) {
            println("split right")
            val newSnail = splitValueIntoSnailFishNumber(result.rightValue)
            result.rightSnail = newSnail
            newSnail.parentSnail = result
            result.rightValue = -1
            return true
        }

        if (result.rightSnail != null && split(result.rightSnail!!)) {
            return true
        }
        return false
    }

    private fun splitValueIntoSnailFishNumber(
        valueToSplit: Int
    ): SnailFishNumber {
        val newSnail = SnailFishNumber("", 0)
        newSnail.leftValue = valueToSplit / 2
        newSnail.rightValue = valueToSplit - newSnail.leftValue
        return newSnail
    }

    val part2ExpectedResult = 3993L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        var maxMagnittude = 0L
        input.forEachIndexed { index, _ ->
            input.forEachIndexed { index1, _ ->
                if (index != index1) {
                    val input = clean(rawInput)
                    val line = input[index]
                    val otherline = input[index1]
                    val reduceSnail = reduceSnail(listOf(line, otherline))
                    val magnitude = magnitude(reduceSnail)
                    maxMagnittude = kotlin.math.max(maxMagnittude, magnitude)
                }
            }
        }
        return maxMagnittude
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

//    runPart("part1", puzzle.part1ExpectedResult, puzzle::part1)
    runPart("part2", puzzle.part2ExpectedResult, puzzle::part2)

}
