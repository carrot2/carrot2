/* Imported from Mahout. */package org.carrot2.mahout.math.matrix.linalg;

import org.carrot2.mahout.math.Matrix;
import org.carrot2.mahout.math.function.Functions;
import org.carrot2.mahout.math.matrix.DoubleMatrix1D;
import org.carrot2.mahout.math.matrix.DoubleMatrix2D;
import org.carrot2.mahout.math.matrix.impl.AbstractMatrix2D;
import org.carrot2.mahout.math.matrix.impl.DenseDoubleMatrix2D;

public final class Property {

  
  public static final Property DEFAULT = new Property(1.0E-9);

  
  public static final Property ZERO = new Property(0.0);

  private final double tolerance;

  
  public Property(double newTolerance) {
    tolerance = Math.abs(newTolerance);
  }

  
  public static void checkRectangular(AbstractMatrix2D a) {
    if (a.rows() < a.columns()) {
      throw new IllegalArgumentException("Matrix must be rectangular");
    }
  }

  
  public static void checkSquare(AbstractMatrix2D a) {
    if (a.rows() != a.columns()) {
      throw new IllegalArgumentException("Matrix must be square");
    }
  }

  public static void checkSquare(Matrix matrix) {
    if(matrix.numRows() != matrix.numCols()) {
      throw new IllegalArgumentException("Matrix must be square");      
    }
  }

  
  public static double density(DoubleMatrix2D a) {
    return a.cardinality() / (double) a.size();
  }

  
  public boolean equals(DoubleMatrix1D a, double value) {
    if (a == null) {
      return false;
    }
    double epsilon = tolerance();
    for (int i = a.size(); --i >= 0;) {
      //if (!(A.getQuick(i) == value)) return false;
      //if (Math.abs(value - A.getQuick(i)) > epsilon) return false;
      double x = a.getQuick(i);
      double diff = Math.abs(value - x);
      if (Double.isNaN(diff) && (Double.isNaN(value) && Double.isNaN(x) || value == x)) {
        diff = 0.0;
      }
      if (diff > epsilon) {
        return false;
      }
    }
    return true;
  }

  
  public boolean equals(DoubleMatrix1D a, DoubleMatrix1D b) {
    if (a == b) {
      return true;
    }
    if (!(a != null && b != null)) {
      return false;
    }
    int size = a.size();
    if (size != b.size()) {
      return false;
    }

    double epsilon = tolerance();
    for (int i = size; --i >= 0;) {
      //if (!(getQuick(i) == B.getQuick(i))) return false;
      //if (Math.abs(A.getQuick(i) - B.getQuick(i)) > epsilon) return false;
      double x = a.getQuick(i);
      double value = b.getQuick(i);
      double diff = Math.abs(value - x);
      if (Double.isNaN(diff) && (Double.isNaN(value) && Double.isNaN(x) || value == x)) {
        diff = 0.0;
      }
      if (diff > epsilon) {
        return false;
      }
    }
    return true;
  }

  
  public boolean equals(DoubleMatrix2D a, double value) {
    if (a == null) {
      return false;
    }
    int rows = a.rows();
    int columns = a.columns();

    double epsilon = tolerance();
    for (int row = rows; --row >= 0;) {
      for (int column = columns; --column >= 0;) {
        //if (!(A.getQuick(row,column) == value)) return false;
        //if (Math.abs(value - A.getQuick(row,column)) > epsilon) return false;
        double x = a.getQuick(row, column);
        double diff = Math.abs(value - x);
        if (Double.isNaN(diff) && (Double.isNaN(value) && Double.isNaN(x) || value == x)) {
          diff = 0.0;
        }
        if (diff > epsilon) {
          return false;
        }
      }
    }
    return true;
  }

  
  public boolean equals(DoubleMatrix2D a, DoubleMatrix2D b) {
    if (a == b) {
      return true;
    }
    if (!(a != null && b != null)) {
      return false;
    }
    int rows = a.rows();
    int columns = a.columns();
    if (columns != b.columns() || rows != b.rows()) {
      return false;
    }

    double epsilon = tolerance();
    for (int row = rows; --row >= 0;) {
      for (int column = columns; --column >= 0;) {
        //if (!(A.getQuick(row,column) == B.getQuick(row,column))) return false;
        //if (Math.abs((A.getQuick(row,column) - B.getQuick(row,column)) > epsilon) return false;
        double x = a.getQuick(row, column);
        double value = b.getQuick(row, column);
        double diff = Math.abs(value - x);
        if (Double.isNaN(diff) && (Double.isNaN(value) && Double.isNaN(x) || value == x)) {
          diff = 0.0;
        }
        if (diff > epsilon) {
          return false;
        }
      }
    }
    return true;
  }

  
  public boolean isDiagonal(DoubleMatrix2D a) {
    double epsilon = tolerance();
    int rows = a.rows();
    int columns = a.columns();
    for (int row = rows; --row >= 0;) {
      for (int column = columns; --column >= 0;) {
        //if (row!=column && A.getQuick(row,column) != 0) return false;
        if (row != column && Math.abs(a.getQuick(row, column)) > epsilon) {
          return false;
        }
      }
    }
    return true;
  }

  
  public static boolean isDiagonallyDominantByColumn(DoubleMatrix2D a) {
    //double epsilon = tolerance();
    int min = Math.min(a.rows(), a.columns());
    for (int i = min; --i >= 0;) {
      double diag = Math.abs(a.getQuick(i, i));
      diag += diag;
      if (diag <= a.viewColumn(i).aggregate(Functions.PLUS, Functions.ABS)) {
        return false;
      }
    }
    return true;
  }

  
  public static boolean isDiagonallyDominantByRow(DoubleMatrix2D a) {
    //double epsilon = tolerance();
    int min = Math.min(a.rows(), a.columns());
    for (int i = min; --i >= 0;) {
      double diag = Math.abs(a.getQuick(i, i));
      diag += diag;
      if (diag <= a.viewRow(i).aggregate(Functions.PLUS, Functions.ABS)) {
        return false;
      }
    }
    return true;
  }

  
  public boolean isIdentity(DoubleMatrix2D a) {
    double epsilon = tolerance();
    int rows = a.rows();
    int columns = a.columns();
    for (int row = rows; --row >= 0;) {
      for (int column = columns; --column >= 0;) {
        double v = a.getQuick(row, column);
        if (row == column) {
          if (Math.abs(1 - v) > epsilon) {
            return false;
          }
        } else if (Math.abs(v) > epsilon) {
          return false;
        }
      }
    }
    return true;
  }

  
  public boolean isLowerBidiagonal(DoubleMatrix2D a) {
    double epsilon = tolerance();
    int rows = a.rows();
    int columns = a.columns();
    for (int row = rows; --row >= 0;) {
      for (int column = columns; --column >= 0;) {
        if (!(row == column || row == column + 1) && Math.abs(a.getQuick(row, column)) > epsilon) {
          return false;
        }
      }
    }
    return true;
  }

  
  public boolean isLowerTriangular(DoubleMatrix2D a) {
    double epsilon = tolerance();
    int rows = a.rows();
    int columns = a.columns();
    for (int column = columns; --column >= 0;) {
      for (int row = Math.min(column, rows); --row >= 0;) {
        //if (A.getQuick(row,column) != 0) return false;
        if (Math.abs(a.getQuick(row, column)) > epsilon) {
          return false;
        }
      }
    }
    return true;
  }

  
  public static boolean isNonNegative(DoubleMatrix2D a) {
    int rows = a.rows();
    int columns = a.columns();
    for (int row = rows; --row >= 0;) {
      for (int column = columns; --column >= 0;) {
        if (a.getQuick(row, column) < 0) {
          return false;
        }
      }
    }
    return true;
  }

  
  public boolean isOrthogonal(DoubleMatrix2D a) {
    checkSquare(a);
    return equals(a.zMult(a, null, 1, 0, false, true),
                  DenseDoubleMatrix2D.identity(a.rows()));
  }

  
  public static boolean isPositive(DoubleMatrix2D a) {
    int rows = a.rows();
    int columns = a.columns();
    for (int row = rows; --row >= 0;) {
      for (int column = columns; --column >= 0;) {
        if (a.getQuick(row, column) <= 0) {
          return false;
        }
      }
    }
    return true;
  }

  
  public boolean isSkewSymmetric(DoubleMatrix2D a) {
    checkSquare(a);
    double epsilon = tolerance();
    int rows = a.rows();
    //int columns = A.columns();
    for (int row = rows; --row >= 0;) {
      for (int column = rows; --column >= 0;) {
        //if (A.getQuick(row,column) != -A.getQuick(column,row)) return false;
        if (Math.abs(a.getQuick(row, column) + a.getQuick(column, row)) > epsilon) {
          return false;
        }
      }
    }
    return true;
  }

  
  public static boolean isSquare(AbstractMatrix2D a) {
    return a.rows() == a.columns();
  }

  
  public boolean isStrictlyLowerTriangular(DoubleMatrix2D a) {
    double epsilon = tolerance();
    int rows = a.rows();
    int columns = a.columns();
    for (int column = columns; --column >= 0;) {
      for (int row = Math.min(rows, column + 1); --row >= 0;) {
        //if (A.getQuick(row,column) != 0) return false;
        if (Math.abs(a.getQuick(row, column)) > epsilon) {
          return false;
        }
      }
    }
    return true;
  }

  
  public boolean isStrictlyTriangular(DoubleMatrix2D a) {
    if (isTriangular(a)) {
      double epsilon = tolerance();
      for (int i = Math.min(a.rows(), a.columns()); --i >= 0;) {
        //if (A.getQuick(i,i) != 0) return false;
        if (Math.abs(a.getQuick(i, i)) > epsilon) {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  }

  
  public boolean isStrictlyUpperTriangular(DoubleMatrix2D a) {
    double epsilon = tolerance();
    int rows = a.rows();
    int columns = a.columns();
    for (int column = columns; --column >= 0;) {
      for (int row = rows; --row >= column;) {
        //if (A.getQuick(row,column) != 0) return false;
        if (Math.abs(a.getQuick(row, column)) > epsilon) {
          return false;
        }
      }
    }
    return true;
  }

  
  public boolean isSymmetric(DoubleMatrix2D a) {
    checkSquare(a);
    return equals(a, a.viewDice());
  }

  
  public boolean isTriangular(DoubleMatrix2D a) {
    return isLowerTriangular(a) || isUpperTriangular(a);
  }

  
  public boolean isTridiagonal(DoubleMatrix2D a) {
    double epsilon = tolerance();
    int rows = a.rows();
    int columns = a.columns();
    for (int row = rows; --row >= 0;) {
      for (int column = columns; --column >= 0;) {
        if (Math.abs(row - column) > 1 && Math.abs(a.getQuick(row, column)) > epsilon) {
          return false;
        }
      }
    }
    return true;
  }

  
  public boolean isUnitTriangular(DoubleMatrix2D a) {
    if (isTriangular(a)) {
      double epsilon = tolerance();
      for (int i = Math.min(a.rows(), a.columns()); --i >= 0;) {
        //if (A.getQuick(i,i) != 1) return false;
        if (Math.abs(1 - a.getQuick(i, i)) > epsilon) {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  }

  
  public boolean isUpperBidiagonal(DoubleMatrix2D a) {
    double epsilon = tolerance();
    int rows = a.rows();
    int columns = a.columns();
    for (int row = rows; --row >= 0;) {
      for (int column = columns; --column >= 0;) {
        if (!(row == column || row == column - 1) && Math.abs(a.getQuick(row, column)) > epsilon) {
          return false;
        }
      }
    }
    return true;
  }

  
  public boolean isUpperTriangular(DoubleMatrix2D a) {
    double epsilon = tolerance();
    int rows = a.rows();
    int columns = a.columns();
    for (int column = columns; --column >= 0;) {
      for (int row = rows; --row > column;) {
        //if (A.getQuick(row,column) != 0) return false;
        if (Math.abs(a.getQuick(row, column)) > epsilon) {
          return false;
        }
      }
    }
    return true;
  }

  
  public boolean isZero(DoubleMatrix2D a) {
    return equals(a, 0);
  }

  
  public int lowerBandwidth(DoubleMatrix2D a) {
    checkSquare(a);
    double epsilon = tolerance();
    int rows = a.rows();

    for (int k = rows; --k >= 0;) {
      for (int i = rows - k; --i >= 0;) {
        int j = i + k;
        //if (A.getQuick(j,i) != 0) return k;
        if (Math.abs(a.getQuick(j, i)) > epsilon) {
          return k;
        }
      }
    }
    return 0;
  }

  public int semiBandwidth(DoubleMatrix2D a) {
    checkSquare(a);
    double epsilon = tolerance();
    int rows = a.rows();

    for (int k = rows; --k >= 0;) {
      for (int i = rows - k; --i >= 0;) {
        int j = i + k;
        //if (A.getQuick(j,i) != 0) return k+1;
        //if (A.getQuick(i,j) != 0) return k+1;
        if (!(Math.abs(a.getQuick(j, i)) <= epsilon)) {
          return k + 1;
        }
        if (Math.abs(a.getQuick(i, j)) > epsilon) {
          return k + 1;
        }
      }
    }
    return 1;
  }

  
  public double tolerance() {
    return tolerance;
  }

  
  public int upperBandwidth(DoubleMatrix2D a) {
    checkSquare(a);
    double epsilon = tolerance();
    int rows = a.rows();

    for (int k = rows; --k >= 0;) {
      for (int i = rows - k; --i >= 0;) {
        int j = i + k;
        //if (A.getQuick(i,j) != 0) return k;
        if (!(Math.abs(a.getQuick(i, j)) <= epsilon)) {
          return k;
        }
      }
    }
    return 0;
  }
}
