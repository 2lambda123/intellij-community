// "Initialize in constructor" "true-preview"
class Main {
  private final int var;

  private Main() {
      var = 0;
  }

  private Main(int var) {
      this.var = 0<caret>;
  }
}