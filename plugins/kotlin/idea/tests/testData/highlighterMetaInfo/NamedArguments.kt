// FIR_IDENTICAL
// CHECK_SYMBOL_NAMES
// HIGHLIGHTER_ATTRIBUTES_KEY
@Suppress(names = ["foo"])
fun foo(p1: Int, p2: String): String {
    return p2 + p1
}

fun bar() {
    foo(1, p2 = "")
}