// FIR_COMPARISON
// FIR_IDENTICAL
class Test {
    val S<caret>
}

// INVOCATION_COUNT: 2
// EXIST: { lookupString:"String", tailText:" (kotlin)", icon: "org/jetbrains/kotlin/idea/icons/classKotlin.svg"}
// EXIST: IllegalStateException
// EXIST: StringBuilder
// EXIST_JAVA_ONLY: StringBuffer
// EXIST_JS_ONLY: JSON
// EXIST_JAVA_ONLY: { lookupString:"Statement", tailText:" (java.sql)" }
