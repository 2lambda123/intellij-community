fun X.foo(s: String, k: Int): Boolean {
    return this@foo.k + s.length - k > 0
}

class X(val k: Int)

fun test() {
    X(0).foo("1", 2)
}