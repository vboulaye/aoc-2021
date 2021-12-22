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
        val isIncl = dimensions.xs.contains(this.xs.first)
                && dimensions.xs.contains(this.xs.last)
                && dimensions.ys.contains(this.ys.first)
                && dimensions.ys.contains(this.ys.last)
                && dimensions.zs.contains(this.zs.first)
                && dimensions.zs.contains(this.zs.last)
        return isIncl
    }

    fun contains(vertex: XYZ): Boolean {
        return xs.contains(vertex.x) && ys.contains(vertex.y) && zs.contains(vertex.z)
    }

    fun volume() = xs.length().toLong() * ys.length().toLong() * zs.length().toLong()

    fun vertices() =
        listOf(xs.first, xs.last).flatMap { x ->
            listOf(ys.first, ys.last).flatMap { y ->
                listOf(zs.first, zs.last).map { z -> XYZ(x, y, z) }
            }
        }

    fun intersect(cube: Dims): Boolean {
        return cube.xs.overlap(this.xs)
                && cube.ys.overlap(this.ys)
                && cube.zs.overlap(this.zs)
    }
}

data class Input(val mode: Boolean, val dimensions: Dims) {
    val contents: Int = if (mode) {
        dimensions.xs.length() * dimensions.ys.length() * dimensions.zs.length()
    } else {
        0
    }

    fun contains(x: Int, y: Int, z: Int) =
        this.dimensions.xs.contains(x) && this.dimensions.ys.contains(y) && this.dimensions.zs.contains(z)

}

data class Grid(val instructions: List<Input>) {
    //    val cache: MutableMap<XYZ, Boolean> = mutableMapOf()
    fun getMode(x: Int, y: Int, z: Int): Boolean {
//        return cache.computeIfAbsent(XYZ(x, y, z)) {
        val instructions1 = instructions
        return getMode(instructions1, x, y, z)
//        }

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

    data class HoledDims(
        val xs: MutableList<IntRange> = mutableListOf(),
        val ys: MutableList<IntRange> = mutableListOf(),
        val zs: MutableList<IntRange> = mutableListOf(),
    )

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

//                val filtered = splitCubes.filter { test ->
//                    keptCubes.none { existingDims -> test !== existingDims && test.isIncludedIn(existingDims) }
//                }

                if (instruction.mode) {
                    splitCubes + listOf(nextCube)
                } else {
                    splitCubes
                }
            }

        }
//        val instr = instructions.toMutableList()
//        var keptCubes = listOf(instr.removeFirst().dimensions)
//        while (!instr.isEmpty()) {
//            val nextCube = instr.removeFirst()
//            println("" + instr.size + " : " + keptCubes.size)
//
//        }

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
//2758642609268716
//2758642609268716
//2758642609274960
//    fun getOnCount(): Long {
////        return instructions.foldIndexed(0L) { i, acc, input: Input ->
////            println(i)
////            acc + getOnCount(input.dimensions.xs, input.dimensions.ys, input.dimensions.zs)
////        }
//
//
//        checkEquals(
//            getVx(2..6, 0..4),
//            listOf(0..1, 2..4, 5..6)
//        )
//
//        checkEquals(
//            getVx(2..6, 2..4),
//            listOf(2..4, 5..6)
//        )
//
//        checkEquals(
//            getVx(1..6, 3..4),
//            listOf(1..2, 3..4, 5..6)
//        )
//
//        checkEquals(
//            getVx(1..6, 4..8),
//            listOf(1..3, 4..6, 7..8)
//        )
//        checkEquals(
//            getVx(1..6, 4..6),
//            listOf(1..3, 4..6)
//        )
//
//        checkEquals(
//            getVx(1..3, 0..2),
//            listOf(0..0, 1..2, 3..3)
//        )
//        val splitCubes = mutableListOf<Input>()
//
//        val inputsToInclude = instructions.toMutableList()
//
//
//        while (!inputsToInclude.isEmpty()) {
//            val inputToInclude = inputsToInclude.removeLast()
//            splitCubes.forEach { input ->
//                if (input.dimensions.isIncludedIn(inputToInclude.dimensions)) {
//                    emptyList<Input>()
//                } else {
//                    emptyList()
//                }
//            }
//        }
//
//        instructions.forEach { input ->
//
//            var i = 0
//            while (i < splitCubes.size) {
//                val inputToCheck = splitCubes[i]
//                if (inputToCheck.dimensions.isIncludedIn(inputToInclude.dimensions)) {
//                    splitCubes.remove(inputToCheck)
//                } else {
//                    inputToInclude.dimensions.vertices.forEach { vertex ->
//                        if (inputToCheck.dimensions.contains(vertex)) {
//                            splitCubes.remove(inputToCheck)
//                            val splitX = getVx(inputToCheck.dimensions.xs, inputToInclude.dimensions.xs)
//                            val splitY = getVx(inputToCheck.dimensions.ys, inputToInclude.dimensions.ys)
//                            val splitZ = getVx(inputToCheck.dimensions.zs, inputToInclude.dimensions.zs)
//
//                            val splitCubes: List<Input> = splitX.flatMap { vX ->
//                                splitY.flatMap { vY ->
//                                    splitZ.map { vZ ->
//                                        Input(
//                                            getMode(
//                                                listOf(inputToCheck, inputToInclude),
//                                                vX.first,
//                                                vY.first,
//                                                vZ.first
//                                            ),
//                                            Dims(vX, vY, vZ)
//                                        )
//                                    }
//                                }
//                            }
//                            val newCubes = mutableListOf<Input>()
//                            splitCubes.forEach {
//                                if (it.dimensions.isIncludedIn(inputToCheck.dimensions)) {
//                                    splitCubes.add(it)
//                                } else {
//                                    newCubes.add(it)
//                                }
//                            }
//                            return newCubes
//                        }
//                    }
//
//                }
//                i++
//            }
//
//
////            for (instruction in instructions) {
////
////            }
////            while (!inputsToInclude.isEmpty()) {
////                val inputToInclude = inputsToInclude.removeFirst()
//////                holedDims.removeIf { it.dimensions.isIncludedIn(inputToInclude.dimensions) }
////                holedDims.toList()
////                    .forEach { inputToCheck ->
////                        if (inputToCheck.dimensions.isIncludedIn(inputToInclude.dimensions)) {
////                            holedDims.remove(inputToCheck)
////                        } else {
////
////                        }
////                    }
////                holedDims.add(inputToInclude)
////                val newCubes = emptyList<Input>()
////                inputsToInclude.addAll(newCubes)
////            }
//        }
//
////        instructions.forEach { input ->
////
////            holedDims.removeIf { it.dimensions.isIncludedIn(input.dimensions) }
////            input.dimensions.vertices.forEach { vertex ->
////                holedDims.toList()
////                    .forEach { existingDim ->
////                        if (existingDim.dimensions.contains(vertex)) {
////                            holedDims.remove(existingDim)
////                            val splitX = getVx(existingDim.dimensions.xs, input.dimensions.xs)
////                            val splitY = getVx(existingDim.dimensions.ys, input.dimensions.ys)
////                            val splitZ = getVx(existingDim.dimensions.zs, input.dimensions.zs)
////
////                            val splitCubes: List<Input> = splitX.flatMap { vX ->
////                                splitY.flatMap { vY ->
////                                    splitZ.map { vZ ->
////                                        Input(
////                                            getMode(listOf(existingDim, input), vX.first, vY.first, vZ.first),
////                                            Dims(vX, vY, vZ)
////                                        )
////                                    }
////                                }
////                            }
////                            holedDims.addAll(splitCubes)
////                        }
////                    }
//////            val oldRanges: MutableList<IntRange> = holedDims.map { it.xs }.toMutableList()
//////            newRange.forEach({ x ->
//////                val index = oldRanges.indexOfFirst { it.contains(x) }
//////                if (index == -1) {
//////                    oldRanges.add(x)
//////                } else {
//////                    oldRanges[index] = oldRanges[index].intersect(x)
//////                }
//////            })
////
////            }
////        }
//
//        return splitCubes.fold(0L) { acc, input ->
//            acc + input.contents
//        }
//    }

    fun getVx(
        existingRange: IntRange, newRange: IntRange
    ): List<IntRange> {
        val list: MutableList<IntRange> = mutableListOf()
        val points = mutableSetOf<Int>(existingRange.first, existingRange.last, newRange.first, newRange.last).sorted()
            .toMutableList()
        var start = points.removeFirst() - 1
        while (!points.isEmpty()) {
            val next = points.removeFirst()
            val rangeStart = start + 1
            val rangeEnd =
                if (existingRange.contains(next) && newRange.contains(next) && (!(existingRange.contains(rangeStart) && newRange.contains(
                        rangeStart
                    )))
                ) next - 1 else next
            list.add(rangeStart..rangeEnd)
            start = rangeEnd
        }
//        val list: MutableList<IntRange> =
//            points
//                .windowed(2)
//                .map { (x, y) ->
//                    x..y - 1
//                }
//                .toMutableList()
//
//        val lqst = list[list.lastIndex]
//        val lastEnd: Int = lqst.last + 1
//        val lastPoint: Int = points.last()
//        val element: IntRange = lastEnd..lastPoint
//        list.add(element)
        return list

//        val splitX = mutableListOf<Pair<Int, Int>>()
//        var start = min(existingRange.first, newRange.first)
//        if (existingRange.contains(newRange.first) && existingRange.first != newRange.first) {
//            splitX.add(start to newRange.first - 1)
//            start = newRange.first
//        }
//        if (existingRange.contains(newRange.last) && existingRange.last != newRange.last) {
//            splitX.add(start to newRange.last - 1)
//            start = newRange.last
//        }
//        val xMax = max(existingRange.last, newRange.last)
//        splitX.add(start to xMax)
//        return splitX
    }
//        val count = holedDims.xs.sumOf {
//            it.sumOf { x ->
//                holedDims.ys.sumOf {
//                    it.sumOf { y ->
//                        holedDims.zs.sumOf {
//                            it.count { z ->
//                                getMode(x, y, z)
//                            }
//                        }
//                    }
//
//                }
//            }
//        }
//
//        return count.toLong()
//}


//private fun append(
//    holedRange: MutableList<IntRange>,
//    newRange: IntRange
//) {
//    var newRange1 = newRange
//    var overlaped: Int = holedRange.indexOfFirst { it.overlap(newRange1) }
//    while (overlaped > -1) {
//        val oldRange = holedRange[overlaped]
//        holedRange.removeAt(overlaped)
//        newRange1 = (min(oldRange.first, newRange1.first)..max(oldRange.last, newRange1.last))
//        overlaped = holedRange.indexOfFirst { it.overlap(newRange1) }
//    }
//    holedRange.add(newRange1)
//}

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
        val inputs = input.filter { line -> true }.map { line ->
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
