
// Generated file. Do not edit by hand.

package org.carrot2.text.analysis;

@SuppressWarnings("all")
/** JFlex-generated scanner. */

public final class ExtendedWhitespaceTokenizerImpl {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0, 0
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\0\1\23\1\0\1\0\1\24\22\0\1\0\1\42\1\0"+
    "\1\51\2\35\1\41\1\30\4\35\1\40\1\33\1\34\1\37\12\26"+
    "\1\36\1\43\1\0\1\35\1\0\1\42\1\46\32\50\1\35\1\0"+
    "\1\35\1\0\1\32\1\31\1\21\1\14\1\16\1\12\1\11\1\5"+
    "\1\7\1\47\1\2\2\50\1\3\1\1\1\4\1\6\1\22\1\50"+
    "\1\17\1\45\1\20\1\13\1\10\3\50\1\15\3\0\1\35\6\0"+
    "\1\0\72\0\27\25\1\0\37\25\1\0\u0568\25\12\27\206\25\12\27"+
    "\u026a\25\2\44\12\27\166\25\12\27\166\25\12\27\166\25\12\27\166\25"+
    "\12\27\167\25\11\27\166\25\12\27\166\25\12\27\166\25\12\27\340\25"+
    "\12\27\166\25\12\27\u0166\25\12\27\u0fb6\25\50\0\1\0\1\0\u1016\0"+
    "\u0150\25\u0170\0\200\25\200\0\u092e\25\u10d2\0\u5200\25\u0c00\0\u2ba4\25\u215c\0"+
    "\u0200\25\u0500\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\1\0\1\1\3\2\1\1\2\3\1\4\1\5\1\1"+
    "\3\2\1\0\2\6\1\2\1\0\1\2\1\0\1\2"+
    "\1\3\1\6\2\0\1\6\1\2\1\0\1\2\1\0"+
    "\2\2\1\7\12\6\1\10\1\2\1\11\1\2\1\3"+
    "\12\6\5\3\2\2\1\0\1\2\1\0\2\12\1\10"+
    "\10\12\2\0\1\3\1\10\2\2\1\7\1\6\3\13"+
    "\2\6\1\0\1\6\2\13\2\2\12\6\1\11\1\0"+
    "\2\3\5\0";

  private static int [] zzUnpackAction() {
    int [] result = new int[118];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\52\0\124\0\176\0\250\0\322\0\374\0\u0126"+
    "\0\52\0\u0150\0\u017a\0\u01a4\0\u01ce\0\u01f8\0\u0222\0\u024c"+
    "\0\u0276\0\u02a0\0\u02ca\0\u02f4\0\u031e\0\u0348\0\u0372\0\u039c"+
    "\0\u03c6\0\u03f0\0\u041a\0\u0444\0\u046e\0\u0498\0\u04c2\0\u04ec"+
    "\0\u0516\0\u0540\0\u056a\0\u0594\0\u05be\0\u05e8\0\u0612\0\u063c"+
    "\0\u0666\0\u0690\0\u06ba\0\u06e4\0\u070e\0\u0222\0\u0738\0\u0762"+
    "\0\u078c\0\u07b6\0\u07e0\0\u080a\0\u0834\0\u085e\0\u0888\0\u08b2"+
    "\0\u08dc\0\u0906\0\u0930\0\u095a\0\u0984\0\u09ae\0\u09d8\0\u0a02"+
    "\0\u0a2c\0\u0a56\0\u0a80\0\u0aaa\0\u0ad4\0\u0afe\0\u0b28\0\u02a0"+
    "\0\u0b52\0\u0b7c\0\u0ba6\0\u0bd0\0\u0bfa\0\u0c24\0\u0c4e\0\u0c78"+
    "\0\u0ca2\0\u0ccc\0\u0cf6\0\u0444\0\u0d20\0\u0d4a\0\u0d74\0\u0afe"+
    "\0\u0d9e\0\u0dc8\0\u0df2\0\u0e1c\0\u0e46\0\u0e70\0\u0e9a\0\u0ec4"+
    "\0\u0eee\0\u0f18\0\u0f42\0\u0f6c\0\u0f96\0\u0fc0\0\u0fea\0\u1014"+
    "\0\u103e\0\u1068\0\u1092\0\u10bc\0\u10e6\0\u1110\0\u113a\0\u1164"+
    "\0\u118e\0\u11b8\0\u11e2\0\u120c\0\u1236\0\u1260";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[118];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\2\1\3\3\4\1\5\15\4\1\2\1\6\1\4"+
    "\1\7\1\10\2\11\1\2\1\11\1\12\1\2\1\11"+
    "\1\2\1\11\1\13\3\12\1\4\1\2\1\14\1\4"+
    "\1\2\53\0\20\15\1\16\1\15\2\0\3\15\2\17"+
    "\1\20\1\21\1\22\4\0\1\23\3\0\1\24\1\25"+
    "\2\15\2\0\22\15\2\0\3\15\2\17\1\20\1\21"+
    "\1\22\4\0\1\23\3\0\1\24\1\25\2\15\2\0"+
    "\17\15\1\26\2\15\2\0\3\15\2\17\1\20\1\21"+
    "\1\22\4\0\1\23\3\0\1\24\1\25\2\15\24\0"+
    "\1\2\27\0\22\15\2\0\1\15\1\7\1\27\2\0"+
    "\1\20\1\30\1\31\1\0\3\32\4\0\1\15\1\25"+
    "\2\15\2\0\22\15\2\0\1\15\2\27\2\17\1\20"+
    "\1\33\1\34\1\0\3\32\1\23\3\0\1\24\1\25"+
    "\2\15\35\0\1\12\5\0\3\12\6\0\22\35\3\0"+
    "\1\35\16\0\1\35\1\0\3\35\1\0\17\15\1\36"+
    "\2\15\2\0\3\15\2\17\1\20\1\21\1\22\4\0"+
    "\1\23\3\0\1\24\1\25\2\15\2\0\22\15\2\0"+
    "\3\15\2\17\1\20\1\21\1\37\4\0\1\23\3\0"+
    "\1\24\1\25\2\15\2\0\1\15\1\40\20\15\2\0"+
    "\3\15\2\17\1\20\1\21\1\37\4\0\1\23\3\0"+
    "\1\24\1\25\2\15\2\0\22\41\2\0\1\41\1\0"+
    "\1\41\15\0\1\41\1\0\2\41\2\0\22\20\2\0"+
    "\3\20\2\0\2\20\1\37\10\0\1\20\1\25\2\20"+
    "\2\0\22\42\2\0\1\42\1\21\1\42\2\0\2\20"+
    "\1\37\10\0\1\42\1\25\2\42\2\0\1\43\1\44"+
    "\1\45\1\46\1\45\1\47\1\50\1\45\1\51\2\45"+
    "\1\52\1\45\1\53\2\45\1\54\1\45\2\0\1\45"+
    "\1\20\1\45\15\0\1\45\1\0\2\45\2\0\22\55"+
    "\2\0\1\55\1\23\1\55\15\0\1\55\1\0\2\55"+
    "\2\0\22\15\2\0\3\15\2\56\1\20\1\21\1\37"+
    "\4\0\1\23\3\0\1\24\1\25\2\15\2\0\22\57"+
    "\2\0\3\57\15\0\1\57\1\0\2\57\2\0\21\15"+
    "\1\60\2\0\3\15\2\17\1\20\1\21\1\37\4\0"+
    "\1\23\3\0\1\24\1\25\2\15\2\0\22\15\2\0"+
    "\1\15\2\27\2\17\1\20\1\33\1\31\1\0\3\32"+
    "\1\23\3\0\1\24\1\25\2\15\2\0\22\20\2\0"+
    "\1\20\2\61\2\0\2\20\1\37\10\0\1\20\1\25"+
    "\2\20\2\0\1\62\1\63\1\64\1\65\1\64\1\66"+
    "\1\67\1\64\1\70\2\64\1\71\1\64\1\72\2\64"+
    "\1\73\1\64\2\0\1\64\1\61\1\74\15\0\1\64"+
    "\1\0\2\64\27\0\2\75\23\0\22\42\2\0\1\42"+
    "\1\76\1\77\2\0\2\20\1\37\10\0\1\42\1\25"+
    "\2\42\2\0\1\43\1\44\1\45\1\46\1\45\1\47"+
    "\1\50\1\45\1\51\2\45\1\52\1\45\1\53\2\45"+
    "\1\54\1\45\2\0\1\45\1\61\1\100\15\0\1\45"+
    "\1\0\2\45\2\0\22\35\3\0\1\35\14\0\1\2"+
    "\1\0\1\35\1\0\3\35\1\0\17\15\1\101\2\15"+
    "\2\0\3\15\2\17\1\20\1\21\1\37\4\0\1\23"+
    "\3\0\1\24\1\25\2\15\2\0\1\62\1\63\1\64"+
    "\1\65\1\64\1\66\1\67\1\64\1\70\2\64\1\71"+
    "\1\64\1\72\2\64\1\73\1\64\2\0\1\64\1\20"+
    "\1\64\15\0\1\64\1\0\2\64\2\0\2\15\1\102"+
    "\17\15\2\0\3\15\2\17\1\20\1\21\1\37\4\0"+
    "\1\23\3\0\1\24\1\25\2\15\2\0\22\41\2\0"+
    "\3\41\2\17\1\0\1\103\5\0\1\23\3\0\1\104"+
    "\1\0\2\41\2\0\22\42\2\0\3\42\2\105\1\20"+
    "\1\21\1\37\10\0\1\42\1\25\2\42\2\0\1\106"+
    "\1\107\20\106\2\0\1\106\1\20\1\106\2\0\2\20"+
    "\1\110\10\0\1\106\1\25\2\106\2\0\3\106\1\111"+
    "\16\106\2\0\1\106\1\20\1\106\2\0\2\20\1\110"+
    "\10\0\1\106\1\25\2\106\2\0\22\106\2\0\1\106"+
    "\1\20\1\106\2\0\2\20\1\110\10\0\1\106\1\25"+
    "\2\106\2\0\10\106\1\112\11\106\2\0\1\106\1\20"+
    "\1\106\2\0\2\20\1\110\10\0\1\106\1\25\2\106"+
    "\2\0\16\106\1\113\3\106\2\0\1\106\1\20\1\106"+
    "\2\0\2\20\1\110\10\0\1\106\1\25\2\106\2\0"+
    "\5\106\1\114\14\106\2\0\1\106\1\20\1\106\2\0"+
    "\2\20\1\110\10\0\1\106\1\25\2\106\2\0\11\106"+
    "\1\115\10\106\2\0\1\106\1\20\1\106\2\0\2\20"+
    "\1\110\10\0\1\106\1\25\2\106\2\0\1\106\1\116"+
    "\20\106\2\0\1\106\1\20\1\106\2\0\2\20\1\110"+
    "\10\0\1\106\1\25\2\106\2\0\5\106\1\117\14\106"+
    "\2\0\1\106\1\20\1\106\2\0\2\20\1\110\10\0"+
    "\1\106\1\25\2\106\2\0\16\106\1\120\3\106\2\0"+
    "\1\106\1\20\1\106\2\0\2\20\1\110\10\0\1\106"+
    "\1\25\2\106\2\0\22\55\2\0\3\55\2\121\7\0"+
    "\1\23\3\0\1\55\1\0\2\55\2\0\22\57\2\0"+
    "\3\57\2\0\2\57\1\25\10\0\1\57\1\0\2\57"+
    "\2\0\22\15\2\0\3\15\2\17\1\20\1\21\1\37"+
    "\1\0\1\122\2\0\1\23\3\0\1\24\1\25\2\15"+
    "\2\0\22\20\2\0\1\20\2\61\2\0\1\20\1\30"+
    "\1\31\1\0\3\32\4\0\1\20\1\25\2\20\2\0"+
    "\1\106\1\107\20\106\2\0\1\106\1\20\1\106\2\0"+
    "\2\20\1\37\10\0\1\106\1\25\2\106\2\0\3\106"+
    "\1\111\16\106\2\0\1\106\1\20\1\106\2\0\2\20"+
    "\1\37\10\0\1\106\1\25\2\106\2\0\22\106\2\0"+
    "\1\106\1\20\1\106\2\0\2\20\1\37\10\0\1\106"+
    "\1\25\2\106\2\0\10\106\1\112\11\106\2\0\1\106"+
    "\1\20\1\106\2\0\2\20\1\37\10\0\1\106\1\25"+
    "\2\106\2\0\16\106\1\113\3\106\2\0\1\106\1\20"+
    "\1\106\2\0\2\20\1\37\10\0\1\106\1\25\2\106"+
    "\2\0\5\106\1\114\14\106\2\0\1\106\1\20\1\106"+
    "\2\0\2\20\1\37\10\0\1\106\1\25\2\106\2\0"+
    "\11\106\1\115\10\106\2\0\1\106\1\20\1\106\2\0"+
    "\2\20\1\37\10\0\1\106\1\25\2\106\2\0\1\106"+
    "\1\116\20\106\2\0\1\106\1\20\1\106\2\0\2\20"+
    "\1\37\10\0\1\106\1\25\2\106\2\0\5\106\1\117"+
    "\14\106\2\0\1\106\1\20\1\106\2\0\2\20\1\37"+
    "\10\0\1\106\1\25\2\106\2\0\16\106\1\120\3\106"+
    "\2\0\1\106\1\20\1\106\2\0\2\20\1\37\10\0"+
    "\1\106\1\25\2\106\2\0\22\106\2\0\1\106\1\61"+
    "\1\123\2\0\1\20\1\30\1\31\1\0\3\32\4\0"+
    "\1\106\1\25\2\106\27\0\2\75\3\0\2\32\1\0"+
    "\3\32\12\0\22\42\2\0\1\42\1\76\1\77\2\0"+
    "\1\20\1\30\1\31\1\0\3\32\4\0\1\42\1\25"+
    "\2\42\2\0\22\42\2\0\1\42\2\77\2\105\1\20"+
    "\1\33\1\31\1\0\3\32\4\0\1\42\1\25\2\42"+
    "\2\0\22\106\2\0\1\106\1\61\1\123\2\0\1\20"+
    "\1\30\1\124\1\0\3\32\4\0\1\106\1\25\2\106"+
    "\2\0\21\15\1\125\2\0\3\15\2\17\1\20\1\21"+
    "\1\37\4\0\1\23\3\0\1\24\1\25\2\15\2\0"+
    "\17\15\1\126\2\15\2\0\3\15\2\17\1\20\1\21"+
    "\1\37\4\0\1\23\3\0\1\24\1\25\2\15\2\0"+
    "\22\127\2\0\1\127\1\103\1\127\15\0\1\127\1\0"+
    "\2\127\2\0\22\41\2\0\3\41\2\56\1\0\1\103"+
    "\5\0\1\23\3\0\1\104\1\0\2\41\2\0\22\127"+
    "\2\0\1\127\1\0\1\127\15\0\1\127\1\0\2\127"+
    "\2\0\22\130\2\0\3\130\1\131\1\0\2\130\1\132"+
    "\7\131\1\0\1\130\1\133\2\130\1\131\1\0\2\130"+
    "\1\106\17\130\2\0\3\130\1\131\1\0\2\130\1\132"+
    "\7\131\1\0\1\130\1\133\2\130\1\131\1\0\4\130"+
    "\1\134\15\130\2\0\3\130\1\131\1\0\2\130\1\132"+
    "\7\131\1\0\1\130\1\133\2\130\1\131\1\0\17\130"+
    "\1\106\2\130\2\0\3\130\1\131\1\0\2\130\1\132"+
    "\7\131\1\0\1\130\1\133\2\130\1\131\1\0\6\130"+
    "\1\106\13\130\2\0\3\130\1\131\1\0\2\130\1\132"+
    "\7\131\1\0\1\130\1\133\2\130\1\131\1\0\7\130"+
    "\1\106\12\130\2\0\3\130\1\131\1\0\2\130\1\132"+
    "\7\131\1\0\1\130\1\133\2\130\1\131\1\0\12\130"+
    "\1\106\7\130\2\0\3\130\1\131\1\0\2\130\1\132"+
    "\7\131\1\0\1\130\1\133\2\130\1\131\1\0\14\130"+
    "\1\106\5\130\2\0\3\130\1\131\1\0\2\130\1\132"+
    "\7\131\1\0\1\130\1\133\2\130\1\131\1\0\1\106"+
    "\21\130\2\0\3\130\1\131\1\0\2\130\1\132\7\131"+
    "\1\0\1\130\1\133\2\130\1\131\1\0\21\130\1\135"+
    "\2\0\3\130\1\131\1\0\2\130\1\132\7\131\1\0"+
    "\1\130\1\133\2\130\1\131\1\0\22\55\2\0\1\55"+
    "\1\0\1\55\15\0\1\55\1\0\2\55\40\0\1\136"+
    "\13\0\22\130\2\0\1\130\2\123\1\131\1\0\1\130"+
    "\1\137\1\140\1\131\3\141\3\131\1\0\1\130\1\133"+
    "\2\130\1\131\1\0\22\15\2\0\3\15\2\17\1\20"+
    "\1\21\1\37\1\0\1\122\2\0\1\23\3\0\1\142"+
    "\1\25\2\15\2\0\5\15\1\143\14\15\2\0\3\15"+
    "\2\17\1\20\1\21\1\37\4\0\1\23\3\0\1\24"+
    "\1\25\2\15\2\0\22\127\2\0\3\127\2\105\1\0"+
    "\1\103\11\0\1\127\1\0\2\127\2\0\22\131\2\0"+
    "\4\131\1\0\12\131\1\0\5\131\1\0\1\144\1\145"+
    "\1\146\1\147\1\146\1\150\1\151\1\146\1\152\2\146"+
    "\1\153\1\146\1\154\2\146\1\155\1\146\2\0\1\146"+
    "\1\130\1\146\1\131\1\0\12\131\1\0\1\146\1\131"+
    "\2\146\1\131\1\0\22\156\2\0\3\156\1\131\1\0"+
    "\12\131\1\0\1\156\1\131\2\156\1\131\1\0\5\130"+
    "\1\106\14\130\2\0\3\130\1\131\1\0\2\130\1\132"+
    "\7\131\1\0\1\130\1\133\2\130\1\131\1\0\20\130"+
    "\1\106\1\130\2\0\3\130\1\131\1\0\2\130\1\132"+
    "\7\131\1\0\1\130\1\133\2\130\1\131\37\0\1\157"+
    "\13\0\22\130\2\0\1\130\2\123\1\131\1\0\2\130"+
    "\1\132\7\131\1\0\1\130\1\133\2\130\1\131\1\0"+
    "\1\144\1\145\1\146\1\147\1\146\1\150\1\151\1\146"+
    "\1\152\2\146\1\153\1\146\1\154\2\146\1\155\1\146"+
    "\2\0\1\146\1\123\1\160\1\131\1\0\12\131\1\0"+
    "\1\146\1\131\2\146\1\131\1\0\22\131\2\0\1\131"+
    "\2\161\1\131\1\0\12\131\1\0\5\131\1\0\22\15"+
    "\2\0\3\15\2\56\1\20\1\21\1\37\1\0\1\122"+
    "\2\0\1\23\3\0\1\24\1\25\2\15\2\0\22\15"+
    "\2\0\3\15\2\17\1\20\1\21\1\37\1\0\1\162"+
    "\2\0\1\23\3\0\1\24\1\25\2\15\2\0\1\106"+
    "\1\107\20\106\2\0\1\106\1\130\1\106\1\131\1\0"+
    "\2\130\1\132\7\131\1\0\1\106\1\133\2\106\1\131"+
    "\1\0\3\106\1\111\16\106\2\0\1\106\1\130\1\106"+
    "\1\131\1\0\2\130\1\132\7\131\1\0\1\106\1\133"+
    "\2\106\1\131\1\0\22\106\2\0\1\106\1\130\1\106"+
    "\1\131\1\0\2\130\1\132\7\131\1\0\1\106\1\133"+
    "\2\106\1\131\1\0\10\106\1\112\11\106\2\0\1\106"+
    "\1\130\1\106\1\131\1\0\2\130\1\132\7\131\1\0"+
    "\1\106\1\133\2\106\1\131\1\0\16\106\1\113\3\106"+
    "\2\0\1\106\1\130\1\106\1\131\1\0\2\130\1\132"+
    "\7\131\1\0\1\106\1\133\2\106\1\131\1\0\5\106"+
    "\1\114\14\106\2\0\1\106\1\130\1\106\1\131\1\0"+
    "\2\130\1\132\7\131\1\0\1\106\1\133\2\106\1\131"+
    "\1\0\11\106\1\115\10\106\2\0\1\106\1\130\1\106"+
    "\1\131\1\0\2\130\1\132\7\131\1\0\1\106\1\133"+
    "\2\106\1\131\1\0\1\106\1\116\20\106\2\0\1\106"+
    "\1\130\1\106\1\131\1\0\2\130\1\132\7\131\1\0"+
    "\1\106\1\133\2\106\1\131\1\0\5\106\1\117\14\106"+
    "\2\0\1\106\1\130\1\106\1\131\1\0\2\130\1\132"+
    "\7\131\1\0\1\106\1\133\2\106\1\131\1\0\16\106"+
    "\1\120\3\106\2\0\1\106\1\130\1\106\1\131\1\0"+
    "\2\130\1\132\7\131\1\0\1\106\1\133\2\106\1\131"+
    "\1\0\22\156\2\0\3\156\1\131\1\0\2\156\1\133"+
    "\7\131\1\0\1\156\1\131\2\156\1\131\1\0\22\163"+
    "\2\0\3\163\15\0\1\163\1\0\2\163\2\0\22\106"+
    "\2\0\1\106\2\123\1\131\1\0\1\130\1\137\1\140"+
    "\1\131\3\141\3\131\1\0\1\106\1\133\2\106\1\131"+
    "\1\0\22\131\2\0\1\131\2\161\1\131\1\0\1\131"+
    "\2\141\1\131\3\141\3\131\1\0\5\131\1\0\22\164"+
    "\2\0\3\164\15\0\1\164\1\0\2\164\2\0\22\163"+
    "\2\0\3\163\2\0\2\163\1\165\10\0\1\163\1\0"+
    "\2\163\2\0\22\164\2\0\3\164\2\0\2\164\1\162"+
    "\10\0\1\164\1\25\2\164\2\0\22\166\2\0\1\166"+
    "\1\163\1\166\15\0\1\166\1\0\2\166\2\0\22\131"+
    "\2\0\1\131\1\163\1\131\2\0\2\163\1\165\10\0"+
    "\1\131\1\0\2\131\1\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[4746];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\1\0\1\11\6\1\1\11\5\1\1\0\3\1\1\0"+
    "\1\1\1\0\3\1\2\0\2\1\1\0\1\1\1\0"+
    "\43\1\1\0\1\1\1\0\13\1\2\0\13\1\1\0"+
    "\20\1\1\0\2\1\5\0";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[118];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;

  /* user code: */
	public final int yychar()
	{
	    return yychar;
	}
    /**
     * Return the internal token's character buffer.
     */
	final char [] yybuffer() {
		return zzBuffer;
	}

	final int yystart() {
		return zzStartRead;
	}


  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public ExtendedWhitespaceTokenizerImpl(java.io.Reader in) {
    this.zzReader = in;
  }


  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 224) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzCurrentPos*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = zzReader.read(zzBuffer, zzEndRead,
                                            zzBuffer.length-zzEndRead);

    if (numRead > 0) {
      zzEndRead+= numRead;
      return false;
    }
    // unlikely but not impossible: read 0 characters, but not at end of stream    
    if (numRead == 0) {
      int c = zzReader.read();
      if (c == -1) {
        return true;
      } else {
        zzBuffer[zzEndRead++] = (char) c;
        return false;
      }     
    }

    // numRead < 0
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * Internal scan buffer is resized down to its initial length, if it has grown.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEOFDone = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
    if (zzBuffer.length > ZZ_BUFFERSIZE)
      zzBuffer = new char[ZZ_BUFFERSIZE];
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public int getNextToken() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      yychar+= zzMarkedPosL-zzStartRead;

      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL[zzCurrentPosL++];
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL[zzCurrentPosL++];
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 1: 
          { ;
          }
        case 12: break;
        case 2: 
          { return ITokenizer.TT_TERM;
          }
        case 13: break;
        case 3: 
          { return ITokenizer.TT_NUMERIC;
          }
        case 14: break;
        case 4: 
          { return ITokenizer.TT_PUNCTUATION;
          }
        case 15: break;
        case 5: 
          { return ITokenizer.TT_PUNCTUATION | ITokenizer.TF_SEPARATOR_SENTENCE;
          }
        case 16: break;
        case 6: 
          { return ITokenizer.TT_FILE;
          }
        case 17: break;
        case 7: 
          { return ITokenizer.TT_HYPHTERM;
          }
        case 18: break;
        case 8: 
          { return ITokenizer.TT_ACRONYM;
          }
        case 19: break;
        case 9: 
          { return ITokenizer.TT_EMAIL;
          }
        case 20: break;
        case 10: 
          { return ITokenizer.TT_BARE_URL;
          }
        case 21: break;
        case 11: 
          { return ITokenizer.TT_FULL_URL;
          }
        case 22: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            return YYEOF;
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
