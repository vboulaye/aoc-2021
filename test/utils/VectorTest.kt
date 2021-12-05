package utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class VectorTest {
    @Test
    fun euclidLength() {
        assertEquals(Vector(Point(1, 0), Point(1, 1)).euclidLength(), 1.0, 0.1)
        assertEquals(Vector(Point(0, 0), Point(1, 1)).euclidLength(), 1.4, 0.1)
    }

    @Test
    fun gridLength() {
        assertEquals(Vector(Point(0, 0), Point(0, 1)).gridLength(), 1)
        assertEquals(Vector(Point(1, 0), Point(1, 1)).gridLength(), 1)
        assertEquals(Vector(Point(0, 0), Point(1, 1)).gridLength(), 1)
    }

    @Test
    fun isHorizontal() {

        assertTrue(Vector(Point(0, 0), Point(1, 0)).isHorizontal())
        assertTrue(Vector(Point(1, 0), Point(0, 0)).isHorizontal())
    }

    @Test
    fun isVertical() {

        assertTrue(Vector(Point(0, 0), Point(0, 1)).isVertical())
        assertTrue(Vector(Point(0, 1), Point(0, 0)).isVertical())

    }

    @Test
    fun isDiagonal() {

        assertTrue(Vector(Point(0, 0), Point(1, 1)).isDiagonal())
        assertTrue(Vector(Point(1, 1), Point(0, 0)).isDiagonal())
    }

}
