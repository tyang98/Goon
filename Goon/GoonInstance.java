package Goon;

import java.util.HashMap;
import java.util.Map;

class GoonInstance {
  private GoonClass klass;

  GoonInstance(GoonClass klass) {
    this.klass = klass;
  }

  @Override
  public String toString() {
    return klass.name + " instance";
  }
  
}