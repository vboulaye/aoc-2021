package day19

import utils.checkEquals
import utils.max
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

val rootVector = Vector(XYZ(0, 0, 0), XYZ(0, 0, 0))

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


val allOrientations3: List<Orientation> = transformations.map {
    Orientation(it)
}
val allOrientations: List<Orientation> = dims.flatMap { x ->
    dims
        .filter { y -> y != x }
        .flatMap { y ->
            val z = (0..2).first { it != x && it != y }
            factor.flatMap { xFactor ->
                factor.flatMap { yFactor ->
                    factor
                        .filter { !(it == -1 && xFactor == -1 && yFactor == -1) }
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


data class XYZ(val x: Int, val y: Int, val z: Int) : Comparable<XYZ> {
    fun asArray(): IntArray {
        return intArrayOf(x, y, z)
    }

    companion object {
        fun fromArray(array: List<Int>): XYZ {
            return XYZ(array[0], array[1], array[2])
        }

    }

    override fun compareTo(other: XYZ): Int {
        val cX = x.compareTo(other.x)
        if (cX != 0) return cX
        val cY = y.compareTo(other.y)
        if (cY != 0) return cY
        return z.compareTo(other.z)
    }

    fun rotate(orientation: Orientation): XYZ {

        val mapIndexed: List<Int> = orientation.matrix
            .map { mapping: List<Int> ->
                mapping.mapIndexed { index, mappingValue ->
                    mappingValue * this.asArray()[index]
                }.sum()
            }

        return XYZ.fromArray(mapIndexed)
    }

    fun translate(vector: Vector): XYZ {
        return XYZ(x + vector.dx, y + vector.dy, z + vector.dz)
    }
}

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

    fun translate(vector: Vector): Vector {
        return Vector(from, to.translate(vector))
    }

}

data class Scanner(val index: Int, val points: List<XYZ>) {
    init {

    }

    fun rotate(orientation: Orientation): Scanner {
        val rotated = points.map { it.rotate(orientation) }
        return Scanner(index, rotated)
    }

    val allVectors: List<Vector> = points.flatMap { from ->
        points
            .filter { from !== it }
            .filter { from < it }
            .map { to -> Vector(from, to) }
            .toSet()
    }

    fun translate(vector: Vector): Scanner {
        val translated = points.map { it.translate(vector) }
        return Scanner(index, translated.sorted())
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
        val resultBeaconsRoot = ResultBeacons(allOrientations[0], rootVector, listOf(), input[0])
        val processezd = mutableSetOf(resultBeaconsRoot)
        println("  ${input[0].points.take(3)}")
        findBeaconsInScanner(input[0], processezd, input);

        val eachCount = processezd.flatMap { it.translated.points }.groupingBy { it }.eachCount()
        val count: Int = eachCount.size
        return count.toLong()
//        val commonPoints = processezd.map { it.points.toSet() }.reduce {
//                acc, points ->
//
//            val intersect = acc.intersect(points)
//            intersect
//
//        }
//        //val findBeacons1 = findBeacons(input[0], input[1])
//        val mapped: MutableMap<Scanner, MutableList<ScannerMapping>> = mutableMapOf()
//        input
//            .forEach { sourceScanner ->
//                input
//                    .filter { targetScanner -> targetScanner !== sourceScanner }
//                    //  .filter { it.index > sourceScanner.index }
//                    .forEach { targetScanner ->
//                        // if (!mapped.containsKey(sourceScanner)) {
//                        //  println("${sourceScanner.index} -> ${targetScanner.index}")
//                        val findBeacons = findBeacons(sourceScanner, targetScanner)
//                        if (findBeacons != null) {
//                            println("${sourceScanner.index} -> ${targetScanner.index} + ${findBeacons.beacons.size}")
//                            val scannerMapping = ScannerMapping(sourceScanner, targetScanner, findBeacons)
//                            if (mapped[sourceScanner] != null) {
//                                mapped[sourceScanner]!!.add(scannerMapping)
//                            } else {
//                                mapped[sourceScanner] = mutableListOf(scannerMapping)
//                            }
//                        }
//                        // }
//                    }
//            }
//
//        val scannerToTranslations: MutableMap<Scanner, List<ScannerMapping>> = mutableMapOf()
//        val translations: List<ScannerMapping> = listOf()
//        val rootVector = Vector(XYZ(0, 0, 0), XYZ(0, 0, 0))
//        val rootMapping = ScannerMapping(input[0], input[0], ResultBeacons(allOrientations[0], rootVector, listOf()))
//        populateMappings(input[0], listOf(), mapped, scannerToTranslations)
//        val processed = mutableSetOf(input[0])
        //val commonPoints = mutableListOf<XYZ>()
//
//        val rootScannerMapping = ScannerMapping(input[0], input[0], ResultBeacons(allOrientations[0], rootVector, listOf()))
//
//        agrgegateBeacons(rootScannerMapping, processed, commonPoints, scannerToTranslations)
//        val commonPoints = input.fold(input[0].points.toSet()) { acc: Set<XYZ>, scanner ->
//            val translation: List<ScannerMapping> = scannerToTranslations[scanner]!!
//            translation
//                .filter { !processed.contains(it.target) }
//                .forEach { scannerMapping ->
//                    processed[scannerMapping.target] = true
//
//                }
////            val translatedPoints: List<XYZ> = scanner.points.map { point ->
////
////                val pointMoved = translation.reversed().fold(point) { acc, trans ->
////                    val rotated = point.rotate(trans.resultBeacons.orientation)
////                    rotated.translate(trans.resultBeacons.translation)
////                }
////                pointMoved
////            }
//
//            val translatedScanner = translation.reversed().subList(1,translation.size)
//                .fold(scanner) { scannerInTranslation, trans ->
//                scannerInTranslation.rotate(trans.resultBeacons.orientation).translate(trans.resultBeacons.translation)
//            }
//            val intersect: Set<XYZ> = acc.intersect(translatedScanner.points)
//            intersect
//        }
//        return commonPoints.size.toLong();
    }

    private fun findBeaconsInScanner(toProcess: Scanner, processezd: MutableSet<ResultBeacons>, input: List<Scanner>) {
        input.forEach { scanner ->
            if (!processezd.map { it.translated.index }.contains(scanner.index)) {

                val findBeacons =
                //        val vectorsByDistance = scanner1.allVectors.groupBy { vector: Vector -> vector.length }

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

                    computeResultBeacons(toProcess, scanner)//        if (result.size == 0) return null
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
                if (findBeacons != null) {
                    println(
                        "${toProcess.index} -> ${findBeacons.translated.index} + ${
                            findBeacons.translated.points.take(
                                3
                            )
                        }"
                    )
                    processezd.add(findBeacons)
                    findBeaconsInScanner(findBeacons.translated, processezd, input)
                }

            }
        }
    }

    private fun agrgegateBeacons(
        scannerMapping: ScannerMapping,
        processed: MutableSet<Scanner>,
        beacons: MutableList<XYZ>,
        scannerToTranslations: MutableMap<Scanner, List<ScannerMapping>>
    ) {
        val translation: List<ScannerMapping> = scannerToTranslations[scannerMapping.target]!!
        translation
            .filter { !processed.contains(it.target) }
            .forEach { subScannerMapping ->
                processed.add(subScannerMapping.target)
                val translatedPoints = subScannerMapping.resultBeacons.beacons.map { beacon ->
                    val translatedPoint = beacon.rotate(scannerMapping.resultBeacons.orientation)
                        .translate(scannerMapping.resultBeacons.translation)
                    translatedPoint
                }
                beacons.addAll(translatedPoints)

                agrgegateBeacons(subScannerMapping, processed, beacons, scannerToTranslations)
            }
    }

    private fun populateMappings(
        scanner: Scanner,
        translation: List<Puzzle.ScannerMapping>,
        mapped: MutableMap<Scanner, MutableList<Puzzle.ScannerMapping>>,
        scannerToTranslations: MutableMap<Scanner, List<ScannerMapping>>
    ) {
        mapped[scanner]!!.forEach { scannerMapping ->
            if (scannerToTranslations[scannerMapping.target] == null) {
                val nextTranslation = translation + listOf(scannerMapping)
                scannerToTranslations[scannerMapping.target] = nextTranslation
                populateMappings(scannerMapping.target, nextTranslation, mapped, scannerToTranslations)
            }
        }
    }

    data class ResultBeacons(
        val orientation: Orientation,
        val translation: Vector,
        val beacons: List<XYZ>,
        val translated: Scanner
    )

    private fun computeResultBeacons(source: Scanner, target: Scanner): ResultBeacons? {
        val commonLength = source.allVectors.map { it.length }.intersect(target.allVectors.map { it.length })
        if (commonLength.size < 12) return null
        //  println("${commonLength.size}")
        allOrientations.forEach { orientation ->
            val rotated = target.rotate(orientation)

            val possibleVectors = source.allVectors.filter { commonLength.contains(it.length) }
            val possibleSources = (possibleVectors.map { it.from } + possibleVectors.map { it.to }).toSet()
            val possibleTargetVectors = rotated.allVectors.filter { commonLength.contains(it.length) }
            val possibleTargets = (possibleTargetVectors.map { it.from } + possibleTargetVectors.map { it.to }).toSet()
            possibleSources.forEach { startPoint ->
                possibleTargets.forEach { otherPoint ->
                    val translation = Vector(otherPoint, startPoint)
                    val translated = rotated.translate(translation)
                    val matchingPoints = source.points.filter { translated.points.contains(it) }
                    if (matchingPoints.size < 12) {
                        null
                    } else {
                        return ResultBeacons(orientation, translation, matchingPoints, translated)
                    }
                }
            }
        }
        return null;
    }


    val part2ExpectedResult = 3621L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val resultBeaconsRoot = ResultBeacons(allOrientations[0], rootVector, listOf(), input[0])
        val processezd = mutableSetOf(resultBeaconsRoot)
        println("  ${input[0].points.take(3)}")
        findBeaconsInScanner(input[0], processezd, input);

        val maxDistance = processezd.flatMap { from ->
            processezd.map { to ->
                abs(from.translation.dx - to.translation.dx) +
                        abs(from.translation.dy - to.translation.dy) +
                        abs(from.translation.dz - to.translation.dz)

            }
        }.max()
        return maxDistance.toLong()
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
