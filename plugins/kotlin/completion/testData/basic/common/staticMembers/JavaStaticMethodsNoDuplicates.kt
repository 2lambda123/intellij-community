import java.util.Collections.max

fun foo() {
    lastIndexOfSub<caret>
}

// IGNORE_K2
// INVOCATION_COUNT: 2
// EXIST_JAVA_ONLY: { allLookupStrings: "lastIndexOfSubList", itemText: "Collections.lastIndexOfSubList" }
// NOTHING_ELSE
