
// Generated file. Do not edit by hand.

package org.carrot2.filter.trc.carrot.tokenizer;

public class TokenizerImplTokenManager implements TokenizerImplConstants
{
  public  java.io.PrintStream debugStream = System.out;
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjMoveStringLiteralDfa0_0()
{
   return jjMoveNfa_0(31, 0);
}
private final void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private final void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private final void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}
private final void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}
private final void jjCheckNAddStates(int start)
{
   jjCheckNAdd(jjnextStates[start]);
   jjCheckNAdd(jjnextStates[start + 1]);
}
static final long[] jjbitVec0 = {
   0x1ff00000fffffffeL, 0xffffffffffffc000L, 0xffffffffL, 0x600000000000000L
};
static final long[] jjbitVec2 = {
   0x0L, 0x0L, 0x0L, 0xff7fffffff7fffffL
};
static final long[] jjbitVec3 = {
   0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL
};
static final long[] jjbitVec4 = {
   0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffL, 0x0L
};
static final long[] jjbitVec5 = {
   0xffffffffffffffffL, 0xffffffffffffffffL, 0x0L, 0x0L
};
static final long[] jjbitVec6 = {
   0x3fffffffffffL, 0x0L, 0x0L, 0x0L
};
static final long[] jjbitVec7 = {
   0x1600L, 0x0L, 0x0L, 0x0L
};
static final long[] jjbitVec8 = {
   0x0L, 0xffc000000000L, 0x0L, 0xffc000000000L
};
static final long[] jjbitVec9 = {
   0x0L, 0x3ff00000000L, 0x0L, 0x3ff000000000000L
};
static final long[] jjbitVec10 = {
   0x0L, 0xffc000000000L, 0x0L, 0xff8000000000L
};
static final long[] jjbitVec11 = {
   0x0L, 0xffc000000000L, 0x0L, 0x0L
};
static final long[] jjbitVec12 = {
   0x0L, 0x3ff0000L, 0x0L, 0x3ff0000L
};
static final long[] jjbitVec13 = {
   0x0L, 0x3ffL, 0x0L, 0x0L
};
private final int jjMoveNfa_0(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 96;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 31:
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 3)
                        kind = 3;
                     jjCheckNAddStates(0, 5);
                  }
                  else if ((0x100002600L & l) != 0L)
                  {
                     if (kind > 16)
                        kind = 16;
                     jjCheckNAdd(62);
                  }
                  else if ((0x800100000000000L & l) != 0L)
                  {
                     if (kind > 9)
                        kind = 9;
                     jjCheckNAdd(41);
                  }
                  else if ((0x8000400200000000L & l) != 0L)
                  {
                     if (kind > 8)
                        kind = 8;
                     jjCheckNAdd(40);
                  }
                  else if (curChar == 38)
                     jjAddStates(6, 7);
                  break;
               case 1:
                  if (curChar == 47)
                     jjstateSet[jjnewStateCnt++] = 2;
                  break;
               case 2:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(8, 14);
                  break;
               case 3:
                  if ((0x3ff200000000000L & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(15, 18);
                  break;
               case 4:
                  if (curChar == 46)
                     jjstateSet[jjnewStateCnt++] = 5;
                  break;
               case 5:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(19, 22);
                  break;
               case 6:
                  if ((0x3ff200000000000L & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(19, 22);
                  break;
               case 7:
                  if (curChar != 47)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(23, 26);
                  break;
               case 8:
                  if (curChar != 46)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(23, 26);
                  break;
               case 9:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(27, 31);
                  break;
               case 10:
                  if ((0x3ff200000000000L & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(27, 31);
                  break;
               case 11:
                  if (curChar == 63)
                     jjstateSet[jjnewStateCnt++] = 12;
                  break;
               case 12:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(32, 34);
                  break;
               case 13:
                  if ((0x3ff200000000000L & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(32, 34);
                  break;
               case 14:
                  if ((0x2c00004000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 15;
                  break;
               case 15:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(35, 37);
                  break;
               case 16:
                  if ((0x3ff200000000000L & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(35, 37);
                  break;
               case 17:
                  if ((0x3ff200000000000L & l) != 0L)
                     jjCheckNAddStates(38, 40);
                  break;
               case 18:
                  if (curChar == 46)
                     jjstateSet[jjnewStateCnt++] = 19;
                  break;
               case 19:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(41, 43);
                  break;
               case 20:
                  if ((0x3ff200000000000L & l) != 0L)
                     jjCheckNAddStates(41, 43);
                  break;
               case 22:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddTwoStates(23, 24);
                  break;
               case 23:
                  if ((0x3ff200000000000L & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddTwoStates(23, 24);
                  break;
               case 24:
                  if (curChar == 46)
                     jjstateSet[jjnewStateCnt++] = 25;
                  break;
               case 25:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddTwoStates(24, 26);
                  break;
               case 26:
                  if ((0x3ff200000000000L & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddTwoStates(24, 26);
                  break;
               case 27:
                  if (curChar == 47)
                     jjstateSet[jjnewStateCnt++] = 1;
                  break;
               case 28:
                  if (curChar == 58)
                     jjstateSet[jjnewStateCnt++] = 27;
                  break;
               case 40:
                  if ((0x8000400200000000L & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAdd(40);
                  break;
               case 41:
                  if ((0x800100000000000L & l) == 0L)
                     break;
                  if (kind > 9)
                     kind = 9;
                  jjCheckNAdd(41);
                  break;
               case 42:
                  if (curChar == 38)
                     jjAddStates(6, 7);
                  break;
               case 44:
                  if (curChar == 59 && kind > 10)
                     kind = 10;
                  break;
               case 52:
                  if (curChar == 35)
                     jjAddStates(44, 45);
                  break;
               case 53:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(44, 54);
                  break;
               case 54:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(46, 48);
                  break;
               case 55:
               case 61:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAdd(44);
                  break;
               case 56:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(55, 44);
                  break;
               case 58:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(49, 52);
                  break;
               case 59:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(53, 55);
                  break;
               case 60:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(61, 44);
                  break;
               case 62:
                  if ((0x100002600L & l) == 0L)
                     break;
                  if (kind > 16)
                     kind = 16;
                  jjCheckNAdd(62);
                  break;
               case 63:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 3)
                     kind = 3;
                  jjCheckNAddStates(0, 5);
                  break;
               case 64:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 3)
                     kind = 3;
                  jjCheckNAdd(64);
                  break;
               case 65:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(65, 66);
                  break;
               case 66:
                  if (curChar == 38)
                     jjCheckNAdd(67);
                  break;
               case 67:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 6)
                     kind = 6;
                  jjCheckNAddTwoStates(66, 67);
                  break;
               case 68:
                  if ((0x3ff200000000000L & l) != 0L)
                     jjCheckNAddStates(56, 58);
                  break;
               case 69:
                  if (curChar == 46)
                     jjstateSet[jjnewStateCnt++] = 70;
                  break;
               case 70:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(59, 61);
                  break;
               case 71:
                  if ((0x3ff200000000000L & l) != 0L)
                     jjCheckNAddStates(59, 61);
                  break;
               case 73:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(74, 75);
                  break;
               case 74:
                  if ((0x3ff200000000000L & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(74, 75);
                  break;
               case 75:
                  if (curChar == 46)
                     jjstateSet[jjnewStateCnt++] = 76;
                  break;
               case 76:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(75, 77);
                  break;
               case 77:
                  if ((0x3ff200000000000L & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(75, 77);
                  break;
               case 80:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 81;
                  break;
               case 83:
                  if (curChar == 39)
                     jjstateSet[jjnewStateCnt++] = 84;
                  break;
               case 85:
                  if (curChar == 46)
                     jjCheckNAddTwoStates(86, 87);
                  break;
               case 86:
                  if ((0x100002600L & l) != 0L)
                     jjCheckNAddTwoStates(86, 87);
                  break;
               case 88:
                  if (curChar == 46)
                     jjCheckNAddTwoStates(89, 90);
                  break;
               case 89:
                  if ((0x100002600L & l) != 0L)
                     jjCheckNAddTwoStates(89, 90);
                  break;
               case 93:
                  if (curChar == 46)
                     jjCheckNAdd(94);
                  break;
               case 95:
                  if (curChar != 46)
                     break;
                  if (kind > 6)
                     kind = 6;
                  jjCheckNAdd(94);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 31:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                  {
                     if (kind > 4)
                        kind = 4;
                     jjCheckNAddStates(62, 67);
                  }
                  if ((0x7fffffe07fffffeL & l) != 0L)
                  {
                     if (kind > 3)
                        kind = 3;
                     jjCheckNAddStates(0, 5);
                  }
                  if (curChar == 109)
                     jjstateSet[jjnewStateCnt++] = 38;
                  else if (curChar == 102)
                     jjstateSet[jjnewStateCnt++] = 32;
                  else if (curChar == 104)
                     jjstateSet[jjnewStateCnt++] = 30;
                  break;
               case 0:
                  if (curChar == 112)
                     jjCheckNAdd(28);
                  break;
               case 2:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(8, 14);
                  break;
               case 3:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(15, 18);
                  break;
               case 5:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(19, 22);
                  break;
               case 6:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(19, 22);
                  break;
               case 9:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(27, 31);
                  break;
               case 10:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(27, 31);
                  break;
               case 12:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(32, 34);
                  break;
               case 13:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(32, 34);
                  break;
               case 14:
                  if (curChar == 64)
                     jjstateSet[jjnewStateCnt++] = 15;
                  break;
               case 15:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(35, 37);
                  break;
               case 16:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(35, 37);
                  break;
               case 17:
                  if ((0x7fffffe87fffffeL & l) != 0L)
                     jjCheckNAddStates(38, 40);
                  break;
               case 19:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddStates(41, 43);
                  break;
               case 20:
                  if ((0x7fffffe87fffffeL & l) != 0L)
                     jjCheckNAddStates(41, 43);
                  break;
               case 21:
                  if (curChar == 64)
                     jjstateSet[jjnewStateCnt++] = 22;
                  break;
               case 22:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddTwoStates(23, 24);
                  break;
               case 23:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddTwoStates(23, 24);
                  break;
               case 25:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddTwoStates(24, 26);
                  break;
               case 26:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddTwoStates(24, 26);
                  break;
               case 29:
               case 32:
                  if (curChar == 116)
                     jjCheckNAdd(0);
                  break;
               case 30:
                  if (curChar == 116)
                     jjstateSet[jjnewStateCnt++] = 29;
                  break;
               case 33:
                  if (curChar == 102)
                     jjstateSet[jjnewStateCnt++] = 32;
                  break;
               case 34:
                  if (curChar == 111)
                     jjCheckNAdd(28);
                  break;
               case 35:
                  if (curChar == 116)
                     jjstateSet[jjnewStateCnt++] = 34;
                  break;
               case 36:
                  if (curChar == 108)
                     jjstateSet[jjnewStateCnt++] = 35;
                  break;
               case 37:
                  if (curChar == 105)
                     jjstateSet[jjnewStateCnt++] = 36;
                  break;
               case 38:
                  if (curChar == 97)
                     jjstateSet[jjnewStateCnt++] = 37;
                  break;
               case 39:
                  if (curChar == 109)
                     jjstateSet[jjnewStateCnt++] = 38;
                  break;
               case 43:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(44, 45);
                  break;
               case 45:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddStates(68, 74);
                  break;
               case 46:
               case 61:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAdd(44);
                  break;
               case 47:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(46, 44);
                  break;
               case 48:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddStates(75, 77);
                  break;
               case 49:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddStates(78, 81);
                  break;
               case 50:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddStates(82, 86);
                  break;
               case 51:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddStates(87, 92);
                  break;
               case 57:
                  if (curChar == 120)
                     jjstateSet[jjnewStateCnt++] = 58;
                  break;
               case 58:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddStates(49, 52);
                  break;
               case 59:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddStates(53, 55);
                  break;
               case 60:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(61, 44);
                  break;
               case 63:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 3)
                     kind = 3;
                  jjCheckNAddStates(0, 5);
                  break;
               case 64:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 3)
                     kind = 3;
                  jjCheckNAdd(64);
                  break;
               case 65:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(65, 66);
                  break;
               case 67:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 6)
                     kind = 6;
                  jjCheckNAddTwoStates(66, 67);
                  break;
               case 68:
                  if ((0x7fffffe87fffffeL & l) != 0L)
                     jjCheckNAddStates(56, 58);
                  break;
               case 70:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddStates(59, 61);
                  break;
               case 71:
                  if ((0x7fffffe87fffffeL & l) != 0L)
                     jjCheckNAddStates(59, 61);
                  break;
               case 72:
                  if (curChar == 64)
                     jjstateSet[jjnewStateCnt++] = 73;
                  break;
               case 73:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(74, 75);
                  break;
               case 74:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(74, 75);
                  break;
               case 76:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(75, 77);
                  break;
               case 77:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(75, 77);
                  break;
               case 78:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 4)
                     kind = 4;
                  jjCheckNAddStates(62, 67);
                  break;
               case 79:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 4)
                     kind = 4;
                  jjCheckNAddTwoStates(79, 80);
                  break;
               case 81:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 4)
                     kind = 4;
                  jjCheckNAddTwoStates(80, 81);
                  break;
               case 82:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(82, 83);
                  break;
               case 84:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 5)
                     kind = 5;
                  jjCheckNAddTwoStates(83, 84);
                  break;
               case 87:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(88, 91);
                  break;
               case 90:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAdd(91);
                  break;
               case 91:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAdd(92);
                  break;
               case 92:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 5)
                     kind = 5;
                  jjCheckNAdd(92);
                  break;
               case 94:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 95;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int hiByte = (int)(curChar >> 8);
         int i1 = hiByte >> 6;
         long l1 = 1L << (hiByte & 077);
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 31:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                  {
                     if (kind > 3)
                        kind = 3;
                     jjCheckNAddStates(0, 5);
                  }
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                  {
                     if (kind > 4)
                        kind = 4;
                     jjCheckNAddStates(62, 67);
                  }
                  break;
               case 2:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(8, 14);
                  break;
               case 3:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(15, 18);
                  break;
               case 5:
               case 6:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(19, 22);
                  break;
               case 9:
               case 10:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(27, 31);
                  break;
               case 12:
               case 13:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(32, 34);
                  break;
               case 15:
               case 16:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(35, 37);
                  break;
               case 17:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(38, 40);
                  break;
               case 19:
               case 20:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(41, 43);
                  break;
               case 22:
               case 23:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddTwoStates(23, 24);
                  break;
               case 25:
               case 26:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddTwoStates(24, 26);
                  break;
               case 43:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(44, 45);
                  break;
               case 45:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(68, 74);
                  break;
               case 46:
               case 61:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAdd(44);
                  break;
               case 47:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(46, 44);
                  break;
               case 48:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(75, 77);
                  break;
               case 49:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(78, 81);
                  break;
               case 50:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(82, 86);
                  break;
               case 51:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(87, 92);
                  break;
               case 53:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(44, 54);
                  break;
               case 54:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(46, 48);
                  break;
               case 55:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAdd(44);
                  break;
               case 56:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(55, 44);
                  break;
               case 58:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(49, 52);
                  break;
               case 59:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(53, 55);
                  break;
               case 60:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(61, 44);
                  break;
               case 63:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 3)
                     kind = 3;
                  jjCheckNAddStates(0, 5);
                  break;
               case 64:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 3)
                     kind = 3;
                  jjCheckNAdd(64);
                  break;
               case 65:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(65, 66);
                  break;
               case 67:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 6)
                     kind = 6;
                  jjCheckNAddTwoStates(66, 67);
                  break;
               case 68:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(56, 58);
                  break;
               case 70:
               case 71:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(59, 61);
                  break;
               case 73:
               case 74:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(74, 75);
                  break;
               case 76:
               case 77:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(75, 77);
                  break;
               case 78:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 4)
                     kind = 4;
                  jjCheckNAddStates(62, 67);
                  break;
               case 79:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 4)
                     kind = 4;
                  jjCheckNAddTwoStates(79, 80);
                  break;
               case 81:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 4)
                     kind = 4;
                  jjCheckNAddTwoStates(80, 81);
                  break;
               case 82:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(82, 83);
                  break;
               case 84:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 5)
                     kind = 5;
                  jjCheckNAddTwoStates(83, 84);
                  break;
               case 87:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(88, 91);
                  break;
               case 90:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAdd(91);
                  break;
               case 91:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAdd(92);
                  break;
               case 92:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 5)
                     kind = 5;
                  jjCheckNAdd(92);
                  break;
               case 94:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjstateSet[jjnewStateCnt++] = 95;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 96 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   64, 65, 66, 68, 69, 72, 43, 52, 3, 4, 7, 11, 17, 18, 21, 3, 
   4, 7, 11, 4, 6, 7, 11, 7, 8, 9, 11, 7, 8, 9, 10, 11, 
   11, 13, 14, 11, 14, 16, 17, 18, 21, 18, 20, 21, 53, 57, 55, 44, 
   56, 59, 60, 61, 44, 60, 61, 44, 68, 69, 72, 69, 71, 72, 79, 80, 
   82, 83, 85, 93, 46, 44, 47, 48, 49, 50, 51, 46, 44, 47, 46, 44, 
   47, 48, 46, 44, 47, 48, 49, 46, 44, 47, 48, 49, 50, 
};
private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2)
{
   switch(hiByte)
   {
      case 0:
         return ((jjbitVec2[i2] & l2) != 0L);
      case 48:
         return ((jjbitVec3[i2] & l2) != 0L);
      case 49:
         return ((jjbitVec4[i2] & l2) != 0L);
      case 51:
         return ((jjbitVec5[i2] & l2) != 0L);
      case 61:
         return ((jjbitVec6[i2] & l2) != 0L);
      default : 
         if ((jjbitVec0[i1] & l1) != 0L)
            return true;
         return false;
   }
}
private static final boolean jjCanMove_1(int hiByte, int i1, int i2, long l1, long l2)
{
   switch(hiByte)
   {
      case 6:
         return ((jjbitVec9[i2] & l2) != 0L);
      case 11:
         return ((jjbitVec10[i2] & l2) != 0L);
      case 13:
         return ((jjbitVec11[i2] & l2) != 0L);
      case 14:
         return ((jjbitVec12[i2] & l2) != 0L);
      case 16:
         return ((jjbitVec13[i2] & l2) != 0L);
      default : 
         if ((jjbitVec7[i1] & l1) != 0L)
            if ((jjbitVec8[i2] & l2) == 0L)
               return false;
            else
            return true;
         return false;
   }
}
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, null, null, null, null, null, null, null, null, 
null, null, null, null, null, };
public static final String[] lexStateNames = {
   "DEFAULT", 
};
static final long[] jjtoToken = {
   0x7fbL, 
};
static final long[] jjtoSkip = {
   0x30000L, 
};
protected SimpleCharStream input_stream;
private final int[] jjrounds = new int[96];
private final int[] jjstateSet = new int[192];
protected char curChar;
public TokenizerImplTokenManager(SimpleCharStream stream)
{
   if (SimpleCharStream.staticFlag)
      throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
   input_stream = stream;
}
public TokenizerImplTokenManager(SimpleCharStream stream, int lexState)
{
   this(stream);
   SwitchTo(lexState);
}
public void ReInit(SimpleCharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private final void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 96; i-- > 0;)
      jjrounds[i] = 0x80000000;
}
public void ReInit(SimpleCharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}
public void SwitchTo(int lexState)
{
   if (lexState >= 1 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

protected Token jjFillToken()
{
   Token t = Token.newToken(jjmatchedKind);
   t.kind = jjmatchedKind;
   String im = jjstrLiteralImages[jjmatchedKind];
   t.image = (im == null) ? input_stream.GetImage() : im;
   t.beginLine = input_stream.getBeginLine();
   t.beginColumn = input_stream.getBeginColumn();
   t.endLine = input_stream.getEndLine();
   t.endColumn = input_stream.getEndColumn();
   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

public Token getNextToken() 
{
  int kind;
  Token specialToken = null;
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {   
   try   
   {     
      curChar = input_stream.BeginToken();
   }     
   catch(java.io.IOException e)
   {        
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedPos == 0 && jjmatchedKind > 17)
   {
      jjmatchedKind = 17;
   }
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
      if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
      {
         matchedToken = jjFillToken();
         return matchedToken;
      }
      else
      {
         continue EOFLoop;
      }
   }
   int error_line = input_stream.getEndLine();
   int error_column = input_stream.getEndColumn();
   String error_after = null;
   boolean EOFSeen = false;
   try { input_stream.readChar(); input_stream.backup(1); }
   catch (java.io.IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
      if (curChar == '\n' || curChar == '\r') {
         error_line++;
         error_column = 0;
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

}
