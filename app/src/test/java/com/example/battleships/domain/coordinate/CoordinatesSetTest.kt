package com.example.battleships.domain.coordinate

import com.example.battleships.game.domain.board.Coordinate
import com.example.battleships.game.domain.board.CoordinateSet
import com.example.battleships.game.domain.board.moveFromTo
import org.junit.Assert.*
import org.junit.Test


internal class CoordinatesSetTest {

    @Test
    fun moveFromTo_test_down_with_valid_values() {
        val testCoordinates : CoordinateSet = setOf(
            Coordinate(1,1),
            Coordinate(1,2),
            Coordinate(1,3),
            Coordinate(1,4),
        )
        val origin = Coordinate(1,2)
        val destination = Coordinate(2,2)

        val newCoordinates = testCoordinates.moveFromTo(origin, destination, 10)

        val expectedCoordinates : CoordinateSet = setOf(
            Coordinate(2,1),
            Coordinate(2,2),
            Coordinate(2,3),
            Coordinate(2,4),
        )

        assertTrue(newCoordinates.containsAll(expectedCoordinates))
        assertEquals(newCoordinates.size, expectedCoordinates.size)

    }

    @Test
    fun moveFromTo_test_left_with_valid_values() {
        val testCoordinates : CoordinateSet = setOf(
            Coordinate(1,2),
            Coordinate(1,3),
            Coordinate(1,4),
            Coordinate(1,5),
        )
        val origin = Coordinate(1,2)
        val destination = Coordinate(1,1)

        val newCoordinates = testCoordinates.moveFromTo(origin, destination, 10)

        val expectedCoordinates : CoordinateSet = setOf(
            Coordinate(1,1),
            Coordinate(1,2),
            Coordinate(1,3),
            Coordinate(1,4),
        )

        assertTrue(newCoordinates.containsAll(expectedCoordinates))
        assertEquals(newCoordinates.size, expectedCoordinates.size)

    }

    @Test
    fun moveFromTo_test_up_with_valid_values() {
        val testCoordinates : CoordinateSet = setOf(
            Coordinate(2,2),
            Coordinate(2,3),
            Coordinate(2,4),
            Coordinate(2,5),
        )
        val origin = Coordinate(2,2)
        val destination = Coordinate(1,2)

        val newCoordinates = testCoordinates.moveFromTo(origin, destination, 10)

        val expectedCoordinates : CoordinateSet = setOf(
            Coordinate(1,2),
            Coordinate(1,3),
            Coordinate(1,4),
            Coordinate(1,5),
        )

        assertTrue(newCoordinates.containsAll(expectedCoordinates))
        assertEquals(newCoordinates.size, expectedCoordinates.size)

    }

    @Test
    fun moveFromTo_test_right_with_valid_values() {
        val testCoordinates : CoordinateSet = setOf(
            Coordinate(1,2),
            Coordinate(1,3),
            Coordinate(1,4),
            Coordinate(1,5),
        )
        val origin = Coordinate(1,2)
        val destination = Coordinate(1,3)

        val newCoordinates = testCoordinates.moveFromTo(origin, destination, 10)

        val expectedCoordinates : CoordinateSet = setOf(
            Coordinate(1,3),
            Coordinate(1,4),
            Coordinate(1,5),
            Coordinate(1,6),
        )

        assertTrue(newCoordinates.containsAll(expectedCoordinates))
        assertEquals(newCoordinates.size, expectedCoordinates.size)

    }

@Test
    fun moveFromTo_test_down_left_with_valid_values() {
        val testCoordinates : CoordinateSet = setOf(
            Coordinate(2,2),
            Coordinate(2,3),
            Coordinate(2,4),
            Coordinate(2,5),
        )
        val origin = Coordinate(2,2)
        val destination = Coordinate(3,1)

        val newCoordinates = testCoordinates.moveFromTo(origin, destination, 10)

        val expectedCoordinates : CoordinateSet = setOf(
            Coordinate(3,1),
            Coordinate(3,2),
            Coordinate(3,3),
            Coordinate(3,4),
        )

        assertTrue(newCoordinates.containsAll(expectedCoordinates))
        assertEquals(newCoordinates.size, expectedCoordinates.size)

    }

    @Test
    fun moveFromTo_test_up_left_with_valid_values() {
        val testCoordinates : CoordinateSet = setOf(
            Coordinate(2,2),
            Coordinate(2,3),
            Coordinate(2,4),
            Coordinate(2,5),
        )
        val origin = Coordinate(2,2)
        val destination = Coordinate(1,1)

        val newCoordinates = testCoordinates.moveFromTo(origin, destination, 10)

        val expectedCoordinates : CoordinateSet = setOf(
            Coordinate(1,1),
            Coordinate(1,2),
            Coordinate(1,3),
            Coordinate(1,4),
        )

        assertTrue(newCoordinates.containsAll(expectedCoordinates))
        assertEquals(newCoordinates.size, expectedCoordinates.size)

    }

    @Test
    fun moveFromTo_test_up_right_with_valid_values() {
        val testCoordinates : CoordinateSet = setOf(
            Coordinate(2,2),
            Coordinate(2,3),
            Coordinate(2,4),
            Coordinate(2,5),
        )
        val origin = Coordinate(2,2)
        val destination = Coordinate(1,3)

        val newCoordinates = testCoordinates.moveFromTo(origin, destination, 10)

        val expectedCoordinates : CoordinateSet = setOf(
            Coordinate(1,3),
            Coordinate(1,4),
            Coordinate(1,5),
            Coordinate(1,6),
        )

        assertTrue(newCoordinates.containsAll(expectedCoordinates))
        assertEquals(newCoordinates.size, expectedCoordinates.size)

    }

    @Test
    fun moveFromTo_test_down_right_with_valid_values() {
        val testCoordinates : CoordinateSet = setOf(
            Coordinate(1,2),
            Coordinate(1,3),
            Coordinate(1,4),
            Coordinate(1,5),
        )
        val origin = Coordinate(1,2)
        val destination = Coordinate(2,3)

        val newCoordinates = testCoordinates.moveFromTo(origin, destination, 10)

        val expectedCoordinates : CoordinateSet = setOf(
            Coordinate(2,3),
            Coordinate(2,4),
            Coordinate(2,5),
            Coordinate(2,6),
        )

        assertTrue(newCoordinates.containsAll(expectedCoordinates))
        assertEquals(newCoordinates.size, expectedCoordinates.size)

    }

    @Test
    fun moveFromTo_test_horizontal_invalid_values() {

        try {
            val testCoordinates : CoordinateSet = setOf(
                Coordinate(1,2),
                Coordinate(1,3),
                Coordinate(1,4),
                Coordinate(1,5),
            )
            val origin = Coordinate(1,2)
            val destination = Coordinate(1,1000)

            testCoordinates.moveFromTo(origin, destination, 10)

            fail("Should have thrown an exception")
        } catch (e: Exception) {
            assertEquals(e.message, "Unable to move horizontally")
        }

        /*
        val thrown: Exception = Assertions.assertThrows(
            Exception::class.java,

            ) {
            val testCoordinates : CoordinateSet = setOf(
                Coordinate(1,2),
                Coordinate(1,3),
                Coordinate(1,4),
                Coordinate(1,5),
            )
            val origin = Coordinate(1,2)
            val destination = Coordinate(1,1000)

            testCoordinates.moveFromTo(origin, destination, 10)
        }
        assertTrue(thrown.message!! == "Unable to move horizontally")
         */
    }


@Test
    fun moveFromTo_test_vertically_invalid_values() {

    try {
        val testCoordinates : CoordinateSet = setOf(
            Coordinate(1,2),
            Coordinate(1,3),
            Coordinate(1,4),
            Coordinate(1,5),
        )
        val origin = Coordinate(1,2)
        val destination = Coordinate(1000,2)

        testCoordinates.moveFromTo(origin, destination, 10)

        fail("Expected an Exception to be thrown")
    } catch (e: Exception) {
        assertTrue(e.message!! == "Unable to move vertically")
    }

    /*
        val thrown: Exception = Assertions.assertThrows(
            Exception::class.java,

            ) {
            val testCoordinates : CoordinateSet = setOf(
                Coordinate(1,2),
                Coordinate(1,3),
                Coordinate(1,4),
                Coordinate(1,5),
            )
            val origin = Coordinate(1,2)
            val destination = Coordinate(1000,2)

            testCoordinates.moveFromTo(origin, destination, 10)
        }
        assertTrue(thrown.message!! == "Unable to move vertically")
        */
    }


    @Test
    fun moveFromTo_test_from_invalid_origin() {

        try {
            val testCoordinates : CoordinateSet = setOf(
                Coordinate(1,2),
                Coordinate(1,3),
                Coordinate(1,4),
                Coordinate(1,5),
            )
            val origin = Coordinate(1,1)
            val destination = Coordinate(1,3)

            testCoordinates.moveFromTo(origin, destination, 10)

            fail("Expected an Exception to be thrown")
        } catch (e: Exception) {
            assertTrue(e.message == "The origin coordinate is not included in the set of coordinates")
        }

        /*
        val thrown: Exception = Assertions.assertThrows(
            Exception::class.java,

            ) {
            val testCoordinates : CoordinateSet = setOf(
                Coordinate(1,2),
                Coordinate(1,3),
                Coordinate(1,4),
                Coordinate(1,5),
            )
            val origin = Coordinate(1,1)
            val destination = Coordinate(1,3)

            testCoordinates.moveFromTo(origin, destination, 10)


        }

        assertTrue(thrown.message == "The origin coordinate is not included in the set of coordinates")
         */
    }

}