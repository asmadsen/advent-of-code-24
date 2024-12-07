private val whitespaceRegex = Regex("\\s")
private data class CalibrationEquation(val testValue: Long, val segments: List<Long>) {
    companion object {
        fun fromString(line: String): CalibrationEquation {
            val (testValue, segmentsString) = line.split(':')
            return CalibrationEquation(
                testValue.trim().toLong(),
                segmentsString.split(whitespaceRegex).mapNotNull { it.toLongOrNull() }
            )
        }
    }
}

private enum class Operator(val run: (Long, Long) -> Long) {
    Plus(Long::plus),
    Multiply(Long::times),
    Concatenation( {a, b -> "$a$b".toLong() });
    companion object {
        fun sequenceOfUniqueCombinations(size: Int, ignoredOperators: Set<Operator>): Sequence<List<Operator>> {
            if (size == 0) return emptySequence()
            val uniqueOperators = Operator.entries - ignoredOperators
            if (size == 1) return uniqueOperators.map { listOf(it) }.asSequence()
            return sequence {
                uniqueOperators.forEach {
                    sequenceOfUniqueCombinations(size - 1, ignoredOperators).forEach { prefix ->
                        yield((prefix + it))
                    }
                }
            }
        }
    }
}


private fun CalibrationEquation.test(ignoredOperators: Set<Operator>): Boolean {
    for (operators in Operator.sequenceOfUniqueCombinations(segments.size - 1, ignoredOperators)) {
        if (testValue == segments.reduceIndexed { index, acc, i ->
                operators[index - 1].run(acc, i)
            }) {
            return true
        }
    }
    return false
}


private fun String.transform() = map {
    when (it) {
        '+' -> Operator.Plus
        '*' -> Operator.Multiply
        else -> error("unexpected")
    }
}

fun main() {
    fun part1(input: List<String>): Long {
        val ignoredOperators = setOf(Operator.Concatenation)
        val equations = input.map { CalibrationEquation.fromString(it) }
        return equations.sumOf {
            if (it.test(ignoredOperators)) {
                it.testValue
            } else 0
        }
    }

    fun part2(input: List<String>): Long  {
        val ignoredOperators = emptySet<Operator>()
        val equations = input.map { CalibrationEquation.fromString(it) }
        return equations.sumOf {
            if (it.test(ignoredOperators)) {
                it.testValue
            } else 0
        }
    }

    val testInput = readInput("Day07_test")
    check(part1(testInput) == 3749L)
    check(part2(testInput) == 11387L)

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}
