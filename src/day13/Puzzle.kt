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

data class Input(val points: Set<Point>, val folds: List<Fold>) {
    val maxX = this.points.map { it.x }.max()
    val maxY = this.points.map { it.y }.max()
    val array: List<List<Boolean>> = convert()
    private fun convert(): List<List<Boolean>> {
        val array: List<List<Boolean>> = (0..maxY).map { y ->
            (0..maxX).map { x ->
                this.points.contains(Point(x, y))
            }
        }
        return array
    }
}


class Puzzle {

    fun clean(input: List<String>): Input {
        val points: Set<Point> =
            input.filter { it.contains(',') }
                .map { it.split(",") }
                .map {
                    check(it.size == 2)
                    Point(it[0].toInt(), it[1].toInt())
                }
                .toSet()
        val folds: List<Fold> = input.filter { it.contains("fold along ") }
            .map { it.substring("fold along ".length) }
            .map { strings -> strings.split("=") }
            .map { instr ->
                check(instr.size == 2)
                Fold(instr[0], instr[1].toInt())
            }
        return Input(points, folds)
    }

    val part1ExpectedResult = 17L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val foldPaper = foldPaper(input.array, input.folds[0])
        return foldPaper.sumOf { it.count { it } }.toLong()
    }


    data class Folder(val toX: Int, val toY: Int, val yGetter: (Int) -> Int, val xGetter: (Int) -> Int) {

        fun fold(points: List<List<Boolean>>): List<List<Boolean>> {
            return (0..toY).map { y ->
                (0..toX).map { x ->
                    if (points[y][x]) true
                    else {
                        val other = try {
                            points[yGetter(y)][xGetter(x)]
                        } catch (e: Exception) {
                            false
                        }
                        other
                    }
                }
            }
        }
    }

    private fun foldPaper(points: List<List<Boolean>>, it: Fold): List<List<Boolean>> {
        val maxY = points.size
        val maxX = points[0].size
        val mirrorGetter: (Int) -> Int = { index -> (it.pos - index) + it.pos }
        val identityGetter: (Int) -> Int = { index -> index }

        val folder = if (it.dir == Y) {
            Folder(maxX - 1, it.pos, mirrorGetter, identityGetter)
        } else {
            Folder(it.pos, maxY - 1, identityGetter, mirrorGetter)
        }
        return folder.fold(points)

    }

    val part2ExpectedResult = 0L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val result: List<List<Boolean>> = input.folds.fold(input.array) { acc, it -> foldPaper(acc, it) }
        val output = result.joinToString("\n") { line -> line.joinToString { if (it) "#" else " " } }
        println(output)
        return 0;
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
