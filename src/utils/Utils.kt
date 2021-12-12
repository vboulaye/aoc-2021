package utils

import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max
import kotlin.reflect.KClass

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String, baseClass: KClass<*>) =
    File(File("src", baseClass.qualifiedName!!.removeSuffix(".${baseClass.simpleName}")), "$name.txt").readLines()


/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)

fun String.isUpper() = this.uppercase(Locale.getDefault()) == this
fun String.isLower() = this.lowercase(Locale.getDefault()) == this

/**
 * sort characters inside a string
 */
fun String.sortString(): String =  this.toList().sorted().joinToString("")


fun String.asBinary(): Int = this.toInt(2)

fun <T : Comparable<T>> Iterable<T>.min(): T = this.minOrNull()!!
fun <T : Comparable<T>> Iterable<T>.max(): T = this.maxOrNull()!!


fun main() {

    check(Vector(Point(0, 0), Point(0, 1)).gridLength() == 1)
    check(Vector(Point(1, 0), Point(1, 1)).gridLength() == 1)
    check(Vector(Point(0, 0), Point(1, 1)).gridLength() == 1)

    //  assertEquals(Vector(Point(0, 0), Point(0, 1)).euclidLength(),1)
    check(Vector(Point(1, 0), Point(1, 1)).gridLength() == 1)
    check(Vector(Point(0, 0), Point(1, 1)).gridLength() == 1)

    check(Vector(Point(0, 0), Point(0, 1)).isVertical())
    check(Vector(Point(0, 1), Point(0, 0)).isVertical())

    check(Vector(Point(0, 0), Point(1, 0)).isHorizontal())
    check(Vector(Point(1, 0), Point(0, 0)).isHorizontal())

    check(Vector(Point(0, 0), Point(1, 1)).isDiagonal())
    check(Vector(Point(1, 1), Point(0, 0)).isDiagonal())
}
