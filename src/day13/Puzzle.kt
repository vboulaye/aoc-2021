package day13

import utils.Point
import utils.max
import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long

val X = "x"
val Y = "y"

data class Fold(val dir: String, val pos: Int) {

}


data class Input(val points: List<Point>, val folds: List<Fold>) {
}

class Puzzle {

    fun clean(input: List<String>): Input {
        val points: List<Point> =
            input.filter { it.contains(',') }.map { it.split(",") }.map { Point(it[0].toInt(), it[1].toInt()) }
        val folds: List<Fold> = input.filter { it.contains("fold along") }
            .map { it.split(" ") }.map { strings -> strings[2].split("=") }
            .map { instr -> Fold(instr[0], instr[1].toInt()) }
        return Input(points, folds)
    }

    val part1ExpectedResult = 17L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val array: List<List<Boolean>> = convert(input)
        val foldPaper = foldPaper(array, input.folds[0])
        //val result: List<List<Boolean>> = input.folds.fold(array) { acc, it -> foldPaper(acc, it) }
        //667 too low
        return foldPaper.sumOf { it.count { it } }.toLong()
    }

    private fun convert(input: Input): List<List<Boolean>> {
        val maxX = input.points.map { it.x }.max()
        val maxY = input.points.map { it.y }.max()
        val array: List<List<Boolean>> = (0..maxY).map { y ->
            (0..maxX).map { x ->
                val matchingPoint = input.points.find { it.x == x && it.y == y }
                matchingPoint != null
            }
        }
        return array
    }

    private fun foldPaper(points: List<List<Boolean>>, it: Fold): List<List<Boolean>> {

        val maxX = points[0].size
        val maxY = points.size
        val toY = if (it.dir == Y) {

            return (0..it.pos).map { y ->
                (0..maxX - 1).map { x ->
                    if (points[y][x]) true
                    else {
                        val other = try {
                            // 6->8
                            // 7-6 +7
                            points[(it.pos - y) + it.pos][x]
                        } catch (e: Exception) {
                            false
                        }
                        other
                    }
                }

            }
        } else {
            return (0..maxY - 1).map { y ->
                (0..it.pos).map { x ->
                    if (points[y][x]) true
                    else {
                        val other = try {
                            // 6->8
                            // 7-6 +7
                            points[y][(it.pos - x) + it.pos]
                        } catch (e: Exception) {
                            false
                        }
                        other
                    }
                }

            }
        }


    }

    val part2ExpectedResult = 0L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val array: List<List<Boolean>> = convert(input)
        // val foldPaper = foldPaper(array, input.folds[0])
        val result: List<List<Boolean>> = input.folds.fold(array) { acc, it -> foldPaper(acc, it) }
        //667 too low
        val output = result.joinToString("\n") { line -> line.joinToString { if (it) "#" else " " } }
        System.err.println(output)
        return 0;//foldPaper.sumOf { it.count { it } }.toLong()
    }

}

//#, #, #, #,  ,  , #, #,  ,  ,  , #, #,  ,  , #,  ,  , #,  , #, #, #,  ,  , #, #, #, #,  , #,  ,  , #,  , #, #, #, #,  ,
//#,  ,  ,  ,  , #,  ,  , #,  , #,  ,  , #,  , #,  ,  , #,  , #,  ,  , #,  ,  ,  ,  , #,  , #,  ,  , #,  , #,  ,  ,  ,  ,
//#, #, #,  ,  , #,  ,  , #,  , #,  ,  ,  ,  , #,  ,  , #,  , #,  ,  , #,  ,  ,  , #,  ,  , #, #, #, #,  , #, #, #,  ,  ,
//#,  ,  ,  ,  , #, #, #, #,  , #,  , #, #,  , #,  ,  , #,  , #, #, #,  ,  ,  , #,  ,  ,  , #,  ,  , #,  , #,  ,  ,  ,  ,
//#,  ,  ,  ,  , #,  ,  , #,  , #,  ,  , #,  , #,  ,  , #,  , #,  , #,  ,  , #,  ,  ,  ,  , #,  ,  , #,  , #,  ,  ,  ,  ,
//#,  ,  ,  ,  , #,  ,  , #,  ,  , #, #, #,  ,  , #, #,  ,  , #,  ,  , #,  , #, #, #, #,  , #,  ,  , #,  , #, #, #, #,  ,
//FAGURZME
// FAGURZHE
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
