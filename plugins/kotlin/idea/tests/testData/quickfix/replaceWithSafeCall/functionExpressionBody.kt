// "Replace with safe (?.) call" "true"
// WITH_STDLIB
// ERROR: Type mismatch: inferred type is Int? but Int was expected

// Note: There should be no error in FIR but errors are not currently checked for FIR
class T {
    fun foo(s: String?): Int = s<caret>.length
}

// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.ReplaceWithSafeCallFix
// FUS_K2_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.ReplaceWithSafeCallFix