package day16

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long

//0 = 0000
//1 = 0001
//2 = 0010
//3 = 0011
//4 = 0100
//5 = 0101
//6 = 0110
//7 = 0111
//8 = 1000
//9 = 1001
//A = 1010
//B = 1011
//C = 1100
//D = 1101
//E = 1110
//F = 1111

class Puzzle {

    fun clean(input: List<String>): List<String> {
        return input.map { x: String -> x.toList() }
            .flatMap { a: List<Char> ->
                a.map {
                    Integer.toBinaryString(
                        it.toString().toInt(16)
                    ).padStart(4, '0')
                }
            }
    }

    val part1ExpectedResult = 31L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        //  check(getVersionSum(clean(listOf("8A004A801A8002F478"))) == 16L)
        check(getVersionSum(clean(listOf("620080001611562C8802118E34"))) == 12L)
        check(getVersionSum(clean(listOf("C0015000016115A2E0802F182340"))) == 23L)
        check(getVersionSum(clean(listOf("A0016C880162017C3686B18A3D4780"))) == 31L)
        return getVersionSum(input)
    }

    var versions = ArrayList<Int>()
    private fun getVersionSum(input: List<String>): Long {
        parse(input)
        return versions.sum().toLong()
    }


    enum class OperatorType {
        value, sum, mult, min, max, greater, less, equals//, jump, jumpIfTrue, jumpIfFalse,
    }

    data class Packet(
        val version: Int,
        val operator: OperatorType,
        val subPackets: List<Packet>,
        val value: Long = 0L
    ) {

        fun getVersions() {
            subPackets.fold(version) { acc, packet -> acc + packet.version }
        }

        fun calculate(): Long {
            return when (operator) {
                OperatorType.value -> {
                    value
                }
                OperatorType.sum -> {
                    subPackets.sumOf { it.calculate() }
                }
                OperatorType.mult -> {
                    subPackets.fold(1L) { acc, packet -> acc * packet.calculate() }
                }
                OperatorType.min -> {
                    subPackets.map { it.calculate() }.minOrNull()!!
                }
                OperatorType.max -> {
                    subPackets.map { it.calculate() }.maxOrNull()!!
                }
                OperatorType.greater -> {
                    check(subPackets.size == 2)
                    if (subPackets[0].calculate() > subPackets[1].calculate()) 1 else 0
                }
                OperatorType.less -> {
                    check(subPackets.size == 2)
                    if (subPackets[0].calculate() < subPackets[1].calculate()) 1 else 0
                }
                OperatorType.equals -> {
                    check(subPackets.size == 2)
                    if (subPackets[0].calculate() == subPackets[1].calculate()) 1 else 0
                }
            }
        }
    }

    private fun parse(input: List<String>): Long {
        versions = ArrayList<Int>()
        index = 0
        val parsePacket = parsePacket(input, versions)
        return parsePacket

    }


    private fun calc(input: List<String>): Long {
        return parse(input)
    }

    val part2ExpectedResult = 54L
    var index = 3
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


    private fun parsePacket(input: List<String>, versions: ArrayList<Int>): Long {

        val version = getNextCar(3, input).toInt(2)
        versions.add(version)

        val l = input.sumOf { it.length }

        val typeSting = getNextCar(3, input)
        val type = typeSting.toInt(2)
        val l1 = when (type) {
            4 -> parseNextValue(input, versions)
            else -> parseOperator(type, input, versions)
        }
        return l1

    }

    private fun parseOperator(type: Int, input: List<String>, versions: ArrayList<Int>): Long {
        val lengthType = getNextCar(1, input)
        var subPacks = ArrayList<Long>()
        when (lengthType) {
            "0" -> {
                val totalLength = getNextCar(15, input).toInt(2)
                val endPacket = index + totalLength
                while (index < endPacket - 1) {
                    val parsePacket = parsePacket(input, versions)
                    subPacks.add(parsePacket)
                }
            }
            "1" -> {
                val totalBlocks = getNextCar(11, input).toInt(2)
                (0 until totalBlocks).forEach {
                    val parsePacket = parsePacket(input, versions)
                    subPacks.add(parsePacket)
                }
            }
        }
        return when (type) {
            0 -> subPacks.sum()
            1 -> {
                val fold: Long = subPacks.fold(1) { acc, l -> acc * l }
                fold
            }
            2 -> subPacks.minOrNull()!!
            3 -> subPacks.maxOrNull()!!
            5 -> {
                check(subPacks.size == 2); if (subPacks[0] > subPacks[1]) 1 else 0
            }
            6 -> {
                check(subPacks.size == 2); if (subPacks[0] <subPacks[1]) 1 else 0
            }
            7 -> {
                check(subPacks.size == 2); if (subPacks[0] == subPacks[1]) 1 else 0
            }
            else -> throw IllegalStateException("type " + type)
        }

    }

    private fun parseNextValue(input: List<String>, versions: ArrayList<Int>): Long {
        var nextCar = getNextCar(5, input)
        val s = StringBuilder()
        // result.add(nextCar.substring(1).toInt(2))
        while (nextCar.startsWith("1")) {
            s.append(nextCar.substring(1))
            //   result.add(nextCar.substring(1).toInt(2))
            nextCar = getNextCar(5, input)
        }
        s.append(nextCar.substring(1))
        val longnumber = s.toString()
//        val block = longnumber.length / 8
//        var l: Long = 0L
//        (0 until block).forEach {
//            l += longnumber.substring(8 * it, 8 * it + 8).toInt().toLong()
//        }

        val toInt = longnumber.toLong(2)
        return toInt
    }

    private fun getNextCar(size: Int, input: List<String>): String {
        val block = size / 4
        val remain = size % 4
        val s = StringBuilder()
        (0 until block).forEach {
            val typeSting = getNext4Digits(4, input)
            s.append(typeSting)
        }
        s.append(getNext4Digits(remain, input))
        return s.toString()
    }

    private fun getNext4Digits(size: Int, input: List<String>): String {
        val car = index / 4
        val starPos = index % 4
        try {

            val typeSting = if (starPos + size <= 4) {
                input[car].substring(starPos, starPos + size)
            } else {
                input[car].substring(starPos) + input[car + 1].substring(0, size - (4 - starPos))
            }
            index += size
            return typeSting
        } catch (e: Exception) {
            throw e;
//            System.err.println(e)
//            // break;
//            return "0"
        }

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
