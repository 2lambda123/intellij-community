// NEW_NAME: foo
// RENAME: variable
fun xyzzy(x: Pair<Int, String>) {
    val (bar, ba<caret>z) = x
    println(baz)
    println(baz.length())
}
