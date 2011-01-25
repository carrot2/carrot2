
package nni;

public class LAPACK
{

    private LAPACK()
    {
    }

    public static void init()
    {
    }

    public static native double lamch(char ac[]);

    public static native int laenv(int i, char ac[], char ac1[], int j, int k, int l, int i1);

    public static native void bdsdc(char ac[], char ac1[], int ai[], double ad[], double ad1[], double ad2[], int ai1[], double ad3[], 
            int ai2[], double ad4[], int ai3[], double ad5[], int ai4[], int ai5[]);

    public static native void bdsqr(char ac[], int ai[], int ai1[], int ai2[], int ai3[], double ad[], double ad1[], double ad2[], 
            int ai4[], double ad3[], int ai5[], double ad4[], int ai6[], double ad5[], int ai7[]);

    public static native void disna(char ac[], int ai[], int ai1[], double ad[], double ad1[], int ai2[]);

    public static native void gbbrd(char ac[], int ai[], int ai1[], int ai2[], int ai3[], int ai4[], double ad[], int ai5[], 
            double ad1[], double ad2[], double ad3[], int ai6[], double ad4[], int ai7[], double ad5[], 
            int ai8[], double ad6[], int ai9[]);

    public static native void gbcon(char ac[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], int ai4[], double ad1[], 
            double ad2[], double ad3[], int ai5[], int ai6[]);

    public static native void gbequ(int ai[], int ai1[], int ai2[], int ai3[], double ad[], int ai4[], double ad1[], double ad2[], 
            double ad3[], double ad4[], double ad5[], int ai5[]);

    public static native void gbrfs(char ac[], int ai[], int ai1[], int ai2[], int ai3[], double ad[], int ai4[], double ad1[], 
            int ai5[], int ai6[], double ad2[], int ai7[], double ad3[], int ai8[], double ad4[], 
            double ad5[], double ad6[], int ai9[], int ai10[]);

    public static native void gbsv(int ai[], int ai1[], int ai2[], int ai3[], double ad[], int ai4[], int ai5[], double ad1[], 
            int ai6[], int ai7[]);

    public static native void gbsvx(char ac[], char ac1[], int ai[], int ai1[], int ai2[], int ai3[], double ad[], int ai4[], 
            double ad1[], int ai5[], int ai6[], char ac2[], double ad2[], double ad3[], double ad4[], 
            int ai7[], double ad5[], int ai8[], double ad6[], double ad7[], double ad8[], double ad9[], 
            int ai9[], int ai10[]);

    public static native void gbtf2(int ai[], int ai1[], int ai2[], int ai3[], double ad[], int ai4[], int ai5[], int ai6[]);

    public static native void gbtrf(int ai[], int ai1[], int ai2[], int ai3[], double ad[], int ai4[], int ai5[], int ai6[]);

    public static native void gbtrs(char ac[], int ai[], int ai1[], int ai2[], int ai3[], double ad[], int ai4[], int ai5[], 
            double ad1[], int ai6[], int ai7[]);

    public static native void gebak(char ac[], char ac1[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], 
            int ai4[], int ai5[]);

    public static native void gebal(char ac[], int ai[], double ad[], int ai1[], int ai2[], int ai3[], double ad1[], int ai4[]);

    public static native void gebd2(int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], double ad3[], double ad4[], 
            double ad5[], int ai3[]);

    public static native void gebrd(int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], double ad3[], double ad4[], 
            double ad5[], int ai3[], int ai4[]);

    public static native void gecon(char ac[], int ai[], double ad[], int ai1[], double ad1[], double ad2[], double ad3[], int ai2[], 
            int ai3[]);

    public static native void geequ(int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], double ad3[], double ad4[], 
            double ad5[], int ai3[]);

    public static native void gees(char ac[], char ac1[], Object obj, int ai[], double ad[], int ai1[], int ai2[], double ad1[], 
            double ad2[], double ad3[], int ai3[], double ad4[], int ai4[], boolean aflag[], int ai5[]);

    public static native void geesx(char ac[], char ac1[], Object obj, char ac2[], int ai[], double ad[], int ai1[], int ai2[], 
            double ad1[], double ad2[], double ad3[], int ai3[], double ad4[], double ad5[], double ad6[], 
            int ai4[], int ai5[], int ai6[], boolean aflag[], int ai7[]);

    public static native void geev(char ac[], char ac1[], int ai[], double ad[], int ai1[], double ad1[], double ad2[], double ad3[], 
            int ai2[], double ad4[], int ai3[], double ad5[], int ai4[], int ai5[]);

    public static native void geevx(char ac[], char ac1[], char ac2[], char ac3[], int ai[], double ad[], int ai1[], double ad1[], 
            double ad2[], double ad3[], int ai2[], double ad4[], int ai3[], int ai4[], int ai5[], 
            double ad5[], double ad6[], double ad7[], double ad8[], double ad9[], int ai6[], int ai7[], 
            int ai8[]);

    public static native void gegs(char ac[], char ac1[], int ai[], double ad[], int ai1[], double ad1[], int ai2[], double ad2[], 
            double ad3[], double ad4[], double ad5[], int ai3[], double ad6[], int ai4[], double ad7[], 
            int ai5[], int ai6[]);

    public static native void gegv(char ac[], char ac1[], int ai[], double ad[], int ai1[], double ad1[], int ai2[], double ad2[], 
            double ad3[], double ad4[], double ad5[], int ai3[], double ad6[], int ai4[], double ad7[], 
            int ai5[], int ai6[]);

    public static native void gehd2(int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], double ad2[], int ai4[]);

    public static native void gehrd(int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], double ad2[], int ai4[], 
            int ai5[]);

    public static native void gelq2(int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], int ai3[]);

    public static native void gelqf(int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], int ai3[], int ai4[]);

    public static native void gels(char ac[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], int ai4[], 
            double ad2[], int ai5[], int ai6[]);

    public static native void gelsd(int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], int ai4[], double ad2[], 
            double ad3[], int ai5[], double ad4[], int ai6[], int ai7[], int ai8[]);

    public static native void gelss(int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], int ai4[], double ad2[], 
            double ad3[], int ai5[], double ad4[], int ai6[], int ai7[]);

    public static native void gelsx(int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], int ai4[], int ai5[], 
            double ad2[], int ai6[], double ad3[], int ai7[]);

    public static native void gelsy(int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], int ai4[], int ai5[], 
            double ad2[], int ai6[], double ad3[], int ai7[], int ai8[]);

    public static native void geql2(int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], int ai3[]);

    public static native void geqlf(int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], int ai3[], int ai4[]);

    public static native void geqp3(int ai[], int ai1[], double ad[], int ai2[], int ai3[], double ad1[], double ad2[], int ai4[], 
            int ai5[]);

    public static native void geqpf(int ai[], int ai1[], double ad[], int ai2[], int ai3[], double ad1[], double ad2[], int ai4[]);

    public static native void geqr2(int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], int ai3[]);

    public static native void geqrf(int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], int ai3[], int ai4[]);

    public static native void gerfs(char ac[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], int ai3[], int ai4[], 
            double ad2[], int ai5[], double ad3[], int ai6[], double ad4[], double ad5[], double ad6[], 
            int ai7[], int ai8[]);

    public static native void gerq2(int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], int ai3[]);

    public static native void gerqf(int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], int ai3[], int ai4[]);

    public static native void gesc2(int ai[], double ad[], int ai1[], double ad1[], int ai2[], int ai3[], double ad2[]);

    public static native void gesdd(char ac[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], int ai3[], 
            double ad3[], int ai4[], double ad4[], int ai5[], int ai6[], int ai7[]);

    public static native void gesv(int ai[], int ai1[], double ad[], int ai2[], int ai3[], double ad1[], int ai4[], int ai5[]);

    public static native void gesvd(char ac[], char ac1[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], 
            int ai3[], double ad3[], int ai4[], double ad4[], int ai5[], int ai6[]);

    public static native void gesvx(char ac[], char ac1[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], int ai3[], 
            int ai4[], char ac2[], double ad2[], double ad3[], double ad4[], int ai5[], double ad5[], 
            int ai6[], double ad6[], double ad7[], double ad8[], double ad9[], int ai7[], int ai8[]);

    public static native void getc2(int ai[], double ad[], int ai1[], int ai2[], int ai3[], int ai4[]);

    public static native void getf2(int ai[], int ai1[], double ad[], int ai2[], int ai3[], int ai4[]);

    public static native void getrf(int ai[], int ai1[], double ad[], int ai2[], int ai3[], int ai4[]);

    public static native void getri(int ai[], double ad[], int ai1[], int ai2[], double ad1[], int ai3[], int ai4[]);

    public static native void getrs(char ac[], int ai[], int ai1[], double ad[], int ai2[], int ai3[], double ad1[], int ai4[], 
            int ai5[]);

    public static native void ggbak(char ac[], char ac1[], int ai[], int ai1[], int ai2[], double ad[], double ad1[], int ai3[], 
            double ad2[], int ai4[], int ai5[]);

    public static native void ggbal(char ac[], int ai[], double ad[], int ai1[], double ad1[], int ai2[], int ai3[], int ai4[], 
            double ad2[], double ad3[], double ad4[], int ai5[]);

    public static native void gges(char ac[], char ac1[], char ac2[], Object obj, int ai[], double ad[], int ai1[], double ad1[], 
            int ai2[], int ai3[], double ad2[], double ad3[], double ad4[], double ad5[], int ai4[], 
            double ad6[], int ai5[], double ad7[], int ai6[], boolean aflag[], int ai7[]);

    public static native void ggesx(char ac[], char ac1[], char ac2[], Object obj, char ac3[], int ai[], double ad[], int ai1[], 
            double ad1[], int ai2[], int ai3[], double ad2[], double ad3[], double ad4[], double ad5[], 
            int ai4[], double ad6[], int ai5[], double ad7[], double ad8[], double ad9[], int ai6[], 
            int ai7[], int ai8[], boolean aflag[], int ai9[]);

    public static native void ggev(char ac[], char ac1[], int ai[], double ad[], int ai1[], double ad1[], int ai2[], double ad2[], 
            double ad3[], double ad4[], double ad5[], int ai3[], double ad6[], int ai4[], double ad7[], 
            int ai5[], int ai6[]);

    public static native void ggevx(char ac[], char ac1[], char ac2[], char ac3[], int ai[], double ad[], int ai1[], double ad1[], 
            int ai2[], double ad2[], double ad3[], double ad4[], double ad5[], int ai3[], double ad6[], 
            int ai4[], int ai5[], int ai6[], double ad7[], double ad8[], double ad9[], double ad10[], 
            double ad11[], double ad12[], double ad13[], int ai7[], int ai8[], boolean aflag[], int ai9[]);

    public static native void ggglm(int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], int ai4[], double ad2[], 
            double ad3[], double ad4[], double ad5[], int ai5[], int ai6[]);

    public static native void gghrd(char ac[], char ac1[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], 
            int ai4[], double ad2[], int ai5[], double ad3[], int ai6[], int ai7[]);

    public static native void gglse(int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], int ai4[], double ad2[], 
            double ad3[], double ad4[], double ad5[], int ai5[], int ai6[]);

    public static native void ggqrf(int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], double ad2[], int ai4[], 
            double ad3[], double ad4[], int ai5[], int ai6[]);

    public static native void ggrqf(int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], double ad2[], int ai4[], 
            double ad3[], double ad4[], int ai5[], int ai6[]);

    public static native void ggsvd(char ac[], char ac1[], char ac2[], int ai[], int ai1[], int ai2[], int ai3[], int ai4[], 
            double ad[], int ai5[], double ad1[], int ai6[], double ad2[], double ad3[], double ad4[], 
            int ai7[], double ad5[], int ai8[], double ad6[], int ai9[], double ad7[], int ai10[], 
            int ai11[]);

    public static native void ggsvp(char ac[], char ac1[], char ac2[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], 
            double ad1[], int ai4[], double ad2[], double ad3[], int ai5[], int ai6[], double ad4[], 
            int ai7[], double ad5[], int ai8[], double ad6[], int ai9[], int ai10[], double ad7[], 
            double ad8[], int ai11[]);

    public static native void gtcon(char ac[], int ai[], double ad[], double ad1[], double ad2[], double ad3[], int ai1[], double ad4[], 
            double ad5[], double ad6[], int ai2[], int ai3[]);

    public static native void gtrfs(char ac[], int ai[], int ai1[], double ad[], double ad1[], double ad2[], double ad3[], double ad4[], 
            double ad5[], double ad6[], int ai2[], double ad7[], int ai3[], double ad8[], int ai4[], 
            double ad9[], double ad10[], double ad11[], int ai5[], int ai6[]);

    public static native void gtsv(int ai[], int ai1[], double ad[], double ad1[], double ad2[], double ad3[], int ai2[], int ai3[]);

    public static native void gtsvx(char ac[], char ac1[], int ai[], int ai1[], double ad[], double ad1[], double ad2[], double ad3[], 
            double ad4[], double ad5[], double ad6[], int ai2[], double ad7[], int ai3[], double ad8[], 
            int ai4[], double ad9[], double ad10[], double ad11[], double ad12[], int ai5[], int ai6[]);

    public static native void gttrf(int ai[], double ad[], double ad1[], double ad2[], double ad3[], int ai1[], int ai2[]);

    public static native void gttrs(char ac[], int ai[], int ai1[], double ad[], double ad1[], double ad2[], double ad3[], int ai2[], 
            double ad4[], int ai3[], int ai4[]);

    public static native void gtts2(int ai[], int ai1[], int ai2[], double ad[], double ad1[], double ad2[], double ad3[], int ai3[], 
            double ad4[], int ai4[]);

    public static native void hgeqz(char ac[], char ac1[], char ac2[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], 
            double ad1[], int ai4[], double ad2[], double ad3[], double ad4[], double ad5[], int ai5[], 
            double ad6[], int ai6[], double ad7[], int ai7[], int ai8[]);

    public static native void hsein(char ac[], char ac1[], char ac2[], boolean aflag[], int ai[], double ad[], int ai1[], double ad1[], 
            double ad2[], double ad3[], int ai2[], double ad4[], int ai3[], int ai4[], int ai5[], 
            double ad5[], int ai6[], int ai7[], int ai8[]);

    public static native void hseqr(char ac[], char ac1[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], 
            double ad2[], double ad3[], int ai4[], double ad4[], int ai5[], int ai6[]);

    public static native void labad(double ad[], double ad1[]);

    public static native void labrd(int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], double ad2[], double ad3[], 
            double ad4[], double ad5[], int ai4[], double ad6[], int ai5[]);

    public static native void lacon(int ai[], double ad[], double ad1[], int ai1[], double ad2[], int ai2[]);

    public static native void lacpy(char ac[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], int ai3[]);

    public static native void ladiv(double ad[], double ad1[], double ad2[], double ad3[], double ad4[], double ad5[]);

    public static native void lae2(double ad[], double ad1[], double ad2[], double ad3[], double ad4[]);

    public static native void laebz(int ai[], int ai1[], int ai2[], int ai3[], int ai4[], int ai5[], double ad[], double ad1[], 
            double ad2[], double ad3[], double ad4[], double ad5[], int ai6[], double ad6[], double ad7[], 
            int ai7[], int ai8[], double ad8[], int ai9[], int ai10[]);

    public static native void laed0(int ai[], int ai1[], int ai2[], double ad[], double ad1[], double ad2[], int ai3[], double ad3[], 
            int ai4[], double ad4[], int ai5[], int ai6[]);

    public static native void laed1(int ai[], double ad[], double ad1[], int ai1[], int ai2[], double ad2[], int ai3[], double ad3[], 
            int ai4[], int ai5[]);

    public static native void laed2(int ai[], int ai1[], int ai2[], double ad[], double ad1[], int ai3[], int ai4[], double ad2[], 
            double ad3[], double ad4[], double ad5[], double ad6[], int ai5[], int ai6[], int ai7[], 
            int ai8[], int ai9[]);

    public static native void laed3(int ai[], int ai1[], int ai2[], double ad[], double ad1[], int ai3[], double ad2[], double ad3[], 
            double ad4[], int ai4[], int ai5[], double ad5[], double ad6[], int ai6[]);

    public static native void laed4(int ai[], int ai1[], double ad[], double ad1[], double ad2[], double ad3[], double ad4[], int ai2[]);

    public static native void laed5(int ai[], double ad[], double ad1[], double ad2[], double ad3[], double ad4[]);

    public static native void laed6(int ai[], boolean aflag[], double ad[], double ad1[], double ad2[], double ad3[], double ad4[], int ai1[]);

    public static native void laed7(int ai[], int ai1[], int ai2[], int ai3[], int ai4[], int ai5[], double ad[], double ad1[], 
            int ai6[], int ai7[], double ad2[], int ai8[], double ad3[], int ai9[], int ai10[], 
            int ai11[], int ai12[], int ai13[], double ad4[], double ad5[], int ai14[], int ai15[]);

    public static native void laed8(int ai[], int ai1[], int ai2[], int ai3[], double ad[], double ad1[], int ai4[], int ai5[], 
            double ad2[], int ai6[], double ad3[], double ad4[], double ad5[], int ai7[], double ad6[], 
            int ai8[], int ai9[], int ai10[], double ad7[], int ai11[], int ai12[], int ai13[]);

    public static native void laed9(int ai[], int ai1[], int ai2[], int ai3[], double ad[], double ad1[], int ai4[], double ad2[], 
            double ad3[], double ad4[], double ad5[], int ai5[], int ai6[]);

    public static native void laeda(int ai[], int ai1[], int ai2[], int ai3[], int ai4[], int ai5[], int ai6[], int ai7[], 
            double ad[], double ad1[], int ai8[], double ad2[], double ad3[], int ai9[]);

    public static native void laein(boolean aflag[], boolean aflag1[], int ai[], double ad[], int ai1[], double ad1[], double ad2[], double ad3[], 
            double ad4[], double ad5[], int ai2[], double ad6[], double ad7[], double ad8[], double ad9[], 
            int ai3[]);

    public static native void laev2(double ad[], double ad1[], double ad2[], double ad3[], double ad4[], double ad5[], double ad6[]);

    public static native void laexc(boolean aflag[], int ai[], double ad[], int ai1[], double ad1[], int ai2[], int ai3[], int ai4[], 
            int ai5[], double ad2[], int ai6[]);

    public static native void lag2(double ad[], int ai[], double ad1[], int ai1[], double ad2[], double ad3[], double ad4[], double ad5[], 
            double ad6[], double ad7[]);

    public static native void lags2(boolean aflag[], double ad[], double ad1[], double ad2[], double ad3[], double ad4[], double ad5[], double ad6[], 
            double ad7[], double ad8[], double ad9[], double ad10[], double ad11[]);

    public static native void lagtf(int ai[], double ad[], double ad1[], double ad2[], double ad3[], double ad4[], double ad5[], int ai1[], 
            int ai2[]);

    public static native void lagtm(char ac[], int ai[], int ai1[], double ad[], double ad1[], double ad2[], double ad3[], double ad4[], 
            int ai2[], double ad5[], double ad6[], int ai3[]);

    public static native void lagts(int ai[], int ai1[], double ad[], double ad1[], double ad2[], double ad3[], int ai2[], double ad4[], 
            double ad5[], int ai3[]);

    public static native void lagv2(double ad[], int ai[], double ad1[], int ai1[], double ad2[], double ad3[], double ad4[], double ad5[], 
            double ad6[], double ad7[], double ad8[]);

    public static native void lahqr(boolean aflag[], boolean aflag1[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], 
            double ad2[], int ai4[], int ai5[], double ad3[], int ai6[], int ai7[]);

    public static native void lahrd(int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], double ad2[], int ai4[], 
            double ad3[], int ai5[]);

    public static native void laic1(int ai[], int ai1[], double ad[], double ad1[], double ad2[], double ad3[], double ad4[], double ad5[], 
            double ad6[]);

    public static native void laln2(boolean aflag[], int ai[], int ai1[], double ad[], double ad1[], double ad2[], int ai2[], double ad3[], 
            double ad4[], double ad5[], int ai3[], double ad6[], double ad7[], double ad8[], int ai4[], 
            double ad9[], double ad10[], int ai5[]);

    public static native void lals0(int ai[], int ai1[], int ai2[], int ai3[], int ai4[], double ad[], int ai5[], double ad1[], 
            int ai6[], int ai7[], int ai8[], int ai9[], int ai10[], double ad2[], int ai11[], 
            double ad3[], double ad4[], double ad5[], double ad6[], int ai12[], double ad7[], double ad8[], 
            double ad9[], int ai13[]);

    public static native void lalsa(int ai[], int ai1[], int ai2[], int ai3[], double ad[], int ai4[], double ad1[], int ai5[], 
            double ad2[], int ai6[], double ad3[], int ai7[], double ad4[], double ad5[], double ad6[], 
            double ad7[], int ai8[], int ai9[], int ai10[], int ai11[], double ad8[], double ad9[], 
            double ad10[], double ad11[], int ai12[], int ai13[]);

    public static native void lalsd(char ac[], int ai[], int ai1[], int ai2[], double ad[], double ad1[], double ad2[], int ai3[], 
            double ad3[], int ai4[], double ad4[], int ai5[], int ai6[]);

    public static native void lamc1(int ai[], int ai1[], boolean aflag[], boolean aflag1[]);

    public static native void lamc2(int ai[], int ai1[], boolean aflag[], double ad[], int ai2[], double ad1[], int ai3[], double ad2[]);

    public static native void lamc4(int ai[], double ad[], int ai1[]);

    public static native void lamc5(int ai[], int ai1[], int ai2[], boolean aflag[], int ai3[], double ad[]);

    public static native void lamrg(int ai[], int ai1[], double ad[], int ai2[], int ai3[], int ai4[]);

    public static native void lanv2(double ad[], double ad1[], double ad2[], double ad3[], double ad4[], double ad5[], double ad6[], double ad7[], 
            double ad8[], double ad9[]);

    public static native void lapll(int ai[], double ad[], int ai1[], double ad1[], int ai2[], double ad2[]);

    public static native void lapmt(boolean aflag[], int ai[], int ai1[], double ad[], int ai2[], int ai3[]);

    public static native void laqgb(int ai[], int ai1[], int ai2[], int ai3[], double ad[], int ai4[], double ad1[], double ad2[], 
            double ad3[], double ad4[], double ad5[], char ac[]);

    public static native void laqge(int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], double ad3[], double ad4[], 
            double ad5[], char ac[]);

    public static native void laqp2(int ai[], int ai1[], int ai2[], double ad[], int ai3[], int ai4[], double ad1[], double ad2[], 
            double ad3[], double ad4[]);

    public static native void laqps(int ai[], int ai1[], int ai2[], int ai3[], int ai4[], double ad[], int ai5[], int ai6[], 
            double ad1[], double ad2[], double ad3[], double ad4[], double ad5[], int ai7[]);

    public static native void laqsb(char ac[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], double ad3[], 
            char ac1[]);

    public static native void laqsp(char ac[], int ai[], double ad[], double ad1[], double ad2[], double ad3[], char ac1[]);

    public static native void laqsy(char ac[], int ai[], double ad[], int ai1[], double ad1[], double ad2[], double ad3[], char ac1[]);

    public static native void laqtr(boolean aflag[], boolean aflag1[], int ai[], double ad[], int ai1[], double ad1[], double ad2[], double ad3[], 
            double ad4[], double ad5[], int ai2[]);

    public static native void lar1v(int ai[], int ai1[], int ai2[], double ad[], double ad1[], double ad2[], double ad3[], double ad4[], 
            double ad5[], double ad6[], double ad7[], double ad8[], int ai3[], int ai4[], double ad9[]);

    public static native void lar2v(int ai[], double ad[], double ad1[], double ad2[], int ai1[], double ad3[], double ad4[], int ai2[]);

    public static native void larf(char ac[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], int ai3[], 
            double ad3[]);

    public static native void larfb(char ac[], char ac1[], char ac2[], char ac3[], int ai[], int ai1[], int ai2[], double ad[], 
            int ai3[], double ad1[], int ai4[], double ad2[], int ai5[], double ad3[], int ai6[]);

    public static native void larfg(int ai[], double ad[], double ad1[], int ai1[], double ad2[]);

    public static native void larft(char ac[], char ac1[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], 
            int ai3[]);

    public static native void larfx(char ac[], int ai[], int ai1[], double ad[], double ad1[], double ad2[], int ai2[], double ad3[]);

    public static native void largv(int ai[], double ad[], int ai1[], double ad1[], int ai2[], double ad2[], int ai3[]);

    public static native void larnv(int ai[], int ai1[], int ai2[], double ad[]);

    public static native void larrb(int ai[], double ad[], double ad1[], double ad2[], double ad3[], int ai1[], int ai2[], double ad4[], 
            double ad5[], double ad6[], double ad7[], double ad8[], double ad9[], int ai3[], int ai4[]);

    public static native void larre(int ai[], double ad[], double ad1[], double ad2[], int ai1[], int ai2[], int ai3[], double ad3[], 
            double ad4[], double ad5[], double ad6[], int ai4[]);

    public static native void larrf(int ai[], double ad[], double ad1[], double ad2[], double ad3[], int ai1[], int ai2[], double ad4[], 
            double ad5[], double ad6[], double ad7[], int ai3[], int ai4[]);

    public static native void larrv(int ai[], double ad[], double ad1[], int ai1[], int ai2[], double ad2[], int ai3[], double ad3[], 
            double ad4[], double ad5[], int ai4[], int ai5[], double ad6[], int ai6[], int ai7[]);

    public static native void lartg(double ad[], double ad1[], double ad2[], double ad3[], double ad4[]);

    public static native void lartv(int ai[], double ad[], int ai1[], double ad1[], int ai2[], double ad2[], double ad3[], int ai3[]);

    public static native void laruv(int ai[], int ai1[], double ad[]);

    public static native void larz(char ac[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], double ad2[], 
            int ai4[], double ad3[]);

    public static native void larzb(char ac[], char ac1[], char ac2[], char ac3[], int ai[], int ai1[], int ai2[], int ai3[], 
            double ad[], int ai4[], double ad1[], int ai5[], double ad2[], int ai6[], double ad3[], 
            int ai7[]);

    public static native void larzt(char ac[], char ac1[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], 
            int ai3[]);

    public static native void las2(double ad[], double ad1[], double ad2[], double ad3[], double ad4[]);

    public static native void lascl(char ac[], int ai[], int ai1[], double ad[], double ad1[], int ai2[], int ai3[], double ad2[], 
            int ai4[], int ai5[]);

    public static native void lasd0(int ai[], int ai1[], double ad[], double ad1[], double ad2[], int ai2[], double ad3[], int ai3[], 
            int ai4[], int ai5[], double ad4[], int ai6[]);

    public static native void lasd1(int ai[], int ai1[], int ai2[], double ad[], double ad1[], double ad2[], double ad3[], int ai3[], 
            double ad4[], int ai4[], int ai5[], int ai6[], double ad5[], int ai7[]);

    public static native void lasd2(int ai[], int ai1[], int ai2[], int ai3[], double ad[], double ad1[], double ad2[], double ad3[], 
            double ad4[], int ai4[], double ad5[], int ai5[], double ad6[], double ad7[], int ai6[], 
            double ad8[], int ai7[], int ai8[], int ai9[], int ai10[], int ai11[], int ai12[], 
            int ai13[]);

    public static native void lasd3(int ai[], int ai1[], int ai2[], int ai3[], double ad[], double ad1[], int ai4[], double ad2[], 
            double ad3[], int ai5[], double ad4[], int ai6[], double ad5[], int ai7[], double ad6[], 
            int ai8[], int ai9[], int ai10[], double ad7[], int ai11[]);

    public static native void lasd4(int ai[], int ai1[], double ad[], double ad1[], double ad2[], double ad3[], double ad4[], double ad5[], 
            int ai2[]);

    public static native void lasd5(int ai[], double ad[], double ad1[], double ad2[], double ad3[], double ad4[], double ad5[]);

    public static native void lasd6(int ai[], int ai1[], int ai2[], int ai3[], double ad[], double ad1[], double ad2[], double ad3[], 
            double ad4[], int ai4[], int ai5[], int ai6[], int ai7[], int ai8[], double ad5[], 
            int ai9[], double ad6[], double ad7[], double ad8[], double ad9[], int ai10[], double ad10[], 
            double ad11[], double ad12[], int ai11[], int ai12[]);

    public static native void lasd7(int ai[], int ai1[], int ai2[], int ai3[], int ai4[], double ad[], double ad1[], double ad2[], 
            double ad3[], double ad4[], double ad5[], double ad6[], double ad7[], double ad8[], double ad9[], 
            int ai5[], int ai6[], int ai7[], int ai8[], int ai9[], int ai10[], int ai11[], 
            double ad10[], int ai12[], double ad11[], double ad12[], int ai13[]);

    public static native void lasd8(int ai[], int ai1[], double ad[], double ad1[], double ad2[], double ad3[], double ad4[], double ad5[], 
            int ai2[], double ad6[], double ad7[], int ai3[]);

    public static native void lasd9(int ai[], int ai1[], int ai2[], double ad[], double ad1[], double ad2[], double ad3[], double ad4[], 
            double ad5[], double ad6[], double ad7[], int ai3[]);

    public static native void lasda(int ai[], int ai1[], int ai2[], int ai3[], double ad[], double ad1[], double ad2[], int ai4[], 
            double ad3[], int ai5[], double ad4[], double ad5[], double ad6[], double ad7[], int ai6[], 
            int ai7[], int ai8[], int ai9[], double ad8[], double ad9[], double ad10[], double ad11[], 
            int ai10[], int ai11[]);

    public static native void lasdq(char ac[], int ai[], int ai1[], int ai2[], int ai3[], int ai4[], double ad[], double ad1[], 
            double ad2[], int ai5[], double ad3[], int ai6[], double ad4[], int ai7[], double ad5[], 
            int ai8[]);

    public static native void lasdt(int ai[], int ai1[], int ai2[], int ai3[], int ai4[], int ai5[], int ai6[]);

    public static native void laset(char ac[], int ai[], int ai1[], double ad[], double ad1[], double ad2[], int ai2[]);

    public static native void lasq1(int ai[], double ad[], double ad1[], double ad2[], int ai1[]);

    public static native void lasq2(int ai[], double ad[], int ai1[]);

    public static native void lasq3(int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], double ad3[], double ad4[], 
            int ai3[], int ai4[], int ai5[], boolean aflag[]);

    public static native void lasq4(int ai[], int ai1[], double ad[], int ai2[], int ai3[], double ad1[], double ad2[], double ad3[], 
            double ad4[], double ad5[], double ad6[], double ad7[], int ai4[]);

    public static native void lasq5(int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], double ad3[], double ad4[], 
            double ad5[], double ad6[], double ad7[], boolean aflag[]);

    public static native void lasq6(int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], double ad3[], double ad4[], 
            double ad5[], double ad6[]);

    public static native void lasr(char ac[], char ac1[], char ac2[], int ai[], int ai1[], double ad[], double ad1[], double ad2[], 
            int ai2[]);

    public static native void lasrt(char ac[], int ai[], double ad[], int ai1[]);

    public static native void lassq(int ai[], double ad[], int ai1[], double ad1[], double ad2[]);

    public static native void lasv2(double ad[], double ad1[], double ad2[], double ad3[], double ad4[], double ad5[], double ad6[], double ad7[], 
            double ad8[]);

    public static native void laswp(int ai[], double ad[], int ai1[], int ai2[], int ai3[], int ai4[], int ai5[]);

    public static native void lasy2(boolean aflag[], boolean aflag1[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], 
            int ai4[], double ad2[], int ai5[], double ad3[], double ad4[], int ai6[], double ad5[], 
            int ai7[]);

    public static native void lasyf(char ac[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], int ai4[], double ad1[], 
            int ai5[], int ai6[]);

    public static native void latbs(char ac[], char ac1[], char ac2[], char ac3[], int ai[], int ai1[], double ad[], int ai2[], 
            double ad1[], double ad2[], double ad3[], int ai3[]);

    public static native void latdf(int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], double ad3[], int ai3[], 
            int ai4[]);

    public static native void latps(char ac[], char ac1[], char ac2[], char ac3[], int ai[], double ad[], double ad1[], double ad2[], 
            double ad3[], int ai1[]);

    public static native void latrd(char ac[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], double ad3[], 
            int ai3[]);

    public static native void latrs(char ac[], char ac1[], char ac2[], char ac3[], int ai[], double ad[], int ai1[], double ad1[], 
            double ad2[], double ad3[], int ai2[]);

    public static native void latrz(int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], double ad2[]);

    public static native void latzm(char ac[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], double ad3[], 
            int ai3[], double ad4[]);

    public static native void lauu2(char ac[], int ai[], double ad[], int ai1[], int ai2[]);

    public static native void lauum(char ac[], int ai[], double ad[], int ai1[], int ai2[]);

    public static native void opgtr(char ac[], int ai[], double ad[], double ad1[], double ad2[], int ai1[], double ad3[], int ai2[]);

    public static native void opmtr(char ac[], char ac1[], char ac2[], int ai[], int ai1[], double ad[], double ad1[], double ad2[], 
            int ai2[], double ad3[], int ai3[]);

    public static native void org2l(int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], double ad2[], int ai4[]);

    public static native void org2r(int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], double ad2[], int ai4[]);

    public static native void orgbr(char ac[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], double ad2[], 
            int ai4[], int ai5[]);

    public static native void orghr(int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], double ad2[], int ai4[], 
            int ai5[]);

    public static native void orgl2(int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], double ad2[], int ai4[]);

    public static native void orglq(int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], double ad2[], int ai4[], 
            int ai5[]);

    public static native void orgql(int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], double ad2[], int ai4[], 
            int ai5[]);

    public static native void orgqr(int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], double ad2[], int ai4[], 
            int ai5[]);

    public static native void orgr2(int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], double ad2[], int ai4[]);

    public static native void orgrq(int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], double ad2[], int ai4[], 
            int ai5[]);

    public static native void orgtr(char ac[], int ai[], double ad[], int ai1[], double ad1[], double ad2[], int ai2[], int ai3[]);

    public static native void orm2l(char ac[], char ac1[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], 
            double ad2[], int ai4[], double ad3[], int ai5[]);

    public static native void orm2r(char ac[], char ac1[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], 
            double ad2[], int ai4[], double ad3[], int ai5[]);

    public static native void ormbr(char ac[], char ac1[], char ac2[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], 
            double ad1[], double ad2[], int ai4[], double ad3[], int ai5[], int ai6[]);

    public static native void ormhr(char ac[], char ac1[], int ai[], int ai1[], int ai2[], int ai3[], double ad[], int ai4[], 
            double ad1[], double ad2[], int ai5[], double ad3[], int ai6[], int ai7[]);

    public static native void orml2(char ac[], char ac1[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], 
            double ad2[], int ai4[], double ad3[], int ai5[]);

    public static native void ormlq(char ac[], char ac1[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], 
            double ad2[], int ai4[], double ad3[], int ai5[], int ai6[]);

    public static native void ormql(char ac[], char ac1[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], 
            double ad2[], int ai4[], double ad3[], int ai5[], int ai6[]);

    public static native void ormqr(char ac[], char ac1[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], 
            double ad2[], int ai4[], double ad3[], int ai5[], int ai6[]);

    public static native void ormr2(char ac[], char ac1[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], 
            double ad2[], int ai4[], double ad3[], int ai5[]);

    public static native void ormr3(char ac[], char ac1[], int ai[], int ai1[], int ai2[], int ai3[], double ad[], int ai4[], 
            double ad1[], double ad2[], int ai5[], double ad3[], int ai6[]);

    public static native void ormrq(char ac[], char ac1[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], 
            double ad2[], int ai4[], double ad3[], int ai5[], int ai6[]);

    public static native void ormrz(char ac[], char ac1[], int ai[], int ai1[], int ai2[], int ai3[], double ad[], int ai4[], 
            double ad1[], double ad2[], int ai5[], double ad3[], int ai6[], int ai7[]);

    public static native void ormtr(char ac[], char ac1[], char ac2[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], 
            double ad2[], int ai3[], double ad3[], int ai4[], int ai5[]);

    public static native void pbcon(char ac[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], double ad3[], 
            int ai3[], int ai4[]);

    public static native void pbequ(char ac[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], double ad3[], 
            int ai3[]);

    public static native void pbrfs(char ac[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], int ai4[], 
            double ad2[], int ai5[], double ad3[], int ai6[], double ad4[], double ad5[], double ad6[], 
            int ai7[], int ai8[]);

    public static native void pbstf(char ac[], int ai[], int ai1[], double ad[], int ai2[], int ai3[]);

    public static native void pbsv(char ac[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], int ai4[], 
            int ai5[]);

    public static native void pbsvx(char ac[], char ac1[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], 
            int ai4[], char ac2[], double ad2[], double ad3[], int ai5[], double ad4[], int ai6[], 
            double ad5[], double ad6[], double ad7[], double ad8[], int ai7[], int ai8[]);

    public static native void pbtf2(char ac[], int ai[], int ai1[], double ad[], int ai2[], int ai3[]);

    public static native void pbtrf(char ac[], int ai[], int ai1[], double ad[], int ai2[], int ai3[]);

    public static native void pbtrs(char ac[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], int ai4[], 
            int ai5[]);

    public static native void pocon(char ac[], int ai[], double ad[], int ai1[], double ad1[], double ad2[], double ad3[], int ai2[], 
            int ai3[]);

    public static native void poequ(int ai[], double ad[], int ai1[], double ad1[], double ad2[], double ad3[], int ai2[]);

    public static native void porfs(char ac[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], int ai3[], double ad2[], 
            int ai4[], double ad3[], int ai5[], double ad4[], double ad5[], double ad6[], int ai6[], 
            int ai7[]);

    public static native void posv(char ac[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], int ai3[], int ai4[]);

    public static native void posvx(char ac[], char ac1[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], int ai3[], 
            char ac2[], double ad2[], double ad3[], int ai4[], double ad4[], int ai5[], double ad5[], 
            double ad6[], double ad7[], double ad8[], int ai6[], int ai7[]);

    public static native void potf2(char ac[], int ai[], double ad[], int ai1[], int ai2[]);

    public static native void potrf(char ac[], int ai[], double ad[], int ai1[], int ai2[]);

    public static native void potri(char ac[], int ai[], double ad[], int ai1[], int ai2[]);

    public static native void potrs(char ac[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], int ai3[], int ai4[]);

    public static native void ppcon(char ac[], int ai[], double ad[], double ad1[], double ad2[], double ad3[], int ai1[], int ai2[]);

    public static native void ppequ(char ac[], int ai[], double ad[], double ad1[], double ad2[], double ad3[], int ai1[]);

    public static native void pprfs(char ac[], int ai[], int ai1[], double ad[], double ad1[], double ad2[], int ai2[], double ad3[], 
            int ai3[], double ad4[], double ad5[], double ad6[], int ai4[], int ai5[]);

    public static native void ppsv(char ac[], int ai[], int ai1[], double ad[], double ad1[], int ai2[], int ai3[]);

    public static native void ppsvx(char ac[], char ac1[], int ai[], int ai1[], double ad[], double ad1[], char ac2[], double ad2[], 
            double ad3[], int ai2[], double ad4[], int ai3[], double ad5[], double ad6[], double ad7[], 
            double ad8[], int ai4[], int ai5[]);

    public static native void pptrf(char ac[], int ai[], double ad[], int ai1[]);

    public static native void pptri(char ac[], int ai[], double ad[], int ai1[]);

    public static native void pptrs(char ac[], int ai[], int ai1[], double ad[], double ad1[], int ai2[], int ai3[]);

    public static native void ptcon(int ai[], double ad[], double ad1[], double ad2[], double ad3[], double ad4[], int ai1[]);

    public static native void pteqr(char ac[], int ai[], double ad[], double ad1[], double ad2[], int ai1[], double ad3[], int ai2[]);

    public static native void ptrfs(int ai[], int ai1[], double ad[], double ad1[], double ad2[], double ad3[], double ad4[], int ai2[], 
            double ad5[], int ai3[], double ad6[], double ad7[], double ad8[], int ai4[]);

    public static native void ptsv(int ai[], int ai1[], double ad[], double ad1[], double ad2[], int ai2[], int ai3[]);

    public static native void ptsvx(char ac[], int ai[], int ai1[], double ad[], double ad1[], double ad2[], double ad3[], double ad4[], 
            int ai2[], double ad5[], int ai3[], double ad6[], double ad7[], double ad8[], double ad9[], 
            int ai4[]);

    public static native void pttrf(int ai[], double ad[], double ad1[], int ai1[]);

    public static native void pttrs(int ai[], int ai1[], double ad[], double ad1[], double ad2[], int ai2[], int ai3[]);

    public static native void ptts2(int ai[], int ai1[], double ad[], double ad1[], double ad2[], int ai2[]);

    public static native void rscl(int ai[], double ad[], double ad1[], int ai1[]);

    public static native void sbev(char ac[], char ac1[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], 
            int ai3[], double ad3[], int ai4[]);

    public static native void sbevd(char ac[], char ac1[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], 
            int ai3[], double ad3[], int ai4[], int ai5[], int ai6[], int ai7[]);

    public static native void sbevx(char ac[], char ac1[], char ac2[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], 
            int ai3[], double ad2[], double ad3[], int ai4[], int ai5[], double ad4[], int ai6[], 
            double ad5[], double ad6[], int ai7[], double ad7[], int ai8[], int ai9[], int ai10[]);

    public static native void sbgst(char ac[], char ac1[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], 
            int ai4[], double ad2[], int ai5[], double ad3[], int ai6[]);

    public static native void sbgv(char ac[], char ac1[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], 
            int ai4[], double ad2[], double ad3[], int ai5[], double ad4[], int ai6[]);

    public static native void sbgvd(char ac[], char ac1[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], 
            int ai4[], double ad2[], double ad3[], int ai5[], double ad4[], int ai6[], int ai7[], 
            int ai8[], int ai9[]);

    public static native void sbgvx(char ac[], char ac1[], char ac2[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], 
            double ad1[], int ai4[], double ad2[], int ai5[], double ad3[], double ad4[], int ai6[], 
            int ai7[], double ad5[], int ai8[], double ad6[], double ad7[], int ai9[], double ad8[], 
            int ai10[], int ai11[], int ai12[]);

    public static native void sbtrd(char ac[], char ac1[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], 
            double ad3[], int ai3[], double ad4[], int ai4[]);

    public static native void spcon(char ac[], int ai[], double ad[], int ai1[], double ad1[], double ad2[], double ad3[], int ai2[], 
            int ai3[]);

    public static native void spev(char ac[], char ac1[], int ai[], double ad[], double ad1[], double ad2[], int ai1[], double ad3[], 
            int ai2[]);

    public static native void spevd(char ac[], char ac1[], int ai[], double ad[], double ad1[], double ad2[], int ai1[], double ad3[], 
            int ai2[], int ai3[], int ai4[], int ai5[]);

    public static native void spevx(char ac[], char ac1[], char ac2[], int ai[], double ad[], double ad1[], double ad2[], int ai1[], 
            int ai2[], double ad3[], int ai3[], double ad4[], double ad5[], int ai4[], double ad6[], 
            int ai5[], int ai6[], int ai7[]);

    public static native void spgst(int ai[], char ac[], int ai1[], double ad[], double ad1[], int ai2[]);

    public static native void spgv(int ai[], char ac[], char ac1[], int ai1[], double ad[], double ad1[], double ad2[], double ad3[], 
            int ai2[], double ad4[], int ai3[]);

    public static native void spgvd(int ai[], char ac[], char ac1[], int ai1[], double ad[], double ad1[], double ad2[], double ad3[], 
            int ai2[], double ad4[], int ai3[], int ai4[], int ai5[], int ai6[]);

    public static native void spgvx(int ai[], char ac[], char ac1[], char ac2[], int ai1[], double ad[], double ad1[], double ad2[], 
            double ad3[], int ai2[], int ai3[], double ad4[], int ai4[], double ad5[], double ad6[], 
            int ai5[], double ad7[], int ai6[], int ai7[], int ai8[]);

    public static native void sprfs(char ac[], int ai[], int ai1[], double ad[], double ad1[], int ai2[], double ad2[], int ai3[], 
            double ad3[], int ai4[], double ad4[], double ad5[], double ad6[], int ai5[], int ai6[]);

    public static native void spsv(char ac[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], int ai3[], int ai4[]);

    public static native void spsvx(char ac[], char ac1[], int ai[], int ai1[], double ad[], double ad1[], int ai2[], double ad2[], 
            int ai3[], double ad3[], int ai4[], double ad4[], double ad5[], double ad6[], double ad7[], 
            int ai5[], int ai6[]);

    public static native void sptrd(char ac[], int ai[], double ad[], double ad1[], double ad2[], double ad3[], int ai1[]);

    public static native void sptrf(char ac[], int ai[], double ad[], int ai1[], int ai2[]);

    public static native void sptri(char ac[], int ai[], double ad[], int ai1[], double ad1[], int ai2[]);

    public static native void sptrs(char ac[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], int ai3[], int ai4[]);

    public static native void stebz(char ac[], char ac1[], int ai[], double ad[], double ad1[], int ai1[], int ai2[], double ad2[], 
            double ad3[], double ad4[], int ai3[], int ai4[], double ad5[], int ai5[], int ai6[], 
            double ad6[], int ai7[], int ai8[]);

    public static native void stedc(char ac[], int ai[], double ad[], double ad1[], double ad2[], int ai1[], double ad3[], int ai2[], 
            int ai3[], int ai4[], int ai5[]);

    public static native void stegr(char ac[], char ac1[], int ai[], double ad[], double ad1[], double ad2[], double ad3[], int ai1[], 
            int ai2[], double ad4[], int ai3[], double ad5[], double ad6[], int ai4[], int ai5[], 
            double ad7[], int ai6[], int ai7[], int ai8[], int ai9[]);

    public static native void stein(int ai[], double ad[], double ad1[], int ai1[], double ad2[], int ai2[], int ai3[], double ad3[], 
            int ai4[], double ad4[], int ai5[], int ai6[], int ai7[]);

    public static native void steqr(char ac[], int ai[], double ad[], double ad1[], double ad2[], int ai1[], double ad3[], int ai2[]);

    public static native void sterf(int ai[], double ad[], double ad1[], int ai1[]);

    public static native void stev(char ac[], int ai[], double ad[], double ad1[], double ad2[], int ai1[], double ad3[], int ai2[]);

    public static native void stevd(char ac[], int ai[], double ad[], double ad1[], double ad2[], int ai1[], double ad3[], int ai2[], 
            int ai3[], int ai4[], int ai5[]);

    public static native void stevr(char ac[], char ac1[], int ai[], double ad[], double ad1[], double ad2[], double ad3[], int ai1[], 
            int ai2[], double ad4[], int ai3[], double ad5[], double ad6[], int ai4[], int ai5[], 
            double ad7[], int ai6[], int ai7[], int ai8[], int ai9[]);

    public static native void stevx(char ac[], char ac1[], int ai[], double ad[], double ad1[], double ad2[], double ad3[], int ai1[], 
            int ai2[], double ad4[], int ai3[], double ad5[], double ad6[], int ai4[], double ad7[], 
            int ai5[], int ai6[], int ai7[]);

    public static native void sycon(char ac[], int ai[], double ad[], int ai1[], int ai2[], double ad1[], double ad2[], double ad3[], 
            int ai3[], int ai4[]);

    public static native void syev(char ac[], char ac1[], int ai[], double ad[], int ai1[], double ad1[], double ad2[], int ai2[], 
            int ai3[]);

    public static native void syevd(char ac[], char ac1[], int ai[], double ad[], int ai1[], double ad1[], double ad2[], int ai2[], 
            int ai3[], int ai4[], int ai5[]);

    public static native void syevr(char ac[], char ac1[], char ac2[], int ai[], double ad[], int ai1[], double ad1[], double ad2[], 
            int ai2[], int ai3[], double ad3[], int ai4[], double ad4[], double ad5[], int ai5[], 
            int ai6[], double ad6[], int ai7[], int ai8[], int ai9[], int ai10[]);

    public static native void syevx(char ac[], char ac1[], char ac2[], int ai[], double ad[], int ai1[], double ad1[], double ad2[], 
            int ai2[], int ai3[], double ad3[], int ai4[], double ad4[], double ad5[], int ai5[], 
            double ad6[], int ai6[], int ai7[], int ai8[], int ai9[]);

    public static native void sygs2(int ai[], char ac[], int ai1[], double ad[], int ai2[], double ad1[], int ai3[], int ai4[]);

    public static native void sygst(int ai[], char ac[], int ai1[], double ad[], int ai2[], double ad1[], int ai3[], int ai4[]);

    public static native void sygv(int ai[], char ac[], char ac1[], int ai1[], double ad[], int ai2[], double ad1[], int ai3[], 
            double ad2[], double ad3[], int ai4[], int ai5[]);

    public static native void sygvd(int ai[], char ac[], char ac1[], int ai1[], double ad[], int ai2[], double ad1[], int ai3[], 
            double ad2[], double ad3[], int ai4[], int ai5[], int ai6[], int ai7[]);

    public static native void sygvx(int ai[], char ac[], char ac1[], char ac2[], int ai1[], double ad[], int ai2[], double ad1[], 
            int ai3[], double ad2[], double ad3[], int ai4[], int ai5[], double ad4[], int ai6[], 
            double ad5[], double ad6[], int ai7[], double ad7[], int ai8[], int ai9[], int ai10[], 
            int ai11[]);

    public static native void syrfs(char ac[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], int ai3[], int ai4[], 
            double ad2[], int ai5[], double ad3[], int ai6[], double ad4[], double ad5[], double ad6[], 
            int ai7[], int ai8[]);

    public static native void sysv(char ac[], int ai[], int ai1[], double ad[], int ai2[], int ai3[], double ad1[], int ai4[], 
            double ad2[], int ai5[], int ai6[]);

    public static native void sysvx(char ac[], char ac1[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], int ai3[], 
            int ai4[], double ad2[], int ai5[], double ad3[], int ai6[], double ad4[], double ad5[], 
            double ad6[], double ad7[], int ai7[], int ai8[], int ai9[]);

    public static native void sytd2(char ac[], int ai[], double ad[], int ai1[], double ad1[], double ad2[], double ad3[], int ai2[]);

    public static native void sytf2(char ac[], int ai[], double ad[], int ai1[], int ai2[], int ai3[]);

    public static native void sytrd(char ac[], int ai[], double ad[], int ai1[], double ad1[], double ad2[], double ad3[], double ad4[], 
            int ai2[], int ai3[]);

    public static native void sytrf(char ac[], int ai[], double ad[], int ai1[], int ai2[], double ad1[], int ai3[], int ai4[]);

    public static native void sytri(char ac[], int ai[], double ad[], int ai1[], int ai2[], double ad1[], int ai3[]);

    public static native void sytrs(char ac[], int ai[], int ai1[], double ad[], int ai2[], int ai3[], double ad1[], int ai4[], 
            int ai5[]);

    public static native void tbcon(char ac[], char ac1[], char ac2[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], 
            double ad2[], int ai3[], int ai4[]);

    public static native void tbrfs(char ac[], char ac1[], char ac2[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], 
            double ad1[], int ai4[], double ad2[], int ai5[], double ad3[], double ad4[], double ad5[], 
            int ai6[], int ai7[]);

    public static native void tbtrs(char ac[], char ac1[], char ac2[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], 
            double ad1[], int ai4[], int ai5[]);

    public static native void tgevc(char ac[], char ac1[], boolean aflag[], int ai[], double ad[], int ai1[], double ad1[], int ai2[], 
            double ad2[], int ai3[], double ad3[], int ai4[], int ai5[], int ai6[], double ad4[], 
            int ai7[]);

    public static native void tgex2(boolean aflag[], boolean aflag1[], int ai[], double ad[], int ai1[], double ad1[], int ai2[], double ad2[], 
            int ai3[], double ad3[], int ai4[], int ai5[], int ai6[], int ai7[], double ad4[], 
            int ai8[], int ai9[]);

    public static native void tgexc(boolean aflag[], boolean aflag1[], int ai[], double ad[], int ai1[], double ad1[], int ai2[], double ad2[], 
            int ai3[], double ad3[], int ai4[], int ai5[], int ai6[], double ad4[], int ai7[], 
            int ai8[]);

    public static native void tgsen(int ai[], boolean aflag[], boolean aflag1[], boolean aflag2[], int ai1[], double ad[], int ai2[], double ad1[], 
            int ai3[], double ad2[], double ad3[], double ad4[], double ad5[], int ai4[], double ad6[], 
            int ai5[], int ai6[], double ad7[], double ad8[], double ad9[], double ad10[], int ai7[], 
            int ai8[], int ai9[], int ai10[]);

    public static native void tgsja(char ac[], char ac1[], char ac2[], int ai[], int ai1[], int ai2[], int ai3[], int ai4[], 
            double ad[], int ai5[], double ad1[], int ai6[], double ad2[], double ad3[], double ad4[], 
            double ad5[], double ad6[], int ai7[], double ad7[], int ai8[], double ad8[], int ai9[], 
            double ad9[], int ai10[], int ai11[]);

    public static native void tgsna(char ac[], char ac1[], boolean aflag[], int ai[], double ad[], int ai1[], double ad1[], int ai2[], 
            double ad2[], int ai3[], double ad3[], int ai4[], double ad4[], double ad5[], int ai5[], 
            int ai6[], double ad6[], int ai7[], int ai8[], int ai9[]);

    public static native void tgsy2(char ac[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], int ai4[], 
            double ad2[], int ai5[], double ad3[], int ai6[], double ad4[], int ai7[], double ad5[], 
            int ai8[], double ad6[], double ad7[], double ad8[], int ai9[], int ai10[], int ai11[]);

    public static native void tgsyl(char ac[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], int ai4[], 
            double ad2[], int ai5[], double ad3[], int ai6[], double ad4[], int ai7[], double ad5[], 
            int ai8[], double ad6[], double ad7[], double ad8[], int ai9[], int ai10[], int ai11[]);

    public static native void tpcon(char ac[], char ac1[], char ac2[], int ai[], double ad[], double ad1[], double ad2[], int ai1[], 
            int ai2[]);

    public static native void tprfs(char ac[], char ac1[], char ac2[], int ai[], int ai1[], double ad[], double ad1[], int ai2[], 
            double ad2[], int ai3[], double ad3[], double ad4[], double ad5[], int ai4[], int ai5[]);

    public static native void tptri(char ac[], char ac1[], int ai[], double ad[], int ai1[]);

    public static native void tptrs(char ac[], char ac1[], char ac2[], int ai[], int ai1[], double ad[], double ad1[], int ai2[], 
            int ai3[]);

    public static native void trcon(char ac[], char ac1[], char ac2[], int ai[], double ad[], int ai1[], double ad1[], double ad2[], 
            int ai2[], int ai3[]);

    public static native void trevc(char ac[], char ac1[], boolean aflag[], int ai[], double ad[], int ai1[], double ad1[], int ai2[], 
            double ad2[], int ai3[], int ai4[], int ai5[], double ad3[], int ai6[]);

    public static native void trexc(char ac[], int ai[], double ad[], int ai1[], double ad1[], int ai2[], int ai3[], int ai4[], 
            double ad2[], int ai5[]);

    public static native void trrfs(char ac[], char ac1[], char ac2[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], 
            int ai3[], double ad2[], int ai4[], double ad3[], double ad4[], double ad5[], int ai5[], 
            int ai6[]);

    public static native void trsen(char ac[], char ac1[], boolean aflag[], int ai[], double ad[], int ai1[], double ad1[], int ai2[], 
            double ad2[], double ad3[], int ai3[], double ad4[], double ad5[], double ad6[], int ai4[], 
            int ai5[], int ai6[], int ai7[]);

    public static native void trsna(char ac[], char ac1[], boolean aflag[], int ai[], double ad[], int ai1[], double ad1[], int ai2[], 
            double ad2[], int ai3[], double ad3[], double ad4[], int ai4[], int ai5[], double ad5[], 
            int ai6[], int ai7[], int ai8[]);

    public static native void trsyl(char ac[], char ac1[], int ai[], int ai1[], int ai2[], double ad[], int ai3[], double ad1[], 
            int ai4[], double ad2[], int ai5[], double ad3[], int ai6[]);

    public static native void trti2(char ac[], char ac1[], int ai[], double ad[], int ai1[], int ai2[]);

    public static native void trtri(char ac[], char ac1[], int ai[], double ad[], int ai1[], int ai2[]);

    public static native void trtrs(char ac[], char ac1[], char ac2[], int ai[], int ai1[], double ad[], int ai2[], double ad1[], 
            int ai3[], int ai4[]);

    public static native void tzrqf(int ai[], int ai1[], double ad[], int ai2[], double ad1[], int ai3[]);

    public static native void tzrzf(int ai[], int ai1[], double ad[], int ai2[], double ad1[], double ad2[], int ai3[], int ai4[]);

    static 
    {
        System.loadLibrary("nni_lapack");
    }
}
