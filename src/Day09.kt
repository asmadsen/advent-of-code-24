import java.util.Comparator

private sealed interface DiskDescriptor {
    val length: Int
    val index: Int

    data class FileDescriptor(
        override val index: Int,
        val id: Int,
        override val length: Int,
    ) : DiskDescriptor

    data class EmptyDescriptor(
        override val index: Int,
        override val length: Int,
    ) : DiskDescriptor
}

private fun List<DiskDescriptor>.toStringFormat() =
    joinToString("") {
        when (it) {
            is DiskDescriptor.FileDescriptor -> "${it.id}"
            is DiskDescriptor.EmptyDescriptor -> "."
        }.repeat(it.length)
    }

private fun String.parseInput(): List<DiskDescriptor> {
    var id = 0
    var runningIndex = 0
    return mapIndexed { index, c ->
        if (index % 2 == 0) {
            DiskDescriptor.FileDescriptor(runningIndex, id++, c.digitToInt())
        } else {
            DiskDescriptor.EmptyDescriptor(runningIndex, c.digitToInt())
        }.also {
            runningIndex += it.length
        }
    }
}

private fun List<DiskDescriptor>.blockFragment(): List<DiskDescriptor> {
    val remainingFiles = filterIsInstance<DiskDescriptor.FileDescriptor>().sortedBy { it.id }.toMutableList()
    var currentFile: DiskDescriptor.FileDescriptor? = remainingFiles.removeLast()
    val movedEmptyDescriptors = mutableListOf<DiskDescriptor.EmptyDescriptor>()
    return flatMap { descriptor ->
        buildList {
            when (descriptor) {
                is DiskDescriptor.FileDescriptor -> {
                    if (currentFile?.id != descriptor.id && remainingFiles.contains(descriptor)) {
                        remainingFiles.removeFirst()
                        add(descriptor)
                    }
                }

                is DiskDescriptor.EmptyDescriptor -> {
                    var remaining = descriptor.length
                    while (remaining > 0) {
                        if (currentFile?.length == 0) {
                            currentFile = remainingFiles.removeLastOrNull()
                        }
                        val tmpFile = currentFile
                        if (tmpFile == null) {
                            add(DiskDescriptor.EmptyDescriptor(descriptor.index + descriptor.length - remaining, remaining))
                            return@buildList
                        }
                        if (tmpFile.length > remaining) {
                            add(tmpFile.copy(length = remaining))
                            currentFile = tmpFile.copy(length = tmpFile.length - remaining)
                            remaining = 0
                        } else {
                            add(tmpFile)
                            remaining -= tmpFile.length
                            currentFile = tmpFile.copy(length = 0)
                        }
                    }
                    movedEmptyDescriptors += descriptor
                }
            }
        }
    } + movedEmptyDescriptors
}

private fun List<DiskDescriptor>.fileFragment(): List<DiskDescriptor> {
    val freeSpaceRanges =
        filterIsInstance<DiskDescriptor.EmptyDescriptor>()
            .map {
                (it.index..<(it.index + it.length))
            }.toSortedSet(Comparator.comparingInt(IntRange::start))

    fun recomputeFreeRemainingSpace(freeSpace: IntRange) {
        val leadingAdjacent =
            freeSpaceRanges
                .firstOrNull { it.last + 1 == freeSpace.first }
                ?.also {
                    freeSpaceRanges.remove(it)
                }
        val trailingAdjacent =
            freeSpaceRanges
                .firstOrNull { it.first - 1 == freeSpace.last }
                ?.also {
                    freeSpaceRanges.remove(it)
                }

        val newRange = (leadingAdjacent?.first ?: freeSpace.first)..(trailingAdjacent?.last ?: freeSpace.last)

        freeSpaceRanges.add(newRange)
    }

    val files = filterIsInstance<DiskDescriptor.FileDescriptor>().sortedByDescending { it.id }

    val movedFiles =
        buildMap {
            for (file in files) {
                val availableSpace =
                    freeSpaceRanges.firstOrNull {
                        val length = it.last - it.first + 1
                        length >= file.length
                    } ?: continue

                val availableSpaceLength = availableSpace.last - availableSpace.first + 1

                if (file.index > availableSpace.first) {
                    freeSpaceRanges.remove(availableSpace)
                    val remainingFreeSpace = availableSpaceLength - file.length
                    put(file.id, file.copy(index = availableSpace.first))
                    recomputeFreeRemainingSpace(
                        file.index..<(file.index + file.length),
                    )
                    if (remainingFreeSpace > 0) {
                        val remainingIndex = availableSpace.first + file.length
                        recomputeFreeRemainingSpace(
                            remainingIndex..<(remainingIndex + remainingFreeSpace),
                        )
                    }
                }
            }
        }

    val nonMovedFiles = files.associateBy { it.id } - movedFiles.keys

    return (
        nonMovedFiles.values + movedFiles.values +
            freeSpaceRanges.map {
                DiskDescriptor
                    .EmptyDescriptor(
                        it.first,
                        it.last - it.first + 1,
                    )
            }
    ).sortedBy { it.index }
}

fun main() {
    fun part1(input: List<String>): Long {
        val descriptors = input.first().parseInput()
        return descriptors
            .blockFragment()
            .flatMap { descriptor ->
                when (descriptor) {
                    is DiskDescriptor.FileDescriptor -> List(descriptor.length) { descriptor.id }
                    is DiskDescriptor.EmptyDescriptor -> List(descriptor.length) { 0 }
                }
            }.mapIndexed { index, id ->
                (index.toLong() * id)
            }.sum()
    }

    fun part2(input: List<String>): Long {
        val descriptors = input.first().parseInput()
        return descriptors
            .fileFragment()
            .flatMap { descriptor ->
                when (descriptor) {
                    is DiskDescriptor.FileDescriptor -> List(descriptor.length) { descriptor.id }
                    is DiskDescriptor.EmptyDescriptor -> List(descriptor.length) { 0 }
                }
            }.mapIndexed { index, id ->
                (index.toLong() * id)
            }.sum()
    }

    val testInput = readInput("Day09_test")
    check(part1(testInput) == 1928L)
    check(part2(testInput) == 2858L)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}
