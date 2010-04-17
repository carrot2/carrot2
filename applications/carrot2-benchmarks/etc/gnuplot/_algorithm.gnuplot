set output OUTPUT
set multiplot title INPUT_TITLE layout 2,2

# size/docs
set xlabel "documents"
plot INPUT using 1:2 t "input size [MB]" with linespoints

# mem/docs
set xlabel "documents"
plot INPUT using 1:4 t "peak memory [MB]" with linespoints
    
# time/docs
set xlabel "documents"
plot INPUT using 1:3 t "time [s]" with linespoints

# time/size
set xlabel "input size [MB]"
plot INPUT using 2:3 t "time [s]" with linespoints

unset multiplot
