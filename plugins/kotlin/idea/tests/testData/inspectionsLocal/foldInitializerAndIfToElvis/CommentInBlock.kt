// WITH_DIFFERENT_DATA_FOR_K2
fun foo(p: List<String?>): Int {
    val v = p[0]
    <caret>if (v == null) {
        // return -1 if null
        return -1
    }
    return v.length
}