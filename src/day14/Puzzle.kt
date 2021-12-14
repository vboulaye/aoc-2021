package day14

import utils.max
import utils.readInput
import java.util.*
import kotlin.collections.AbstractList
import kotlin.collections.ArrayList
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


data class Input(val polymerTemplate: String, val pairInsertions: Map<String, String>) {

}

data class Input2(val polymerTemplate: List<List<Char>>, val pairInsertions: Map<List<Char>, List<Char>>) {

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

        var next = input
        (0..9).forEach {
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
        val sourcePairs = input.pairInsertions.map { entry -> entry.key to entry.value }
        //sourcePairs + (input.polymerTemplate.)
        return Input(polymerTemplate, input.pairInsertions)
    }

    private fun insert2(input: Input2): Input2 {
        val newList = LinkedList<Char>()
        var map: MutableMap<List<Char>, List<Char>> = input.pairInsertions.toMutableMap()
        val flatMap =  input.polymerTemplate.flatMap { it.subList(0, 1) } + input.polymerTemplate[input.polymerTemplate.size-1][input.polymerTemplate[input.polymerTemplate.size-1].size-1]
        (input.polymerTemplate.size/2.. input.polymerTemplate.size).forEach{windowSize ->
            flatMap.windowed(windowSize).forEach {
                map[it] = it.subList(1, it.size-1)
            }
        }
        return input
       // val remplate = input.polymerTemplate.toList().map { pairInsertions[listOf(it)]!! }
//        newList.add(input.polymerTemplate[0])
//        val newTemplate = input.polymerTemplate.windowed(2)
//            .forEach {
//                val insert = input.pairInsertions[it]!!
//                newList.add(insert)
//                newList.add(it[1])
//            }
//       // val sourcePairs = input.pairInsertions.map { entry -> entry.key to entry.value }
//        //sourcePairs + (input.polymerTemplate.)
//        return Input2(newList, input.pairInsertions)
    }

    val part2ExpectedResult = 2188189693529L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val pairInsertions: Map<List<Char>, List<Char>> =
            input.pairInsertions.map { it.key.toList() to listOf(it.key[0], it.value[0], it.key[1]) }.toMap()
        val remplate: List<List<Char>> = input.polymerTemplate.toList().windowed(2).map {
            val chars = pairInsertions[it]
            chars!!
        }
        var next = Input2(
            remplate,
            pairInsertions
        )
        (0..39).forEach {
            println(it)
            next = insert2(next)
        }
        return 0
//        val groupingBy: Map<Char, Int> = next.polymerTemplate.groupingBy { it }.eachCount()
//        val max = groupingBy.values.max()
//        val min = groupingBy.values.minOrNull()!!
//        return (max - min).toLong()
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
