package day12

import utils.readInput
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.collections.ArrayDeque
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


class Puzzle {

    fun clean(input: List<String>): Map<String, List<String>> {
        val list: List<List<String>> = input.map { it.split("-") }
        val m: MutableMap<String, ArrayDeque<String>> = HashMap()
        list.forEach {
            if (m[it[0]] == null) {
                m[it[0]] = ArrayDeque()
            }
            if (m[it[1]] == null) {
                m[it[1]] = ArrayDeque()
            }
            m[it[0]]!!.add(it[1])
            if (it[0] != "start") {
                m[it[1]]!!.add(it[0])
            }
        }
        return m
//        return list
//            .groupBy({ it[0] }, { it[1] })
    }

    val part1ExpectedResult = 10L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val visited = HashSet<String>().toMutableSet()
        val paths = ArrayDeque<ArrayDeque<String>>()
        //paths.addLast(ArrayDeque(listOf("start")))
        computePaths(ArrayDeque(listOf("start")), paths, input, visited)// default value = visitedted)
        return paths.size.toLong()
    }

    fun String.isUpper() = this.uppercase(Locale.getDefault()).equals(this)

    private fun computePaths(
        currentPath: ArrayDeque<String>,
        paths: ArrayDeque<ArrayDeque<String>>,
        input: Map<String, List<String>>,
        visited: MutableSet<String>
    ) {
        val currentStep = currentPath.get(currentPath.size - 1)
        if (currentStep == "end") {
            paths.addLast(currentPath)
            // System.err.println(currentPath)
            return
        }
        val nextSteps = input[currentStep]

        if (nextSteps != null) {
            nextSteps.forEach nextStep@{ nextStep ->
                if (!nextStep.isUpper() && "end" != nextStep) {
                    if (currentPath.contains(nextStep)) {
                        return@nextStep
                    }
                }
                val currentPath1 = ArrayDeque(currentPath)
                currentPath1.addLast(nextStep)
                computePaths(currentPath1, paths, input, visited)
            }
        } else {
            if (!currentStep.isUpper()) {
                val previous = currentPath.get(currentPath.size - 2)
                if (previous.isUpper()) {
                    val currentPath1 = ArrayDeque(currentPath)
                    currentPath1.addLast(previous)
                    computePaths(currentPath1, paths, input, visited)
                }
            }
        }

//        nextSteps.forEach nextStep@{ nextStep ->
//            if (nextStep == "end") {
//                currentPath.addLast(nextStep)
//                paths.addLast(currentPath)
//                System.err.println(currentPath)
//                return@nextStep
//            }
//            val upper = nextStep.isUpper()
//            if(!upper) {
//                if(currentPath.contains(nextStep)) {
//                  //  currentPath.removeLast()
//                    return@nextStep
//                }
//            }
//            //visited.add(nextStep)
//            //   this.uppercase(Locale.getDefault()).equals(this)
//            if(upper) {
//                val currentPath1 = ArrayDeque(currentPath)
//
//                currentPath1.addLast(nextStep)
//                computePaths(currentPath1, paths, input, visited)
//            } else {
//                if(currentStep.isUpper()) {
//                    val currentPath1 = ArrayDeque(currentPath)
//                    currentPath1.addLast(nextStep)
//                    currentPath1.addLast(currentStep)
//                    computePaths(currentPath1, paths, input, visited)
//                }
//
//            }
//        }
//        currentStep
//            .let {
//                if (it.isUpper()) {
//                    paths.add(currentPath)
//                }
//            }
//        val targets: List<String> = input[nextStep]!!
//        if (targets.size == 1 && targets[0] == "end") {
//            return
//        }
//        targets.forEach { target ->
//            if (target.uppercase(Locale.getDefault()) == target) {
//                paths.forEach { it.addLast(target) }
//                (0..paths.size - 1) {
//                    paths.addLast(Array)
//                }
//                computePaths(target, paths, input)
//            } else {
//                paths.removeIf { !target.isUpper() }
//                paths.forEach { it.addLast(target) }
//            }
//        }

    }


    private fun computePaths2(
        currentPath: ArrayDeque<String>,
        paths: ArrayDeque<ArrayDeque<String>>,
        input: Map<String, List<String>>,
        visited: MutableSet<String>,
        smalCaves: ArrayDeque<String>,
        twiceVisited: String?
    ) {
        val currentStep = currentPath.get(currentPath.size - 1)
        if (currentStep == "end") {
            paths.addLast(currentPath)
            System.err.println(currentPath)
            return
        }
        val nextSteps = input[currentStep]

        if (nextSteps != null) {
            nextSteps.forEach nextStep@{ nextStep ->
                val atomicReference = AtomicReference(twiceVisited)
                if (!nextStep.isUpper() && "end" != nextStep) {
                    if (currentPath.contains(nextStep) && twiceVisited != null) {
                        return@nextStep
                    }
                    if (currentPath.contains(nextStep)) {
                        if (!smalCaves.contains(nextStep)) {
                            return@nextStep
                        }
                        //val visittwice = smalCaves.remove(nextStep)
                        atomicReference.set(nextStep)
                    }
                }
                val currentPath1 = ArrayDeque(currentPath)
                currentPath1.addLast(nextStep)
                computePaths2(currentPath1, paths, input, visited, smalCaves, atomicReference.get())
            }
        } else {
            if (!currentStep.isUpper()) {
                val previous = currentPath.get(currentPath.size - 2)
                if (previous.isUpper()) {
                    val currentPath1 = ArrayDeque(currentPath)
                    currentPath1.addLast(previous)
                    computePaths2(currentPath1, paths, input, visited, smalCaves, twiceVisited)
                }
            }
        }
    }

    val part2ExpectedResult = 36L
    fun part2(rawInput: List<String>): Result {


        val input = clean(rawInput)
        val visited = HashSet<String>().toMutableSet()
        val paths = ArrayDeque<ArrayDeque<String>>()
        //paths.addLast(ArrayDeque(listOf("start")))
        val smalCaves = ArrayDeque<String>(input.keys.filter { it != "start" && it != "end" }.filter { !it.isUpper() })
        val twiceVisited = AtomicReference<String>(null)
        computePaths2(ArrayDeque(listOf("start")), paths, input, visited, smalCaves, null)// default value = visitedted)
        return paths.size.toLong()
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
