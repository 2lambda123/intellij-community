package java

annotation class Hello
val v = 1

@<caret>

// INVOCATION_COUNT: 0
// EXIST: Hello
// EXIST: Suppress
// ABSENT: String
// ABSENT: v
// EXIST: kotlin.
// ABSENT: collections.
// ABSENT: io.
// ABSENT: lang.
