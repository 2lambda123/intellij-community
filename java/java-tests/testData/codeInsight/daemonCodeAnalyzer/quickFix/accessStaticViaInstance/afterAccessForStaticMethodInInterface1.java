// "Access static 'I.m()' via class 'I' reference" "true-preview"
interface I {
    static void m() {}
}

class A {
    void f(I i){
        I.m();
    }
}