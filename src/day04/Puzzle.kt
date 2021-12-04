package day04

import utils.readInput

data class Plays(private val numbers: List<Int>) {
    fun next(): Pair<Int, Plays> = Pair(numbers[0], Plays(numbers.drop(1)))
}

const val ARRAY_DIM = 5

private const val FOUND = -1

data class Grid(private val array: List<List<Int>>) {
    init {
        check(array.size == ARRAY_DIM)
        check(array.all { it.size == ARRAY_DIM })
    }

    fun playNumber(numberPlayed: Int): Grid {
        return Grid(this.array
            .map { line ->
                line
                    .map { numberInGrid -> if (numberPlayed == numberInGrid) FOUND else numberInGrid }
            })
    }

    fun hasWon(): Boolean {
        return hasRowWon() || hasColWon()
    }

    private fun hasRowWon() = array.any { line -> line.all { numberInGrid -> numberInGrid == FOUND } }

    private fun hasColWon(): Boolean {
        val indexBrowser = 0 until ARRAY_DIM
        return indexBrowser.any { colIndex -> indexBrowser.all { lineIndex -> array[lineIndex][colIndex] == FOUND } }
    }

    fun result(): Int {
        return array.fold(0) { acc, it -> acc + it.filter { it != FOUND }.sum() }
    }
}

data class Games(val list: List<Grid>) {

    fun getWinner(): Grid? = list.find { it.hasWon() }

    fun getNonWinners(): List<Grid> = list.filter { !it.hasWon() }
}

data class Input(val plays: Plays, val games: Games) {
}

tailrec fun playPart1(input: Input): Int {
    val (numberPlayed, nextPlays) = input.plays.next()
    val newGames = Games(input.games.list.map { grid -> grid.playNumber(numberPlayed) })
    return newGames.getWinner()
        ?.let { winner -> winner.result() * numberPlayed }
        ?: playPart1(Input(nextPlays, newGames))
}

tailrec fun playPart2(input: Input): Int {
    val (numberPlayed, nextPlays) = input.plays.next()
    val nonWinners = input.games.getNonWinners()
    val newGames = Games(input.games.list.map { grid -> grid.playNumber(numberPlayed) })

    if (nonWinners.size == 1) {
        val winner = newGames.getWinner()
        if (winner != null) {
            //  println("grid won " + winner)
            return winner.result() * numberPlayed
        }
    }

    return playPart2(Input(nextPlays, Games(newGames.list.filter { !it.hasWon() })))
}

class Puzzle {

    fun clean(input: List<String>): Input {
        val plays = Plays(input[0].split(",").map { x -> x.toInt() })
        val games = Games(input.subList(2, input.size)
            .windowed(ARRAY_DIM, 6)
            .map { table ->
                Grid(table.map { line ->
                    line.split(Regex(" +"))
                        .filter(String::isNotBlank)
                        .map(String::toInt)
                }
                )
            })
        return Input(plays, games)
    }

    val part1ExpectedResult = 4512
    fun part1(rawInput: List<String>): Int {
        val input = clean(rawInput)
        return playPart1(input);// play(0, input.plays, input.games)
    }

    val part2ExpectedResult = 1924
    fun part2(rawInput: List<String>): Int {
        val input = clean(rawInput)
        return playPart2(input)
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
