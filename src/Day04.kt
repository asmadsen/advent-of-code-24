private data class ExpectChar(
    val expected: Char,
    val row: Int,
    val column: Int,
    var next: ExpectChar?
)

private fun ExpectChar.next() = checkNotNull(next)

private fun buildHorizontalExpectChain(search: String, row: Int, column: Int): ExpectChar {
    val root = ExpectChar(
        search.first(),
        row,
        column,
        null
    )
    var parent =  root
    for (char in search.drop(1)) {
        val current = ExpectChar(
            char,
            row,
            parent.column + 1,
            null
        )
        parent.next = current
        parent = current
    }

    return root
}

private fun buildVerticalExpectChain(search: String, row: Int, column: Int): ExpectChar {
    val root = ExpectChar(
        search.first(),
        row,
        column,
        null
    )
    var parent =  root
    for (char in search.drop(1)) {
        val current = ExpectChar(
            char,
            parent.row + 1,
            column,
            null
        )
        parent.next = current
        parent = current
    }

    return root
}

private enum class Direction {
    Left(),
    Right()
}

private fun buildDiagonalExpectChain(search: String, row: Int, column: Int, direction: Direction): ExpectChar {
    val root = ExpectChar(
        search.first(),
        row,
        column,
        null
    )
    var parent =  root
    for (char in search.drop(1)) {
        val current = ExpectChar(
            char,
            parent.row + 1,
            when (direction) {
                Direction.Left -> parent.column - 1
                Direction.Right -> parent.column + 1
            },
            null
        )
        parent.next = current
        parent = current
    }

    return root
}

private fun ExpectChar.add(next: ExpectChar): ExpectChar {
    this.next?.add(next)
    if (this.next == null) {
        this.next = next
    }
    return this
}

private fun buildXMasExpectChain(initial: Char, row: Int, column: Int): List<ExpectChar> {
    val chainRight = ExpectChar(if (initial.equals('M', ignoreCase = true)) 'S' else 'M', row + 2, column + 2, null)

    return listOf(
        buildDiagonalExpectChain("MAS", row, column + 2, Direction.Left).add(chainRight),
        buildDiagonalExpectChain("MAS".reversed(), row, column + 2, Direction.Left).add(chainRight)
    )
}

fun main() {
    fun part1(input: List<String>): Int {
        var sum = 0
        val rows = input.asGrid()
        val expectedChars = mutableListOf<ExpectChar>()
        for ((rowIndex, row) in rows.withIndex()) {
            expectedChars.removeIf { it.row < rowIndex }
            for ((colIndex, char) in row.withIndex()) {
                expectedChars.removeIf { it.row == rowIndex && it.column < colIndex }

                val expectMatches = expectedChars.filter { it.row == rowIndex && it.column == colIndex }

                for (expectedChar in expectMatches) {
                    if (expectedChar.expected.equals(char, ignoreCase = true)) {
                        val next = expectedChar.next
                        if (next == null) {
                            sum++
                            continue
                        }
                        expectedChars.add(next)
                    }
                }


                when (char.uppercaseChar()) {
                    'X' -> {
                        expectedChars.add(buildHorizontalExpectChain("XMAS", rowIndex, colIndex).next())
                        expectedChars.add(buildVerticalExpectChain("XMAS", rowIndex, colIndex).next())
                        expectedChars.add(buildDiagonalExpectChain("XMAS", rowIndex, colIndex, Direction.Left).next())
                        expectedChars.add(buildDiagonalExpectChain("XMAS", rowIndex, colIndex, Direction.Right).next())
                    }
                    'S' -> {
                        expectedChars.add(buildHorizontalExpectChain("XMAS".reversed(), rowIndex, colIndex).next())
                        expectedChars.add(buildVerticalExpectChain("XMAS".reversed(), rowIndex, colIndex).next())
                        expectedChars.add(buildDiagonalExpectChain("XMAS".reversed(), rowIndex, colIndex, Direction.Left).next())
                        expectedChars.add(buildDiagonalExpectChain("XMAS".reversed(), rowIndex, colIndex, Direction.Right).next())
                    }
                }
            }
        }
        return sum
    }

    fun part2(input: List<String>): Int {
        var sum = 0
        val rows = input.asGrid()
        val expectedChars = mutableListOf<ExpectChar>()
        for ((rowIndex, row) in rows.withIndex()) {
            expectedChars.removeIf { it.row < rowIndex }
            for ((colIndex, char) in row.withIndex()) {
                expectedChars.removeIf { it.row == rowIndex && it.column < colIndex }

                val expectMatches = expectedChars.filter { it.row == rowIndex && it.column == colIndex }

                for (expectedChar in expectMatches) {
                    if (expectedChar.expected.equals(char, ignoreCase = true)) {
                        val next = expectedChar.next
                        if (next == null) {
                            sum++
                            continue
                        }
                        expectedChars.add(next)
                    }
                }


                when (char.uppercaseChar()) {
                    'M', 'S' -> {
                        expectedChars.addAll(buildXMasExpectChain(char.uppercaseChar(), rowIndex, colIndex))
                    }
                }
            }
        }
        return sum
    }

    val testInput = readInput("Day04_test")
    check(part1(testInput) == 18)
    check(part2(testInput) == 9)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
