// WITH_STDLIB
// COMPILER_ARGUMENTS: -XXLanguage:-RangeUntilOperator
val N = 42
fun test(a: Int) = a >= 0 && a < N<caret>