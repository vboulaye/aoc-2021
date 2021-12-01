package day02

import utils.readInput

data class Ctx(
    val data:String
) {

}

class Puzzle {

    fun part1(input: List<String>): Int {
        return part1Work(part1Clean(input))
    }


    fun part2(input: List<String>): Int {
        return part2Work(part1Clean(input))
    }

}
fun part1Clean(input: List<String>): List<Int> {
    return input.map(String::toInt)
}

fun part1Work(input: List<Int>): Int {
    return input.size

}

fun part2Work(input: List<Int>): Int {
    return input.size
}

fun main() {
    val puzzle = Puzzle()
    println(Puzzle::class.qualifiedName)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("test", Puzzle::class)
    check(puzzle.part1(testInput) == 0)
    //check(puzzle.part2(testInput) == 0)

    val input = readInput("data", Ctx::class)
    println(puzzle.part1(input))
    println(puzzle.part2(input))
}
