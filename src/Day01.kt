fun main() {

    data class Ctx(
        val last: Int = Integer.MAX_VALUE,
        val counter: Int = 0
    ) {
        fun next(newVal: Int): Ctx = when {
            newVal > last -> Ctx(newVal, counter + 1)
            else -> Ctx(newVal, counter)
        }
    }

    fun part1Clean(input: List<String>): List<Int> {
        return input.map(String::toInt)
    }

    fun part1Work(input: List<Int>): Int {
        return input
            .fold(Ctx()) { a, v -> a.next(v) }
            .counter
    }

    fun part2Work(input: List<Int>): Int {
        return input
            .foldIndexed(Ctx()) { i, a, v ->
                when {
                    i < 2 -> a
                    else -> a.next(v + input[i - 1] + input[i - 2])
                }

            }
            .counter
    }

    fun part1(input: List<String>): Int {
        return part1Work(part1Clean(input))
    }

    fun part2(input: List<String>): Int {
        return part2Work(part1Clean(input))
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 7)
    check(part2(testInput) == 5)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
