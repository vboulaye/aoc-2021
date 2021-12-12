package day12

import utils.isUpper
import utils.max
import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long

const val START = "start"
const val END = "end"
const val TWO_VISIT_TOKEN = "__TWO_VISIT_TOKEN__"

data class Graph(
    val links: Map<String, List<String>>,
    val shouldSKipSmallCaveEvaluator: (currentPath: List<String>) -> Boolean = { true },
) {

    val paths = ArrayDeque<List<String>>()

    fun computePaths(
        currentPath: List<String>,
    ) {
        val currentStep = currentPath[currentPath.size - 1]

        val nextSteps = links[currentStep]
        if (nextSteps != null) {
            nextSteps.forEach nextStep@{ nextStep ->
                if (nextStep == END) {
                    paths.addLast(currentPath)
                    // System.err.println(currentPath)
                    return@nextStep
                }
                if (!nextStep.isUpper()
                    && currentPath.contains(nextStep)
                    && shouldSKipSmallCaveEvaluator(currentPath)
                ) {
                    return@nextStep
                }
                val nextPath =
                    if (!nextStep.isUpper() &&  currentPath.contains(nextStep)) {
                        currentPath + listOf(TWO_VISIT_TOKEN, nextStep)
                    } else {
                        currentPath + listOf(nextStep)
                    }
                computePaths(nextPath)
            }
        } else {
            if (!currentStep.isUpper()) {
                val previousStep = currentPath[currentPath.size - 2]
                if (previousStep.isUpper()) {
                    computePaths(currentPath + listOf(previousStep))
                }
            }
        }

    }


}

class Puzzle {

    fun clean(input: List<String>): Map<String, List<String>> {
        val list: List<Pair<String, String>> = input.map { it.split("-") }.map { it[0] to it[1] }
        return (list + list.filter { it.first != START }.map { Pair(it.second, it.first) }).groupBy({ it.first },
            { it.second })
    }

    val part1ExpectedResult = 10L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val graph = Graph(input)
        graph.computePaths(listOf(START))
        return graph.paths.size.toLong()
    }


    val part2ExpectedResult = 36L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)

        val graph = Graph(input) { currentPath ->
//            val hasVisitedSmallCaveTwice =
//                currentPath.filter { !it.isUpper() }.groupingBy { it }.eachCount().values.max() == 2
            val hasVisitedSmallCaveTwice = currentPath.contains(TWO_VISIT_TOKEN)
            hasVisitedSmallCaveTwice
        }
        graph.computePaths(listOf(START))
        return graph.paths.size.toLong()
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
