package day05

import utils.Point
import utils.Vector
import utils.computeStep
import utils.readInput
import kotlin.math.*


typealias Count = Int
typealias Day5Row = MutableList<Count>
typealias Day5Array = MutableList<Day5Row>


data class Grid(val array: Day5Array) {

    fun apply(vector: Vector, allowDiagonal: Boolean) {
        check(vector.isVertical() || vector.isHorizontal() || vector.isDiagonal())
        if (allowDiagonal || vector.isHorizontal() || vector.isVertical()) {

            val length = vector.gridLength()
            val b = vector.from.x == vector.from.y
            val stepX = computeStep(vector.from.x, vector.to.x)
            val stepY = computeStep(vector.from.y, vector.to.y)

            (0..length).forEach {
                val x = vector.from.x + it * stepX
                val y = vector.from.y + it * stepY
                array[y][x] += 1
            }

        }
    }

    fun result(): Int = array.flatMap { row -> row.filter { cellCount -> cellCount >= 2 } }.count()

    fun display() {
        val message = array
            .map { it.joinToString((" ")) }
            .joinToString("\n")
        println(message)
    }


}


class Puzzle {
    init {
        check(computeStep(0, 1) == 1)
        check(computeStep(1, 0) == -1)
        check(computeStep(1, 1) == 0)
    }

    fun clean(input: List<String>): Pair<Grid, List<Vector>> {
        val vectors = input.map { line ->
            val vector = line.split(" -> ").map { xy ->
                val coords = xy.split(",").map { pos -> pos.toInt() }
                check(coords.size == 2)
                Point(coords[0], coords[1])
            }
            check(vector.size == 2)
            Vector(vector[0], vector[1])
        }
        val maxX = vectors.map { max(it.from.x, it.to.x) }.maxOfOrNull { it }!! + 1
        val maxY = vectors.map { max(it.from.y, it.to.y) }.maxOfOrNull { it }!! + 1

        val array: Day5Array = MutableList(maxY) { MutableList(maxX) { 0 } }
        val grid = Grid(array)

        return Pair(grid, vectors)
    }

    val part1ExpectedResult = 5
    fun part1(rawInput: List<String>): Int {
        val (grid, vectors) = clean(rawInput)
        vectors.forEach {
            grid.apply(it, false)
        }
        //grid.display()
        return grid.result()
    }

    val part2ExpectedResult = 12
    fun part2(rawInput: List<String>): Int {
        val (grid, vectors) = clean(rawInput)
        vectors.forEach {
            grid.apply(it, true)
        }
        //grid.display()
        return grid.result()
    }

}


fun main() {
    val puzzle = Puzzle()
    println(Puzzle::class.qualifiedName)

    val testInput = readInput("00test", Puzzle::class)
    val input = readInput("zzdata", Puzzle::class)


    println("test1: ${puzzle.part1(testInput)} == ${puzzle.part1ExpectedResult}")
    check(puzzle.part1(testInput) == puzzle.part1ExpectedResult)
    println("part1: ${puzzle.part1(input)}")

    println("test2: ${puzzle.part2(testInput)} == ${puzzle.part2ExpectedResult}")
    check(puzzle.part2(testInput) == puzzle.part2ExpectedResult)
    println("part2: ${puzzle.part2(input)}")

}
