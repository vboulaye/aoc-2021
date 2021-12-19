package day19

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
import utils.checkEquals
import utils.readInput
import java.lang.Math.abs
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long

data class Orientation(val matrix: List<List<Int>>) {


}

val orientation = listOf(
    Orientation(
        listOf(
            listOf(1, 0, 0),
            listOf(0, 1, 0),
            listOf(0, 0, 1),
        )
    ),
    Orientation(
        listOf(
            listOf(0, 1, 0),
            listOf(1, 0, 0),
            listOf(0, 0, 1),
        )
    ),
    Orientation(
        listOf(
            listOf(0, 0, 1),
            listOf(1, 0, 0),
            listOf(0, 1, 0),
        )
    ),
    Orientation(
        listOf(
            listOf(0, 0, 1),
            listOf(0, 1, 0),
            listOf(1, 0, 0),
        )
    ),
    Orientation(
        listOf(
            listOf(1, 0, 0),
            listOf(0, 0, 1),
            listOf(0, 1, 0),
        )
    ),
    Orientation(
        listOf(
            listOf(0, 1, 0),
            listOf(0, 0, 1),
            listOf(1, 0, 0),
        )
    ),
)

val factor = listOf(1, -1)
val dims = listOf(0, 1, 2)


fun get(test: Int, pos: Int, value: Int): Int {
    return if (test == pos) value else 0
}


fun multiply(a: List<List<Int>>, b: List<List<Int>>): List<List<Int>> {
    return (0..2).map { i ->
        (0..2).map { j ->
            (0..2).sumOf { a[i][it] * b[it][j] }
        }
    }

}

fun c(deg: Double): Int = cos(deg).toInt()
fun s(deg: Double): Int = sin(deg).toInt()

fun toRadian(deg: Int): Double = PI * deg / 360
val angles = listOf(0, 90, 180, 270).map { toRadian(it) }
val allOrientations2: List<Orientation> = angles.flatMap { x ->
    val matX: List<List<Int>> = listOf(
        listOf(1, 0, 0),
        listOf(0, cos(x).toInt(), -sin(x).toInt()),
        listOf(0, sin(x).toInt(), cos(x).toInt()),
    )

    angles.flatMap { y ->
        val matY = listOf(
            listOf(cos(y).toInt(), 0, sin(y).toInt()),
            listOf(0, 1, 0),
            listOf(-sin(y).toInt(), 0, cos(y).toInt()),
        )
        angles.map { z ->

            val matZ = listOf(
                listOf(cos(z).toInt(), -sin(z).toInt(), 0),
                listOf(sin(z).toInt(), cos(z).toInt(), 0),
                listOf(0, 0, 1),
            )
            val a = multiply(matX, matY)
            val matrix = multiply(a, matZ)
//            matrix.forEach { checkEquals(it[0] + it[1] + it[2], 1) }
            Orientation(
                listOf(
                    listOf(c(x) * c(y), c(x) * s(y) * s(z) - s(x) * c(z), c(x) * s(y) * c(z) + s(x) * s(z)),
                    listOf(s(x) * c(y), s(x) * s(y) * s(z) + c(x) * c(z), s(x) * s(y) * c(z) - c(x) * s(z)),
                    listOf(-s(y), c(y) * s(z), c(y) * c(z)),
                )
                //
                //                matrix
//                listOf(
//                    listOf(get(0, x, xFactor), get(1, x, xFactor), get(2, x, xFactor)),
//                    listOf(get(0, y, yFactor), get(1, y, yFactor), get(2, y, yFactor)),
//                    listOf(get(0, z, zFactor), get(1, z, zFactor), get(2, z, zFactor)),
//                )
            )
        }
    }
        .filter { isValid(it) }

}

private fun isValid(orientation1: Orientation): Boolean {
    return orientation1.matrix.all { intList: List<Int> ->
        intList.map { abs(it) }.sumOf { it } == 1
    }
}


val allOrientations: List<Orientation> = dims.flatMap { x ->
    dims
        .filter { y -> y != x }
        .flatMap { y ->
//            dims
//                .filter { it != x && it != y }
//                .flatMap { z ->
            val z = (0..2).first { it != x && it != y }
            factor.flatMap { xFactor ->
                factor.flatMap { yFactor ->
                    factor//listOf(1)
                        //.filter { !(it == -1 && xFactor == -1 && yFactor == -1) }
                        .map { zFactor ->
                            Orientation(
                                listOf(
                                    listOf(get(0, x, xFactor), get(1, x, xFactor), get(2, x, xFactor)),
                                    listOf(get(0, y, yFactor), get(1, y, yFactor), get(2, y, yFactor)),
                                    listOf(get(0, z, zFactor), get(1, z, zFactor), get(2, z, zFactor)),
                                )
                            )
                        }
                }
            }
//                }
        }
}
//    .distinct()


fun rotate(point: XYZ, orientation: Orientation): XYZ {
    val mapIndexed: List<Int> = point.asArray().mapIndexed { index, value ->
        orientation.matrix[index].sumOf { it * value }
    }
    return XYZ.fromArray(mapIndexed)
}

data class XYZ(val x: Int, val y: Int, val z: Int) : Comparable<XYZ> {
    fun asArray(): IntArray {
        return intArrayOf(x, y, z)
    }

    companion object {
        fun fromArray(array: List<Int>): XYZ {
            return XYZ(array[0], array[1], array[2])
        }

    }

    fun asNdArray(): NDArray<Int, D1> {
        return mk.ndarray(asArray())
    }

    override fun compareTo(other: XYZ): Int {
        val cX = x.compareTo(other.x)
        if (cX != 0) return cX
        val cY = y.compareTo(other.y)
        if (cY != 0) return cY
        return z.compareTo(other.z)
    }

    fun rotate(orientation: Orientation): XYZ {
        val mapIndexed: List<Int> = this.asArray()
            .mapIndexed { coord, value ->
                orientation.matrix[coord].sumOf { it * value }
            }

        checkEquals(x * x + y * y + z * z, mapIndexed.map { it * it }.sum())

        return XYZ.fromArray(mapIndexed)
    }

    fun translate(vector: Vector): XYZ {
        return XYZ(x + vector.dx, y + vector.dy, z + vector.dz)
    }
}

//data class Vector(val dx: Int, val dy: Int, val dz: Int) {
//    fun matches(v: Vector) = dx * dx + dy * dy + dz * dz == v.dx * v.dx + v.dy * v.dy + v.dz * v.dz
//}


data class Vector(val from: XYZ, val to: XYZ) : Comparable<Vector> {
    val dx = to.x - from.x
    val dy = to.y - from.y
    val dz = to.z - from.z
    val length = dx * dx + dy * dy + dz * dz
    fun matches(v: Vector): Boolean {
        return length == v.length
    }

    override fun compareTo(other: Vector): Int {
        val cX = dx.compareTo(other.dx)
        if (cX != 0) return cX
        val cY = dy.compareTo(other.dy)
        if (cY != 0) return cY
        return dz.compareTo(other.dz)
    }


}

data class Scanner(val index: Int, val points: List<XYZ>) {
    init {

    }

    val allVectors: List<Vector> = points.flatMap { from ->
        points
            .filter { from !== it }
            .filter { from < it }
            .map { to -> Vector(from, to) }
            .toSet()
    }

    fun rotate(orientation: Orientation): Scanner {
        val rotated = points.map { it.rotate(orientation) }
        return Scanner(index, rotated)
    }

    fun translate(vector: Vector): Scanner {
        val translated = points.map { it.translate(vector) }
        return Scanner(index, translated)
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
                            result.add(Scanner(index, points.sorted()))
                            index++
                            points = mutableListOf<XYZ>()
                        }
                    }

                } else {
                    points.add(line.split(",").let { XYZ(it[0].toInt(), it[1].toInt(), it[2].toInt()) })
                }
            }

        result.add(Scanner(index, points.sorted()))

        return result
    }


    val part1ExpectedResult = 79L

    data class ScannerMapping(val source: Scanner, val target: Scanner, val resultBeacons: ResultBeacons)

    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        //val findBeacons1 = findBeacons(input[0], input[1])
        val mapped: MutableMap<Scanner, ScannerMapping> = mutableMapOf()
        input
            .forEach { sourceScanner ->
                input
                    .filter { targetScanner -> targetScanner !== sourceScanner }
                    //  .filter { it.index > sourceScanner.index }
                    .forEach { targetScanner ->
                        if (!mapped.containsKey(sourceScanner)) {
                            println("${sourceScanner.index} -> ${targetScanner.index}")
                            val findBeacons = findBeacons(sourceScanner, targetScanner)
                            if (findBeacons != null) {
                                println("${sourceScanner.index} -> ${targetScanner.index} + ${findBeacons.beacons.size}")
                                val scannerMapping = ScannerMapping(sourceScanner, targetScanner, findBeacons)
                                mapped[sourceScanner] = scannerMapping
                                mapped[targetScanner] = scannerMapping
                            }
                        }
                    }
            }
//        val results = input.subList(1, input.size)
//            .map { findBeacons(input[0], it) }

//        mapped[input|0]
//        input.map {
//            if(it.index==0) it
//            else {
//                val scannerMapping = mapped[it]
//                if(scannerMapping.source==
//            }
//        }
        return 0;// results.flatMap { it.beacons }.distinct().size.toLong()
    }

    data class ResultBeacons(val orientation: Orientation, val translation: Vector, val beacons: List<XYZ>)

    private fun findBeacons(source: Scanner, target: Scanner): ResultBeacons? {
//        val vectorsByDistance = scanner1.allVectors.groupBy { vector: Vector -> vector.length }

        val result = computeResultBeacons(source, target)

//        val result = allOrientations.flatMap { orientation ->
//            val rotated = target.rotate(orientation)
//            source.points.flatMap { startPoint ->
//                rotated.points.mapNotNull { otherPoint ->
//                    val translation = Vector(otherPoint, startPoint)
//                    val translated = rotated.translate(translation)
//                    val matchingPoints = source.points.filter { translated.points.contains(it) }
//                    if (matchingPoints.size < 12) {
//                        null
//                    } else {
//                        ResultBeacons(orientation, translation, matchingPoints)
//                    }
//                }
//            }
//        }
//            .first()
        return result
//        if (result.size == 0) return null
//        return result[0]!!
//        val vectorsByDistance = scanner1.allVectors.groupBy { vector: Vector -> vector.length }
//        val matchingVectors: List<Pair<Vector, Vector>> = scanner0.allVectors
//            .mapNotNull { vector0 ->
//                vectorsByDistance[vector0.length]
//                    ?.map { vector1 ->
//                        vector0 to vector1
//                    }
//            }
//            .flatMap { it }
//        println(matchingVectors)
//        println(allOrientations)

    }

    private fun computeResultBeacons(source: Scanner, target: Scanner): ResultBeacons? {
        val commonLength = source.allVectors.map { it.length }.intersect(target.allVectors.map { it.length })
        println("${commonLength.size}")
        allOrientations.forEach { orientation ->
            val rotated = target.rotate(orientation)
            source.points.forEach { startPoint ->
                rotated.points.forEach { otherPoint ->
                    val translation = Vector(otherPoint, startPoint)
                    val translated = rotated.translate(translation)
                    val matchingPoints = source.points.filter { translated.points.contains(it) }
                    if (matchingPoints.size < 12) {
                        null
                    } else {
                        return ResultBeacons(orientation, translation, matchingPoints)
                    }
                }
            }
        }
        return null;
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
