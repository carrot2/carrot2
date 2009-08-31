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
 * Native LAPACK. Has the same calling conventions as the
 * <a href="http://www.netlib.org/clapack">CLAPACK</a>.
 * <p>
 * Full documentation is available at the
 * <a href="http://www.netlib.org/lapack">LAPACK</a> site.
 */
public class LAPACK {

	private LAPACK() {
		// No need to create an instance
	}

	static {
		System.loadLibrary("nni_lapack");
	}

	/**
	 * A dummy method which can be called to load the library
	 */
	public static void init() {
		// Once this is called, the static clause is called
	}

	public static native double lamch(char[] cmach);

	public static native int laenv(
		int ispec,
		char[] name,
		char[] opts,
		int n1,
		int n2,
		int n3,
		int n4);

	public static native void bdsdc(
		char[] uplo,
		char[] compq,
		int[] n,
		double[] d,
		double[] e,
		double[] u,
		int[] ldu,
		double[] vt,
		int[] ldvt,
		double[] q,
		int[] iq,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void bdsqr(
		char[] uplo,
		int[] n,
		int[] ncvt,
		int[] nru,
		int[] ncc,
		double[] d,
		double[] e,
		double[] vt,
		int[] ldvt,
		double[] u,
		int[] ldu,
		double[] c,
		int[] ldc,
		double[] work,
		int[] info);

	public static native void disna(
		char[] job,
		int[] m,
		int[] n,
		double[] d,
		double[] sep,
		int[] info);

	public static native void gbbrd(
		char[] vect,
		int[] m,
		int[] n,
		int[] ncc,
		int[] kl,
		int[] ku,
		double[] ab,
		int[] ldab,
		double[] d,
		double[] e,
		double[] q,
		int[] ldq,
		double[] pt,
		int[] ldpt,
		double[] c,
		int[] ldc,
		double[] work,
		int[] info);

	public static native void gbcon(
		char[] norm,
		int[] n,
		int[] kl,
		int[] ku,
		double[] ab,
		int[] ldab,
		int[] ipiv,
		double[] anorm,
		double[] rcond,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void gbequ(
		int[] m,
		int[] n,
		int[] kl,
		int[] ku,
		double[] ab,
		int[] ldab,
		double[] r,
		double[] c,
		double[] rowcnd,
		double[] colcnd,
		double[] amax,
		int[] info);

	public static native void gbrfs(
		char[] trans,
		int[] n,
		int[] kl,
		int[] ku,
		int[] nrhs,
		double[] ab,
		int[] ldab,
		double[] afb,
		int[] ldafb,
		int[] ipiv,
		double[] b,
		int[] ldb,
		double[] x,
		int[] ldx,
		double[] ferr,
		double[] berr,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void gbsv(
		int[] n,
		int[] kl,
		int[] ku,
		int[] nrhs,
		double[] ab,
		int[] ldab,
		int[] ipiv,
		double[] b,
		int[] ldb,
		int[] info);

	public static native void gbsvx(
		char[] fact,
		char[] trans,
		int[] n,
		int[] kl,
		int[] ku,
		int[] nrhs,
		double[] ab,
		int[] ldab,
		double[] afb,
		int[] ldafb,
		int[] ipiv,
		char[] equed,
		double[] r,
		double[] c,
		double[] b,
		int[] ldb,
		double[] x,
		int[] ldx,
		double[] rcond,
		double[] ferr,
		double[] berr,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void gbtf2(
		int[] m,
		int[] n,
		int[] kl,
		int[] ku,
		double[] ab,
		int[] ldab,
		int[] ipiv,
		int[] info);

	public static native void gbtrf(
		int[] m,
		int[] n,
		int[] kl,
		int[] ku,
		double[] ab,
		int[] ldab,
		int[] ipiv,
		int[] info);

	public static native void gbtrs(
		char[] trans,
		int[] n,
		int[] kl,
		int[] ku,
		int[] nrhs,
		double[] ab,
		int[] ldab,
		int[] ipiv,
		double[] b,
		int[] ldb,
		int[] info);

	public static native void gebak(
		char[] job,
		char[] side,
		int[] n,
		int[] ilo,
		int[] ihi,
		double[] scale,
		int[] m,
		double[] v,
		int[] ldv,
		int[] info);

	public static native void gebal(
		char[] job,
		int[] n,
		double[] a,
		int[] lda,
		int[] ilo,
		int[] ihi,
		double[] scale,
		int[] info);

	public static native void gebd2(
		int[] m,
		int[] n,
		double[] a,
		int[] lda,
		double[] d,
		double[] e,
		double[] tauq,
		double[] taup,
		double[] work,
		int[] info);

	public static native void gebrd(
		int[] m,
		int[] n,
		double[] a,
		int[] lda,
		double[] d,
		double[] e,
		double[] tauq,
		double[] taup,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void gecon(
		char[] norm,
		int[] n,
		double[] a,
		int[] lda,
		double[] anorm,
		double[] rcond,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void geequ(
		int[] m,
		int[] n,
		double[] a,
		int[] lda,
		double[] r,
		double[] c,
		double[] rowcnd,
		double[] colcnd,
		double[] amax,
		int[] info);

	public static native void gees(
		char[] jobvs,
		char[] sort,
		Object select,
		int[] n,
		double[] a,
		int[] lda,
		int[] sdim,
		double[] wr,
		double[] wi,
		double[] vs,
		int[] ldvs,
		double[] work,
		int[] lwork,
		boolean[] bwork,
		int[] info);

	public static native void geesx(
		char[] jobvs,
		char[] sort,
		Object select,
		char[] sense,
		int[] n,
		double[] a,
		int[] lda,
		int[] sdim,
		double[] wr,
		double[] wi,
		double[] vs,
		int[] ldvs,
		double[] rconde,
		double[] rcondv,
		double[] work,
		int[] lwork,
		int[] iwork,
		int[] liwork,
		boolean[] bwork,
		int[] info);

	public static native void geev(
		char[] jobvl,
		char[] jobvr,
		int[] n,
		double[] a,
		int[] lda,
		double[] wr,
		double[] wi,
		double[] vl,
		int[] ldvl,
		double[] vr,
		int[] ldvr,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void geevx(
		char[] balanc,
		char[] jobvl,
		char[] jobvr,
		char[] sense,
		int[] n,
		double[] a,
		int[] lda,
		double[] wr,
		double[] wi,
		double[] vl,
		int[] ldvl,
		double[] vr,
		int[] ldvr,
		int[] ilo,
		int[] ihi,
		double[] scale,
		double[] abnrm,
		double[] rconde,
		double[] rcondv,
		double[] work,
		int[] lwork,
		int[] iwork,
		int[] info);

	public static native void gegs(
		char[] jobvsl,
		char[] jobvsr,
		int[] n,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] alphar,
		double[] alphai,
		double[] beta,
		double[] vsl,
		int[] ldvsl,
		double[] vsr,
		int[] ldvsr,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void gegv(
		char[] jobvl,
		char[] jobvr,
		int[] n,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] alphar,
		double[] alphai,
		double[] beta,
		double[] vl,
		int[] ldvl,
		double[] vr,
		int[] ldvr,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void gehd2(
		int[] n,
		int[] ilo,
		int[] ihi,
		double[] a,
		int[] lda,
		double[] tau,
		double[] work,
		int[] info);

	public static native void gehrd(
		int[] n,
		int[] ilo,
		int[] ihi,
		double[] a,
		int[] lda,
		double[] tau,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void gelq2(
		int[] m,
		int[] n,
		double[] a,
		int[] lda,
		double[] tau,
		double[] work,
		int[] info);

	public static native void gelqf(
		int[] m,
		int[] n,
		double[] a,
		int[] lda,
		double[] tau,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void gels(
		char[] trans,
		int[] m,
		int[] n,
		int[] nrhs,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void gelsd(
		int[] m,
		int[] n,
		int[] nrhs,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] s,
		double[] rcond,
		int[] rank,
		double[] work,
		int[] lwork,
		int[] iwork,
		int[] info);

	public static native void gelss(
		int[] m,
		int[] n,
		int[] nrhs,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] s,
		double[] rcond,
		int[] rank,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void gelsx(
		int[] m,
		int[] n,
		int[] nrhs,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		int[] jpvt,
		double[] rcond,
		int[] rank,
		double[] work,
		int[] info);

	public static native void gelsy(
		int[] m,
		int[] n,
		int[] nrhs,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		int[] jpvt,
		double[] rcond,
		int[] rank,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void geql2(
		int[] m,
		int[] n,
		double[] a,
		int[] lda,
		double[] tau,
		double[] work,
		int[] info);

	public static native void geqlf(
		int[] m,
		int[] n,
		double[] a,
		int[] lda,
		double[] tau,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void geqp3(
		int[] m,
		int[] n,
		double[] a,
		int[] lda,
		int[] jpvt,
		double[] tau,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void geqpf(
		int[] m,
		int[] n,
		double[] a,
		int[] lda,
		int[] jpvt,
		double[] tau,
		double[] work,
		int[] info);

	public static native void geqr2(
		int[] m,
		int[] n,
		double[] a,
		int[] lda,
		double[] tau,
		double[] work,
		int[] info);

	public static native void geqrf(
		int[] m,
		int[] n,
		double[] a,
		int[] lda,
		double[] tau,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void gerfs(
		char[] trans,
		int[] n,
		int[] nrhs,
		double[] a,
		int[] lda,
		double[] af,
		int[] ldaf,
		int[] ipiv,
		double[] b,
		int[] ldb,
		double[] x,
		int[] ldx,
		double[] ferr,
		double[] berr,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void gerq2(
		int[] m,
		int[] n,
		double[] a,
		int[] lda,
		double[] tau,
		double[] work,
		int[] info);

	public static native void gerqf(
		int[] m,
		int[] n,
		double[] a,
		int[] lda,
		double[] tau,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void gesc2(
		int[] n,
		double[] a,
		int[] lda,
		double[] rhs,
		int[] ipiv,
		int[] jpiv,
		double[] scale);

	public static native void gesdd(
		char[] jobz,
		int[] m,
		int[] n,
		double[] a,
		int[] lda,
		double[] s,
		double[] u,
		int[] ldu,
		double[] vt,
		int[] ldvt,
		double[] work,
		int[] lwork,
		int[] iwork,
		int[] info);

	public static native void gesv(
		int[] n,
		int[] nrhs,
		double[] a,
		int[] lda,
		int[] ipiv,
		double[] b,
		int[] ldb,
		int[] info);

	public static native void gesvd(
		char[] jobu,
		char[] jobvt,
		int[] m,
		int[] n,
		double[] a,
		int[] lda,
		double[] s,
		double[] u,
		int[] ldu,
		double[] vt,
		int[] ldvt,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void gesvx(
		char[] fact,
		char[] trans,
		int[] n,
		int[] nrhs,
		double[] a,
		int[] lda,
		double[] af,
		int[] ldaf,
		int[] ipiv,
		char[] equed,
		double[] r,
		double[] c,
		double[] b,
		int[] ldb,
		double[] x,
		int[] ldx,
		double[] rcond,
		double[] ferr,
		double[] berr,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void getc2(
		int[] n,
		double[] a,
		int[] lda,
		int[] ipiv,
		int[] jpiv,
		int[] info);

	public static native void getf2(
		int[] m,
		int[] n,
		double[] a,
		int[] lda,
		int[] ipiv,
		int[] info);

	public static native void getrf(
		int[] m,
		int[] n,
		double[] a,
		int[] lda,
		int[] ipiv,
		int[] info);

	public static native void getri(
		int[] n,
		double[] a,
		int[] lda,
		int[] ipiv,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void getrs(
		char[] trans,
		int[] n,
		int[] nrhs,
		double[] a,
		int[] lda,
		int[] ipiv,
		double[] b,
		int[] ldb,
		int[] info);

	public static native void ggbak(
		char[] job,
		char[] side,
		int[] n,
		int[] ilo,
		int[] ihi,
		double[] lscale,
		double[] rscale,
		int[] m,
		double[] v,
		int[] ldv,
		int[] info);

	public static native void ggbal(
		char[] job,
		int[] n,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		int[] ilo,
		int[] ihi,
		double[] lscale,
		double[] rscale,
		double[] work,
		int[] info);

	public static native void gges(
		char[] jobvsl,
		char[] jobvsr,
		char[] sort,
		Object delztg,
		int[] n,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		int[] sdim,
		double[] alphar,
		double[] alphai,
		double[] beta,
		double[] vsl,
		int[] ldvsl,
		double[] vsr,
		int[] ldvsr,
		double[] work,
		int[] lwork,
		boolean[] bwork,
		int[] info);

	public static native void ggesx(
		char[] jobvsl,
		char[] jobvsr,
		char[] sort,
		Object delztg,
		char[] sense,
		int[] n,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		int[] sdim,
		double[] alphar,
		double[] alphai,
		double[] beta,
		double[] vsl,
		int[] ldvsl,
		double[] vsr,
		int[] ldvsr,
		double[] rconde,
		double[] rcondv,
		double[] work,
		int[] lwork,
		int[] iwork,
		int[] liwork,
		boolean[] bwork,
		int[] info);

	public static native void ggev(
		char[] jobvl,
		char[] jobvr,
		int[] n,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] alphar,
		double[] alphai,
		double[] beta,
		double[] vl,
		int[] ldvl,
		double[] vr,
		int[] ldvr,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void ggevx(
		char[] balanc,
		char[] jobvl,
		char[] jobvr,
		char[] sense,
		int[] n,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] alphar,
		double[] alphai,
		double[] beta,
		double[] vl,
		int[] ldvl,
		double[] vr,
		int[] ldvr,
		int[] ilo,
		int[] ihi,
		double[] lscale,
		double[] rscale,
		double[] abnrm,
		double[] bbnrm,
		double[] rconde,
		double[] rcondv,
		double[] work,
		int[] lwork,
		int[] iwork,
		boolean[] bwork,
		int[] info);

	public static native void ggglm(
		int[] n,
		int[] m,
		int[] p,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] d,
		double[] x,
		double[] y,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void gghrd(
		char[] compq,
		char[] compz,
		int[] n,
		int[] ilo,
		int[] ihi,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] q,
		int[] ldq,
		double[] z,
		int[] ldz,
		int[] info);

	public static native void gglse(
		int[] m,
		int[] n,
		int[] p,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] c,
		double[] d,
		double[] x,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void ggqrf(
		int[] n,
		int[] m,
		int[] p,
		double[] a,
		int[] lda,
		double[] taua,
		double[] b,
		int[] ldb,
		double[] taub,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void ggrqf(
		int[] m,
		int[] p,
		int[] n,
		double[] a,
		int[] lda,
		double[] taua,
		double[] b,
		int[] ldb,
		double[] taub,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void ggsvd(
		char[] jobu,
		char[] jobv,
		char[] jobq,
		int[] m,
		int[] n,
		int[] p,
		int[] k,
		int[] l,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] alpha,
		double[] beta,
		double[] u,
		int[] ldu,
		double[] v,
		int[] ldv,
		double[] q,
		int[] ldq,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void ggsvp(
		char[] jobu,
		char[] jobv,
		char[] jobq,
		int[] m,
		int[] p,
		int[] n,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] tola,
		double[] tolb,
		int[] k,
		int[] l,
		double[] u,
		int[] ldu,
		double[] v,
		int[] ldv,
		double[] q,
		int[] ldq,
		int[] iwork,
		double[] tau,
		double[] work,
		int[] info);

	public static native void gtcon(
		char[] norm,
		int[] n,
		double[] dl,
		double[] d,
		double[] du,
		double[] du2,
		int[] ipiv,
		double[] anorm,
		double[] rcond,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void gtrfs(
		char[] trans,
		int[] n,
		int[] nrhs,
		double[] dl,
		double[] d,
		double[] du,
		double[] dlf,
		double[] df,
		double[] duf,
		double[] du2,
		int[] ipiv,
		double[] b,
		int[] ldb,
		double[] x,
		int[] ldx,
		double[] ferr,
		double[] berr,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void gtsv(
		int[] n,
		int[] nrhs,
		double[] dl,
		double[] d,
		double[] du,
		double[] b,
		int[] ldb,
		int[] info);

	public static native void gtsvx(
		char[] fact,
		char[] trans,
		int[] n,
		int[] nrhs,
		double[] dl,
		double[] d,
		double[] du,
		double[] dlf,
		double[] df,
		double[] duf,
		double[] du2,
		int[] ipiv,
		double[] b,
		int[] ldb,
		double[] x,
		int[] ldx,
		double[] rcond,
		double[] ferr,
		double[] berr,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void gttrf(
		int[] n,
		double[] dl,
		double[] d,
		double[] du,
		double[] du2,
		int[] ipiv,
		int[] info);

	public static native void gttrs(
		char[] trans,
		int[] n,
		int[] nrhs,
		double[] dl,
		double[] d,
		double[] du,
		double[] du2,
		int[] ipiv,
		double[] b,
		int[] ldb,
		int[] info);

	public static native void gtts2(
		int[] itrans,
		int[] n,
		int[] nrhs,
		double[] dl,
		double[] d,
		double[] du,
		double[] du2,
		int[] ipiv,
		double[] b,
		int[] ldb);

	public static native void hgeqz(
		char[] job,
		char[] compq,
		char[] compz,
		int[] n,
		int[] ilo,
		int[] ihi,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] alphar,
		double[] alphai,
		double[] beta,
		double[] q,
		int[] ldq,
		double[] z,
		int[] ldz,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void hsein(
		char[] side,
		char[] eigsrc,
		char[] initv,
		boolean[] select,
		int[] n,
		double[] h,
		int[] ldh,
		double[] wr,
		double[] wi,
		double[] vl,
		int[] ldvl,
		double[] vr,
		int[] ldvr,
		int[] mm,
		int[] m,
		double[] work,
		int[] ifaill,
		int[] ifailr,
		int[] info);

	public static native void hseqr(
		char[] job,
		char[] compz,
		int[] n,
		int[] ilo,
		int[] ihi,
		double[] h,
		int[] ldh,
		double[] wr,
		double[] wi,
		double[] z,
		int[] ldz,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void labad(double[] small, double[] large);

	public static native void labrd(
		int[] m,
		int[] n,
		int[] nb,
		double[] a,
		int[] lda,
		double[] d,
		double[] e,
		double[] tauq,
		double[] taup,
		double[] x,
		int[] ldx,
		double[] y,
		int[] ldy);

	public static native void lacon(
		int[] n,
		double[] v,
		double[] x,
		int[] isgn,
		double[] est,
		int[] kase);

	public static native void lacpy(
		char[] uplo,
		int[] m,
		int[] n,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb);

	public static native void ladiv(
		double[] a,
		double[] b,
		double[] c,
		double[] d,
		double[] p,
		double[] q);

	public static native void lae2(
		double[] a,
		double[] b,
		double[] c,
		double[] rt1,
		double[] rt2);

	public static native void laebz(
		int[] ijob,
		int[] nitmax,
		int[] n,
		int[] mmax,
		int[] minp,
		int[] nbmin,
		double[] abstol,
		double[] reltol,
		double[] pivmin,
		double[] d,
		double[] e,
		double[] e2,
		int[] nval,
		double[] ab,
		double[] c,
		int[] mout,
		int[] nab,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void laed0(
		int[] icompq,
		int[] qsiz,
		int[] n,
		double[] d,
		double[] e,
		double[] q,
		int[] ldq,
		double[] qstore,
		int[] ldqs,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void laed1(
		int[] n,
		double[] d,
		double[] q,
		int[] ldq,
		int[] indxq,
		double[] rho,
		int[] cutpnt,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void laed2(
		int[] k,
		int[] n,
		int[] n1,
		double[] d,
		double[] q,
		int[] ldq,
		int[] indxq,
		double[] rho,
		double[] z,
		double[] dlamda,
		double[] w,
		double[] q2,
		int[] indx,
		int[] indxc,
		int[] indxp,
		int[] coltyp,
		int[] info);

	public static native void laed3(
		int[] k,
		int[] n,
		int[] n1,
		double[] d,
		double[] q,
		int[] ldq,
		double[] rho,
		double[] dlamda,
		double[] q2,
		int[] indx,
		int[] ctot,
		double[] w,
		double[] s,
		int[] info);

	public static native void laed4(
		int[] n,
		int[] i,
		double[] d,
		double[] z,
		double[] delta,
		double[] rho,
		double[] dlam,
		int[] info);

	public static native void laed5(
		int[] i,
		double[] d,
		double[] z,
		double[] delta,
		double[] rho,
		double[] dlam);

	public static native void laed6(
		int[] kniter,
		boolean[] orgati,
		double[] rho,
		double[] d,
		double[] z,
		double[] finit,
		double[] tau,
		int[] info);

	public static native void laed7(
		int[] icompq,
		int[] n,
		int[] qsiz,
		int[] tlvls,
		int[] curlvl,
		int[] curpbm,
		double[] d,
		double[] q,
		int[] ldq,
		int[] indxq,
		double[] rho,
		int[] cutpnt,
		double[] qstore,
		int[] qptr,
		int[] prmptr,
		int[] perm,
		int[] givptr,
		int[] givcol,
		double[] givnum,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void laed8(
		int[] icompq,
		int[] k,
		int[] n,
		int[] qsiz,
		double[] d,
		double[] q,
		int[] ldq,
		int[] indxq,
		double[] rho,
		int[] cutpnt,
		double[] z,
		double[] dlamda,
		double[] q2,
		int[] ldq2,
		double[] w,
		int[] perm,
		int[] givptr,
		int[] givcol,
		double[] givnum,
		int[] indxp,
		int[] indx,
		int[] info);

	public static native void laed9(
		int[] k,
		int[] kstart,
		int[] kstop,
		int[] n,
		double[] d,
		double[] q,
		int[] ldq,
		double[] rho,
		double[] dlamda,
		double[] w,
		double[] s,
		int[] lds,
		int[] info);

	public static native void laeda(
		int[] n,
		int[] tlvls,
		int[] curlvl,
		int[] curpbm,
		int[] prmptr,
		int[] perm,
		int[] givptr,
		int[] givcol,
		double[] givnum,
		double[] q,
		int[] qptr,
		double[] z,
		double[] ztemp,
		int[] info);

	public static native void laein(
		boolean[] rightv,
		boolean[] noinit,
		int[] n,
		double[] h,
		int[] ldh,
		double[] wr,
		double[] wi,
		double[] vr,
		double[] vi,
		double[] b,
		int[] ldb,
		double[] work,
		double[] eps3,
		double[] smlnum,
		double[] bignum,
		int[] info);

	public static native void laev2(
		double[] a,
		double[] b,
		double[] c,
		double[] rt1,
		double[] rt2,
		double[] cs1,
		double[] sn1);

	public static native void laexc(
		boolean[] wantq,
		int[] n,
		double[] t,
		int[] ldt,
		double[] q,
		int[] ldq,
		int[] j1,
		int[] n1,
		int[] n2,
		double[] work,
		int[] info);

	public static native void lag2(
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] safmin,
		double[] scale1,
		double[] scale2,
		double[] wr1,
		double[] wr2,
		double[] wi);

	public static native void lags2(
		boolean[] upper,
		double[] a1,
		double[] a2,
		double[] a3,
		double[] b1,
		double[] b2,
		double[] b3,
		double[] csu,
		double[] snu,
		double[] csv,
		double[] snv,
		double[] csq,
		double[] snq);

	public static native void lagtf(
		int[] n,
		double[] a,
		double[] lambda,
		double[] b,
		double[] c,
		double[] tol,
		double[] d,
		int[] in,
		int[] info);

	public static native void lagtm(
		char[] trans,
		int[] n,
		int[] nrhs,
		double[] alpha,
		double[] dl,
		double[] d,
		double[] du,
		double[] x,
		int[] ldx,
		double[] beta,
		double[] b,
		int[] ldb);

	public static native void lagts(
		int[] job,
		int[] n,
		double[] a,
		double[] b,
		double[] c,
		double[] d,
		int[] in,
		double[] y,
		double[] tol,
		int[] info);

	public static native void lagv2(
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] alphar,
		double[] alphai,
		double[] beta,
		double[] csl,
		double[] snl,
		double[] csr,
		double[] snr);

	public static native void lahqr(
		boolean[] wantt,
		boolean[] wantz,
		int[] n,
		int[] ilo,
		int[] ihi,
		double[] h,
		int[] ldh,
		double[] wr,
		double[] wi,
		int[] iloz,
		int[] ihiz,
		double[] z,
		int[] ldz,
		int[] info);

	public static native void lahrd(
		int[] n,
		int[] k,
		int[] nb,
		double[] a,
		int[] lda,
		double[] tau,
		double[] t,
		int[] ldt,
		double[] y,
		int[] ldy);

	public static native void laic1(
		int[] job,
		int[] j,
		double[] x,
		double[] sest,
		double[] w,
		double[] gamma,
		double[] sestpr,
		double[] s,
		double[] c);

	public static native void laln2(
		boolean[] ltrans,
		int[] na,
		int[] nw,
		double[] smin,
		double[] ca,
		double[] a,
		int[] lda,
		double[] d1,
		double[] d2,
		double[] b,
		int[] ldb,
		double[] wr,
		double[] wi,
		double[] x,
		int[] ldx,
		double[] scale,
		double[] xnorm,
		int[] info);

	public static native void lals0(
		int[] icompq,
		int[] nl,
		int[] nr,
		int[] sqre,
		int[] nrhs,
		double[] b,
		int[] ldb,
		double[] bx,
		int[] ldbx,
		int[] perm,
		int[] givptr,
		int[] givcol,
		int[] ldgcol,
		double[] givnum,
		int[] ldgnum,
		double[] poles,
		double[] difl,
		double[] difr,
		double[] z,
		int[] k,
		double[] c,
		double[] s,
		double[] work,
		int[] info);

	public static native void lalsa(
		int[] icompq,
		int[] smlsiz,
		int[] n,
		int[] nrhs,
		double[] b,
		int[] ldb,
		double[] bx,
		int[] ldbx,
		double[] u,
		int[] ldu,
		double[] vt,
		int[] k,
		double[] difl,
		double[] difr,
		double[] z,
		double[] poles,
		int[] givptr,
		int[] givcol,
		int[] ldgcol,
		int[] perm,
		double[] givnum,
		double[] c,
		double[] s,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void lalsd(
		char[] uplo,
		int[] smlsiz,
		int[] n,
		int[] nrhs,
		double[] d,
		double[] e,
		double[] b,
		int[] ldb,
		double[] rcond,
		int[] rank,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void lamc1(
		int[] beta,
		int[] t,
		boolean[] rnd,
		boolean[] ieee1);

	public static native void lamc2(
		int[] beta,
		int[] t,
		boolean[] rnd,
		double[] eps,
		int[] emin,
		double[] rmin,
		int[] emax,
		double[] rmax);

	public static native void lamc4(int[] emin, double[] start, int[] base);

	public static native void lamc5(
		int[] beta,
		int[] p,
		int[] emin,
		boolean[] ieee,
		int[] emax,
		double[] rmax);

	public static native void lamrg(
		int[] n1,
		int[] n2,
		double[] a,
		int[] dtrd1,
		int[] dtrd2,
		int[] index);

	public static native void lanv2(
		double[] a,
		double[] b,
		double[] c,
		double[] d,
		double[] rt1r,
		double[] rt1i,
		double[] rt2r,
		double[] rt2i,
		double[] cs,
		double[] sn);

	public static native void lapll(
		int[] n,
		double[] x,
		int[] incx,
		double[] y,
		int[] incy,
		double[] ssmin);

	public static native void lapmt(
		boolean[] forwrd,
		int[] m,
		int[] n,
		double[] x,
		int[] ldx,
		int[] k);

	public static native void laqgb(
		int[] m,
		int[] n,
		int[] kl,
		int[] ku,
		double[] ab,
		int[] ldab,
		double[] r,
		double[] c,
		double[] rowcnd,
		double[] colcnd,
		double[] amax,
		char[] equed);

	public static native void laqge(
		int[] m,
		int[] n,
		double[] a,
		int[] lda,
		double[] r,
		double[] c,
		double[] rowcnd,
		double[] colcnd,
		double[] amax,
		char[] equed);

	public static native void laqp2(
		int[] m,
		int[] n,
		int[] offset,
		double[] a,
		int[] lda,
		int[] jpvt,
		double[] tau,
		double[] vn1,
		double[] vn2,
		double[] work);

	public static native void laqps(
		int[] m,
		int[] n,
		int[] offset,
		int[] nb,
		int[] kb,
		double[] a,
		int[] lda,
		int[] jpvt,
		double[] tau,
		double[] vn1,
		double[] vn2,
		double[] auxv,
		double[] f,
		int[] ldf);

	public static native void laqsb(
		char[] uplo,
		int[] n,
		int[] kd,
		double[] ab,
		int[] ldab,
		double[] s,
		double[] scond,
		double[] amax,
		char[] equed);

	public static native void laqsp(
		char[] uplo,
		int[] n,
		double[] ap,
		double[] s,
		double[] scond,
		double[] amax,
		char[] equed);

	public static native void laqsy(
		char[] uplo,
		int[] n,
		double[] a,
		int[] lda,
		double[] s,
		double[] scond,
		double[] amax,
		char[] equed);

	public static native void laqtr(
		boolean[] ltran,
		boolean[] lreal,
		int[] n,
		double[] t,
		int[] ldt,
		double[] b,
		double[] w,
		double[] scale,
		double[] x,
		double[] work,
		int[] info);

	public static native void lar1v(
		int[] n,
		int[] b1,
		int[] bn,
		double[] sigma,
		double[] d,
		double[] l,
		double[] ld,
		double[] lld,
		double[] gersch,
		double[] z,
		double[] ztz,
		double[] mingma,
		int[] r,
		int[] isuppz,
		double[] work);

	public static native void lar2v(
		int[] n,
		double[] x,
		double[] y,
		double[] z,
		int[] incx,
		double[] c,
		double[] s,
		int[] incc);

	public static native void larf(
		char[] side,
		int[] m,
		int[] n,
		double[] v,
		int[] incv,
		double[] tau,
		double[] c,
		int[] ldc,
		double[] work);

	public static native void larfb(
		char[] side,
		char[] trans,
		char[] direct,
		char[] storev,
		int[] m,
		int[] n,
		int[] k,
		double[] v,
		int[] ldv,
		double[] t,
		int[] ldt,
		double[] c,
		int[] ldc,
		double[] work,
		int[] ldwork);

	public static native void larfg(
		int[] n,
		double[] alpha,
		double[] x,
		int[] incx,
		double[] tau);

	public static native void larft(
		char[] direct,
		char[] storev,
		int[] n,
		int[] k,
		double[] v,
		int[] ldv,
		double[] tau,
		double[] t,
		int[] ldt);

	public static native void larfx(
		char[] side,
		int[] m,
		int[] n,
		double[] v,
		double[] tau,
		double[] c,
		int[] ldc,
		double[] work);

	public static native void largv(
		int[] n,
		double[] x,
		int[] incx,
		double[] y,
		int[] incy,
		double[] c,
		int[] incc);

	public static native void larnv(
		int[] idist,
		int[] iseed,
		int[] n,
		double[] x);

	public static native void larrb(
		int[] n,
		double[] d,
		double[] l,
		double[] ld,
		double[] lld,
		int[] ifirst,
		int[] ilast,
		double[] sigma,
		double[] reltol,
		double[] w,
		double[] wgap,
		double[] werr,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void larre(
		int[] n,
		double[] d,
		double[] e,
		double[] tol,
		int[] nsplit,
		int[] isplit,
		int[] m,
		double[] w,
		double[] woff,
		double[] gersch,
		double[] work,
		int[] info);

	public static native void larrf(
		int[] n,
		double[] d,
		double[] l,
		double[] ld,
		double[] lld,
		int[] ifirst,
		int[] ilast,
		double[] w,
		double[] dplus,
		double[] lplus,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void larrv(
		int[] n,
		double[] d,
		double[] l,
		int[] isplit,
		int[] m,
		double[] w,
		int[] iblock,
		double[] gersch,
		double[] tol,
		double[] z,
		int[] ldz,
		int[] isuppz,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void lartg(
		double[] f,
		double[] g,
		double[] cs,
		double[] sn,
		double[] r);

	public static native void lartv(
		int[] n,
		double[] x,
		int[] incx,
		double[] y,
		int[] incy,
		double[] c,
		double[] s,
		int[] incc);

	public static native void laruv(int[] iseed, int[] n, double[] x);

	public static native void larz(
		char[] side,
		int[] m,
		int[] n,
		int[] l,
		double[] v,
		int[] incv,
		double[] tau,
		double[] c,
		int[] ldc,
		double[] work);

	public static native void larzb(
		char[] side,
		char[] trans,
		char[] direct,
		char[] storev,
		int[] m,
		int[] n,
		int[] k,
		int[] l,
		double[] v,
		int[] ldv,
		double[] t,
		int[] ldt,
		double[] c,
		int[] ldc,
		double[] work,
		int[] ldwork);

	public static native void larzt(
		char[] direct,
		char[] storev,
		int[] n,
		int[] k,
		double[] v,
		int[] ldv,
		double[] tau,
		double[] t,
		int[] ldt);

	public static native void las2(
		double[] f,
		double[] g,
		double[] h,
		double[] ssmin,
		double[] ssmax);

	public static native void lascl(
		char[] type,
		int[] kl,
		int[] ku,
		double[] cfrom,
		double[] cto,
		int[] m,
		int[] n,
		double[] a,
		int[] lda,
		int[] info);

	public static native void lasd0(
		int[] n,
		int[] sqre,
		double[] d,
		double[] e,
		double[] u,
		int[] ldu,
		double[] vt,
		int[] ldvt,
		int[] smlsiz,
		int[] iwork,
		double[] work,
		int[] info);

	public static native void lasd1(
		int[] nl,
		int[] nr,
		int[] sqre,
		double[] d,
		double[] alpha,
		double[] beta,
		double[] u,
		int[] ldu,
		double[] vt,
		int[] ldvt,
		int[] idxq,
		int[] iwork,
		double[] work,
		int[] info);

	public static native void lasd2(
		int[] nl,
		int[] nr,
		int[] sqre,
		int[] k,
		double[] d,
		double[] z,
		double[] alpha,
		double[] beta,
		double[] u,
		int[] ldu,
		double[] vt,
		int[] ldvt,
		double[] dsigma,
		double[] u2,
		int[] ldu2,
		double[] vt2,
		int[] ldvt2,
		int[] idxp,
		int[] idx,
		int[] idxc,
		int[] idxq,
		int[] coltyp,
		int[] info);

	public static native void lasd3(
		int[] nl,
		int[] nr,
		int[] sqre,
		int[] k,
		double[] d,
		double[] q,
		int[] ldq,
		double[] dsigma,
		double[] u,
		int[] ldu,
		double[] u2,
		int[] ldu2,
		double[] vt,
		int[] ldvt,
		double[] vt2,
		int[] ldvt2,
		int[] idxc,
		int[] ctot,
		double[] z,
		int[] info);

	public static native void lasd4(
		int[] n,
		int[] i,
		double[] d,
		double[] z,
		double[] delta,
		double[] rho,
		double[] sigma,
		double[] work,
		int[] info);

	public static native void lasd5(
		int[] i,
		double[] d,
		double[] z,
		double[] delta,
		double[] rho,
		double[] dsigma,
		double[] work);

	public static native void lasd6(
		int[] icompq,
		int[] nl,
		int[] nr,
		int[] sqre,
		double[] d,
		double[] vf,
		double[] vl,
		double[] alpha,
		double[] beta,
		int[] idxq,
		int[] perm,
		int[] givptr,
		int[] givcol,
		int[] ldgcol,
		double[] givnum,
		int[] ldgnum,
		double[] poles,
		double[] difl,
		double[] difr,
		double[] z,
		int[] k,
		double[] c,
		double[] s,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void lasd7(
		int[] icompq,
		int[] nl,
		int[] nr,
		int[] sqre,
		int[] k,
		double[] d,
		double[] z,
		double[] zw,
		double[] vf,
		double[] vfw,
		double[] vl,
		double[] vlw,
		double[] alpha,
		double[] beta,
		double[] dsigma,
		int[] idx,
		int[] idxp,
		int[] idxq,
		int[] perm,
		int[] givptr,
		int[] givcol,
		int[] ldgcol,
		double[] givnum,
		int[] ldgnum,
		double[] c,
		double[] s,
		int[] info);

	public static native void lasd8(
		int[] icompq,
		int[] k,
		double[] d,
		double[] z,
		double[] vf,
		double[] vl,
		double[] difl,
		double[] difr,
		int[] lddifr,
		double[] dsigma,
		double[] work,
		int[] info);

	public static native void lasd9(
		int[] icompq,
		int[] ldu,
		int[] k,
		double[] d,
		double[] z,
		double[] vf,
		double[] vl,
		double[] difl,
		double[] difr,
		double[] dsigma,
		double[] work,
		int[] info);

	public static native void lasda(
		int[] icompq,
		int[] smlsiz,
		int[] n,
		int[] sqre,
		double[] d,
		double[] e,
		double[] u,
		int[] ldu,
		double[] vt,
		int[] k,
		double[] difl,
		double[] difr,
		double[] z,
		double[] poles,
		int[] givptr,
		int[] givcol,
		int[] ldgcol,
		int[] perm,
		double[] givnum,
		double[] c,
		double[] s,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void lasdq(
		char[] uplo,
		int[] sqre,
		int[] n,
		int[] ncvt,
		int[] nru,
		int[] ncc,
		double[] d,
		double[] e,
		double[] vt,
		int[] ldvt,
		double[] u,
		int[] ldu,
		double[] c,
		int[] ldc,
		double[] work,
		int[] info);

	public static native void lasdt(
		int[] n,
		int[] lvl,
		int[] nd,
		int[] inode,
		int[] ndiml,
		int[] ndimr,
		int[] msub);

	public static native void laset(
		char[] uplo,
		int[] m,
		int[] n,
		double[] alpha,
		double[] beta,
		double[] a,
		int[] lda);

	public static native void lasq1(
		int[] n,
		double[] d,
		double[] e,
		double[] work,
		int[] info);

	public static native void lasq2(int[] n, double[] z, int[] info);

	public static native void lasq3(
		int[] i0,
		int[] n0,
		double[] z,
		int[] pp,
		double[] dmin,
		double[] sigma,
		double[] desig,
		double[] qmax,
		int[] nfail,
		int[] iter,
		int[] ndiv,
		boolean[] ieee);

	public static native void lasq4(
		int[] i0,
		int[] n0,
		double[] z,
		int[] pp,
		int[] n0in,
		double[] dmin,
		double[] dmin1,
		double[] dmin2,
		double[] dn,
		double[] dn1,
		double[] dn2,
		double[] tau,
		int[] ttype);

	public static native void lasq5(
		int[] i0,
		int[] n0,
		double[] z,
		int[] pp,
		double[] tau,
		double[] dmin,
		double[] dmin1,
		double[] dmin2,
		double[] dn,
		double[] dnm1,
		double[] dnm2,
		boolean[] ieee);

	public static native void lasq6(
		int[] i0,
		int[] n0,
		double[] z,
		int[] pp,
		double[] dmin,
		double[] dmin1,
		double[] dmin2,
		double[] dn,
		double[] dnm1,
		double[] dnm2);

	public static native void lasr(
		char[] side,
		char[] pivot,
		char[] direct,
		int[] m,
		int[] n,
		double[] c,
		double[] s,
		double[] a,
		int[] lda);

	public static native void lasrt(char[] id, int[] n, double[] d, int[] info);

	public static native void lassq(
		int[] n,
		double[] x,
		int[] incx,
		double[] scale,
		double[] sumsq);

	public static native void lasv2(
		double[] f,
		double[] g,
		double[] h,
		double[] ssmin,
		double[] ssmax,
		double[] snr,
		double[] csr,
		double[] snl,
		double[] csl);

	public static native void laswp(
		int[] n,
		double[] a,
		int[] lda,
		int[] k1,
		int[] k2,
		int[] ipiv,
		int[] incx);

	public static native void lasy2(
		boolean[] ltranl,
		boolean[] ltranr,
		int[] isgn,
		int[] n1,
		int[] n2,
		double[] tl,
		int[] ldtl,
		double[] tr,
		int[] ldtr,
		double[] b,
		int[] ldb,
		double[] scale,
		double[] x,
		int[] ldx,
		double[] xnorm,
		int[] info);

	public static native void lasyf(
		char[] uplo,
		int[] n,
		int[] nb,
		int[] kb,
		double[] a,
		int[] lda,
		int[] ipiv,
		double[] w,
		int[] ldw,
		int[] info);

	public static native void latbs(
		char[] uplo,
		char[] trans,
		char[] diag,
		char[] normin,
		int[] n,
		int[] kd,
		double[] ab,
		int[] ldab,
		double[] x,
		double[] scale,
		double[] cnorm,
		int[] info);

	public static native void latdf(
		int[] ijob,
		int[] n,
		double[] z,
		int[] ldz,
		double[] rhs,
		double[] rdsum,
		double[] rdscal,
		int[] ipiv,
		int[] jpiv);

	public static native void latps(
		char[] uplo,
		char[] trans,
		char[] diag,
		char[] normin,
		int[] n,
		double[] ap,
		double[] x,
		double[] scale,
		double[] cnorm,
		int[] info);

	public static native void latrd(
		char[] uplo,
		int[] n,
		int[] nb,
		double[] a,
		int[] lda,
		double[] e,
		double[] tau,
		double[] w,
		int[] ldw);

	public static native void latrs(
		char[] uplo,
		char[] trans,
		char[] diag,
		char[] normin,
		int[] n,
		double[] a,
		int[] lda,
		double[] x,
		double[] scale,
		double[] cnorm,
		int[] info);

	public static native void latrz(
		int[] m,
		int[] n,
		int[] l,
		double[] a,
		int[] lda,
		double[] tau,
		double[] work);

	public static native void latzm(
		char[] side,
		int[] m,
		int[] n,
		double[] v,
		int[] incv,
		double[] tau,
		double[] c1,
		double[] c2,
		int[] ldc,
		double[] work);

	public static native void lauu2(
		char[] uplo,
		int[] n,
		double[] a,
		int[] lda,
		int[] info);

	public static native void lauum(
		char[] uplo,
		int[] n,
		double[] a,
		int[] lda,
		int[] info);

	public static native void opgtr(
		char[] uplo,
		int[] n,
		double[] ap,
		double[] tau,
		double[] q,
		int[] ldq,
		double[] work,
		int[] info);

	public static native void opmtr(
		char[] side,
		char[] uplo,
		char[] trans,
		int[] m,
		int[] n,
		double[] ap,
		double[] tau,
		double[] c,
		int[] ldc,
		double[] work,
		int[] info);

	public static native void org2l(
		int[] m,
		int[] n,
		int[] k,
		double[] a,
		int[] lda,
		double[] tau,
		double[] work,
		int[] info);

	public static native void org2r(
		int[] m,
		int[] n,
		int[] k,
		double[] a,
		int[] lda,
		double[] tau,
		double[] work,
		int[] info);

	public static native void orgbr(
		char[] vect,
		int[] m,
		int[] n,
		int[] k,
		double[] a,
		int[] lda,
		double[] tau,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void orghr(
		int[] n,
		int[] ilo,
		int[] ihi,
		double[] a,
		int[] lda,
		double[] tau,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void orgl2(
		int[] m,
		int[] n,
		int[] k,
		double[] a,
		int[] lda,
		double[] tau,
		double[] work,
		int[] info);

	public static native void orglq(
		int[] m,
		int[] n,
		int[] k,
		double[] a,
		int[] lda,
		double[] tau,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void orgql(
		int[] m,
		int[] n,
		int[] k,
		double[] a,
		int[] lda,
		double[] tau,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void orgqr(
		int[] m,
		int[] n,
		int[] k,
		double[] a,
		int[] lda,
		double[] tau,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void orgr2(
		int[] m,
		int[] n,
		int[] k,
		double[] a,
		int[] lda,
		double[] tau,
		double[] work,
		int[] info);

	public static native void orgrq(
		int[] m,
		int[] n,
		int[] k,
		double[] a,
		int[] lda,
		double[] tau,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void orgtr(
		char[] uplo,
		int[] n,
		double[] a,
		int[] lda,
		double[] tau,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void orm2l(
		char[] side,
		char[] trans,
		int[] m,
		int[] n,
		int[] k,
		double[] a,
		int[] lda,
		double[] tau,
		double[] c,
		int[] ldc,
		double[] work,
		int[] info);

	public static native void orm2r(
		char[] side,
		char[] trans,
		int[] m,
		int[] n,
		int[] k,
		double[] a,
		int[] lda,
		double[] tau,
		double[] c,
		int[] ldc,
		double[] work,
		int[] info);

	public static native void ormbr(
		char[] vect,
		char[] side,
		char[] trans,
		int[] m,
		int[] n,
		int[] k,
		double[] a,
		int[] lda,
		double[] tau,
		double[] c,
		int[] ldc,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void ormhr(
		char[] side,
		char[] trans,
		int[] m,
		int[] n,
		int[] ilo,
		int[] ihi,
		double[] a,
		int[] lda,
		double[] tau,
		double[] c,
		int[] ldc,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void orml2(
		char[] side,
		char[] trans,
		int[] m,
		int[] n,
		int[] k,
		double[] a,
		int[] lda,
		double[] tau,
		double[] c,
		int[] ldc,
		double[] work,
		int[] info);

	public static native void ormlq(
		char[] side,
		char[] trans,
		int[] m,
		int[] n,
		int[] k,
		double[] a,
		int[] lda,
		double[] tau,
		double[] c,
		int[] ldc,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void ormql(
		char[] side,
		char[] trans,
		int[] m,
		int[] n,
		int[] k,
		double[] a,
		int[] lda,
		double[] tau,
		double[] c,
		int[] ldc,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void ormqr(
		char[] side,
		char[] trans,
		int[] m,
		int[] n,
		int[] k,
		double[] a,
		int[] lda,
		double[] tau,
		double[] c,
		int[] ldc,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void ormr2(
		char[] side,
		char[] trans,
		int[] m,
		int[] n,
		int[] k,
		double[] a,
		int[] lda,
		double[] tau,
		double[] c,
		int[] ldc,
		double[] work,
		int[] info);

	public static native void ormr3(
		char[] side,
		char[] trans,
		int[] m,
		int[] n,
		int[] k,
		int[] l,
		double[] a,
		int[] lda,
		double[] tau,
		double[] c,
		int[] ldc,
		double[] work,
		int[] info);

	public static native void ormrq(
		char[] side,
		char[] trans,
		int[] m,
		int[] n,
		int[] k,
		double[] a,
		int[] lda,
		double[] tau,
		double[] c,
		int[] ldc,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void ormrz(
		char[] side,
		char[] trans,
		int[] m,
		int[] n,
		int[] k,
		int[] l,
		double[] a,
		int[] lda,
		double[] tau,
		double[] c,
		int[] ldc,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void ormtr(
		char[] side,
		char[] uplo,
		char[] trans,
		int[] m,
		int[] n,
		double[] a,
		int[] lda,
		double[] tau,
		double[] c,
		int[] ldc,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void pbcon(
		char[] uplo,
		int[] n,
		int[] kd,
		double[] ab,
		int[] ldab,
		double[] anorm,
		double[] rcond,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void pbequ(
		char[] uplo,
		int[] n,
		int[] kd,
		double[] ab,
		int[] ldab,
		double[] s,
		double[] scond,
		double[] amax,
		int[] info);

	public static native void pbrfs(
		char[] uplo,
		int[] n,
		int[] kd,
		int[] nrhs,
		double[] ab,
		int[] ldab,
		double[] afb,
		int[] ldafb,
		double[] b,
		int[] ldb,
		double[] x,
		int[] ldx,
		double[] ferr,
		double[] berr,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void pbstf(
		char[] uplo,
		int[] n,
		int[] kd,
		double[] ab,
		int[] ldab,
		int[] info);

	public static native void pbsv(
		char[] uplo,
		int[] n,
		int[] kd,
		int[] nrhs,
		double[] ab,
		int[] ldab,
		double[] b,
		int[] ldb,
		int[] info);

	public static native void pbsvx(
		char[] fact,
		char[] uplo,
		int[] n,
		int[] kd,
		int[] nrhs,
		double[] ab,
		int[] ldab,
		double[] afb,
		int[] ldafb,
		char[] equed,
		double[] s,
		double[] b,
		int[] ldb,
		double[] x,
		int[] ldx,
		double[] rcond,
		double[] ferr,
		double[] berr,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void pbtf2(
		char[] uplo,
		int[] n,
		int[] kd,
		double[] ab,
		int[] ldab,
		int[] info);

	public static native void pbtrf(
		char[] uplo,
		int[] n,
		int[] kd,
		double[] ab,
		int[] ldab,
		int[] info);

	public static native void pbtrs(
		char[] uplo,
		int[] n,
		int[] kd,
		int[] nrhs,
		double[] ab,
		int[] ldab,
		double[] b,
		int[] ldb,
		int[] info);

	public static native void pocon(
		char[] uplo,
		int[] n,
		double[] a,
		int[] lda,
		double[] anorm,
		double[] rcond,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void poequ(
		int[] n,
		double[] a,
		int[] lda,
		double[] s,
		double[] scond,
		double[] amax,
		int[] info);

	public static native void porfs(
		char[] uplo,
		int[] n,
		int[] nrhs,
		double[] a,
		int[] lda,
		double[] af,
		int[] ldaf,
		double[] b,
		int[] ldb,
		double[] x,
		int[] ldx,
		double[] ferr,
		double[] berr,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void posv(
		char[] uplo,
		int[] n,
		int[] nrhs,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		int[] info);

	public static native void posvx(
		char[] fact,
		char[] uplo,
		int[] n,
		int[] nrhs,
		double[] a,
		int[] lda,
		double[] af,
		int[] ldaf,
		char[] equed,
		double[] s,
		double[] b,
		int[] ldb,
		double[] x,
		int[] ldx,
		double[] rcond,
		double[] ferr,
		double[] berr,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void potf2(
		char[] uplo,
		int[] n,
		double[] a,
		int[] lda,
		int[] info);

	public static native void potrf(
		char[] uplo,
		int[] n,
		double[] a,
		int[] lda,
		int[] info);

	public static native void potri(
		char[] uplo,
		int[] n,
		double[] a,
		int[] lda,
		int[] info);

	public static native void potrs(
		char[] uplo,
		int[] n,
		int[] nrhs,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		int[] info);

	public static native void ppcon(
		char[] uplo,
		int[] n,
		double[] ap,
		double[] anorm,
		double[] rcond,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void ppequ(
		char[] uplo,
		int[] n,
		double[] ap,
		double[] s,
		double[] scond,
		double[] amax,
		int[] info);

	public static native void pprfs(
		char[] uplo,
		int[] n,
		int[] nrhs,
		double[] ap,
		double[] afp,
		double[] b,
		int[] ldb,
		double[] x,
		int[] ldx,
		double[] ferr,
		double[] berr,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void ppsv(
		char[] uplo,
		int[] n,
		int[] nrhs,
		double[] ap,
		double[] b,
		int[] ldb,
		int[] info);

	public static native void ppsvx(
		char[] fact,
		char[] uplo,
		int[] n,
		int[] nrhs,
		double[] ap,
		double[] afp,
		char[] equed,
		double[] s,
		double[] b,
		int[] ldb,
		double[] x,
		int[] ldx,
		double[] rcond,
		double[] ferr,
		double[] berr,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void pptrf(
		char[] uplo,
		int[] n,
		double[] ap,
		int[] info);

	public static native void pptri(
		char[] uplo,
		int[] n,
		double[] ap,
		int[] info);

	public static native void pptrs(
		char[] uplo,
		int[] n,
		int[] nrhs,
		double[] ap,
		double[] b,
		int[] ldb,
		int[] info);

	public static native void ptcon(
		int[] n,
		double[] d,
		double[] e,
		double[] anorm,
		double[] rcond,
		double[] work,
		int[] info);

	public static native void pteqr(
		char[] compz,
		int[] n,
		double[] d,
		double[] e,
		double[] z,
		int[] ldz,
		double[] work,
		int[] info);

	public static native void ptrfs(
		int[] n,
		int[] nrhs,
		double[] d,
		double[] e,
		double[] df,
		double[] ef,
		double[] b,
		int[] ldb,
		double[] x,
		int[] ldx,
		double[] ferr,
		double[] berr,
		double[] work,
		int[] info);

	public static native void ptsv(
		int[] n,
		int[] nrhs,
		double[] d,
		double[] e,
		double[] b,
		int[] ldb,
		int[] info);

	public static native void ptsvx(
		char[] fact,
		int[] n,
		int[] nrhs,
		double[] d,
		double[] e,
		double[] df,
		double[] ef,
		double[] b,
		int[] ldb,
		double[] x,
		int[] ldx,
		double[] rcond,
		double[] ferr,
		double[] berr,
		double[] work,
		int[] info);

	public static native void pttrf(
		int[] n,
		double[] d,
		double[] e,
		int[] info);

	public static native void pttrs(
		int[] n,
		int[] nrhs,
		double[] d,
		double[] e,
		double[] b,
		int[] ldb,
		int[] info);

	public static native void ptts2(
		int[] n,
		int[] nrhs,
		double[] d,
		double[] e,
		double[] b,
		int[] ldb);

	public static native void rscl(
		int[] n,
		double[] sa,
		double[] sx,
		int[] incx);

	public static native void sbev(
		char[] jobz,
		char[] uplo,
		int[] n,
		int[] kd,
		double[] ab,
		int[] ldab,
		double[] w,
		double[] z,
		int[] ldz,
		double[] work,
		int[] info);

	public static native void sbevd(
		char[] jobz,
		char[] uplo,
		int[] n,
		int[] kd,
		double[] ab,
		int[] ldab,
		double[] w,
		double[] z,
		int[] ldz,
		double[] work,
		int[] lwork,
		int[] iwork,
		int[] liwork,
		int[] info);

	public static native void sbevx(
		char[] jobz,
		char[] range,
		char[] uplo,
		int[] n,
		int[] kd,
		double[] ab,
		int[] ldab,
		double[] q,
		int[] ldq,
		double[] vl,
		double[] vu,
		int[] il,
		int[] iu,
		double[] abstol,
		int[] m,
		double[] w,
		double[] z,
		int[] ldz,
		double[] work,
		int[] iwork,
		int[] ifail,
		int[] info);

	public static native void sbgst(
		char[] vect,
		char[] uplo,
		int[] n,
		int[] ka,
		int[] kb,
		double[] ab,
		int[] ldab,
		double[] bb,
		int[] ldbb,
		double[] x,
		int[] ldx,
		double[] work,
		int[] info);

	public static native void sbgv(
		char[] jobz,
		char[] uplo,
		int[] n,
		int[] ka,
		int[] kb,
		double[] ab,
		int[] ldab,
		double[] bb,
		int[] ldbb,
		double[] w,
		double[] z,
		int[] ldz,
		double[] work,
		int[] info);

	public static native void sbgvd(
		char[] jobz,
		char[] uplo,
		int[] n,
		int[] ka,
		int[] kb,
		double[] ab,
		int[] ldab,
		double[] bb,
		int[] ldbb,
		double[] w,
		double[] z,
		int[] ldz,
		double[] work,
		int[] lwork,
		int[] iwork,
		int[] liwork,
		int[] info);

	public static native void sbgvx(
		char[] jobz,
		char[] range,
		char[] uplo,
		int[] n,
		int[] ka,
		int[] kb,
		double[] ab,
		int[] ldab,
		double[] bb,
		int[] ldbb,
		double[] q,
		int[] ldq,
		double[] vl,
		double[] vu,
		int[] il,
		int[] iu,
		double[] abstol,
		int[] m,
		double[] w,
		double[] z,
		int[] ldz,
		double[] work,
		int[] iwork,
		int[] ifail,
		int[] info);

	public static native void sbtrd(
		char[] vect,
		char[] uplo,
		int[] n,
		int[] kd,
		double[] ab,
		int[] ldab,
		double[] d,
		double[] e,
		double[] q,
		int[] ldq,
		double[] work,
		int[] info);

	public static native void spcon(
		char[] uplo,
		int[] n,
		double[] ap,
		int[] ipiv,
		double[] anorm,
		double[] rcond,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void spev(
		char[] jobz,
		char[] uplo,
		int[] n,
		double[] ap,
		double[] w,
		double[] z,
		int[] ldz,
		double[] work,
		int[] info);

	public static native void spevd(
		char[] jobz,
		char[] uplo,
		int[] n,
		double[] ap,
		double[] w,
		double[] z,
		int[] ldz,
		double[] work,
		int[] lwork,
		int[] iwork,
		int[] liwork,
		int[] info);

	public static native void spevx(
		char[] jobz,
		char[] range,
		char[] uplo,
		int[] n,
		double[] ap,
		double[] vl,
		double[] vu,
		int[] il,
		int[] iu,
		double[] abstol,
		int[] m,
		double[] w,
		double[] z,
		int[] ldz,
		double[] work,
		int[] iwork,
		int[] ifail,
		int[] info);

	public static native void spgst(
		int[] itype,
		char[] uplo,
		int[] n,
		double[] ap,
		double[] bp,
		int[] info);

	public static native void spgv(
		int[] itype,
		char[] jobz,
		char[] uplo,
		int[] n,
		double[] ap,
		double[] bp,
		double[] w,
		double[] z,
		int[] ldz,
		double[] work,
		int[] info);

	public static native void spgvd(
		int[] itype,
		char[] jobz,
		char[] uplo,
		int[] n,
		double[] ap,
		double[] bp,
		double[] w,
		double[] z,
		int[] ldz,
		double[] work,
		int[] lwork,
		int[] iwork,
		int[] liwork,
		int[] info);

	public static native void spgvx(
		int[] itype,
		char[] jobz,
		char[] range,
		char[] uplo,
		int[] n,
		double[] ap,
		double[] bp,
		double[] vl,
		double[] vu,
		int[] il,
		int[] iu,
		double[] abstol,
		int[] m,
		double[] w,
		double[] z,
		int[] ldz,
		double[] work,
		int[] iwork,
		int[] ifail,
		int[] info);

	public static native void sprfs(
		char[] uplo,
		int[] n,
		int[] nrhs,
		double[] ap,
		double[] afp,
		int[] ipiv,
		double[] b,
		int[] ldb,
		double[] x,
		int[] ldx,
		double[] ferr,
		double[] berr,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void spsv(
		char[] uplo,
		int[] n,
		int[] nrhs,
		double[] ap,
		int[] ipiv,
		double[] b,
		int[] ldb,
		int[] info);

	public static native void spsvx(
		char[] fact,
		char[] uplo,
		int[] n,
		int[] nrhs,
		double[] ap,
		double[] afp,
		int[] ipiv,
		double[] b,
		int[] ldb,
		double[] x,
		int[] ldx,
		double[] rcond,
		double[] ferr,
		double[] berr,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void sptrd(
		char[] uplo,
		int[] n,
		double[] ap,
		double[] d,
		double[] e,
		double[] tau,
		int[] info);

	public static native void sptrf(
		char[] uplo,
		int[] n,
		double[] ap,
		int[] ipiv,
		int[] info);

	public static native void sptri(
		char[] uplo,
		int[] n,
		double[] ap,
		int[] ipiv,
		double[] work,
		int[] info);

	public static native void sptrs(
		char[] uplo,
		int[] n,
		int[] nrhs,
		double[] ap,
		int[] ipiv,
		double[] b,
		int[] ldb,
		int[] info);

	public static native void stebz(
		char[] range,
		char[] order,
		int[] n,
		double[] vl,
		double[] vu,
		int[] il,
		int[] iu,
		double[] abstol,
		double[] d,
		double[] e,
		int[] m,
		int[] nsplit,
		double[] w,
		int[] iblock,
		int[] isplit,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void stedc(
		char[] compz,
		int[] n,
		double[] d,
		double[] e,
		double[] z,
		int[] ldz,
		double[] work,
		int[] lwork,
		int[] iwork,
		int[] liwork,
		int[] info);

	public static native void stegr(
		char[] jobz,
		char[] range,
		int[] n,
		double[] d,
		double[] e,
		double[] vl,
		double[] vu,
		int[] il,
		int[] iu,
		double[] abstol,
		int[] m,
		double[] w,
		double[] z,
		int[] ldz,
		int[] isuppz,
		double[] work,
		int[] lwork,
		int[] iwork,
		int[] liwork,
		int[] info);

	public static native void stein(
		int[] n,
		double[] d,
		double[] e,
		int[] m,
		double[] w,
		int[] iblock,
		int[] isplit,
		double[] z,
		int[] ldz,
		double[] work,
		int[] iwork,
		int[] ifail,
		int[] info);

	public static native void steqr(
		char[] compz,
		int[] n,
		double[] d,
		double[] e,
		double[] z,
		int[] ldz,
		double[] work,
		int[] info);

	public static native void sterf(
		int[] n,
		double[] d,
		double[] e,
		int[] info);

	public static native void stev(
		char[] jobz,
		int[] n,
		double[] d,
		double[] e,
		double[] z,
		int[] ldz,
		double[] work,
		int[] info);

	public static native void stevd(
		char[] jobz,
		int[] n,
		double[] d,
		double[] e,
		double[] z,
		int[] ldz,
		double[] work,
		int[] lwork,
		int[] iwork,
		int[] liwork,
		int[] info);

	public static native void stevr(
		char[] jobz,
		char[] range,
		int[] n,
		double[] d,
		double[] e,
		double[] vl,
		double[] vu,
		int[] il,
		int[] iu,
		double[] abstol,
		int[] m,
		double[] w,
		double[] z,
		int[] ldz,
		int[] isuppz,
		double[] work,
		int[] lwork,
		int[] iwork,
		int[] liwork,
		int[] info);

	public static native void stevx(
		char[] jobz,
		char[] range,
		int[] n,
		double[] d,
		double[] e,
		double[] vl,
		double[] vu,
		int[] il,
		int[] iu,
		double[] abstol,
		int[] m,
		double[] w,
		double[] z,
		int[] ldz,
		double[] work,
		int[] iwork,
		int[] ifail,
		int[] info);

	public static native void sycon(
		char[] uplo,
		int[] n,
		double[] a,
		int[] lda,
		int[] ipiv,
		double[] anorm,
		double[] rcond,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void syev(
		char[] jobz,
		char[] uplo,
		int[] n,
		double[] a,
		int[] lda,
		double[] w,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void syevd(
		char[] jobz,
		char[] uplo,
		int[] n,
		double[] a,
		int[] lda,
		double[] w,
		double[] work,
		int[] lwork,
		int[] iwork,
		int[] liwork,
		int[] info);

	public static native void syevr(
		char[] jobz,
		char[] range,
		char[] uplo,
		int[] n,
		double[] a,
		int[] lda,
		double[] vl,
		double[] vu,
		int[] il,
		int[] iu,
		double[] abstol,
		int[] m,
		double[] w,
		double[] z,
		int[] ldz,
		int[] isuppz,
		double[] work,
		int[] lwork,
		int[] iwork,
		int[] liwork,
		int[] info);

	public static native void syevx(
		char[] jobz,
		char[] range,
		char[] uplo,
		int[] n,
		double[] a,
		int[] lda,
		double[] vl,
		double[] vu,
		int[] il,
		int[] iu,
		double[] abstol,
		int[] m,
		double[] w,
		double[] z,
		int[] ldz,
		double[] work,
		int[] lwork,
		int[] iwork,
		int[] ifail,
		int[] info);

	public static native void sygs2(
		int[] itype,
		char[] uplo,
		int[] n,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		int[] info);

	public static native void sygst(
		int[] itype,
		char[] uplo,
		int[] n,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		int[] info);

	public static native void sygv(
		int[] itype,
		char[] jobz,
		char[] uplo,
		int[] n,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] w,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void sygvd(
		int[] itype,
		char[] jobz,
		char[] uplo,
		int[] n,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] w,
		double[] work,
		int[] lwork,
		int[] iwork,
		int[] liwork,
		int[] info);

	public static native void sygvx(
		int[] itype,
		char[] jobz,
		char[] range,
		char[] uplo,
		int[] n,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] vl,
		double[] vu,
		int[] il,
		int[] iu,
		double[] abstol,
		int[] m,
		double[] w,
		double[] z,
		int[] ldz,
		double[] work,
		int[] lwork,
		int[] iwork,
		int[] ifail,
		int[] info);

	public static native void syrfs(
		char[] uplo,
		int[] n,
		int[] nrhs,
		double[] a,
		int[] lda,
		double[] af,
		int[] ldaf,
		int[] ipiv,
		double[] b,
		int[] ldb,
		double[] x,
		int[] ldx,
		double[] ferr,
		double[] berr,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void sysv(
		char[] uplo,
		int[] n,
		int[] nrhs,
		double[] a,
		int[] lda,
		int[] ipiv,
		double[] b,
		int[] ldb,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void sysvx(
		char[] fact,
		char[] uplo,
		int[] n,
		int[] nrhs,
		double[] a,
		int[] lda,
		double[] af,
		int[] ldaf,
		int[] ipiv,
		double[] b,
		int[] ldb,
		double[] x,
		int[] ldx,
		double[] rcond,
		double[] ferr,
		double[] berr,
		double[] work,
		int[] lwork,
		int[] iwork,
		int[] info);

	public static native void sytd2(
		char[] uplo,
		int[] n,
		double[] a,
		int[] lda,
		double[] d,
		double[] e,
		double[] tau,
		int[] info);

	public static native void sytf2(
		char[] uplo,
		int[] n,
		double[] a,
		int[] lda,
		int[] ipiv,
		int[] info);

	public static native void sytrd(
		char[] uplo,
		int[] n,
		double[] a,
		int[] lda,
		double[] d,
		double[] e,
		double[] tau,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void sytrf(
		char[] uplo,
		int[] n,
		double[] a,
		int[] lda,
		int[] ipiv,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void sytri(
		char[] uplo,
		int[] n,
		double[] a,
		int[] lda,
		int[] ipiv,
		double[] work,
		int[] info);

	public static native void sytrs(
		char[] uplo,
		int[] n,
		int[] nrhs,
		double[] a,
		int[] lda,
		int[] ipiv,
		double[] b,
		int[] ldb,
		int[] info);

	public static native void tbcon(
		char[] norm,
		char[] uplo,
		char[] diag,
		int[] n,
		int[] kd,
		double[] ab,
		int[] ldab,
		double[] rcond,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void tbrfs(
		char[] uplo,
		char[] trans,
		char[] diag,
		int[] n,
		int[] kd,
		int[] nrhs,
		double[] ab,
		int[] ldab,
		double[] b,
		int[] ldb,
		double[] x,
		int[] ldx,
		double[] ferr,
		double[] berr,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void tbtrs(
		char[] uplo,
		char[] trans,
		char[] diag,
		int[] n,
		int[] kd,
		int[] nrhs,
		double[] ab,
		int[] ldab,
		double[] b,
		int[] ldb,
		int[] info);

	public static native void tgevc(
		char[] side,
		char[] howmny,
		boolean[] select,
		int[] n,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] vl,
		int[] ldvl,
		double[] vr,
		int[] ldvr,
		int[] mm,
		int[] m,
		double[] work,
		int[] info);

	public static native void tgex2(
		boolean[] wantq,
		boolean[] wantz,
		int[] n,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] q,
		int[] ldq,
		double[] z,
		int[] ldz,
		int[] j1,
		int[] n1,
		int[] n2,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void tgexc(
		boolean[] wantq,
		boolean[] wantz,
		int[] n,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] q,
		int[] ldq,
		double[] z,
		int[] ldz,
		int[] ifst,
		int[] ilst,
		double[] work,
		int[] lwork,
		int[] info);

	public static native void tgsen(
		int[] ijob,
		boolean[] wantq,
		boolean[] wantz,
		boolean[] select,
		int[] n,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] alphar,
		double[] alphai,
		double[] beta,
		double[] q,
		int[] ldq,
		double[] z,
		int[] ldz,
		int[] m,
		double[] pl,
		double[] pr,
		double[] dif,
		double[] work,
		int[] lwork,
		int[] iwork,
		int[] liwork,
		int[] info);

	public static native void tgsja(
		char[] jobu,
		char[] jobv,
		char[] jobq,
		int[] m,
		int[] p,
		int[] n,
		int[] k,
		int[] l,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] tola,
		double[] tolb,
		double[] alpha,
		double[] beta,
		double[] u,
		int[] ldu,
		double[] v,
		int[] ldv,
		double[] q,
		int[] ldq,
		double[] work,
		int[] ncycle,
		int[] info);

	public static native void tgsna(
		char[] job,
		char[] howmny,
		boolean[] select,
		int[] n,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] vl,
		int[] ldvl,
		double[] vr,
		int[] ldvr,
		double[] s,
		double[] dif,
		int[] mm,
		int[] m,
		double[] work,
		int[] lwork,
		int[] iwork,
		int[] info);

	public static native void tgsy2(
		char[] trans,
		int[] ijob,
		int[] m,
		int[] n,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] c,
		int[] ldc,
		double[] d,
		int[] ldd,
		double[] e,
		int[] lde,
		double[] f,
		int[] ldf,
		double[] scale,
		double[] rdsum,
		double[] rdscal,
		int[] iwork,
		int[] pq,
		int[] info);

	public static native void tgsyl(
		char[] trans,
		int[] ijob,
		int[] m,
		int[] n,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] c,
		int[] ldc,
		double[] d,
		int[] ldd,
		double[] e,
		int[] lde,
		double[] f,
		int[] ldf,
		double[] scale,
		double[] dif,
		double[] work,
		int[] lwork,
		int[] iwork,
		int[] info);

	public static native void tpcon(
		char[] norm,
		char[] uplo,
		char[] diag,
		int[] n,
		double[] ap,
		double[] rcond,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void tprfs(
		char[] uplo,
		char[] trans,
		char[] diag,
		int[] n,
		int[] nrhs,
		double[] ap,
		double[] b,
		int[] ldb,
		double[] x,
		int[] ldx,
		double[] ferr,
		double[] berr,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void tptri(
		char[] uplo,
		char[] diag,
		int[] n,
		double[] ap,
		int[] info);

	public static native void tptrs(
		char[] uplo,
		char[] trans,
		char[] diag,
		int[] n,
		int[] nrhs,
		double[] ap,
		double[] b,
		int[] ldb,
		int[] info);

	public static native void trcon(
		char[] norm,
		char[] uplo,
		char[] diag,
		int[] n,
		double[] a,
		int[] lda,
		double[] rcond,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void trevc(
		char[] side,
		char[] howmny,
		boolean[] select,
		int[] n,
		double[] t,
		int[] ldt,
		double[] vl,
		int[] ldvl,
		double[] vr,
		int[] ldvr,
		int[] mm,
		int[] m,
		double[] work,
		int[] info);

	public static native void trexc(
		char[] compq,
		int[] n,
		double[] t,
		int[] ldt,
		double[] q,
		int[] ldq,
		int[] ifst,
		int[] ilst,
		double[] work,
		int[] info);

	public static native void trrfs(
		char[] uplo,
		char[] trans,
		char[] diag,
		int[] n,
		int[] nrhs,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] x,
		int[] ldx,
		double[] ferr,
		double[] berr,
		double[] work,
		int[] iwork,
		int[] info);

	public static native void trsen(
		char[] job,
		char[] compq,
		boolean[] select,
		int[] n,
		double[] t,
		int[] ldt,
		double[] q,
		int[] ldq,
		double[] wr,
		double[] wi,
		int[] m,
		double[] s,
		double[] sep,
		double[] work,
		int[] lwork,
		int[] iwork,
		int[] liwork,
		int[] info);

	public static native void trsna(
		char[] job,
		char[] howmny,
		boolean[] select,
		int[] n,
		double[] t,
		int[] ldt,
		double[] vl,
		int[] ldvl,
		double[] vr,
		int[] ldvr,
		double[] s,
		double[] sep,
		int[] mm,
		int[] m,
		double[] work,
		int[] ldwork,
		int[] iwork,
		int[] info);

	public static native void trsyl(
		char[] trana,
		char[] tranb,
		int[] isgn,
		int[] m,
		int[] n,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		double[] c,
		int[] ldc,
		double[] scale,
		int[] info);

	public static native void trti2(
		char[] uplo,
		char[] diag,
		int[] n,
		double[] a,
		int[] lda,
		int[] info);

	public static native void trtri(
		char[] uplo,
		char[] diag,
		int[] n,
		double[] a,
		int[] lda,
		int[] info);

	public static native void trtrs(
		char[] uplo,
		char[] trans,
		char[] diag,
		int[] n,
		int[] nrhs,
		double[] a,
		int[] lda,
		double[] b,
		int[] ldb,
		int[] info);

	public static native void tzrqf(
		int[] m,
		int[] n,
		double[] a,
		int[] lda,
		double[] tau,
		int[] info);

	public static native void tzrzf(
		int[] m,
		int[] n,
		double[] a,
		int[] lda,
		double[] tau,
		double[] work,
		int[] lwork,
		int[] info);

}
