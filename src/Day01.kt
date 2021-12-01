fun main() {
    fun part1(input: List<String>): Int {
        var counter =0
        var last = Integer.MAX_VALUE
        for (s in input) {
            if(last< Integer.valueOf(s)) {
                counter++

            }

            last=Integer.valueOf(s)
        }
        return counter
    }

    fun part2(input: List<String>): Int {
        val integers = input.map { it -> Integer.valueOf(it) }
        var counter =0
        var last = Integer.MAX_VALUE
        for ((index, s) in integers.withIndex()) {
            if(index<2) continue
            var newLast = integers.get(index)+integers.get(index-1)+integers.get(index-2)
            if(last< newLast) {
                counter++

            }

            last=newLast
        }
        return counter
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 7)
    check(part2(testInput) == 5)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
