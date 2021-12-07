package utils


class NewPuzzle : AbstractPuzzle() {

    fun clean(input: List<String>): List<String> {
        return input
    }

    @TestResult(0)
    override fun part1(rawInput: List<String>): Long {
        val input = clean(rawInput)
        return 0
    }

    @TestResult(0)
    override fun part2(rawInput: List<String>): Long {
        val input = clean(rawInput)
        return 0
    }

}


fun main() {
    NewPuzzle().run()
}
