class CC {
    companion object {
        fun getInstance(): CC {}
    }
}

fun foo(): CC {
    return C<caret>
}

// IGNORE_K2
// ORDER: CC
// ORDER: getInstance
