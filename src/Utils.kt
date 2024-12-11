import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readText().trim().lines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() =
    BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
        .toString(16)
        .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun List<String>.asGrid() = map { it.toCharArray() }

val whitespaceRegex = Regex("\\s")

data class Point(
    val x: Int,
    val y: Int,
) {
    fun diff(other: Point) =
        Point(
            x = other.x - x,
            y = other.y - y,
        )

    fun invert() =
        copy(
            x = x * -1,
            y = y * -1,
        )

    operator fun plus(other: Point) =
        copy(
            x = x + other.x,
            y = y + other.y,
        )
}
