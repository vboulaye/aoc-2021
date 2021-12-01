package utils

import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
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
