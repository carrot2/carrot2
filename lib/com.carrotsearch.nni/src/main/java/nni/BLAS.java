
package nni;


public class BLAS
{

    private BLAS()
    {
    }

    public static void init()
    {
    }

    public static native double dot(int i, double ad[], int j, double ad1[], int k);

    public static native double nrm2(int i, double ad[], int j);

    public static native double asum(int i, double ad[], int j);

    public static native int idamax(int i, double ad[], int j);

    public static native void swap(int i, double ad[], int j, double ad1[], int k);

    public static native void copy(int i, double ad[], int j, double ad1[], int k);

    public static native void axpy(int i, double d, double ad[], int j, double ad1[], int k);

    public static native void rotg(double ad[], double ad1[], double ad2[], double ad3[]);

    public static native void rotmg(double ad[], double ad1[], double ad2[], double d, double ad3[]);

    public static native void rot(int i, double ad[], int j, double ad1[], int k, double d, double d1);

    public static native void rotm(int i, double ad[], int j, double ad1[], int k, double ad2[]);

    public static native void scal(int i, double d, double ad[], int j);

    public static native void gemv(int i, int j, int k, int l, double d, double ad[], int i1, 
            double ad1[], int j1, double d1, double ad2[], int k1);

    public static native void gbmv(int i, int j, int k, int l, int i1, int j1, double d, 
            double ad[], int k1, double ad1[], int l1, double d1, double ad2[], 
            int i2);

    public static native void trmv(int i, int j, int k, int l, int i1, double ad[], int j1, double ad1[], 
            int k1);

    public static native void tbmv(int i, int j, int k, int l, int i1, int j1, double ad[], int k1, 
            double ad1[], int l1);

    public static native void tpmv(int i, int j, int k, int l, int i1, double ad[], double ad1[], int j1);

    public static native void trsv(int i, int j, int k, int l, int i1, double ad[], int j1, double ad1[], 
            int k1);

    public static native void tbsv(int i, int j, int k, int l, int i1, int j1, double ad[], int k1, 
            double ad1[], int l1);

    public static native void tpsv(int i, int j, int k, int l, int i1, double ad[], double ad1[], int j1);

    public static native void symv(int i, int j, int k, double d, double ad[], int l, double ad1[], 
            int i1, double d1, double ad2[], int j1);

    public static native void sbmv(int i, int j, int k, int l, double d, double ad[], int i1, 
            double ad1[], int j1, double d1, double ad2[], int k1);

    public static native void spmv(int i, int j, int k, double d, double ad[], double ad1[], int l, 
            double d1, double ad2[], int i1);

    public static native void ger(int i, int j, int k, double d, double ad[], int l, double ad1[], 
            int i1, double ad2[], int j1);

    public static native void syr(int i, int j, int k, double d, double ad[], int l, double ad1[], 
            int i1);

    public static native void spr(int i, int j, int k, double d, double ad[], int l, double ad1[]);

    public static native void syr2(int i, int j, int k, double d, double ad[], int l, double ad1[], 
            int i1, double ad2[], int j1);

    public static native void spr2(int i, int j, int k, double d, double ad[], int l, double ad1[], 
            int i1, double ad2[]);

    public static native void gemm(int i, int j, int k, int l, int i1, int j1, double d, 
            double ad[], int k1, double ad1[], int l1, double d1, double ad2[], 
            int i2);

    public static native void symm(int i, int j, int k, int l, int i1, double d, double ad[], 
            int j1, double ad1[], int k1, double d1, double ad2[], int l1);

    public static native void syrk(int i, int j, int k, int l, int i1, double d, double ad[], 
            int j1, double d1, double ad1[], int k1);

    public static native void syr2k(int i, int j, int k, int l, int i1, double d, double ad[], 
            int j1, double ad1[], int k1, double d1, double ad2[], int l1);

    public static native void trmm(int i, int j, int k, int l, int i1, int j1, int k1, double d, double ad[], int l1, double ad1[], int i2);

    public static native void trsm(int i, int j, int k, int l, int i1, int j1, int k1, double d, double ad[], int l1, double ad1[], int i2);

    public static final int RowMajor = 101;
    public static final int ColMajor = 102;
    public static final int NoTrans = 111;
    public static final int Trans = 112;
    public static final int Upper = 121;
    public static final int Lower = 122;
    public static final int NonUnit = 131;
    public static final int Unit = 132;
    public static final int Left = 141;
    public static final int Right = 142;

    static 
    {
        System.loadLibrary("nni_blas");
    }
}
