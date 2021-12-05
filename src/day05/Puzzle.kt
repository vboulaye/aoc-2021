package day05

import utils.readInput
import java.lang.Integer.max
import java.lang.Integer.min
import java.lang.Math.abs

data class Point(val x: Int, val y: Int)
data class Vector(val from: Point, val to: Point) {
    fun minX(): Int = min(from.x, to.x)
    fun minY(): Int = min(from.y, to.y)
    fun maxX(): Int = max(from.x, to.x)
    fun maxY(): Int = max(from.y, to.y)


}


data class Grid(val array: MutableList<MutableList<Int>>, val maxX: Int)


class Puzzle {

    fun clean(input: List<String>): Pair<Grid, List<Vector>> {
        val vectors = input.map { line ->
            val vector = line.split(" -> ").map {
                val coords = it.split(",").map { it.toInt() }
                check(coords.size == 2)
                Point(coords[0], coords[1])
            }
            Vector(vector[0], vector[1])
        }
        val maxX = vectors.map { max(it.from.x, it.to.x) }.maxOfOrNull { it }!! + 1
        val maxY = vectors.map { max(it.from.y, it.to.y) }.maxOfOrNull { it }!! + 1

        val array: MutableList<MutableList<Int>> = MutableList(maxY) { MutableList(maxX) { 0 } }
        val grid = Grid(array, maxX)

        return Pair(grid, vectors)
    }

    val part1ExpectedResult = 5
    fun part1(rawInput: List<String>): Int {
        val (grid, vectors) = clean(rawInput)
        vectors.forEach { vector ->
            if (vector.from.x == vector.to.x || vector.from.y == vector.to.y) {

                (vector.minX()..vector.maxX()).forEach { x ->
                    (vector.minY()..vector.maxY()).forEach { y ->

                        grid.array[y][x] += 1
                    }
                }

            }
        }
        return grid.array.flatMap { it.filter { it >= 2 } }.count()
    }

    val part2ExpectedResult = 12
    fun part2(rawInput: List<String>): Int {
        val (grid, vectors) = clean(rawInput)
        vectors.forEach { vector ->
            if (vector.from.x == vector.to.x || vector.from.y == vector.to.y) {
                (vector.minX()..vector.maxX()).forEach { x ->
                    (vector.minY()..vector.maxY()).forEach { y ->

                        grid.array[y][x] += 1
                    }
                }
            } else {
//                8,0 -> 0,8
//                0,0 -> 8,8

                val length = abs(vector.minX() - vector.maxX())
                val stepX = if (vector.from.x< vector.to.x) 1 else -1
                val stepY = if (vector.from.y < vector.to.y) 1 else -1

                (0..length).forEach{
                    val x = vector.from.x + it * stepX
                    val y = vector.from.y + it * stepY
                    grid.array[y][x] += 1
                }


            }
        }
        //18708 >
        val message = grid.array
            .map { it.joinToString((" ")) }
            .joinToString("\n")
        //println(message)
        return grid.array.flatMap { it.filter { it >= 2 } }.count()
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
