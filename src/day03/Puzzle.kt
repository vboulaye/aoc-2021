package day03

import utils.readInput

fun matchesDigit(c: Char, c1: Char): Byte {
    return if (c == c1) 1 else 0;
}

data class Result(val gamma: Int, val epsilon: Int = 0)
data class Popularity(val one: Int = 0, val zero: Int = 0) {
    fun mergeRecord(index: Int, char: Char): Popularity {
      return  Popularity(
            this.one + matchesDigit(char, '1'),
            this.zero + matchesDigit(char, '0')
        )
    }
}

data class Ctx(
    val values: List<Popularity>,
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
        val transformMost: (Popularity) -> Int = { if (it.one > it.zero) 1 else 0 }
        val transformLeast: (Popularity) -> Int = { if (it.one > it.zero) 0 else 1 }
        val map = this.values.map(transformMost)
        val map2 = this.values.map(transformLeast)
        var gamma: String = ""
        for (i in map) {
            gamma += i
        }

        var epsilon: String = ""
        for (i in map2) {
            epsilon += i
        }

        return gamma.toInt(2) * epsilon.toInt(2);//depth * pos
    }

    fun merge(move: String): Ctx {

        val mergeRecord: (index: Int, Char) -> Popularity = { index, c ->
            Popularity(
                this.values[index].one + matchesDigit(c, '1'),
                this.values[index].zero + matchesDigit(c, '0')
            )
        }
        val mapIndexed = move.mapIndexed(mergeRecord)
        return Ctx(mapIndexed)
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

    fun clean(input: List<String>): List<String> {
        return input
//            .map { x -> x.toInt() }
//            .map { x -> Move() }
//            .map { line -> line.split(" ") }
//            .map { (rawDir, rawMove) -> Move(Direction.valueOf(rawDir), rawMove.toInt()) }
    }

    fun part1(rawInput: List<String>): Int {
        val input = clean(rawInput)

        val init = List(input[0].length) { Popularity() }
        val fold = input.fold(Ctx(init))
        { acc, move -> acc.merge(move) }
        return fold
//            .fold(Ctx()) { acc, move -> acc.next(move) }
            .result()
    }

    fun part2(rawInput: List<String>): Int {
        val input = clean(rawInput)

        val oxygen = processs(input, 0, true)
        val co2 = processs(input, 0, false)

        val toInt = oxygen.toInt(2)
        val toInt1 = co2.toInt(2)
        return toInt * toInt1
        //    .fold(Ctx()) { acc, move -> acc.next2(move) }
        //     .result()
    }

     tailrec fun processs(input: List<String>, index: Int, mode: Boolean): String {
        val popularityForIndexedValue = input.fold(Popularity()) { acc, record -> acc.mergeRecord(index, record[index]) }
        val matchingChar = if(mode) {
            if(popularityForIndexedValue.one>=popularityForIndexedValue.zero) '1' else '0'
        } else {
            if(popularityForIndexedValue.one==popularityForIndexedValue.zero)  '0'
            if(popularityForIndexedValue.one<popularityForIndexedValue.zero) '1' else '0'
        }
        val filtered = input.filter { it[index] == matchingChar }
        if(filtered.size==1) {return filtered[0]}
        else {
           return  processs(filtered, index+1, mode)
        }
    }

}

private fun <E> List<E>.merge(move: String) {
    move.mapIndexed { index, c -> }
    TODO("Not yet implemented")
}


fun main() {
    val puzzle = Puzzle()
    println(Puzzle::class.qualifiedName)

    val testInput = readInput("00test", Puzzle::class)
    val input = readInput("zzdata", Ctx::class)

    val part1ExpectedResult = 198

    println("test1: ${puzzle.part1(testInput)} == $part1ExpectedResult")
    println("part1: ${puzzle.part1(input)}")
    check(puzzle.part1(testInput) == part1ExpectedResult)

    val part2ExpectedResult = 230

    println("test2: ${puzzle.part2(testInput)} == $part2ExpectedResult")
    println("part2: ${puzzle.part2(input)}")
    check(puzzle.part2(testInput) == part2ExpectedResult)

}
