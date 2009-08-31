/*
 * Copyright (C) 2003, 2004 Bjorn-Ove Heimsund
 * 
 * This file is part of NNI.
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package nni;

/**
 * Native BLAS. The same calling conventions as the
 * <a href="http://www.netlib.org/blas">CBLAS</a>. Note that the methods
 * using enumerations to indicate matrix type now uses class integer constants.
 */
public class BLAS {

	private BLAS() {
		// No need to create an instance
	}

	static {
		System.loadLibrary("nni_blas");
	}

	/**
	 * A dummy method which can be called to load the library
	 */
	public static void init() {
		// Once this is called, the static clause is called
	}

	/**
	 * Order enumeration
	 */
	public final static int RowMajor = 101, ColMajor = 102;

	/**
	 * Transpose enumeration
	 */
	public final static int NoTrans = 111, Trans = 112;

	/**
	 * Upper/lower enumeration
	 */
	public final static int Upper = 121, Lower = 122;

	/**
	 * Diagonal enumeration
	 */
	public final static int NonUnit = 131, Unit = 132;

	/**
	 * Side enumeration
	 */
	public final static int Left = 141, Right = 142;

	public static native double dot(
		int N,
		double[] X,
		int incX,
		double[] Y,
		int incY);

	public static native double nrm2(int N, double[] X, int incX);

	public static native double asum(int N, double[] X, int incX);

	public static native int idamax(int N, double[] X, int incX);

	public static native void swap(
		int N,
		double[] X,
		int incX,
		double[] Y,
		int incY);

	public static native void copy(
		int N,
		double[] X,
		int incX,
		double[] Y,
		int incY);

	public static native void axpy(
		int N,
		double alpha,
		double[] X,
		int incX,
		double[] Y,
		int incY);

	public static native void rotg(
		double[] a,
		double[] b,
		double[] c,
		double[] s);

	public static native void rotmg(
		double[] d1,
		double[] d2,
		double[] b1,
		double b2,
		double[] P);

	public static native void rot(
		int N,
		double[] X,
		int incX,
		double[] Y,
		int incY,
		double c,
		double s);

	public static native void rotm(
		int N,
		double[] X,
		int incX,
		double[] Y,
		int incY,
		double[] P);

	public static native void scal(int N, double alpha, double[] X, int incX);

	public static native void gemv(
		int order,
		int TransA,
		int M,
		int N,
		double alpha,
		double[] A,
		int lda,
		double[] X,
		int incX,
		double beta,
		double[] Y,
		int incY);

	public static native void gbmv(
		int order,
		int TransA,
		int M,
		int N,
		int KL,
		int KU,
		double alpha,
		double[] A,
		int lda,
		double[] X,
		int incX,
		double beta,
		double[] Y,
		int incY);

	public static native void trmv(
		int order,
		int Uplo,
		int TransA,
		int Diag,
		int N,
		double[] A,
		int lda,
		double[] X,
		int incX);

	public static native void tbmv(
		int order,
		int Uplo,
		int TransA,
		int Diag,
		int N,
		int K,
		double[] A,
		int lda,
		double[] X,
		int incX);

	public static native void tpmv(
		int order,
		int Uplo,
		int TransA,
		int Diag,
		int N,
		double[] Ap,
		double[] X,
		int incX);

	public static native void trsv(
		int order,
		int Uplo,
		int TransA,
		int Diag,
		int N,
		double[] A,
		int lda,
		double[] X,
		int incX);

	public static native void tbsv(
		int order,
		int Uplo,
		int TransA,
		int Diag,
		int N,
		int K,
		double[] A,
		int lda,
		double[] X,
		int incX);

	public static native void tpsv(
		int order,
		int Uplo,
		int TransA,
		int Diag,
		int N,
		double[] Ap,
		double[] X,
		int incX);

	public static native void symv(
		int order,
		int Uplo,
		int N,
		double alpha,
		double[] A,
		int lda,
		double[] X,
		int incX,
		double beta,
		double[] Y,
		int incY);

	public static native void sbmv(
		int order,
		int Uplo,
		int N,
		int K,
		double alpha,
		double[] A,
		int lda,
		double[] X,
		int incX,
		double beta,
		double[] Y,
		int incY);

	public static native void spmv(
		int order,
		int Uplo,
		int N,
		double alpha,
		double[] Ap,
		double[] X,
		int incX,
		double beta,
		double[] Y,
		int incY);

	public static native void ger(
		int order,
		int M,
		int N,
		double alpha,
		double[] X,
		int incX,
		double[] Y,
		int incY,
		double[] A,
		int lda);

	public static native void syr(
		int order,
		int Uplo,
		int N,
		double alpha,
		double[] X,
		int incX,
		double[] A,
		int lda);

	public static native void spr(
		int order,
		int Uplo,
		int N,
		double alpha,
		double[] X,
		int incX,
		double[] Ap);

	public static native void syr2(
		int order,
		int Uplo,
		int N,
		double alpha,
		double[] X,
		int incX,
		double[] Y,
		int incY,
		double[] A,
		int lda);

	public static native void spr2(
		int order,
		int Uplo,
		int N,
		double alpha,
		double[] X,
		int incX,
		double[] Y,
		int incY,
		double[] Ap);

	public static native void gemm(
		int Order,
		int TransA,
		int TransB,
		int M,
		int N,
		int K,
		double alpha,
		double[] A,
		int lda,
		double[] B,
		int ldb,
		double beta,
		double[] C,
		int ldc);

	public static native void symm(
		int Order,
		int Side,
		int Uplo,
		int M,
		int N,
		double alpha,
		double[] A,
		int lda,
		double[] B,
		int ldb,
		double beta,
		double[] C,
		int ldc);

	public static native void syrk(
		int Order,
		int Uplo,
		int Trans,
		int N,
		int K,
		double alpha,
		double[] A,
		int lda,
		double beta,
		double[] C,
		int ldc);

	public static native void syr2k(
		int Order,
		int Uplo,
		int Trans,
		int N,
		int K,
		double alpha,
		double[] A,
		int lda,
		double[] B,
		int ldb,
		double beta,
		double[] C,
		int ldc);

	public static native void trmm(
		int Order,
		int Side,
		int Uplo,
		int TransA,
		int Diag,
		int M,
		int N,
		double alpha,
		double[] A,
		int lda,
		double[] B,
		int ldb);

	public static native void trsm(
		int Order,
		int Side,
		int Uplo,
		int TransA,
		int Diag,
		int M,
		int N,
		double alpha,
		double[] A,
		int lda,
		double[] B,
		int ldb);

}
