import kotlin.math.absoluteValue

fun main() {
    fun parseToLists(input: List<String>) =
        input
            .filter { it.isNotEmpty() }
            .map { line ->
                line
                    .split(Regex("\\s"))
                    .filter { it.isNotEmpty() }
                    .map { it.toInt() }
            }.let { lists ->
                Pair(
                    lists.map { it.first() }.sorted(),
                    lists.map { it.last() }.sorted(),
                )
            }

    fun part1(input: List<String>): Int {
        val (list1, list2) = parseToLists(input)

        return list1
            .mapIndexed { index, list1Number ->
                val list2Number = list2.get(index)
                (list1Number - list2Number).absoluteValue
            }.sum()
    }

    fun part2(input: List<String>): Int {
        val (list1, list2) = parseToLists(input)
        return list1
            .sumOf { list1Number ->
                list1Number * list2.count { it == list1Number }
            }
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 11)
    check(part2(testInput) == 31)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
