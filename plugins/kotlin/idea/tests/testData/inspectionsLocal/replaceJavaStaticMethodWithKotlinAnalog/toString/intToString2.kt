// WITH_STDLIB
// FIX: Replace with 'toString' function (may change semantics)

fun foo() {
    val b = listOf(42, 10)
    println(Integer.<caret>toString(b.first(), b.last()).let{it} + 1)
}
