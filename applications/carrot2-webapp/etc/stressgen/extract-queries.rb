#!/usr/bin/ruby

#
# Extract queries from Carrot2 log files. Log file format assumes:
# [ignored] -- algorithm,source,count,time,QUERY
#

if ARGV.empty?
	$stderr.puts "Parameters: [file] [file] ..." 
	exit 1
end

PATTERN	= /(?:[-]{2}\s+)([^,]+)(?:,)([^,]+)(?:,)([^,]+)(?:,)([^,]+)(?:,)(.+)/
ARGV.each do |file|
	IO.foreach(file) do |line|
		(algorithm,source,count,time,query) = PATTERN.match(line.chop).to_a[1..-1]
		puts query
	end
end