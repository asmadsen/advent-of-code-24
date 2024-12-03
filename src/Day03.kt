fun main() {
    val multiplicationRegex = Regex("mul\\((\\d{1,3}),(\\d{1,3})\\)")
    val doDontRegex = Regex("do\\(\\)|don\'t\\(\\)")

    fun part1(input: List<String>): Int =
        multiplicationRegex.findAll(input.joinToString("")).fold(0) { acc, match ->
            val (num1, num2) = match.destructured

            acc + (num1.toInt() * num2.toInt())
        }

    fun part2(input: List<String>): Int {
        val inputString = input.joinToString("")
        val enabledRanges =
            buildList {
                val enabledFrom =
                    doDontRegex.findAll(inputString).fold(0) { enabledFrom: Int?, match ->
                        if (match.value == "don't()") {
                            if (enabledFrom != null) {
                                add(enabledFrom..match.range.first)
                            }
                            null
                        } else {
                            enabledFrom ?: match.range.first
                        }
                    }
                if (enabledFrom != null) {
                    add(enabledFrom..inputString.length)
                }
            }

        return multiplicationRegex.findAll(inputString).fold(0) { acc, match ->
            val enabled = enabledRanges.any { range -> range.contains(match.range.first) }
            if (enabled) {
                val (num1, num2) = match.destructured

                acc + (num1.toInt() * num2.toInt())
            } else {
                acc
            }
        }
    }

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 161)
    check(part2(testInput) == 48)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
