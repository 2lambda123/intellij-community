// "Merge with 'case 1'" "INFORMATION"
class C {
    String foo(int n) {
        switch (n) {
            case 1 -> {
                foo(); // same comment
                return "A";
            }
            case 2 ->{
                foo(); // same comment
                <caret> return "A"; // another comment
            }
        }
        return "";
    }
    void foo(){}
}