package day07

import utils.readInput
import java.lang.Math.abs
import java.math.BigInteger

class Puzzle {
    init {

    }


    fun clean(input: List<String>): List<Int> {
        return input.flatMap { it.split(",")}.map { it.toInt() }

    }

    data class FuelForPos(val index:Int, val counters:List<Long>, val total:Long)
    val part1ExpectedResult = 37L
    fun part1(rawInput: List<String>): Long {
        var input = clean(rawInput)
        val max = input.maxOrNull()!!
        val min = input.minOrNull()!!
        val fuelUses= MutableList(max-min) {0}
        val fuel = (min..max).map { index ->
            val counters = input.map { abs(it - index).toLong() }

            FuelForPos(index, counters, counters.sum().toLong())
        }
        val minconsumptiom = fuel.map { it.total }.minOrNull()!!

        return minconsumptiom
    }
    fun fact(i: BigInteger):BigInteger = if (BigInteger.ZERO.equals(i)) BigInteger.ZERO else i.add(fact(i.minus(
        BigInteger.ONE)))


    val part2ExpectedResult = 168L
    fun part2(rawInput: List<String>): Long {
        var input = clean(rawInput)
        val max = input.maxOrNull()!!
        val min = input.minOrNull()!!
        val fuelUses= MutableList(max-min) {0}
        val fuel = (min..max).map { index ->
            val counters = input.map {
             val x=   fact( BigInteger(""+abs(it - index))).toLong()
                x
            }

            FuelForPos(index, counters, counters.fold(BigInteger.ZERO){acc, bigInteger -> acc.add( BigInteger(""+bigInteger)) }.toLong())
        }
        val minconsumptiom = fuel.map { it.total }.minOrNull()!!

        return minconsumptiom
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
