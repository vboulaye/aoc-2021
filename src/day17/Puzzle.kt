package day17

import utils.Point
import utils.max
import utils.min
import utils.readInput
import java.lang.Integer.max
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


data class Input(val targetX: IntRange, val targetY: IntRange) {

}

class Puzzle {
    //target area: x=20..30, y=-10..-5
    fun clean(input: List<String>): Input {
        val xy = input[0].split(": ")[1].split(", ")
        val rangeXString = xy[0].substring(2).split("..").map { it.toInt() }
        val rangeX: IntRange = rangeXString.min()..rangeXString.maxOrNull()!!
        val rangeYStriung = xy[1].substring(2).split("..").map { it.toInt() }
        val rangeY: IntRange = rangeYStriung.min()..rangeYStriung.maxOrNull()!!
        return Input(rangeX, rangeY)
    }

    data class Position(val x: Int, val y: Int) {
        fun isInTargetArea(input: Input): Boolean {
            return input.targetX.contains(x) && input.targetY.contains(y)
        }
    }

    data class Velocity(val dx: Int, val dy: Int) {
        fun move(position: Position): Position {
            return Position(position.x + dx, position.y + dy)
        }
    }

    data class State(val position: Position, val velocity: Velocity, val steps: Int) {
        fun next(): State {
            val newPosition = velocity.move(position)
            val nextDx = when {
                velocity.dx > 0 -> velocity.dx - 1
                velocity.dx < 0 -> velocity.dx + 1
                else -> 0
            }
            val newVelocity = Velocity(nextDx, velocity.dy - 1)
            return State(newPosition, newVelocity, steps + 1)
        }
    }


    val part1ExpectedResult = 45L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        val result = ArrayList<Int>()
        (input.targetY.start..100).forEach nextY@{ testDy ->
            (1..input.targetX.endInclusive).forEach { testDx ->
                var state = State(Position(0, 0), Velocity(testDx, testDy), 0)
                var maxY = 0
                while (canBeReached(state, input)) {
                    state = state.next()
                    maxY = max(maxY, state.position.y)
                    if (state.position.isInTargetArea(input)) {
                        println("$testDy, $testDx")
                        result.add(maxY)
                        // return@nextY
                    }
                }
            }
        }
        //4371
        return result.max()!!.toLong()
    }

    private fun canBeReached(state: State, input: Input): Boolean {
        return !(state.position.y < input.targetY.first && state.velocity.dy<0)
                && !(state.velocity.dx == 0 && !input.targetX.contains(state.position.x))
                && !(state.velocity.dx > 0 && state.position.x > input.targetX.last)
                && !(state.velocity.dx < 0 && state.position.x < input.targetX.first)
                && state.steps < 1000
    }

    val part2ExpectedResult = 112L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val result = ArrayList<Velocity>()
        testVelocity(Velocity(20, -10), input)
        (input.targetY.start..10000).forEach nextY@{ testDy ->
            (1..input.targetX.endInclusive).forEach { testDx ->
                val testVelocit = Velocity(testDx, testDy)
                if(testVelocity(testVelocit, input)) {
                 //   println("$testDx,$testDy")
                    result.add(testVelocit)
                }
            }
        }
        val expected =
            "23,-10  25,-9   27,-5   29,-6   22,-6   21,-7   9,0     27,-7   24,-5 25,-7   26,-6   25,-5   6,8     11,-2   20,-5   29,-10  6,3     28,-7 8,0     30,-6   29,-8   20,-10  6,7     6,4     6,1     14,-4   21,-6 26,-10  7,-1    7,7     8,-1    21,-9   6,2     20,-7   30,-10  14,-3 20,-8   13,-2   7,3     28,-8   29,-9   15,-3   22,-5   26,-8   25,-8 25,-6   15,-4   9,-2    15,-2   12,-2   28,-9   12,-3   24,-6   23,-7 25,-10  7,8     11,-3   26,-7   7,1     23,-9   6,0     22,-10  27,-6 8,1     22,-8   13,-4   7,6     28,-6   11,-4   12,-4   26,-9   7,4 24,-10  23,-8   30,-8   7,0     9,-1    10,-1   26,-5   22,-9   6,5 7,5     23,-6   28,-10  10,-2   11,-1   20,-9   14,-2   29,-7   13,-3 23,-5   24,-8   27,-9   30,-7   28,-5   21,-10  7,9     6,6     21,-5 27,-10  7,2     30,-9   21,-8   22,-7   24,-9   20,-6   6,9     29,-5 8,-2    27,-8   30,-5   24,-7 "
                .split(Regex(" +"))
                .filter { !it.isBlank() }
                .map { it.split(",") }
                .filter { !it[0].isBlank() }
                .map { Velocity(it[0].toInt(), it[1].toInt()) }
        //
       //expected.toSet().minus(result).forEach { println(it) }

        // 1649
        return result.size.toLong()
    }

    private fun testVelocity(
        test: Velocity,
        input: Input,
    ): Boolean {
        var state = State(Position(0, 0), test, 0)
        var maxY = 0
        while (canBeReached(state, input)) {
            state = state.next()
            maxY = max(maxY, state.position.y)
            if (state.position.isInTargetArea(input)) {
                return true
            }
        }
        return false
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
//            val testResult = partEvaluator(testInput)
//            println("test ${part}: $testResult == ${expectedTestResult}")
//            check(testResult == expectedTestResult) { "$testResult != ${expectedTestResult}" }
        }
        val fullDuration = measureTime {
            val fullResult = partEvaluator(input)
            println("${part}: $fullResult")
        }
        println("${part}: test took ${testDuration.inWholeMilliseconds}ms, full took ${fullDuration.inWholeMilliseconds}ms")
    }

//    runPart("part1", puzzle.part1ExpectedResult, puzzle::part1)
    runPart("part2", puzzle.part2ExpectedResult, puzzle::part2)

}
