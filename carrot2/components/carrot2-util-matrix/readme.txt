Carrot2 Matix Routines
----------------------

This component provides a number of matrix routines (mostly factorizations)
implemented on top of the Colt Framework. It also provides a Colt-compliant
implementation of a double dense 2d matrix that is backed by the Native 
Numerical Interface to BLAS and LAPACK (which may give a 3 to 10-fold speedup 
compared to pure Java code).

To take advantage of the native interface, you need to compile two dynamically
linked libraries (with BLAS and LAPACK routines) specifically for your
operatind system (e.g. a DLL for Windows, .so for Linux, etc.) and for your
CPU. The libraries must be then placed in the search path of your OS.
Instructions on how to build these libraries are provided on the NNI home page: 

http://www.math.uib.no/~bjornoh/mtj/nni/

Two sets of Windows DLL I've compiled under Cygwin are available from here:

P4 1024kB L2 cache SSE2: 

http://www.man.poznan.pl/~stachoo/nni-lib/nni-win32-p4-sse2-1024l2c.zip

P4 512kB L2 cache SSE2:

http://www.man.poznan.pl/~stachoo/nni-lib/nni-win32-p4-sse2-512l2c.zip
