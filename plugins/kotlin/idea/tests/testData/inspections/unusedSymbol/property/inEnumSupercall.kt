// KT-7007 Go to declaration doesn't work inside enum class

val TOP_LEVEL = 5

enum class MyEnum(value: Int) {
    VALUE(TOP_LEVEL)
    init {
       println(value)
    }
}

fun main(args: Array<String>) {
    println(args)
    MyEnum.VALUE
}
