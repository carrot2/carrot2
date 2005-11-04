Carrot2 Matrix Routines
----------------------

This component provides a number of matrix routines (mostly factorizations)
implemented on top of the Colt Framework. It also provides a Colt-compliant
implementation of a double dense 2d matrix that is backed by the Native 
Numerical Interface to BLAS and LAPACK (which may give a 3 to 10-fold speedup 
compared to pure Java code).

To take advantage of the native interface, you need to compile two dynamically
linked libraries (with BLAS and LAPACK routines) specifically for your
operating system (e.g. a DLL for Windows, .so for Linux, etc.) and for your
CPU. The libraries must be then placed in the search path of your OS.
Instructions on how to build these libraries are provided on the NNI home page: 

http://www.math.uib.no/~bjornoh/mtj/nni/

Currently, the nni-lib/ contains the following precompiled libraries:

nni-linux-pIII-sse1.zip - Linux, Pentium III (Coppermine)
nni-win32-pIII-sse1.zip - Windows NT/2000/XP, Pentium III Celeron
nni-win32-pentiumm.zip  - Windows NT/2000/XP, Pentium M (Centrino)

The following precompiled libraries were contributed by Aaron Binns from Groxis Inc.:

nni-linux-2x-p4-sse2-atlas-3.7.11.zip - Linux, 2xPentium 4 (SSE2) (BLAS only)
nni-linux-2x-p4-sse3-atlas-3.7.11.zip - Linux, 2xPentium 4 (SSE3) (BLAS only)
nni-linux-2x-pIII-sse1-atlas-3.7.11.zip - Linux, 2xPentium III (BLAS only)
nni-linux-4x-p4e-sse2-atlas-3.6.0.zip - Linux, 4xPentium 4E (BLAS only)
nni-linux-4x-p4e-sse3-atlas-3.7.11.zip - Linux, 4xPentium 4E (BLAS only)
nni-win32-opteron150-sse2-atlas-3.7.11.zip - Windows, AMD Opteron 150 (BLAS only)
nni-win32-p4-sse2-atlas-3.7.11.zip - Windows, Pentium 4 (BLAS only)

