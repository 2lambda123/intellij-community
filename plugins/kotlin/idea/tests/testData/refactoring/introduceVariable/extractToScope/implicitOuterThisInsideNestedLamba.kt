// IGNORE_K2
class A(val a: Int)
class B(val b: Int)

fun foo(f: A.() -> Int) = A(1).f()
fun bar(f: B.() -> Int) = B(2).f()

fun test() {
    foo { bar { <selection>a</selection> + b } }
}