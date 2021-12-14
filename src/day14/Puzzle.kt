package day14

import utils.max
import utils.min
import utils.readInput
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


data class Input(val polymerTemplate: String, val pairInsertions: Map<String, String>) {

}

data class Input5(val polymerTemplate: LinkedList<Char>, val pairInsertions: Map<List<Char>, Char>) {

}


data class Input2(val polymerTemplate: List<List<Char>>, val pairInsertions: Map<List<Char>, List<Char>>) {

}

data class Input3(val polymerTemplate: List<Char>, val pairInsertions: MutableMap<List<Char>, List<Char>>) {

}


data class Insertion(val elements: List<Insertion>)

data class Input4(val polymerTemplate: List<Char>, val pairInsertions: MutableMap<Insertion, List<Insertion>>) {

}

class Puzzle {

    fun clean(input: List<String>): Input {
        val polymerTemplate = input[0]
        val pairInsertions = input.subList(2, input.size).map { it.split(" -> ") }.map { it[0] to it[1] }.toMap()
        return Input(polymerTemplate, pairInsertions)
    }

    val part1ExpectedResult = 1588L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        var next = Input(input.polymerTemplate, input.pairInsertions)
        (0..9).forEach {
            println(it)
            next = insert(next)
        }
        val groupingBy: Map<Char, Int> = next.polymerTemplate.groupingBy { it }.eachCount()
        val max = groupingBy.values.max()
        val min = groupingBy.values.minOrNull()!!
        return (max - min).toLong()
    }

    private fun insert(input: Input): Input {
        val newTemplate = input.polymerTemplate.windowed(2).map {
            val insert = input.pairInsertions[it]!!
            insert + it[1]
        }.joinToString("") { it }
        val polymerTemplate = input.polymerTemplate[0] + newTemplate
        //val sourcePairs = input.pairInsertions.map { entry -> entry.key to entry.value }
        //sourcePairs + (input.polymerTemplate.)
        return Input(polymerTemplate, input.pairInsertions)
    }

    val part2ExpectedResult = 2188189693529L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)

        cache.clear()
        val counters = HashMap<String, Long>()
        input.pairInsertions.values.forEach { counters[it] = 0 }
        input.polymerTemplate.forEach {
            val existingValue = counters[it.toString()]
            if (existingValue == null) {
                counters[it.toString()] = 1
            } else {
                counters[it.toString()] = existingValue + 1
            }
        }
        input.polymerTemplate.windowed(2)
            .forEach {
                println(it)
//                counters[it[1].toString()] = counters[it[1].toString()]!! + 1
                count(it, input.pairInsertions, 40)
                    .forEach {
                        val existingValue = counters[it.key]
                        if (existingValue == null) {
                            counters[it.key] = it.value
                        } else {
                            counters[it.key] = existingValue + it.value
                        }
                    }
                println(counters)
            }

        val max = counters.values.max()
        val min = counters.values.min()
        // 13099972219652 too high
        // 13099972219652
        return (max - min).toLong()

    }

    val cache: HashMap<Pair<String, Int>, HashMap<String, Long>> = HashMap()

    private fun count(pair: String, pairInsertions: Map<String, String>, level: Int): HashMap<String, Long> {
        if (level == 0) return HashMap();
        val hashMap = cache[Pair(pair, level)]
        if (hashMap != null) return hashMap

        val counters = HashMap<String, Long>()
        //println(level)
        val insertion = pairInsertions[pair]!!
        counters[insertion] = 1

        count(pair[0] + insertion, pairInsertions, level - 1)
            .forEach {
                val existingValue = counters[it.key]
                if (existingValue == null) {
                    counters[it.key] = it.value
                } else {
                    counters[it.key] = existingValue + it.value
                }
            }
        count(insertion + pair[1], pairInsertions, level - 1)
            .forEach {
                val existingValue = counters[it.key]
                if (existingValue == null) {
                    counters[it.key] = it.value
                } else {
                    counters[it.key] = existingValue + it.value
                }
            }
        cache[Pair(pair, level)] = counters
        return counters
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
