package a

class C {

    class B {
        /**
         * [a.C.B.<caret>]
         */
        fun member() {

        }
    }

    fun B.ext() {

    }

    val B.extVal: String
        get() = ""
}

fun C.B.topExt() {

}

val C.B.topExtVal: String
    get() = ""


// IGNORE_K2
// EXIST: ext
// EXIST: extVal
// EXIST: topExt
// EXIST: topExtVal