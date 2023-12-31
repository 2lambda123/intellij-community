// FILE: test.kt
package jcMarkedObject

fun main(args: Array<String>) {
    val javaClass = forTests.javaContext.JavaClass()
    //Breakpoint!
    javaClass.markObject()
}

// STEP_INTO: 1
// STEP_OVER: 1

// EXPRESSION: i
// RESULT: instance of java.lang.Integer(id=ID): Ljava/lang/Integer;

// EXPRESSION: i_DebugLabel
// RESULT: instance of java.lang.Integer(id=ID): Ljava/lang/Integer;

// DEBUG_LABEL: i = i

// FILE: forTests/javaContext/JavaClass.java
package forTests.javaContext;

public class JavaClass {
    public void markObject() {
        Integer i = 1;
        int breakpoint = 1;
    }
}

// IGNORE_K2