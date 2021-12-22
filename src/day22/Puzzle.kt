package day22

import day19.XYZ
import utils.readInput
import java.lang.Math.max
import java.lang.Math.min
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


data class Dims(val xs: IntRange, val ys: IntRange, val zs: IntRange) {
    fun isIncludedIn(dimensions: Dims): Boolean {
        return (dimensions.xs.contains(xs.first)
                && dimensions.xs.contains(xs.last)
                && dimensions.ys.contains(ys.first)
                && dimensions.ys.contains(ys.last)
                && dimensions.zs.contains(zs.first)
                && dimensions.zs.contains(zs.last))
    }

    fun contains(vertex: XYZ): Boolean {
        return xs.contains(vertex.x) && ys.contains(vertex.y) && zs.contains(vertex.z)
    }

    fun volume() = xs.length().toLong() * ys.length().toLong() * zs.length().toLong()

    fun intersect(cube: Dims): Boolean {
        return cube.xs.overlap(this.xs)
                && cube.ys.overlap(this.ys)
                && cube.zs.overlap(this.zs)
    }
}

data class Input(val mode: Boolean, val dimensions: Dims) {
    fun contains(x: Int, y: Int, z: Int) =
        this.dimensions.xs.contains(x) && this.dimensions.ys.contains(y) && this.dimensions.zs.contains(z)
}

data class Grid(val instructions: List<Input>) {
    fun getMode(x: Int, y: Int, z: Int): Boolean {
        val instructions1 = instructions
        return getMode(instructions1, x, y, z)
    }

    fun getMode(
        instructions1: List<Input>, x: Int, y: Int, z: Int
    ): Boolean {
        return instructions1.fold(false) { acc, input ->
            if (input.contains(x, y, z)) {
                input.mode
            } else {
                acc
            }
        }
    }

    fun getOnCount(xs: IntRange, ys: IntRange, zs: IntRange): Int {
        return xs.sumOf { x ->
            ys.sumOf { y ->
                zs.count { z ->
                    getMode(x, y, z)
                }
            }
        }
    }

    fun getOnCount(): Long {
        val onCubes = instructions.foldIndexed(listOf<Dims>()) { i, keptCubes, instruction ->
            println("" + i + " : " + keptCubes.size)

            val nextCube = instruction.dimensions
            if (keptCubes.isEmpty() && instruction.mode) {
                listOf(nextCube)
            } else {
                val splitCubes = keptCubes.flatMap { cube: Dims ->
                    val intersect = nextCube.intersect(cube)
                    if (intersect) {
                        splitXs(cube, nextCube.xs.first, true)
                            .flatMap { splitXs(it, nextCube.xs.last, false) }
                            .flatMap { splitYs(it, nextCube.ys.first, true) }
                            .flatMap { splitYs(it, nextCube.ys.last, false) }
                            .flatMap { splitZs(it, nextCube.zs.first, true) }
                            .flatMap { splitZs(it, nextCube.zs.last, false) }
                            .filter { !it.isIncludedIn(nextCube) }
                    } else {
                        if (!cube.isIncludedIn(nextCube)) {
                            listOf(cube)
                        } else {
                            emptyList()
                        }
                    }
                }

                if (instruction.mode) {
                    splitCubes + listOf(nextCube)
                } else {
                    splitCubes
                }
            }

        }
        return onCubes.sumOf { it.volume() }
    }

    private fun splitXs(existingCube: Dims, intersect: Int, includeRight: Boolean): List<Dims> {
        val range = existingCube.xs
        return if (isInsideRange(intersect, range, includeRight)) {
            listOf(
                existingCube.copy(xs = computeLeftRange(range, intersect, includeRight)),
                existingCube.copy(xs = computeRightRange(range, intersect, includeRight)),
            )
        } else {
            listOf(existingCube)
        }
    }

    private fun computeRightRange(
        range: IntRange,
        intersect: Int,
        includeRight: Boolean
    ) = intersect + (if (includeRight) 0 else 1)..range.last

    private fun computeLeftRange(
        range: IntRange,
        intersect: Int,
        includeRight: Boolean
    ) = range.first..intersect + (if (includeRight) -1 else 0)

    private fun splitYs(existingCube: Dims, intersect: Int, includeRight: Boolean): List<Dims> {
        val range = existingCube.ys
        return if (isInsideRange(intersect, range, includeRight)) {
            listOf(
                existingCube.copy(ys = computeLeftRange(range, intersect, includeRight)),
                existingCube.copy(ys = computeRightRange(range, intersect, includeRight)),
            )
        } else {
            listOf(existingCube)
        }
    }

    private fun splitZs(existingCube: Dims, intersect: Int, includeRight: Boolean): List<Dims> {
        val range = existingCube.zs
        return if (isInsideRange(intersect, range, includeRight)) {
            listOf(
                existingCube.copy(zs = computeLeftRange(range, intersect, includeRight)),
                existingCube.copy(zs = computeRightRange(range, intersect, includeRight)),
            )
        } else {
            listOf(existingCube)
        }
    }

    private fun isInsideRange(intersect: Int, range: IntRange, includeRight: Boolean) =
        if (includeRight)
            intersect > range.first && intersect <= range.last
        else
            intersect >= range.first && intersect < range.last

}

fun IntRange.length() = this.last - this.first + 1

private fun IntRange.overlap(newRange: IntRange): Boolean {
    val newLength = newRange.length()
    val thisLength = this.length()
    return if (thisLength < newLength) {
        this.any { it in newRange }
    } else {
        newRange.any { it in this }
    }
}

class Puzzle {
    fun clean(input: List<String>): Grid {
        val inputs = input
            .map { line ->
                val (mode, dims) = line.split(" ")
                val (fX, tX, fY, tY, fZ, tZ) = Regex("""x=(-?\d+)..(-?\d+),y=(-?\d+)..(-?\d+),z=(-?\d+)..(-?\d+)""").matchEntire(
                    dims
                )!!.destructured
                val xs: IntRange = (min(fX.toInt(), tX.toInt())..max(fX.toInt(), tX.toInt()))
                val ys: IntRange = (min(fY.toInt(), tY.toInt())..max(fY.toInt(), tY.toInt()))
                val zs: IntRange = (min(fZ.toInt(), tZ.toInt())..max(fZ.toInt(), tZ.toInt()))
                Input(mode == "on", Dims(xs, ys, zs))
            }
        return Grid(inputs)

    }

    val part1ExpectedResult = 474140L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        return input.getOnCount(-50..50, -50..50, -50..50).toLong()
    }

    val part2ExpectedResult = 2758514936282235L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        return input.getOnCount()
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

    // runPart("part1", puzzle.part1ExpectedResult, puzzle::part1)
    runPart("part2", puzzle.part2ExpectedResult, puzzle::part2)

}
