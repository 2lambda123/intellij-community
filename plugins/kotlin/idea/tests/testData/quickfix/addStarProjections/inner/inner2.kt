// "Add star projections" "true"
class A<T> {
    inner class B
    fun test(x: Any) = x is B<caret>
}
// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.AddStartProjectionsForInnerClass