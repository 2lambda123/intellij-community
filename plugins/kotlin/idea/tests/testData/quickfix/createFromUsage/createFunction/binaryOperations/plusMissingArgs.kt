// "Create member function 'A.plus'" "true"

class A<T>(val n: T) {
    fun plus(i: Int, s: String): A<T> = throw Exception()
}

fun test() {
    val a: A<Int> = A(1) <caret>+ 2
}
// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.createFromUsage.createCallable.CreateCallableFromUsageFix