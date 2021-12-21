package day21

import utils.modByOne
import utils.readInput
import java.lang.Long.max
import java.lang.Math.min
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


class Dice(val init: Int = 1, val maxVal: Int = 100) {
    var value = init - 1
    fun roll(): Int {
        value++
        return modByOne(value, maxVal)
    }

    fun next(): Int {
        return roll() + roll() + roll()
    }
}

data class Player(val player: Int, var position: Int) {
    var score: Int = 0
    fun move(dice: Dice): Player {
        val next = dice.next()
        position = (position + next )
        score += (position + -1) % 10 + 1
        return this
    }
}

typealias Rolls = Int
class Puzzle {

    fun clean(input: List<String>): List<Player> {
        return input
            .map { line ->
                val (player, position) = Regex("""Player (\d) starting position: (\d)""").matchEntire(line)!!.destructured
                Player(player.toInt(), position.toInt());
            }
    }

    val part1ExpectedResult = 739785L
    fun part1(rawInput: List<String>): Result {
        val (p1, p2) = clean(rawInput)
        val dice = Dice()
        val winningScore = 1000
        while (true) {
            p1.move(dice)
            if (p1.score >= winningScore) {
                break;
            }
            p2.move(dice)
            if (p2.score >= winningScore) {
                break;
            }
        }
        return (dice.value * min(p2.score, p1.score)).toLong()
    }

    val part2ExpectedResult = 444356092776315L
    fun part2(rawInput: List<String>): Result {
        val (p1, p2) = clean(rawInput)
        val diracGame = DiracGame(p1, p2)
        val (p1WinCount, p2WinCount) = diracGame.computeWinningScore()
        return max(p1WinCount, p2WinCount);
    }


    class DiracGame(val p1: Player, val p2: Player) {
        val possibleDiracDiceScores: Map<Int, Long> =
            (1..3).flatMap { x ->
                (1..3).flatMap { y ->
                    (1..3).map { z ->
                        x + y + z
                    }
                }
            }.groupingBy { it }.eachCount()
                .map { (k, v) -> k to v.toLong() }
                .toMap()


        data class WinningUniverses(
            val first: Long = 0,
            val second: Long = 0,
        ) {
            fun reverse() = WinningUniverses(second, first)
            operator fun plus(other: WinningUniverses) = WinningUniverses(first + other.first, second + other.second)
            operator fun times(factor: Long) = WinningUniverses(first * factor, second * factor)
        }


        fun computeWinningScore(): WinningUniverses {
            val d1 = DiracState(p1.player, p1.position, 0)
            val d2 = DiracState(p2.player, p2.position, 0)
            return playRound(d1, d2)
        }

        data class DiracState(
            val player: Int,
            val position: Int,
            val score: Int,
        )

        val cache = mutableMapOf<Pair<DiracState, DiracState>, WinningUniverses>()

        private fun playRound(d1: DiracState, d2: DiracState): WinningUniverses {
            val cached = cache[d1 to d2]
            if (cached != null) {
                return cached
            }

            val winningScores = possibleDiracDiceScores.entries
                .fold(WinningUniverses()) { acc, (rollsScore, universesCountForScore) ->
                    val newPosition = modByOne(d1.position + rollsScore, 10)
                    val newScore = d1.score + newPosition
                    if (newScore >= 21) {
                        acc + WinningUniverses(universesCountForScore, 0)
                    } else {
                        val newD1 = d1.copy(position = newPosition, score = newScore)
                        val subWinCount = playRound(d2, newD1)
                        acc + subWinCount.reverse() * universesCountForScore
                    }
                }
            cache[d1 to d2] = winningScores
            return winningScores
        }
    }


}


@OptIn(ExperimentalTime::class)
fun main() {
    val puzzle = Puzzle()
    println(Puzzle::class.qualifiedName)

    val testInput = readInput("00test", Puzzle::class)
    val input = readInput("zzdata", Puzzle::class)

    fun runPart(part: String, expectedTestResult: Result, partEvaluator: (List<String>) -> Result) {
        val testDuration = measureTime {
            val testResult = partEvaluator(testInput)
            println("test ${part}: $testResult == ${expectedTestResult}")
            check(testResult == expectedTestResult) { "$testResult != ${expectedTestResult}" }
        }
        val fullDuration = measureTime {
            val fullResult = partEvaluator(input)
            println("${part}: $fullResult")
        }
        println("${part}: test took ${testDuration.inWholeMilliseconds}ms, full took ${fullDuration.inWholeMilliseconds}ms")
    }

    runPart("part1", puzzle.part1ExpectedResult, puzzle::part1)
    runPart("part2", puzzle.part2ExpectedResult, puzzle::part2)

}
