// FIR_COMPARISON
// FIR_IDENTICAL
/**
 * @param xyzzy This is a xyzzy
 * @param <caret>
 */
fun foo(xyzzy: Int, foobar: String) {
}

// ABSENT: xyzzy
// EXIST: foobar
