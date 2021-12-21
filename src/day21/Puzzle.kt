package day21

import utils.readInput
import java.lang.Long.max
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


class Dice(val init: Int = 1, val maxVal: Int = 100) {
    var value = init - 1
    fun roll(): Int {
        value++
        return (value - 1) % maxVal + 1
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
    val possibleDiceScores: MutableMap<Int, Int> = mutableMapOf()

    init {
        (3..9).forEach { whishedScore ->

            var counter = 0
            (1..3).forEach { d1 ->
                (1..3).forEach { d2 ->
                    (1..3).forEach { d3 ->
                        if (d1 + d2 + d3 == whishedScore) {
                            counter++
                        }
                    }
                }
            }
            possibleDiceScores[whishedScore] = counter
        }
    }

    fun clean(input: List<String>): List<Player> {
        return input
            .filter { line -> true }
            .map { line ->
                val des = Regex("""Player (\d) starting position: (\d)""").matchEntire(line)!!.destructured
                Player(des.component1().toInt(), des.component2().toInt());
            }
    }

    val part1ExpectedResult = 739785L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val p1 = input[0]
        val p2 = input[1]
        val dice = Dice()
        while (true) {
            p1.move(dice)
            if (p1.score >= 1000) {
                break;
            }
            p2.move(dice)
            if (p2.score >= 1000) {
                break;
            }
        }
        return (dice.value * (if (p1.score >= 1000) p2.score else p1.score)).toLong()
    }


    val part2ExpectedResult = 444356092776315L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val p1 = input[0]
        val p2 = input[1]


//3 -> 1
//        4-> 1+1+2 1+2+1 2+1+1 3

//        5 ->1+1+3 / 1+3+1 /1+1+3/ 1+2+2/ 2+2+1 /2+1+2 6
//        6 -> 3+2+1 /3+1+2 /2+3+1 / 2+1+3/ 2+2+2 /1+3+2/ 1+2+3  -> 7

//        val newD1 = Dirac(1,p1.position, 0, emptyList(), ArrayDeque())
//        val newD2 = Dirac(2, p2.position, 0, emptyList(), ArrayDeque())
        val newD1 = Dirac(1, p1.position, 0)
        val newD2 = Dirac(2, p2.position, 0)

        playD1(newD1, newD2)
//        val s1 = computeCount(newD1.games, possibleDiceScores)
//        val s2 = computeCount(newD2.games, possibleDiceScores)

//        while (true) {
//            p1.move(dice)
//            if (p1.score >= 21) {
//                break;
//            }
//            p2.move(dice)
//            if (p2.score >= 21) {
//                break;
//            }
//        }
//        return (dice.value * (if (p1.score >= 1000) p2.score else p1.score)).toLong()
//        return max(s1, s2);
        return max(newD1.globalCount.get(), newD2.globalCount.get());
    }

    private fun computeWinngin(
        player: Player,
        possibleDiceScores: MutableMap<Int, Int>
    ): Long {
        val games1: ArrayDeque<List<Rolls>> = ArrayDeque()
        computePossibleGames(player.position, games1)
//        val distinct = games1.map { it.sorted() }.distinct()
        return computeCount(games1, possibleDiceScores)
    }

    private fun computeCount(
        games1: ArrayDeque<List<Rolls>>,
        possibleDiceScores: MutableMap<Int, Int>
    ): Long {
        val score1 = games1.fold(BigInteger.ZERO) { acc, list ->
            val fold: BigInteger = list.fold(BigInteger.ONE) { acc2, rolls ->
                acc2 * possibleDiceScores[rolls]!!.toBigInteger()
            }
            acc + fold
        }
        return score1.toLong()
    }

    private fun computePossibleGames(
        position: Int,
        games: ArrayDeque<List<Rolls>>,
        score: Int = 0,
        rolls: List<Rolls> = emptyList()
    ) {
        if (score >= 21) {
            games.add(rolls)
            return
        }
        (3..9).forEach { rollsScore ->
            val newPosition = position + rollsScore
            val newScore = score + (newPosition + -1) % 10 + 1
            possibleDiceScores[rollsScore]!!
            computePossibleGames(newPosition, games, newScore, rolls + listOf(rollsScore))
        }

    }

    data class Dirac(
        val player: Int,
        val position: Int,
        val score: Int = 0,
//        val rolls: List<Rolls> = emptyList(),
//        val games: ArrayDeque<List<Rolls>>,
        val currentCount: Long = 1,
        var globalCount: AtomicLong = AtomicLong(0L),
    )


    private fun playD1(
        d1: Dirac,
        //  games: ArrayDeque<List<Rolls>>,
        d2: Dirac
    ) {
        if (d1.score >= 21) {
            //  println(d1.rolls)
//            d1.games.add(d1.rolls)
            d1.globalCount.set(d1.globalCount.get() + d1.currentCount)
//            if(d1.games.size%100000==0) {
//                println("${d1.player} ${d1.games.size}")
//            }

            return
        }
        (3..9).forEach { rollsScore ->
            val newPosition = d1.position + rollsScore
            val newScore = d1.score + (newPosition + -1) % 10 + 1
            val newD1 = Dirac(
                d1.player, newPosition, newScore, //d1.rolls + listOf(rollsScore), d1.games
                d1.currentCount * possibleDiceScores[rollsScore]!!.toLong(),
                d1.globalCount
            )

            playD1(d2, newD1)
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
