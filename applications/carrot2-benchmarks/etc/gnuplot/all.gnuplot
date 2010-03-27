
#
# Draw charts for each individual algorithm.
#

set terminal postscript eps enhanced color dashed size 20cm,17cm

set datafile missing "-"
set grid

set xtics border nomirror
set ytics border nomirror
set tics scale 1.0
set macro


INPUT="tmp/basic-preprocessing.log"
OUTPUT="basic-preprocessing.eps"
INPUT_TITLE="Basic Preprocessing"
load "etc/gnuplot/_algorithm.gnuplot"


INPUT="tmp/lingo.log"
OUTPUT="lingo.eps"
INPUT_TITLE="Lingo"
load "etc/gnuplot/_algorithm.gnuplot"


INPUT="tmp/stc.log"
OUTPUT="stc.eps"
INPUT_TITLE="STC"
load "etc/gnuplot/_algorithm.gnuplot"

#
# Draw aggregate charts.
#

set output "all-memory.eps"

set xlabel "documents"
set ylabel "peak memory [MB]"
plot \
  "tmp/basic-preprocessing.log" using 1:4 t "Basic Preprocessing" with linespoints, \
  "tmp/stc.log"                 using 1:4 t "STC"                 with linespoints, \
  "tmp/lingo.log"               using 1:4 t "LINGO"               with linespoints


set output "all-times.eps"

set xlabel "documents"
set ylabel "time [s]"
plot \
  "tmp/basic-preprocessing.log" using 1:3 t "Basic Preprocessing" with linespoints, \
  "tmp/stc.log"                 using 1:3 t "STC"                 with linespoints, \
  "tmp/lingo.log"               using 1:3 t "LINGO"               with linespoints

set output "stc-preproc.eps"

plot \
  "tmp/basic-preprocessing.log" using 1:3 t "Basic Preprocessing" with linespoints, \
  "tmp/stc.log"                 using 1:3 t "STC"                 with linespoints
  