package Goon;

import java.util.List;

interface GoonCallable {
  int arity();
  Object call(Interpreter interpreter, List<Object> arguments);
}

