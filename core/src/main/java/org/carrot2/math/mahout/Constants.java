/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2025, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.math.mahout;

public class Constants {
  /*
   * machine constants
   */
  protected static final double MACHEP = 1.11022302462515654042E-16;
  protected static final double MAXLOG = 7.09782712893383996732E2;
  protected static final double MINLOG = -7.451332191019412076235E2;
  protected static final double MAXGAM = 171.624376956302725;
  protected static final double SQTPI = 2.50662827463100050242E0;
  protected static final double SQRTH = 7.07106781186547524401E-1;
  protected static final double LOGPI = 1.14472988584940017414;

  protected static final double big = 4.503599627370496e15;
  protected static final double biginv = 2.22044604925031308085e-16;

  /*
   * MACHEP =  1.38777878078144567553E-17       2**-56
   * MAXLOG =  8.8029691931113054295988E1       log(2**127)
   * MINLOG = -8.872283911167299960540E1        log(2**-128)
   * MAXNUM =  1.701411834604692317316873e38    2**127
   *
   * For IEEE arithmetic (IBMPC):
   * MACHEP =  1.11022302462515654042E-16       2**-53
   * MAXLOG =  7.09782712893383996843E2         log(2**1024)
   * MINLOG = -7.08396418532264106224E2         log(2**-1022)
   * MAXNUM =  1.7976931348623158E308           2**1024
   *
   * The global symbols for mathematical constants are
   * PI     =  3.14159265358979323846           pi
   * PIO2   =  1.57079632679489661923           pi/2
   * PIO4   =  7.85398163397448309616E-1        pi/4
   * SQRT2  =  1.41421356237309504880           sqrt(2)
   * SQRTH  =  7.07106781186547524401E-1        sqrt(2)/2
   * LOG2E  =  1.4426950408889634073599         1/log(2)
   * SQ2OPI =  7.9788456080286535587989E-1      sqrt( 2/pi )
   * LOGE2  =  6.93147180559945309417E-1        log(2)
   * LOGSQ2 =  3.46573590279972654709E-1        log(2)/2
   * THPIO4 =  2.35619449019234492885           3*pi/4
   * TWOOPI =  6.36619772367581343075535E-1     2/pi
   */
  protected Constants() {}
}
