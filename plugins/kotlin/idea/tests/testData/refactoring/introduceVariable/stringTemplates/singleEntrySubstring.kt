// IGNORE_K2
fun foo(a: Int): String {
    val x = "xcd$a"
    val y = "${a}cdx"
    val z = "xcf$a"
    return "ab<selection>cd</selection>ef"
}