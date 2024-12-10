import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

private val searchDirections =
    listOf(
        Point(-1, 0),
        Point(1, 0),
        Point(0, -1),
        Point(0, 1),
    )

private suspend fun List<IntArray>.findPeaks(intersectionPoint: Point): Deferred<List<Point>> =
    coroutineScope {
        async {
            var currentPosition = intersectionPoint
            var currentHeight = get(currentPosition.y)[currentPosition.x]
            while (currentHeight != 9) {
                val possiblePaths =
                    searchDirections
                        .map { currentPosition + it }
                        .filter {
                            getOrNull(it.y)?.getOrNull(it.x) == currentHeight + 1
                        }

                when (possiblePaths.size) {
                    0 -> return@async emptyList()
                    1 -> {
                        currentPosition = possiblePaths.first()
                        currentHeight = get(currentPosition.y)[currentPosition.x]
                    }
                    else -> {
                        return@async possiblePaths
                            .map {
                                findPeaks(it)
                            }.awaitAll()
                            .flatten()
                    }
                }
            }
            listOf(currentPosition)
        }
    }

fun main() {
    suspend fun findTrailPaths(input: List<String>): List<Deferred<List<Point>>> {
        val grid = input.asGrid().map { it.map { it.digitToInt() }.toIntArray() }
        return grid.flatMapIndexed { yIndex, row ->
            row
                .mapIndexed { xIndex, c ->
                    if (c == 0) {
                        grid.findPeaks(Point(xIndex, yIndex))
                    } else {
                        null
                    }
                }.filterNotNull()
        }
    }

    fun part1(input: List<String>) =
        runBlocking {
            val trailPaths = findTrailPaths(input)
            trailPaths
                .awaitAll()
                .sumOf {
                    it.toSet().size
                }
        }

    fun part2(input: List<String>) =
        runBlocking {
            val trailPaths = findTrailPaths(input)
            trailPaths
                .awaitAll()
                .sumOf {
                    it.size
                }
        }

    val testInput = readInput("Day10_test")
    check(part1(testInput) == 36)
    check(part2(testInput) == 81)

    val input = readInput("Day10")
    part1(input).println()
    part2(input).println()
}
