// "Add 'Any' as upper bound for T to make it non-nullable" "true"
// LANGUAGE_VERSION: 1.6

fun <R : Any> foo(x: R) {}

fun <T> bar(x: T) {
    foo(<caret>x)
}

// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.HighPriorityMakeUpperBoundNonNullableFix