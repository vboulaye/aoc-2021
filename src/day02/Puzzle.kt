package day02

import utils.readInput
import java.lang.IllegalStateException

data class Ctx(
    val depth: Int=0,
    val pos: Int=0
) {

    fun move(action: Move): Ctx {
        return when {
            action.dir.startsWith("f") -> Ctx(depth, pos + action.mov)
            action.dir.startsWith("u") -> Ctx(depth - action.mov, pos)
            action.dir.startsWith("d") -> Ctx(depth + action.mov, pos)
            else -> throw IllegalStateException();
        }
    }

    fun result(): Int {
        return depth * pos
    }
}

data class Move(val dir: String, val mov: Int)

class Puzzle {

    fun part1(input: List<String>): Int {
        return part1Work(part1Clean(input))
    }


    fun part2(input: List<String>): Int {
        return part2Work(part1Clean(input))
    }

}

fun part1Clean(input: List<String>): List<Move> {
    return input
        .map { x -> x.split(" ") }
        .map { splited -> Move(splited[0], splited[1].toInt()) }
}

fun part1Work(input: List<Move>): Int {
    return input.fold(Ctx()) {acc, move ->  acc.move(move)}.result()

}

fun part2Work(input: List<Move>): Int {
    return input.size
}

fun main() {
    val puzzle = Puzzle()
    println(Puzzle::class.qualifiedName)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("test", Puzzle::class)
    check(puzzle.part1(testInput) == 150)
    //check(puzzle.part2(testInput) == 0)

    val input = readInput("data", Ctx::class)
    println(puzzle.part1(input))
    println(puzzle.part2(input))
}
