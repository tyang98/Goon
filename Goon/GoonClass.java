package Goon;

import java.util.List;
import java.util.Map;

class GoonClass implements GoonCallable {
  final String name;
  final GoonClass superclass;
  private final Map<String, GoonFunction> methods;

  GoonClass(String name, GoonClass superclass, Map<String, GoonFunction> methods) {
    this.name = name;
    this.superclass = superclass;
    this.methods = methods;
  }

  @Override
  public Object call(Interpreter interpreter, List<Object> arguments) {
    GoonInstance instance = new GoonInstance(this);
    GoonFunction initializer = findMethod("init");
    if (initializer != null) {
      initializer.bind(instance).call(interpreter, arguments);
    }
    return instance;
  }

  GoonFunction findMethod(String name) {
    if (methods.containsKey(name)) {
      return methods.get(name);
    }

    if (superclass != null) {
      return superclass.findMethod(name);
    }

    return null;
  }

  @Override
  public int arity() {
    GoonFunction initializer = findMethod("init");
    if (initializer == null)
      return 0;
    return initializer.arity();
  }

  @Override
  public String toString() {
    return name;
  }
}