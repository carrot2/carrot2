#!/usr/bin/ruby

#
# Extract queries from Carrot2 log files. Log file format assumes:
# date,algorithm,source,count,time,QUERY
#

if ARGV.empty?
	$stderr.puts "Parameters: [file] [file] ..." 
	exit 1
end

PATTERN	= \
/(?:[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2},[0-9]{3},)\
([^,]+)(?:,)\
([^,]+)(?:,)\
([^,]+)(?:,)\
([^,]+)(?:,)\
(.+)/

ARGV.each do |file|
  IO.foreach(file) do |line|
    next if line.empty?
    (algorithm,source,count,time,query) = PATTERN.match(line.chop).to_a[1..-1]
    next if ["wiki", "indeed", "icerocket"].include? source
    next if query.nil? or query.include? ','

    puts "%s,%s,%s,%s" % [algorithm, source, count, query]
  end
end
