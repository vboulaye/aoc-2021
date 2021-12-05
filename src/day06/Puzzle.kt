package day06

import utils.readInput


class Puzzle {
    init {

    }

    fun clean(input: List<String>): List<String> {

        return input
    }

    val part1ExpectedResult = 0
    fun part1(rawInput: List<String>): Int {
        val  input = clean(rawInput)
        return 0
    }

    val part2ExpectedResult = 0
    fun part2(rawInput: List<String>): Int {
        val  input = clean(rawInput)
        return 0
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
