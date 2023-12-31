// FIR_IDENTICAL
// CHECK_SYMBOL_NAMES
// HIGHLIGHTER_ATTRIBUTES_KEY
interface FunctionLike {
    operator fun invoke() {
    }
}

var global : () -> Unit = {}

val Int.ext : () -> Unit
get() {
  return {}
}

fun foo(a : () -> Unit, functionLike: FunctionLike) {
    a()
    functionLike()
    global()
    1.ext();

    {}() //should not be highlighted as "calling variable as function"!
}
