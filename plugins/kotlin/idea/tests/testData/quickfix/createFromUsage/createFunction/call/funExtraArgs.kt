// "Create member function 'A.foo'" "true"

class A<T>(val n: T) {
    fun foo(a: Int): A<T> = throw Exception()
}

fun test() {
    val a: A<Int> = A(1).foo(2, <caret>"2")
}
// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.createFromUsage.createCallable.CreateCallableFromUsageFix