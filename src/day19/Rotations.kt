package day19

val xFacings = arrayOf(
    intArrayOf(1, 0, 0),
    intArrayOf(-1, 0, 0),
    intArrayOf(0, 1, 0),
    intArrayOf(0, -1, 0),
    intArrayOf(0, 0, 1),
    intArrayOf(0, 0, -1)
)

val upDirections = arrayOf(
    intArrayOf(1, 0),
    intArrayOf(0, 1),
    intArrayOf(-1, 0),
    intArrayOf(0, -1)
)

val transformations = xFacings.flatMap { x ->
    upDirections.map { up ->
        transformation(x, up)
    }
}



typealias Transformation = List<List<Int>>

fun transformation(x: IntArray, up: IntArray): Transformation {
    val ret = MutableList(3) { MutableList(3) {0} }
    val y = yFacing(x, up)
    val z = zFacing(x, y)

    for (i in 0..2) {
        ret[i][0] = x[i]
        ret[i][1] = y[i]
        ret[i][2] = z[i]
    }

    return ret
}

fun yFacing(x: IntArray, up: IntArray): IntArray {
    val coordinates = mutableListOf<Int>()
    var j = 0
    for (i in 0..2) {
        if (x[i] != 0) {
            coordinates.add(0)
        } else {
            coordinates.add(up[j])
            j++
        }
    }
    return coordinates.toIntArray()
}

fun zFacing(x: IntArray, y: IntArray): IntArray {
    val z = IntArray(3) { 0 }

    z[0] = x[1] * y[2] - x[2] * y[1]
    z[1] = x[2] * y[0] - x[0] * y[2]
    z[2] = x[0] * y[1] - x[1] * y[0]

    return z
}
