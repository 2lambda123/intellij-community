// WITH_STDLIB

fun test(list: List<Int>): List<Pair<IndexedValue<List<Int>>, IndexedValue<List<Int>>>> {
    return list
            .<caret>chunked(1)
            .distinct()
            .distinctBy { it }
            .drop(1)
            .dropWhile { true }
            .filter { true }
            .filterIndexed { index, list -> true }
            .filterIsInstance<Int>()
            .filterNot { true }
            .filterNotNull()
            .flatMap { listOf(it) }
            .flatMapIndexed { _, i -> listOf(listOf(i)) }
            .flatten()
            .map { it }
            .mapIndexed { index, i -> i }
            .mapIndexedNotNull { index, i -> i }
            .mapNotNull { it }
            .minus(1)
            .minusElement(1)
            .onEach {}
            .onEachIndexed { index, i -> }
            .plus(1)
            .plusElement(1)
            .requireNoNulls()
            .runningFold(0) { acc, i -> acc + i }
            .runningFoldIndexed(0) { _, acc, i -> acc + i }
            .runningReduce { acc, i -> acc + i }
            .runningReduceIndexed { _, acc, i -> acc + i }
            .scan(0) { acc, i -> acc + i }
            .scanIndexed(0) { _, acc, i -> acc + i }
            .sorted()
            .sortedBy { it }
            .sortedByDescending { it }
            .sortedDescending()
            .sortedWith(Comparator { o1, o2 -> 0 })
            .take(1)
            .takeWhile { true }
            .windowed(1, 1, true)
            .withIndex()
            .zipWithNext()
            .zipWithNext { a, b -> a }
}