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

Currently, the nni-lib/ contains the following precompiled libraries:

nni-linux-pIII-sse1.zip - Linux, Pentium III (Coppermine)
nni-win32-pIII-sse1.zip - Windows NT/2000/XP, Pentium III Celeron
nni-win32-pentiumm.zip  - Windows NT/2000/XP, Pentium M (Centrino)
