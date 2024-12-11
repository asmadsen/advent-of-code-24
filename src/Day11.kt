import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.measureTime

private fun List<Long>.blink() =
    flatMapIndexed { index: Int, stone: Long ->
        buildList {
            val stoneAsString = stone.toString()
            when {
                stone == 0L -> add(1L)
                stoneAsString.length % 2 == 0 -> {
                    add(stoneAsString.take(stoneAsString.length / 2).toLong())
                    add(stoneAsString.takeLast(stoneAsString.length / 2).toLong())
                }
                else -> add(stone * 2024)
            }
        }
    }


private fun blink(
    stone: Long,
): List<Long> = buildList {
        val stoneAsString = stone.toString()
        when {
            stone == 0L -> add(1L)
            stoneAsString.length % 2 == 0 -> {
                add(stoneAsString.take(stoneAsString.length / 2).toLong())
                add(stoneAsString.takeLast(stoneAsString.length / 2).toLong())
            }
            else -> add(stone * 2024)
        }
    }


private val mutex = Mutex()

private val memo = mutableMapOf<Pair<Long, Int>, Deferred<Long>>()

private suspend fun blink(
    stone: Long,
    repeat: Int,
): Deferred<Long> = coroutineScope {
        mutex.withLock {
            memo[stone to repeat]
        }?.let {
            return@coroutineScope it
        }
        val stoneAsString = stone.toString()
        when {
            repeat == 0 -> CompletableDeferred(1L)
            stone == 0L -> blink(1L, repeat - 1)
            stoneAsString.length % 2 == 0 -> async {
                val first = blink(stoneAsString.take(stoneAsString.length / 2).toLong(), repeat - 1).await()
                val second = blink(stoneAsString.takeLast(stoneAsString.length / 2).toLong(), repeat - 1).await()

                first + second
            }

            else -> blink(stone * 2024, repeat - 1)
        }.also {
            mutex.withLock {
                memo[stone to repeat] = it
            }
        }
    }


private fun CoroutineScope.blink(
    stones: List<Long>,
    repeat: Int,
): Deferred<Long> {
    if (repeat == 0) return CompletableDeferred( stones.size.toLong())
    val afterBlink = stones.flatMap { blink(it) }
    return if (afterBlink.size > 20 && afterBlink.size % 2 == 0) {
        val first = afterBlink.take(afterBlink.size / 2)
        val second = afterBlink.takeLast(afterBlink.size / 2)
        async {
            listOf(first, second).map {
                blink(it, repeat - 1)
            }.awaitAll().sum()
        }
    } else blink(afterBlink, repeat - 1)
}

fun main() {
    fun part1(input: List<String>) =
        runBlocking(Dispatchers.Default) {
            val initialStones = input.first().split(whitespaceRegex).mapNotNull { it.toLongOrNull() }

            initialStones.map {
                blink(it, 25)
            }.awaitAll().sum()
        }

    fun part2(input: List<String>) =
        runBlocking(Dispatchers.Default) {
            val initialStones = input.first().split(whitespaceRegex).mapNotNull { it.toLongOrNull() }

            initialStones.map {
                blink(it, 75)
            }.awaitAll()
                .sum()
        }

    val testInput = readInput("Day11_test")
    check(part1(testInput) == 55312L)
    // check(part2(testInput) == 81)

    val input = readInput("Day11")
    part1(input).println()
    part2(input).println()
}
