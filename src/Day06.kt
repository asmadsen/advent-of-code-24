private data class Position(val x: Int, val y: Int)

private enum class Move(val x: Int, val y: Int) {
    Up(0, -1),
    Down(0, 1),
    Left(-1, 0),
    Right(1, 0)
}

private fun Move.rotate() = when (this) {
    Move.Up -> Move.Right
    Move.Right -> Move.Down
    Move.Down -> Move.Left
    Move.Left -> Move.Up
}

private fun Position.move(move: Move): Position {
    return Position(x + move.x, y + move.y)
}

private fun Position.isWithinGrid(grid: List<CharArray>): Boolean {
    return y in 0..grid.lastIndex && x in 0..(grid[y].lastIndex)
}

private val obstacleChars = setOf('#', 'O')

private fun Position.isObstacle(grid: List<CharArray>): Boolean {
    return grid.getOrNull(y)?.getOrNull(x) in obstacleChars
}

private class LoopException: Exception()

private fun navigate(grid: List<CharArray>, startPosition: Position): Set<Position> {
    val visitedPositions = mutableSetOf<Pair<Position, Move>>()
    var position = startPosition
    var direction = Move.Up
    while (position.isWithinGrid(grid)) {
        if (!visitedPositions.add(position to direction)) {
            throw LoopException()
        }

        val previousPosition = position
        do {
            if (position.isObstacle(grid)) {
                direction = direction.rotate()
            }
            position = previousPosition.move(direction)
        } while (position.isObstacle(grid))
    }
    return visitedPositions.map { it.first }.toSet()
}

private fun List<CharArray>.withNewObstacle(newObstacle: Position): List<CharArray> {
    val copyGrid = toMutableList()
    copyGrid[newObstacle.y] = copyGrid[newObstacle.y].toMutableList().apply {
        set(newObstacle.x, 'O')
    }.toCharArray()
    return copyGrid.toList()
}

private fun findPositionsForNewObstacles(grid: List<CharArray>, startPosition: Position): Int {
    val positionsInPath = navigate(grid, startPosition) - startPosition

    return positionsInPath.count {
        try {
            navigate(grid.withNewObstacle(it), startPosition)
            false
        } catch (e: LoopException) {
            true
        }
    }
}


fun main() {
    fun part1(input: List<String>): Int {
        val grid = input.asGrid()
        val startRow = grid.indexOfFirst {
            it.contains('^')
        }
        val startColumn = grid[startRow].indexOf('^')

        val visitedPositions = navigate(grid, Position(startColumn, startRow))

        return visitedPositions.size
    }

    fun part2(input: List<String>): Int  {
        val grid = input.asGrid()
        val startRow = grid.indexOfFirst {
            it.contains('^')
        }
        val startColumn = grid[startRow].indexOf('^')

        return findPositionsForNewObstacles(grid, Position(startColumn, startRow))
    }

    val testInput = readInput("Day06_test")
    check(part1(testInput) == 41)
    check(part2(testInput) == 6)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}
