package day06

import utils.readInput
import java.math.BigInteger

data class Pool(val fishCounters: Map<Int, BigInteger>) {
    fun next(): Pool {
        val decreasedCounters = fishCounters
            .map { it.key - 1 to it.value }
            .toMap(LinkedHashMap())
        decreasedCounters.remove(-1)
            ?.let {
                decreasedCounters[6] = (decreasedCounters[6] ?: BigInteger.ZERO).add(it)
                decreasedCounters[8] = it
            }

//        val newGeneration = fishCounters[0]?.let {
//            val regeneration = decreasedCounters
//                //.filter { it.first != 6 }
//                .map { (counter, count) ->
//                    when (counter) {
//                        -1 -> 6 to (fishCounters[6+1] ?: BigInteger.ZERO).add(count)
//                        else -> counter to count
//                    }
//                }
//
//            regeneration + listOf(8 to it)
//
//        } ?: decreasedCounters

//        val newGeneration = when (decreasedCounters[-1]) {
//            null -> regeneration
//            else -> regeneration + listOf(8 to (decreasedCounters[-1]))
//        }

        return Pool(decreasedCounters)
    }


    fun wait(day: Int): Pool {
        var input1 = this
        var day1 = 0
        while (day1 < day) {
            day1++
            input1 = input1.next()
        }
        return input1
    }

    fun result(): Long = fishCounters.values.fold(BigInteger.ZERO) { acc, it -> acc.add(it) }.toLong()
}

class Puzzle {
    init {

    }


    fun clean(input: List<String>): Pool {
        val fishesList = input.flatMap { it.split(",") }.map { it.toInt() }
        val fishCounters = fishesList.groupingBy { it }.aggregate { key, accumulator: BigInteger?, element, first ->
            (accumulator ?: BigInteger.ZERO).add(BigInteger.ONE)
        }
        return Pool(fishCounters)
    }

    val part1ExpectedResult = 5934L
    fun part1(rawInput: List<String>): Long {
        var input = clean(rawInput)
        input = input.wait(80)
        return input.result()
    }

    val part2ExpectedResult = 26984457539L
    fun part2(rawInput: List<String>): Long {
        var input = clean(rawInput)
        input = input.wait(256)
        return input.result()
    }

}


fun main() {
    val puzzle = Puzzle()
    println(Puzzle::class.qualifiedName)

    val testInput = readInput("00test", Puzzle::class)
    val input = readInput("zzdata", Puzzle::class)


    println("test1: ${puzzle.part1(testInput)} == ${puzzle.part1ExpectedResult}")
    check(puzzle.part1(testInput) == puzzle.part1ExpectedResult)
    println("part1: ${puzzle.part1(input)}")

    println("test2: ${puzzle.part2(testInput)} == ${puzzle.part2ExpectedResult}")
    check(puzzle.part2(testInput) == puzzle.part2ExpectedResult)
    println("part2: ${puzzle.part2(input)}")

}
