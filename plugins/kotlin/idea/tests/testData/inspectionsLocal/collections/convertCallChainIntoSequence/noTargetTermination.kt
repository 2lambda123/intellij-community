// WITH_STDLIB

fun test(list: List<List<Int>>): List<Int> {
    return list
        .<caret>filter { it.count() > 2 }
        .map { it + it }
        .flatten()
}