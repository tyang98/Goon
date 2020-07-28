package Goon;

import java.util.List;
import java.util.Map;

class GoonClass implements GoonCallable {
  final String name;
  private final Map<String, GoonFunction> methods;

  GoonClass(String name, Map<String, GoonFunction> methods) {
    this.name = name;
    this.methods = methods;
  }

  @Override
  public Object call(Interpreter interpreter, List<Object> arguments) {
    GoonInstance instance = new GoonInstance(this);
    return instance;
  }

  GoonFunction findMethod(String name) {
    if (methods.containsKey(name)) {
      return methods.get(name);
    }

    return null;
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