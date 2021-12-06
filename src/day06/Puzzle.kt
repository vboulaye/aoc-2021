package day06

import utils.readInput
import java.math.BigInteger


data class Pool(val fishes: List<Long>) {
    fun next(): Pool {
        var counter = 0
        val newstate = fishes.map {
            var newcount = it - 1
            if (newcount == -1L) {
                newcount = 6
                counter++
            }
            newcount
        }

        val fishes1 = newstate + MutableList(counter) { 8L }
        return Pool(fishes1)
    }
}

data class Pool2(val fishes: Map<Int, BigInteger>) {
    fun next(): Pool2 {
        val newstate: MutableMap<Int, BigInteger> = LinkedHashMap<Int, BigInteger>()
        fishes.entries.forEach {
            newstate.put(it.key - 1, it.value)
        }
        val killed = newstate.remove(-1)?.let {
            newstate.put(6, (newstate[6] ?: BigInteger.ZERO).add(it)             )
            newstate[8] = it
        }

//        var counter = 0
//        val newstate = fishes.map {
//            var newcount = it - 1
//            if(newcount==-1L) {
//                newcount=6
//                counter++
//            }
//            newcount
//        }
//
//        val fishes1 = newstate + MutableList(counter) { 8L }
        return Pool2(newstate)
    }
}

class Puzzle {
    init {

    }

    fun clean(input: List<String>): Pool {
        return Pool(input.flatMap { it.split(",") }.map { it.toLong() })
    }

    fun clean2(input: List<String>): Pool2 {

        val fishes = input.flatMap { it.split(",") }.map { it.toInt() }
        val newstate: MutableMap<Int, BigInteger> = LinkedHashMap<Int, BigInteger>()
        fishes.forEach {
            newstate.put(it, (newstate[it] ?: BigInteger.ZERO).add(BigInteger.ONE))

        }
        return Pool2(newstate)
    }

    val part1ExpectedResult = 5934
    fun part1(rawInput: List<String>): Int {
        var input = clean(rawInput)
        var day = 0

        while (day < 80) {
            day++
            input = input.next()
        }

        // 1868 <
        return input.fishes.size
    }

    val part2ExpectedResult = 26984457539L
    fun part2(rawInput: List<String>): Long {
        var input = clean2(rawInput)
        var day = 0

        while (day < 256) {
            day++
            input = input.next()
        }

        // 1868 <
        return input.fishes.values.fold(BigInteger.ZERO) {acc,it -> acc.add(it)} .toLong()
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
