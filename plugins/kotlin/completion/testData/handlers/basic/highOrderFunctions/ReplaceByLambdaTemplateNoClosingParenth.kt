fun foo(p : (String, Char) -> Boolean){}

fun main(args: Array<String>) {
    fo<caret>(1, 2
}

// IGNORE_K2
// ELEMENT: "foo"
// TAIL_TEXT: " { String, Char -> ... } (p: (String, Char) -> Boolean) (<root>)"
// CHAR: '\t'
