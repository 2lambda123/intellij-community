class A(val k: Int) {
    fun foo(x: X, s: String, k: Int): Boolean {
        return x.k + s.length - k + this@A.k/2 > 0
    }

    fun test() {
        foo(X(0), "1", 2)
    }
}

class X(val k: Int)

fun test() {
    with(A(3)) {
        foo(X(0), "1", 2)
    }
}