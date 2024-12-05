
@JvmInline
private value class Rules(val value: List<Pair<Int, Int>>)

private fun List<String>.parseToSections(): Pair<Rules, List<List<Int>>> {
    val rawRules = takeWhile { it.isNotBlank() }
    val rawUpdates = (this - rawRules.toSet()).filter { it.isNotBlank() }

    val rules = Rules(rawRules.map { it.split('|') }
        .map { it.first().toInt() to it.last().toInt() })
    val updates = rawUpdates.map { it.split(',').map { it.toInt() } }
    return rules to updates
}

private fun List<Int>.sortWith(rules: Rules): List<Int> {
    val applicableRules = rules.value.filter {
        contains(it.first) && contains(it.second)
    }

    return sortedWith { a, b ->
        val subjects = listOf(a, b)
        val subjectRules = applicableRules.find { it.first in subjects && it.second in subjects}


        subjectRules?.run {
            if (first == a) -1
            else 1
        } ?: 0
    }
}


fun main() {
    fun part1(input: List<String>): Int {
        val (rules, updates ) = input.parseToSections()


        return updates.sumOf {
            val sorted = it.sortWith(rules)

            if (sorted != it) return@sumOf 0

            sorted[sorted.size / 2]
        }
    }

    fun part2(input: List<String>): Int {
        val (rules, updates ) = input.parseToSections()


        return updates.sumOf {
            val sorted = it.sortWith(rules)

            if (sorted == it) return@sumOf 0

            sorted[sorted.size / 2]
        }
    }

    val testInput = readInput("Day05_test")
    check(part1(testInput) == 143)
    check(part2(testInput) == 123)

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}
