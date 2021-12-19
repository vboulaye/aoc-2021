package day19

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import utils.checkEquals
import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long

class Orientation() {
    val matrix: NDArray<Int, D2> = mk.ndarray(
        mk[
                mk[1, 0, 0],
                mk[0, 1, 0],
                mk[0, 0, 1]
        ]
    )
/*
[[1.5, 2.1, 3.0],
[4.0, 5.0, 6.0]]
*/
}

fun rotate(point: XYZ, orientation: Orientation): XYZ {
    val value =  point.asNdArray() * orientation.matrix
    return value
}

data class XYZ(val x: Int, val y: Int, val z: Int) {
    fun asArray(): IntArray {
        return intArrayOf(x, y, z)
    }
    fun asNdArray(): D1Array<Int> {
        return mk.ndarray(asArray())
    }
}

data class Vector(val dx: Int, val dy: Int, val dz: Int) {

    fun matches(v: Vector) = dx * dx + dy * dy + dz * dz == v.dx * v.dx + v.dy * v.dy + v.dz * v.dz

}

fun XYZ.toVector(end: XYZ) = Vector(end.x - x, end.y - y, end.z - z)

data class Scanner(val index: Int, val points: List<XYZ>) {



    init {

    }
}

class Puzzle {
    fun clean(input: List<String>): MutableList<Scanner> {
        val result = mutableListOf<Scanner>()
        var index = 0
        var points = mutableListOf<XYZ>()
        input
            .filter { it.isNotEmpty() }
            .forEach { line ->

                val isStart = Regex("--- scanner (\\d+) ---").matchEntire(line)
                if (isStart != null) {
                    isStart.groups[1]?.let {
                        if (it.value != "0") {
                            result.add(Scanner(index, points))
                            index++
                            points = mutableListOf<XYZ>()
                        }
                    }

                } else {
                    points.add(line.split(",").let { XYZ(it[0].toInt(), it[1].toInt(), it[2].toInt()) })
                }
            }
        return result
    }


    val part1ExpectedResult = 19L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        input.forEach()
        return 0
    }

    val part2ExpectedResult = 0L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        return 0
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
            checkEquals(testResult, expectedTestResult)
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
