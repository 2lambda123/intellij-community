fun returnFun(): Int = 10
fun <T> returnAnything(): T = null!!

fun usage(a: Int?) {
    a ?: re<caret>
}

// function of the same type as `a` is preferred to return in this case

// IGNORE_K2
// ORDER: returnFun
// ORDER: return
// ORDER: returnAnything