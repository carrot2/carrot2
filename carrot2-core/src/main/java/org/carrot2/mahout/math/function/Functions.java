/* Imported from Mahout. */package org.carrot2.mahout.math.function;


public final class Functions {

  /*
   * <H3>Unary functions</H3>
   */
  
  public static final DoubleFunction ABS = new DoubleFunction() {
    @Override
    public double apply(double a) {
      return Math.abs(a);
    }
  };

  
  public static final DoubleFunction ACOS = new DoubleFunction() {
    @Override
    public double apply(double a) {
      return Math.acos(a);
    }
  };

  
  public static final DoubleFunction ASIN = new DoubleFunction() {
    @Override
    public double apply(double a) {
      return Math.asin(a);
    }
  };

  
  public static final DoubleFunction ATAN = new DoubleFunction() {
    @Override
    public double apply(double a) {
      return Math.atan(a);
    }
  };

  
  public static final DoubleFunction CEIL = new DoubleFunction() {

    @Override
    public double apply(double a) {
      return Math.ceil(a);
    }
  };

  
  public static final DoubleFunction COS = new DoubleFunction() {

    @Override
    public double apply(double a) {
      return Math.cos(a);
    }
  };

  
  public static final DoubleFunction EXP = new DoubleFunction() {

    @Override
    public double apply(double a) {
      return Math.exp(a);
    }
  };

  
  public static final DoubleFunction FLOOR = new DoubleFunction() {

    @Override
    public double apply(double a) {
      return Math.floor(a);
    }
  };

  
  public static final DoubleFunction IDENTITY = new DoubleFunction() {

    @Override
    public double apply(double a) {
      return a;
    }
  };

  
  public static final DoubleFunction INV = new DoubleFunction() {

    @Override
    public double apply(double a) {
      return 1.0 / a;
    }
  };

  
  public static final DoubleFunction LOGARITHM = new DoubleFunction() {

    @Override
    public double apply(double a) {
      return Math.log(a);
    }
  };

  
  public static final DoubleFunction LOG2 = new DoubleFunction() {

    @Override
    public double apply(double a) {
      return Math.log(a) * 1.4426950408889634;
    }
  };

  
  public static final DoubleFunction NEGATE = new DoubleFunction() {

    @Override
    public double apply(double a) {
      return -a;
    }
  };

  
  public static final DoubleFunction RINT = new DoubleFunction() {

    @Override
    public double apply(double a) {
      return Math.rint(a);
    }
  };

  
  public static final DoubleFunction SIGN = new DoubleFunction() {

    @Override
    public double apply(double a) {
      return a < 0 ? -1 : a > 0 ? 1 : 0;
    }
  };

  
  public static final DoubleFunction SIN = new DoubleFunction() {

    @Override
    public double apply(double a) {
      return Math.sin(a);
    }
  };

  
  public static final DoubleFunction SQRT = new DoubleFunction() {

    @Override
    public double apply(double a) {
      return Math.sqrt(a);
    }
  };

  
  public static final DoubleFunction SQUARE = new DoubleFunction() {

    @Override
    public double apply(double a) {
      return a * a;
    }
  };

  
  public static final DoubleFunction SIGMOID = new DoubleFunction() {
    @Override
    public double apply(double a) {
      return 1.0 / (1.0 + Math.exp(-a));
    }
  };

  
  public static final DoubleFunction SIGMOIDGRADIENT = new DoubleFunction() {
    @Override
    public double apply(double a) {
      return a * (1.0 - a);
    }
  };

  
  public static final DoubleFunction TAN = new DoubleFunction() {

    @Override
    public double apply(double a) {
      return Math.tan(a);
    }
  };


  /*
   * <H3>Binary functions</H3>
   */

  
  public static final DoubleDoubleFunction ATAN2 = new DoubleDoubleFunction() {

    @Override
    public double apply(double a, double b) {
      return Math.atan2(a, b);
    }
  };

  
  public static final DoubleDoubleFunction COMPARE = new DoubleDoubleFunction() {

    @Override
    public double apply(double a, double b) {
      return a < b ? -1 : a > b ? 1 : 0;
    }
  };

  
  public static final DoubleDoubleFunction DIV = new DoubleDoubleFunction() {

    @Override
    public double apply(double a, double b) {
      return a / b;
    }
  };

  
  public static final DoubleDoubleFunction EQUALS = new DoubleDoubleFunction() {

    @Override
    public double apply(double a, double b) {
      return a == b ? 1 : 0;
    }
  };

  
  public static final DoubleDoubleFunction GREATER = new DoubleDoubleFunction() {

    @Override
    public double apply(double a, double b) {
      return a > b ? 1 : 0;
    }
  };

  
  public static final DoubleDoubleFunction IEEE_REMAINDER = new DoubleDoubleFunction() {

    @Override
    public double apply(double a, double b) {
      return Math.IEEEremainder(a, b);
    }
  };


  
  public static final DoubleDoubleFunction LESS = new DoubleDoubleFunction() {

    @Override
    public double apply(double a, double b) {
      return a < b ? 1 : 0;
    }
  };

  
  public static final DoubleDoubleFunction LG = new DoubleDoubleFunction() {

    @Override
    public double apply(double a, double b) {
      return Math.log(a) / Math.log(b);
    }
  };

  
  public static final DoubleDoubleFunction MAX = new DoubleDoubleFunction() {

    @Override
    public double apply(double a, double b) {
      return Math.max(a, b);
    }
  };

  
  public static final DoubleDoubleFunction MIN = new DoubleDoubleFunction() {

    @Override
    public double apply(double a, double b) {
      return Math.min(a, b);
    }
  };

  
  public static final DoubleDoubleFunction MINUS = plusMult(-1);
  /*
  new DoubleDoubleFunction() {
    public final double apply(double a, double b) { return a - b; }
  };
  */

  
  public static final DoubleDoubleFunction MOD = new DoubleDoubleFunction() {

    @Override
    public double apply(double a, double b) {
      return a % b;
    }
  };

  
  public static final DoubleDoubleFunction MULT = new DoubleDoubleFunction() {

    @Override
    public double apply(double a, double b) {
      return a * b;
    }
  };
  
  
  public static final DoubleDoubleFunction PLUS = new DoubleDoubleFunction() {
    
    @Override
    public double apply(double a, double b) {
      return a + b;
    }
  };

  
  public static final DoubleDoubleFunction PLUS_ABS = new DoubleDoubleFunction() {

    @Override
    public double apply(double a, double b) {
      return Math.abs(a) + Math.abs(b);
    }
  };

  
  public static final DoubleDoubleFunction POW = new DoubleDoubleFunction() {

    @Override
    public double apply(double a, double b) {
      return Math.pow(a, b);
    }
  };

  private Functions() {
  }

  
  public static DoubleFunction between(final double from, final double to) {
    return new DoubleFunction() {

      @Override
      public double apply(double a) {
        return from <= a && a <= to ? 1 : 0;
      }
    };
  }

  
  public static DoubleFunction bindArg1(final DoubleDoubleFunction function, final double c) {
    return new DoubleFunction() {

      @Override
      public double apply(double var) {
        return function.apply(c, var);
      }
    };
  }

  
  public static DoubleFunction bindArg2(final DoubleDoubleFunction function, final double c) {
    return new DoubleFunction() {

      @Override
      public double apply(double var) {
        return function.apply(var, c);
      }
    };
  }

  
  public static DoubleDoubleFunction chain(final DoubleDoubleFunction f, final DoubleFunction g,
                                           final DoubleFunction h) {
    return new DoubleDoubleFunction() {

      @Override
      public double apply(double a, double b) {
        return f.apply(g.apply(a), h.apply(b));
      }
    };
  }

  
  public static DoubleDoubleFunction chain(final DoubleFunction g, final DoubleDoubleFunction h) {
    return new DoubleDoubleFunction() {

      @Override
      public double apply(double a, double b) {
        return g.apply(h.apply(a, b));
      }
    };
  }

  
  public static DoubleFunction chain(final DoubleFunction g, final DoubleFunction h) {
    return new DoubleFunction() {

      @Override
      public double apply(double a) {
        return g.apply(h.apply(a));
      }
    };
  }

  
  public static DoubleFunction compare(final double b) {
    return new DoubleFunction() {

      @Override
      public double apply(double a) {
        return a < b ? -1 : a > b ? 1 : 0;
      }
    };
  }

  
  public static DoubleFunction constant(final double c) {
    return new DoubleFunction() {

      @Override
      public double apply(double a) {
        return c;
      }
    };
  }


  
  public static DoubleFunction div(double b) {
    return mult(1 / b);
  }

  
  public static DoubleFunction equals(final double b) {
    return new DoubleFunction() {

      @Override
      public double apply(double a) {
        return a == b ? 1 : 0;
      }
    };
  }

  
  public static DoubleFunction greater(final double b) {
    return new DoubleFunction() {

      @Override
      public double apply(double a) {
        return a > b ? 1 : 0;
      }
    };
  }

  
  public static DoubleFunction mathIEEEremainder(final double b) {
    return new DoubleFunction() {

      @Override
      public double apply(double a) {
        return Math.IEEEremainder(a, b);
      }
    };
  }

  
  public static DoubleFunction less(final double b) {
    return new DoubleFunction() {

      @Override
      public double apply(double a) {
        return a < b ? 1 : 0;
      }
    };
  }

  
  public static DoubleFunction lg(final double b) {
    return new DoubleFunction() {
      private final double logInv = 1 / Math.log(b); // cached for speed


      @Override
      public double apply(double a) {
        return Math.log(a) * logInv;
      }
    };
  }

  
  public static DoubleFunction max(final double b) {
    return new DoubleFunction() {

      @Override
      public double apply(double a) {
        return Math.max(a, b);
      }
    };
  }

  
  public static DoubleFunction min(final double b) {
    return new DoubleFunction() {

      @Override
      public double apply(double a) {
        return Math.min(a, b);
      }
    };
  }

  
  public static DoubleFunction minus(double b) {
    return plus(-b);
  }

  
  public static DoubleDoubleFunction minusMult(double constant) {
    return plusMult(-constant);
  }

  
  public static DoubleFunction mod(final double b) {
    return new DoubleFunction() {

      @Override
      public double apply(double a) {
        return a % b;
      }
    };
  }

  
  public static DoubleFunction mult(double b) {
    return new Mult(b);
    /*
    return new DoubleFunction() {
      public final double apply(double a) { return a * b; }
    };
    */
  }

  
  public static DoubleFunction plus(final double b) {
    return new DoubleFunction() {

      @Override
      public double apply(double a) {
        return a + b;
      }
    };
  }

  
  public static DoubleDoubleFunction plusMult(double constant) {
    return new PlusMult(constant);
    /*
    return new DoubleDoubleFunction() {
      public final double apply(double a, double b) { return a + b*constant; }
    };
    */
  }

  
  public static DoubleFunction pow(final double b) {
    return new DoubleFunction() {

      @Override
      public double apply(double a) {
        return Math.pow(a, b);
      }
    };
  }

  
  public static DoubleFunction round(final double precision) {
    return new DoubleFunction() {
      @Override
      public double apply(double a) {
        return Math.rint(a / precision) * precision;
      }
    };
  }

  
  public static DoubleDoubleFunction swapArgs(final DoubleDoubleFunction function) {
    return new DoubleDoubleFunction() {
      @Override
      public double apply(double a, double b) {
        return function.apply(b, a);
      }
    };
  }
}
