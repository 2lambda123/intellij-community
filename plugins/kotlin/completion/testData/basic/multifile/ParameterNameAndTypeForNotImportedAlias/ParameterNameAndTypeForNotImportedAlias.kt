// FIR_COMPARISON
package test2

fun foo(ali<caret>)

// EXIST: { lookupString: "alias: MyAlias", itemText: "alias: MyAlias", tailText: " (test)", typeText: "MyClass", attributes: "", icon: "org/jetbrains/kotlin/idea/icons/typeAlias.svg"}
