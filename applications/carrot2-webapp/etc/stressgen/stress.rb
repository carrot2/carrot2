#!/usr/bin/ruby

#
# Simple stress generator for the webapp.
#

require 'net/http'
require 'thread'
require 'optparse'

#
# Declare options
#

options = {:threads => 1, :mps => 0}
OptionParser.new do |opts|
    opts.banner = "Usage: stress.rb [options]"

    opts.separator ""
    opts.separator "Options:"

    opts.on("-t N", "--threads N", Float, "Number of concurrent threads.") do |n|
        options[:threads] = n
    end

    opts.on("-m N", "--mps N", Float, "Maximum requests per second (total). No limit if zero.") do |mps|
        options[:mps] = mps.to_i
    end

    opts.on("-h", "--help", "Show this message") do
      puts opts
      exit
    end

    opts.separator ""
    opts.separator "Queries are read from the input stream."
end.parse!

#
# Set to a large value if no limits.
#
options[:mps] = 10000 if options[:mps] == 0 

#
# Base URI pattern.
#
host = 'http://localhost:8080';
uri = '/search'
qstring = 'source=#{source}&view=tree&skin=fancy-compact&results=100&algorithm=#{algorithm}&query=#{query}&type=CLUSTERS'
$base_uri = host + uri + "?" + qstring

#
# Sources and algorithms for a given version of the engine
#

CARROT2_30 = {
	:sources => ["web", "boss-web", "boss-images", "yahoo-news", "msn-web", "indeed", "pubmed", "icerocket"],
	:algorithms => ["lingo", "stc", "url", "source"]
}

#
# Sources and algorithms for this run.
#

$sources = CARROT2_30[:sources]
$algorithms = CARROT2_30[:algorithms]

#
# Queries are read from standard input, each query in a single line.
#
$queries = []
$stdin.each_line do |line|
	q = line.chop
	next if q.empty?
	$queries << q
end
$stderr.puts "Read: #{$queries.length} queries"

#
# Take random element from an array
#
def rand_elem(array)
	array[rand(array.length)]
end

#
# Create query threads and the queue
#
threads = []

@waitroom = []
@mutex = Mutex.new
@queue = []

(1..options[:threads]).each do |t|
    threads << Thread.new do
        while true
            waiting = false
            @mutex.synchronize do
                entry = { :time => Time.now, :thread => Thread.current }
                if @queue.size + 1 > options[:mps]
                    waiting = true
                    @waitroom << entry
                else
                    @queue << entry
                end
            end

            if waiting then
                Thread.stop
                next
            end

            query = rand_elem($queries)
            query = URI.encode(query)

            source = rand_elem($sources)
            algorithm = rand_elem($algorithms)

            uri = eval('"' << $base_uri << '"')
            begin
                res = Net::HTTP.get_response(URI.parse(uri))
                $stdout.print '.'
                $stdout.flush
            rescue Exception => e
                $stdout.print '#'
                $stdout.flush
            end
        end
    end
end

#
# Main loop. Control the mps.
#
Thread.current.priority = 2
while true
    @mutex.synchronize do
        deadline = Time.now - 1
        while (not @queue.empty?) and @queue[0][:time] < deadline
            @queue.delete_at(0)
            if @queue.size < options[:mps] and not @waitroom.empty?
                @waitroom.each { |e| e[:thread].wakeup }
                @waitroom.clear
            end
        end
    end
    sleep 0.1
end


