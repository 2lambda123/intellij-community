// "Create member function 'A.foo'" "true"

class A<T>(val n: T) {
    fun foo(i: Int, s: String): A<T> = throw Exception()
}

fun test() {
    val a: A<Int> = A(1).foo(2<caret>)
}
// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.createFromUsage.createCallable.CreateCallableFromUsageFix