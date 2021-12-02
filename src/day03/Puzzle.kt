package day03

import utils.readInput

class Ctx(
    val value: Int = 0,
//    val depth: Int = 0,
//    val pos: Int = 0,
//    val aim: Int = 0,
) {

    fun next(move: Move): Ctx {
        return this
    }

    //    fun next(move: Move): Ctx = move.let { (dir, mov) ->
//        return when (dir) {
//            Direction.forward -> Ctx(depth, pos + mov)
//            Direction.up -> Ctx(depth - mov, pos)
//            Direction.down -> Ctx(depth + mov, pos)
//        }
//    }
//
    fun next2(move: Move): Ctx {
        return this
    }
//    fun next2(move: Move): Ctx = when (move.dir) {
//        Direction.forward -> Ctx(depth + aim * move.mov, pos + move.mov, aim)
//        Direction.up -> Ctx(depth, pos, aim - move.mov)
//        Direction.down -> Ctx(depth, pos, aim + move.mov)
//    }

    fun result(): Int {
        return 0;//depth * pos
    }
}

enum class Direction {
    forward, down, up
}

class Move(
//    val dir: Direction,
//    val mov: Int
)

class Puzzle {

    fun clean(input: List<String>): List<Move> {
        return input
            .map { x -> x.toInt() }
            .map { x -> Move() }
//            .map { line -> line.split(" ") }
//            .map { (rawDir, rawMove) -> Move(Direction.valueOf(rawDir), rawMove.toInt()) }
    }

    fun part1(rawInput: List<String>): Int {
        val input = clean(rawInput)
        return input
            .fold(Ctx()) { acc, move -> acc.next(move) }
            .result()
    }

    fun part2(rawInput: List<String>): Int {
        val input = clean(rawInput)
        return input
            .fold(Ctx()) { acc, move -> acc.next2(move) }
            .result()
    }

}


fun main() {
    val puzzle = Puzzle()
    println(Puzzle::class.qualifiedName)

    val testInput = readInput("00test", Puzzle::class)
    val input = readInput("zzdata", Ctx::class)

    val part1ExpectedResult = 0

    println("test1: ${puzzle.part1(testInput)} == $part1ExpectedResult")
    println("part1: ${puzzle.part1(input)}")
    check(puzzle.part1(testInput) == part1ExpectedResult)

    val part2ExpectedResult = 0

    println("test2: ${puzzle.part2(testInput)} == $part2ExpectedResult")
    println("part2: ${puzzle.part2(input)}")
    check(puzzle.part2(testInput) == part2ExpectedResult)

}
