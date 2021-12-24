package day24

import utils.checkEquals
import utils.max
import utils.readInput
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long

val START8REGS: Map<String, Int> = makeMap(0)

private fun makeMap(z: Int) = mutableMapOf(
    "x" to 0,
    "y" to 0,
    "w" to 0,
    "z" to z,
).toMap()

data class Registry(val input: Long, val vars: Map<String, Int>) {

    fun get(key: String): Int {
        val i = vars[key]
        if (i == null) {
            return try {
                key.toInt()
            } catch (e: NumberFormatException) {
                0
            }
        } else {
            return i
        }
    }

}


//data class ReverseRegistry(val inputList = listOf<Int>(), val vars: Map<String, Int>) {
//
//    fun get(key: String): Int {
//        val i = vars[key]
//        if (i == null) {
//            return try {
//                key.toInt()
//            } catch (e: NumberFormatException) {
//                0
//            }
//        } else {
//            return i
//        }
//    }
//
//}

sealed class Instruction() {
    abstract fun execute(registry: Registry): Registry
//    open fun reverse(registry: ReverseRegistry): List<ReverseRegistry> {
//        return listOf()
//    }
}

data class Input(val variable: String) : Instruction() {
    override fun execute(reg: Registry): Registry {
        return reg.copy(
            vars = reg.vars + mapOf(variable to reg.input.toString().substring(0, 1).toInt()),
            input = 0//reg.input.toString().substring(1).toLong()
        )
    }
}

fun testInt(a: String): Int? {
    return try {
        a.toInt()
    } catch (e: NumberFormatException) {
        null
    }
}

data class Add(val a: String, val b: String) : Instruction() {
    override fun execute(reg: Registry): Registry {
        return reg.copy(
            vars = reg.vars.toMutableMap().apply { this[a] = reg.get(b) + reg.get(a) },
        )
    }
//    override fun execute(reg: Registry) {
//        reg.vars[a] = reg.get(a) + reg.get(b)
//    }

//    override fun reverse(registry: ReverseRegistry): List<ReverseRegistry> {
//        val bVal = testInt(b)
//        if (bVal == null) {
//            val bVals = registry.get(b)
//            registry.vars[a] = registry.get(a).map { it - bVal!! }
//        } else {
//            val reverseRegistry = registry.copy(
//                vars = registry.vars.toMutableMap().apply {
//                    this[a] = get(a) - bVal
//                }
//            )
//            return listOf(reverseRegistry)
//        }
//    }
}

data class Mul(val a: String, val b: String) : Instruction() {
    override fun execute(reg: Registry): Registry {
        return reg.copy(
            vars = reg.vars.toMutableMap().apply { this[a] = reg.get(a) * reg.get(b) },
        )
    }
}

data class Div(val a: String, val b: String) : Instruction() {
    override fun execute(reg: Registry): Registry {
        return reg.copy(
            vars = reg.vars.toMutableMap().apply { this[a] = reg.get(a) / reg.get(b) },
        )
    }
}

data class Mod(val a: String, val b: String) : Instruction() {
    override fun execute(reg: Registry): Registry {
        return reg.copy(
            vars = reg.vars.toMutableMap().apply { this[a] = reg.get(a) % reg.get(b) },
        )
    }


}

data class Eql(val a: String, val b: String) : Instruction() {
    override fun execute(reg: Registry): Registry {
        return reg.copy(
            vars = reg.vars.toMutableMap().apply { this[a] = if (reg.get(a) == reg.get(b)) 1 else 0 },
        )
    }

}


class
Prog(val instructions: List<Instruction>) {

    val cache = mutableMapOf<Pair<Int, Int>, Registry>()

    fun execute2(w: Int, z: Int): Int {
        check(instructions[0] is Input)
        check(instructions[instructions.size - 1] is Add)
        val divZ = (instructions[4] as Div).b.toInt()
        val addX = (instructions[5] as Add).b.toInt()
        val addY = (instructions[15] as Add).b.toInt()

        val newZ = calc(w, z, divZ, addX, addY)

//        val registry = Registry(
//            w.toLong(), mapOf(
//                "x" to 0,
//                "y" to 0,
//                "z" to z
//            )
//        )
//        val newZ2 = execute(registry).get("z")
//        checkEquals(newZ2, newZ)
        return newZ
    }

    fun execute(reg: Registry): Registry {
        check(instructions[0] is Input)
        check(instructions[instructions.size - 1] is Add)
//        val divZ = (instructions[4] as Div).b.toInt()
//        val addX = (instructions[5] as Add).b.toInt()
//        val addY = (instructions[15] as Add).b.toInt()
//
//        val w = reg.input.toString().substring(0, 1).toInt()
//        val z = reg.get("z")
//        val newZ = calc(w, z, divZ, addX, addY)
//
//        return reg.copy(
//            vars = mapOf("w" to w, "z" to newZ),
//            input = 0L//reg.input.toString().substring(1).toLong()
//        )

        return instructions.fold(reg) { acc, instruction ->
            val nextRegistry = instruction.execute(acc)
            nextRegistry
        }


//        val z = reg.get("z")
//        instructions[0].execute(reg)
//        val w = reg.get("w")
//        val newZ = cache.computeIfAbsent(w to z, {
//            instructions.drop(1).fold(reg) { acc, instruction ->
//                instruction.execute(acc)
//            }
//        })
//        return newZ
    }
}

fun calc(
    W: Int, Z: Int,
    zDiv: Int = 26,
    addX: Int = -10,
    addY: Int = 13,
): Int {
//        inp w
//
//                mul x 0
//    if (zDiv == 1) {
//        var w = W
//        var x = Z % 26 + addX
//        x = if (x == w) 0 else 1
//
//        return Z * (25 * x + 1) + (w + addY) * x
//    } else {
        var w = W
        var z = Z
        var x = z % 26 + addX
        x = if (x == w) 0 else 1

        z /= zDiv
        z = z * (25 * x + 1) + (w + addY) * x
        return z
//    }


}

class Puzzle {
    fun clean(input: List<String>): List<Prog> {
        return input
            .filter { line -> true }
            .map { line ->
                val (op, a, b) = Regex("""(\w+) (-?\w+) ?(-?\w+)?""").matchEntire(line)?.destructured!!
                when (op) {
                    "inp" -> Input(a)
                    "add" -> Add(a, b)
                    "mul" -> Mul(a, b)
                    "div" -> Div(a, b)
                    "mod" -> Mod(a, b)
                    "eql" -> Eql(a, b)
                    else -> throw IllegalArgumentException("Unknown op: $op")
                }
            }
            .windowed(18, 18).map { Prog(it) }
    }

    val part1ExpectedResult = 0L

    data class PossibleSolution(val w: Long, val z: Int)

    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)


        val possibleSolutions = listOf<PossibleSolution>(PossibleSolution(0, 0))

        val mapOfPossibleSolution = mapOf<Int, List<Long>>(0 to listOf<Long>())
        var l = System.currentTimeMillis()

        val result = input.reversed()
            .foldIndexed(mapOfPossibleSolution) { index, acc, prog ->
                println(">" + index + ":" + acc.size + "    " + (System.currentTimeMillis() - l))
                l = System.currentTimeMillis()

                val toMutableMap = mutableMapOf<Int, List<Long>>()
                 (1..9).forEach { w ->
                    (0..1000000)
                        .forEach { z ->
                            val newZ = prog.execute2(w, z)
                            val previousSolutions = acc[newZ]
                            if (previousSolutions == null) {
                                null
                            } else {
                                val newSolutions = if (previousSolutions.isEmpty()) {
                                    listOf<Long>(w.toLong())
                                } else {
                                    previousSolutions.map { (w.toString()+it.toString()).toLong()  }
                                }
                                val storedSols = toMutableMap.computeIfAbsent(z) { listOf<Long>() }
                                toMutableMap[z] = storedSols + newSolutions
                            }

                        }
                }

                toMutableMap
            }
        //93979991948956 too high
        //93979991948956
        //93979991948956
        println(result[0]!!.maxOrNull())
        println(result[0]!!.minOrNull())
        return 0;
    }

    fun part1X(rawInput: List<String>): Result {
        val input = clean(rawInput)

        val cache = mutableMapOf<Triple<Int, Int, List<Int>>, Int>()

//        var resut: Long = 9L; //99999999999999L
        var resut: Long = 99999999999999L

        val iteration = (1..9).map {
            val w = it
            val registry = Registry(w.toLong(), START8REGS)
            val result = input[0].execute(registry)
            w.toLong() to result.get("z")
        }

        val previous = AtomicInteger()
        val previousInput = AtomicLong()
        var l = System.currentTimeMillis()
        val result: List<Pair<Long, Int>> = input.foldIndexed(iteration) { index, acc, prog ->
            println(">" + index + ":" + acc.size + "    " + (System.currentTimeMillis() - l))
            l = System.currentTimeMillis()
//

//            val iteration3: List<Pair<List<Int>, Int>> = iteration2.flatMap { listToZ ->
//                val z = listToZ.second
//                val vars = makeMap(z)
//
//                val values = mutableMapOf<Int, Int>()
//                (9 downTo 1).map { w ->
//                    val registry = Registry(listOf(w), vars)
//                    val result = input[1].execute(registry)
//                    val z = result.get("z")
//                    if (values[z] == null) values[z] = w
//                }
//                values.map { pqir ->
//                    (listToZ.first + listOf(pqir.value)) to pqir.key
//                }
//
//            }
            val possiblePaths: List<Pair<Long, Int>> = acc.flatMap { listToZ ->
                val z = listToZ.second
//                val vars = makeMap(z)


                (9 downTo 1).map { w ->
                    val input1 = w.toString()
//                    val registry = Registry(input1, vars)
                    val result = prog.execute2(w, z)
                    (listToZ.first.toString() + input1).toLong() to result
                }


            }.sortedWith { a, b ->
                val zCompare = a.second.compareTo(b.second)
                -if (zCompare == 0) {
                    a.first.compareTo(b.first)
                } else {
                    zCompare
                }
            }
                .filter {
                    val z = it.second
                    if (z == (previous.get())) {
//                        println(previousInput.get())
//                        println(it.first)
                        false
                    } else {
                        previous.set(z)
                        previousInput.set(it.first)
                        true
                    }
                }
            possiblePaths
//            val zToInputs: Map<Int, List<Long>> = possiblePaths
//                .groupBy({ it.second }, { it.first })
//            val map: List<Pair<Long, Int>> = zToInputs
//                .map {
//                    val maxInput = it.value.max()
//                    maxInput to it.key
//                }
//            map
        }

        result
            .filter {
                val score: Int = it.second
                score == 0
            }
            .forEach {
                println(it.first.toString() + ": " + it.second)
            }


//        val iteration2 = iteration.flatMap { listToZ ->
//            val z = listToZ.second
//            val vars = makeMap(z)
//            (1..9).map { w ->
//                val registry = Registry(listOf(w), vars)
//                val result = input[1].execute(registry)
//                (listToZ.first + listOf(w)) to result.get("z")
//            }
//
//        }
//
//        val iteration3: List<Pair<List<Int>, Int>> = iteration2.flatMap { listToZ ->
//            val z = listToZ.second
//            val vars = makeMap(z)
//
//            val values = mutableMapOf<Int, Int>()
//            (9 downTo 1).map { w ->
//                val registry = Registry(listOf(w), vars)
//                val result = input[1].execute(registry)
//                val z = result.get("z")
//                if (values[z] == null) values[z] = w
//            }
//            values.map { pqir ->
//                (listToZ.first + listOf(pqir.value)) to pqir.key
//            }
//
//        }
//
//        print(iteration2)


//        while (resut >= // 1L//
//            11111111111111L
//        ) {
//            val input1 = resut.toString()
//
//            if (!input1.contains('0')) {
//                //  println("input1: $input1")
//                val registry1 = Registry(input1.toList().map { it.toString().toInt() }, START8REGS)
//                val result = input.fold(registry1) { acc, prog ->
//                    prog.execute(acc)
//                }
//                if (resut % 111111 == 0L) println(input1 + "-" + result.vars["z"])
//
//                if (result.vars["z"]!!.toLong() == 0L) {
//                    //   println(resut)
//                    return resut
//                }
//            }
//            resut--
//        }
        return 0L
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
