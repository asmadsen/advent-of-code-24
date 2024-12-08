private data class Antenna(
    val frequency: Char,
    val position: Point,
)

private fun List<CharArray>.findAntennas() =
    flatMapIndexed { yIndex: Int, row: CharArray ->
        row
            .mapIndexed { xIndex, c ->
                if (c.isLetterOrDigit()) {
                    Antenna(c, Point(xIndex, yIndex))
                } else {
                    null
                }
            }.filterNotNull()
    }

private fun List<Antenna>.findAntiNodes(): List<Point> =
    flatMap { antenna ->
        val sameFrequencyAntennas = filter { it != antenna && it.frequency == antenna.frequency }

        sameFrequencyAntennas.map { otherAntenna ->
            antenna.position + antenna.position.diff(otherAntenna.position).invert()
        }
    }

private fun List<Antenna>.findAntiNodesExtended(
    xBound: IntRange,
    yBound: IntRange,
): List<Point> =
    flatMap { antenna ->
        val sameFrequencyAntennas = filter { it != antenna && it.frequency == antenna.frequency }

        sameFrequencyAntennas.flatMap { otherAntenna ->
            val diff = antenna.position.diff(otherAntenna.position).invert()
            var lastPosition = antenna.position
            generateSequence {
                val next = lastPosition + diff
                next
                    .takeIf {
                        it.x in xBound && it.y in yBound
                    }?.also {
                        lastPosition = it
                    }
            }
        } + sameFrequencyAntennas.map { it.position }
    }

private fun List<Point>.filterWithin(
    xRange: IntRange,
    yRange: IntRange,
) = filter {
    it.x in xRange && it.y in yRange
}

fun main() {
    fun part1(input: List<String>): Int {
        val grid = input.asGrid()
        val antennas = grid.findAntennas()
        return antennas
            .findAntiNodes()
            .distinct()
            .filterWithin(0..grid.first().lastIndex, 0..grid.lastIndex)
            .count()
    }

    fun part2(input: List<String>): Int {
        val grid = input.asGrid()
        val antennas = grid.findAntennas()
        return antennas
            .findAntiNodesExtended(0..grid.first().lastIndex, 0..grid.lastIndex)
            .distinct()
            .count()
    }

    val testInput = readInput("Day08_test")
    check(part1(testInput) == 14)
    check(part2(testInput) == 34)

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}
