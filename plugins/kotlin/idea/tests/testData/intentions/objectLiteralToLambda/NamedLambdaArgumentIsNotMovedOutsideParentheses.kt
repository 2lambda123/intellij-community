// WITH_STDLIB
// AFTER-WARNING: Parameter 'runnable' is never used

fun foo(runnable: Runnable) {}

fun bar() {
    foo(runnable = <caret>object : Runnable {
        override fun run() {
            throw UnsupportedOperationException()
        }
    })
}