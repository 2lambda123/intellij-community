fun foo(xxx1: String?, xxx2: String, xxx3: Any, xp: String?) {
    bar(x<caret>)
}

fun bar(pXxx: String){}

// IGNORE_K2
// ORDER: xxx2
// ORDER: xxx1
// ORDER: xp
// ORDER: xxx3
