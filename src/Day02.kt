import kotlin.math.absoluteValue

private fun List<Int>.isSafe(): Boolean {
    var increasing: Boolean? = null

    val iterator = iterator()
    var previous = iterator.next()
    while (iterator.hasNext()) {
        val current = iterator.next()
        if (!validateLevel(previous, current, increasing)) {
            return false
        }
        increasing = increasing ?: (previous < current)
        previous = current
    }
    return true
}

private fun validateLevel(
    previous: Int,
    current: Int,
    increasing: Boolean?,
): Boolean {
    val delta = (previous - current).absoluteValue
    if (delta !in 1..3) {
        return false
    }
    return when (increasing) {
        null -> true
        true -> previous < current
        false -> previous > current
    }
}

private fun List<Int>.isSafeLenient(): Boolean {
    var increasing: Boolean? = null
    val iterator = iterator()
    val previousList = mutableListOf(iterator.next())

    while (iterator.hasNext()) {
        val previous = previousList.last()
        val current = iterator.next()
        if (!validateLevel(previous, current, increasing)) {
            if (previousList.size == 2) {
                val listWithoutFirst = listOf(previousList.last()) + subList(previousList.size, lastIndex + 1)
                if (listWithoutFirst.isSafe()) return true
            }
            val listWithoutPrevious =
                previousList.subList(0, previousList.lastIndex) + subList(previousList.size, lastIndex + 1)

            if (listWithoutPrevious.isSafe()) {
                // println("safe - removing $previous - $this - $listWithoutPrevious")
                return true
            }

            val listWithoutCurrent =
                previousList.toList() + subList(previousList.size + 1, lastIndex + 1)
            if (listWithoutCurrent.isSafe()) {
                // println("safe - removing $current - $this - $listWithoutCurrent")
                return true
            }
            return false
        }
        increasing = increasing ?: (previous < current)
        previousList.add(current)
    }
    return true
}

fun main() {
    fun parseToLevels(input: List<String>) =
        input
            .filter { it.isNotEmpty() }
            .map { line ->
                line
                    .split(Regex("\\s"))
                    .filter { it.isNotEmpty() }
                    .map { it.toInt() }
            }

    fun part1(input: List<String>): Int = parseToLevels(input).count { it.isSafe() }

    fun part2(input: List<String>): Int = parseToLevels(input).withIndex().count { it.value.isSafeLenient() }

    val testInput = readInput("Day02_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
