package Goon;

import java.util.List;
import java.util.Map;

class GoonClass implements GoonCallable {
  final String name;

  GoonClass(String name) {
    this.name = name;
  }

  @Override
  public Object call(Interpreter interpreter, List<Object> arguments) {
    GoonInstance instance = new GoonInstance(this);
    return instance;
  }

  @Override
  public int arity() {
    return 0;
  }

  @Override
  public String toString() {
    return name;
  }
}