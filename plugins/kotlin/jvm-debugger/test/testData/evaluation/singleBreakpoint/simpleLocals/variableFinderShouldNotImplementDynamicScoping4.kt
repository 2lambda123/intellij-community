package variableFinderShouldNotImplementDynamicScoping4

fun outer(x: Int): () -> Int {
    fun inner(): Int {
        return x
    }
    fun returned(): Int {
        //Breakpoint!
        return 0
    }
    return ::returned
}

fun test(x: Int) {
    fun inner() {
        val y = x
        val f = outer(54)
        f()
    }
    inner()
}

fun main() {
   test(45)
}

// EXPRESSION: x
// RESULT: 'x' is not captured