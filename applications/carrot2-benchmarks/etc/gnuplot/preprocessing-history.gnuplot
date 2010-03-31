
#
# Draw charts for each individual algorithm.
#

set terminal postscript eps enhanced color dashed size 20cm,17cm

#
# Draw comparison charts.
#

set output "memory.eps"

set xlabel "documents"
set ylabel "peak memory [MB]"
plot \
  "tmp/basic-preprocessing.log"                      using 1:4 t "HEAD"    with linespoints, \
  "results/20100331-SVN4219/basic-preprocessing.log" using 1:4 t "SVN4219" with linespoints, \
  "results/20100329-SVN4209/basic-preprocessing.log" using 1:4 t "SVN4209" with linespoints, \
  "results/20100328-SVN4203/basic-preprocessing.log" using 1:4 t "SVN4203" with linespoints, \
  "results/20100327-SVN4197/basic-preprocessing.log" using 1:4 t "SVN4197" with linespoints



set output "times.eps"

set xlabel "documents"
set ylabel "time [s]"
plot \
  "tmp/basic-preprocessing.log"                      using 1:3 t "HEAD"    with linespoints, \
  "results/20100331-SVN4219/basic-preprocessing.log" using 1:3 t "SVN4219" with linespoints, \
  "results/20100329-SVN4209/basic-preprocessing.log" using 1:3 t "SVN4209" with linespoints, \
  "results/20100328-SVN4203/basic-preprocessing.log" using 1:3 t "SVN4203" with linespoints, \
  "results/20100327-SVN4197/basic-preprocessing.log" using 1:3 t "SVN4197" with linespoints
