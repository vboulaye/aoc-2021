package day21

import utils.readInput
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

class Puzzle {
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
        val dice = Dice(1, 3)
        while (true) {
            p1.move(dice)
            if (p1.score >= 21) {
                break;
            }
            p2.move(dice)
            if (p2.score >= 21) {
                break;
            }
        }
        return (dice.value * (if (p1.score >= 1000) p2.score else p1.score)).toLong()
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
