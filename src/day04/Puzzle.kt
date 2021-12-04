package day04

import utils.readInput

data class Plays(val numbers: List<Int>)

data class Grid(val array: List<List<Int>>) {
    fun hasWon(): Boolean {
        return array.any { it.all { it == -1 } }
                || (0 until array[0].size).any { col -> (0 until array[0].size).all { line -> array[line][col] == -1 } }
    }

    fun result(): Int {
        val total = array.fold(0) { acc, it -> acc + it.filter { it >= 0 }.sumOf { it } }
        return total
    }
}


data class Games(val players: List<Grid>) {

    fun hasWinner(): Boolean {
        return players.any { it.hasWon() }
    }

    fun getWinner(): Grid? {
        return players.find { it.hasWon() }
    }

    fun getNonWinners(): List<Grid> {
        return players.filter { !it.hasWon() }
    }
}

data class Input(val plays: Plays, val games: Games)
class Puzzle {

    fun clean(input: List<String>): Input {
        val plays = Plays(input[0].split(",").map { x -> x.toInt() })
        val games = Games(input.subList(2, input.size).windowed(5, 6)
            .map {
                Grid(it.map { line ->
                    val transform: (String) -> Int = { x -> x.toInt() }
                    line.split(Regex(" +"))
                        .filter { x -> !x.isBlank() }
                        .map(transform)
                })
            })
        return Input(plays, games)
    }

    data class Ctx(
        val index: Int = 0,
        val games: Games
    )

    val part1ExpectedResult = 4512
    fun part1(rawInput: List<String>): Int {
        val input = clean(rawInput)
        val resultGames: Int = play(0, input.plays, input.games)
        return resultGames
    }

    private fun play(i: Int, plays: Plays, games: Games): Int {
        val numberPlayed = plays.numbers[i]
        println("play " + numberPlayed)
        val newGames = Games(games.players
            .map { grid ->
                Grid(grid.array.map { line -> line.map { numberInGrid -> if (numberPlayed == numberInGrid) -1 else numberInGrid } })
            }
        )


        if (newGames.hasWinner()) {
            println("grid won " + newGames.getWinner()!!)
            return newGames.getWinner()!!.result() * numberPlayed
        }
        return play(i + 1, plays, newGames)
    }

    private fun play2(i: Int, plays: Plays, games: Games): Int {
        val numberPlayed = plays.numbers[i]
        println("play " + numberPlayed)
        val nonWinners = games.getNonWinners()
        val newGames = Games(games.players
            .map { grid ->
                Grid(grid.array.map { line -> line.map { numberInGrid -> if (numberPlayed == numberInGrid) -1 else numberInGrid } })
            }
        )
        if (newGames.hasWinner() && nonWinners.size == 1) {
          //  println("grid won " + newGames.getWinner()!!)
            val r=newGames.getWinner()!!;//Grid(nonWinners[0].array.map { line -> line.map { numberInGrid -> if (numberPlayed == numberInGrid) -1 else numberInGrid } })
            return r.result() * numberPlayed
        }
        //836 too low
        return play2(i + 1, plays, Games(newGames.players.filter { !it.hasWon() }))
    }


    val part2ExpectedResult = 1924
    fun part2(rawInput: List<String>): Int {
        val input = clean(rawInput)
        val resultGames: Int = play2(0, input.plays, input.games)
        return resultGames
    }


}


fun main() {
    val puzzle = Puzzle()
    println(Puzzle::class.qualifiedName)

    val testInput = readInput("00test", Puzzle::class)
    val input = readInput("zzdata", Puzzle::class)


//    println("test1: ${puzzle.part1(testInput)} == $puzzle.part1ExpectedResult")
//    check(puzzle.part1(testInput) == puzzle.part1ExpectedResult)
//    println("part1: ${puzzle.part1(input)}")

    println("test2: ${puzzle.part2(testInput)} == ${puzzle.part2ExpectedResult}")
    check(puzzle.part2(testInput) == puzzle.part2ExpectedResult)
    println("part2: ${puzzle.part2(input)}")

}
